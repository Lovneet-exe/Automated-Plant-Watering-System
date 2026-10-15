package project;
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
public class ButtonListener implements IODeviceEventListener {
    private final Pin pump;
    private long lastPressTime = 0; // used to debounce the button
    public ButtonListener(Pin pump) {
        this.pump = pump;
    }
    @Override
    public void onPinChange(IOEvent event) {
        long now = System.currentTimeMillis();
        // only react if it is the BUTTON pin (pin 6), not the pump pin
        if (event.getPin().getIndex() == 6
                && event.getPin().getValue() == 1
                && (now - lastPressTime) > 500) {
            lastPressTime = now;
            try {
                pump.setValue(0);
                System.out.println("EMERGENCY STOP");
            } catch (Exception e) {
                System.out.println("Button error: " + e.getMessage());
            }
        }
    }
    @Override public void onStart(IOEvent event) {}
    @Override public void onStop(IOEvent event) {}
    @Override public void onMessageReceive(IOEvent event, String message) {}
}