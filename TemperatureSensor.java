package project;

import org.firmata4j.I2CDevice;
import java.io.IOException;
//refrences: Temperature sensor references:
//https://community.bosch-sensortec.com/mems-sensors-forum-jrmujtaw/post/bmp280-reading-data-using-i2c-from-address-0x76-XkXG3A9jAg3E8U0
//https://forum.allaboutcircuits.com/threads/bmp280-digital-pressure-sensor.167356/
//
//https://www.datasheets.com/bosch/bmp280#datasheet page 13 - 27
//
//
//https://www.youtube.com/watch?v=-CgjwthLy5c
//
//https://circuitdigest.com/microcontroller-projects/interfacing-bmp280-sensor-with-arduino
public class TemperatureSensor extends Sensor {

    public static final byte bmp280Address = (byte) 0x77;

    // register addresses - found these in the datasheet section 4.3
    private static final byte controlReg  = (byte) 0xF4;
    private static final byte calibReg = (byte) 0x88;
    private static final byte dataReg  = (byte) 0xF7;
    private static final byte controlNormal = (byte) 0x27; // normal mode, from datasheet

    private final I2CDevice device;
    private final TemperatureSensorListener listener;

    // calibration values - every BMP280 chip has slightly different values
    // have to read them from the sensor itself, can't hardcode them
    private int calibT1, calibT2, calibT3;
    private boolean calibrated = false;

    private double temperature = -99.0;

    public TemperatureSensor(I2CDevice device) {
        super("BMP280");
        this.device = device;
        this.listener = new TemperatureSensorListener();
    }

    // start the sensor and load calibration values
    public void init() throws InterruptedException, IOException {
        device.subscribe(listener);
        Thread.sleep(100);

        // sensor starts in sleep mode by default, need to wake it up
        device.tell(controlReg, controlNormal);
        Thread.sleep(100);

        // request 6 calibration bytes starting at register 0x88
        listener.prepareForNext();
        device.tell(calibReg);
        Thread.sleep(20);
        device.ask((byte) 6, listener);

        Thread.sleep(300);

        // parse calibration bytes - stored low byte first (little-endian)
        // explained in datasheet section 4.2.2
        byte[] calib = listener.getRawBytes();
        if (calib != null && calib.length >= 6) {
            calibT1 = ((calib[1] & 0xFF) << 8) | (calib[0] & 0xFF);
            calibT2 = (short)(((calib[3] & 0xFF) << 8) | (calib[2] & 0xFF));
            calibT3 = (short)(((calib[5] & 0xFF) << 8) | (calib[4] & 0xFF));
            calibrated = true;
            System.out.println("BMP280 ready");
        } else {
            System.out.println("BMP280 calibration failed");
        }
    }

    // read temperature from sensor - called every loop
    public void update() throws InterruptedException, IOException {
        if (!calibrated) return;

        listener.prepareForNext();
        device.tell(dataReg);
        Thread.sleep(20);
        device.ask((byte) 6, listener);
        Thread.sleep(300);

        byte[] data = listener.getRawBytes();
        if (data == null || data.length < 6) return;

        // bytes 3, 4, 5 hold the raw temperature as a 20-bit number
        // how to combine them is in datasheet section 4.3.7
        int rawTemp = ((data[3] & 0xFF) << 12)
                | ((data[4] & 0xFF) << 4)
                | ((data[5] & 0xFF) >> 4);

        // compensation formula from Bosch datasheet section 4.2.3
        // var1 and var2 are just steps in the formula, not my naming
        double step1 = (rawTemp / 16384.0 - calibT1 / 1024.0) * calibT2;
        double step2 = (rawTemp / 131072.0 - calibT1 / 8192.0);
        double step2Squared = step2 * step2 * calibT3; // avoided Math.pow, just multiply it out
        temperature = (step1 + step2Squared) / 5120.0;
    }

    @Override
    public double readValue() {
        return temperature;
    }
}