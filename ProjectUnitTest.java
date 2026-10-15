package project;
import org.junit.Test;
import project.Sensor;
import static org.junit.Assert.*;

public class ProjectUnitTest {
    @Test // test1: checking 3.5V is dry  ref:https://www.yorku.ca/professor/drsmith/2024/03/26/unit-testing-for-a-project-in-java/
    public void testDryVoltage() {
        System.out.println("Testing for 3.5V is correctly detected as dry soil " + 3.5);
        String errorMsg1 = "Erro: 3.5V should have been detected as dry but was not.";
        Sensor.SensorReading reading = new Sensor.SensorReading(3.5, 0.0);
        assertEquals(errorMsg1, true, reading.isDry());
    }
    @Test  // test2: checking 2.8V is wet
    public void testWetVoltage() {
        System.out.println("Testing for  2.8V is correctly detected as wet soil " + 2.8);
        String errorMsg2 = "Error:2.8V should have been detected as wet but was not.";
        Sensor.SensorReading reading = new Sensor.SensorReading(2.8, 0.0);
        assertEquals(errorMsg2, true, reading.isWet());
    }
    @Test  // test3: pump on when soil is dry
    public void testPumpOnWhenDry() {
        System.out.println("Testing- pump turns on when soil is dry" + 3.5);
        String errorMsg3 = "Error: pump should have turned on at dry voltage 3.5V but did not.";
        Sensor.SensorReading reading = new Sensor.SensorReading(3.5, 0.0);
        assertEquals(errorMsg3, true, reading.isDry());
    }
    @Test   // test4: pump off when soil is wet
    public void testPumpOffWhenWet() {
        System.out.println("Testing that pump is off when soil is wet " + 2.8);
        String errorMsg4 = "Error: pump should have stayed off at wet voltage 2.8V but turned on.";
        Sensor.SensorReading reading = new Sensor.SensorReading(2.8, 0.0);
        assertEquals(errorMsg4, false, reading.isDry());
    }
}