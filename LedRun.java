import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LedRun {
    private static GpioController gpio = null;
    private static GpioPinDigitalOutput red = null;
    public static void main(String[] args) throws InterruptedException {
        //When program ends, turn off LEDs and shutdown gpio controller
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                red.low();
                gpio.shutdown();
            }
        });

        gpio = GpioFactory.getInstance();
        red = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "Red", PinState.LOW);

        while(true) {
            red.high();
            System.out.println("LED High.");
            Thread.sleep(3000);
            red.low();
            System.out.println("LED Low.");
            Thread.sleep(3000);
        }        
    }
}
