package project;
//parent class for my sensors - temp sensor uses this
public abstract class Sensor {
    protected String sensorName;//temperature sensor parent class
    public Sensor(String sensorName){ this.sensorName=sensorName;}
    public abstract double readValue();
    // For junit tests
    public static class SensorReading {

        private double voltage;  //voltage from soil sensor
        private double time;  //time recorded

        public SensorReading(double voltage, double time) {
            this.voltage = voltage;
            this.time = time;
        }
        //getters for junit
        public double getVoltage() { return voltage; }
        public double getTime() { return time; }
        // 3.5V means soil dry, pump turn on
        public boolean isDry() {
            return voltage >= 3.5;
        }
        // below 2.8V soil is wet, pump stays off
        public boolean isWet() {
            return voltage <= 2.8;
        }
    }
}