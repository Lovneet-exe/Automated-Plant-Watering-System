package project;
import org.firmata4j.IODevice;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.ssd1306.MonochromeCanvas;

import java.util.Timer;
import java.util.TimerTask;
public class OLEDDisplay extends TimerTask {
    private final SSD1306 display;
    private boolean emergencyStop;
    private double voltage = 0.0;
    private int pumpState = 0;
    private double temperature = 0.0;

    // Constructor OLED
    public OLEDDisplay(SSD1306 display) {
        this.display = display;
    }

    public OLEDDisplay(IODevice arduino, SSD1306 display) {
        this.display = display;
    }

    //Method to update values
    public void setData(double voltage, int pumpState, double temperature) {
        this.voltage = voltage;
        this.pumpState = pumpState;
        this.temperature = temperature;
    }

    @Override
    public void run() {
        MonochromeCanvas canvas = display.getCanvas();
        canvas.clear();  // clear screen
        canvas.setTextsize(1);
// OLED text position
        canvas.setCursor(15, 0);
        canvas.drawString(15, 0, String.format("Volt: %.2fV", voltage));
        //wet or dry soil
        String soilStatus;
        if (voltage >= 3.5) {
            soilStatus = "Dry";
        } else if (voltage <= 2.8) {
            soilStatus = "Wet";
        } else {
            soilStatus = "Good";
        }
        canvas.drawString(15, 15, "Soil: " + soilStatus);
        canvas.setCursor(15, 30);
        String pumpStatus;
        if (pumpState == 1) {
            pumpStatus = "ON";
        } else {
            pumpStatus = "OFF";
        }
        canvas.drawString(15, 30, "Pump: " + pumpStatus);
        canvas.drawString(25, 50, String.format("Temp: %.1fC", temperature));


            display.display();

        }

    }

