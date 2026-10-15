package project;
import org.firmata4j.I2CDevice;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.util.Timer;
import java.util.ArrayList;
import java.util.HashMap;


public class project {
    // Pins
    private static final int soilPin = 14;  // A0
    private static final int pumpPin = 2;   // D2
    private static final int buttonPin = 6; // D6

    // Voltage thresholds
    private static final double dryVoltage = 3.5;
    private static final double wetVoltage = 2.8;

    // Timings
    private static final long burstTime = 4000;  // 4 sec
    private static final long restTime = 56000;  // 56 sec
    private static ArrayList<Double> voltageLog = new ArrayList<>();
    private static HashMap<Long, Double> voltageMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // Arduino setup
        IODevice arduino = new FirmataDevice("COM6");
        arduino.start();
        arduino.ensureInitializationIsDone();

        Pin soil = arduino.getPin(soilPin);
        Pin pump = arduino.getPin(pumpPin);
        Pin button = arduino.getPin(buttonPin);
        soil.setMode(Pin.Mode.ANALOG);
        pump.setMode(Pin.Mode.OUTPUT);
        button.setMode(Pin.Mode.INPUT);
        pump.setValue(0);

        // OLED setup
        var i2cOLED = arduino.getI2CDevice((byte) 0x3C);
        SSD1306 oled = new SSD1306(i2cOLED, SSD1306.Size.SSD1306_128_64);
        oled.init();
        OLEDDisplay oledTask = new OLEDDisplay(oled);
        Timer timer = new Timer();
        timer.schedule(oledTask, 0, 500);

        // Emergency button listener
        arduino.addEventListener(new ButtonListener(pump));

        // Time tracking
        long startTime = System.currentTimeMillis();
        long lastActionTime = -1;




        // Graph
        MoistureGraph graph = new MoistureGraph();

        // Temperature sensor
        I2CDevice tempDevice = arduino.getI2CDevice(TemperatureSensor.bmp280Address);
        TemperatureSensor tempSensor = new TemperatureSensor(tempDevice);
        tempSensor.init();
        // States
        enum State { PUMP_OFF, WATERING_NOW }
        State state = State.PUMP_OFF;

        // Main loop
        while (true) {
            long currentTime = System.currentTimeMillis();
            double t = (currentTime - startTime) / 1000.0;

            // Soil sensor reading - convert once, use voltage everywhere
            double raw = soil.getValue();
            double sensorVoltage = raw * (5.0 / 1023.0);
            // Store in ArrayList and HashMap BEFORE graphing (CLO4)
            voltageLog.add(sensorVoltage);
            voltageMap.put(currentTime, sensorVoltage);

            // Update graph
            graph.updateGraph(sensorVoltage);

            // State machine
            switch (state) {
                case PUMP_OFF:
                    if (sensorVoltage >= dryVoltage) {
                        if (lastActionTime < 0 || (currentTime - lastActionTime) >= restTime) {
                            state = State.WATERING_NOW;
                            lastActionTime = currentTime;
                            System.out.println("Soil is dry. Watering now");
                        }
                    }
                    break;

                case WATERING_NOW:
                    if ((currentTime - lastActionTime) >= burstTime || sensorVoltage <= wetVoltage) {
                        state = State.PUMP_OFF;
                        pump.setValue(1);
                        Thread.sleep(1000);
                        pump.setValue(0);
                    }
                    break;
            }

            // Stop if soil is wet
            if (sensorVoltage <= wetVoltage) {
                state = State.PUMP_OFF;
                pump.setValue(0);
                System.out.println("Soil is wet. Pump remains off");
            }
            // Temperature update
            tempSensor.update();
            double temperature = tempSensor.readValue();

            // OLED update

            int pumpState = (state == State.WATERING_NOW) ? 1 : 0;
            oledTask.setData(sensorVoltage, pumpState, temperature);


            // Console output
            System.out.printf("t=%.1f s | Voltage=%.2f V | Temp=%.1f C | State=%s%n",
                    t, sensorVoltage, temperature, state);

            Thread.sleep(200);
        }
    }
}