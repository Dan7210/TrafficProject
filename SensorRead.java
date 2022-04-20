import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class SensorRead {
    private static GpioController gpio = null;
    private static GpioPinDigitalInput sensor = null;
    
    public static void main(String[] args) throws InterruptedException {
        gpio = GpioFactory.getInstance();
        GpioPinDigitalInput sensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "sensor");
        System.out.println("Listener activated.");
        while(true) {
            checkLED();
        }
    }

    public static void checkLED() throws InterruptedException {
        try {
            sensor.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    System.out.println("GPIO Pin: " + event.getPin() + " = " + event.getState());
                }
            });
    
            /*while(true) {
                Thread.sleep(500);
            }*/
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}