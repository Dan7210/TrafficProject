import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LedRun {
    private static GpioController gpio = null;
    private static GpioPinDigitalOutput red = null;
    private static GpioPinDigitalOutput green = null;
    //private static GpioPinDigitalOutput yellow = null;
    
    public LedRun() {
        gpio = GpioFactory.getInstance();
        green = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10, "Green", PinState.LOW);
        red = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "Red", PinState.LOW);
        //yellow = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, "Yellow", PinState.LOW);
    }

    public void changeLED(int color) {
        switch(color) {
            case(0):
                System.out.println("Green On");
                //yellow.low();
                red.low();
                green.high();
                break;
            case(1):
                System.out.println("Red On");
                green.low();
                //yellow.low();
                red.high();
                break;
            case(2):
                System.out.println("Yellow On");
                green.high();
                red.high();
                //yellow.high();
                break;
        }
    }
}

/*public static void main(String[] args) throws InterruptedException {
        //When program ends, turn off LEDs and shutdown gpio controller
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                red.low();
                gpio.shutdown();
            }
        });

        gpio = GpioFactory.getInstance();
        red = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "Red", PinState.LOW);

        while(true) {
            red.high();
            System.out.println("LED High.");
            Thread.sleep(3000);
            red.low();
            System.out.println("LED Low.");
            Thread.sleep(3000);
        }        
    }*/
