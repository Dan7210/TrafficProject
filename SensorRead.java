import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class SensorRead {
    private static GpioController gpio = null;
    private static GpioPinDigitalInput sensor = null;
    
    public SensorRead() {
        gpio = GpioFactory.getInstance();
        GpioPinDigitalInput sensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "sensor");
    }

    public void main() throws InterruptedException {
        System.out.println("Listener activated.");
        sensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println("GPIO Pin: " + event.getPin() + " = " + event.getState());
            }
        });

        /*while(true) {
            Thread.sleep(500);
        }*/
    }
}