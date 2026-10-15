package project;

import org.firmata4j.I2CEvent;
import org.firmata4j.I2CListener;

// this class waits for data to come back from the sensor over I2C
// refrence:
// https://www.yorku.ca/professor/drsmith/2024/06/11/firmata-example-i2c-sensor-java-firmata4j/
//  https://www.youtube.com/watch?v=6IAkYpmA1DQ

public class TemperatureSensorListener implements I2CListener {

    private byte[] rawBytes = null;
    private boolean dataReady = false;

    // firmata4j calls this automatically when data arrives from the sensor
    @Override
    public void onReceive(I2CEvent event) {
        byte[] incoming = event.getData();
        if (incoming != null && incoming.length > 0) {
            rawBytes = incoming;
            dataReady = true;
        }
    }

    // call this before each new ask() so old data doesn't carry over
    public void prepareForNext() {
        rawBytes = null;
        dataReady = false;
    }

    public boolean isDataReady() {
        return dataReady;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }
}