import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class TrafficProject {

    private static int deltaT = 0;
    private static int carCount = 0;

    private static int carMinimum = 3;
    private static boolean stopLight = false;

    private static GpioController gpio = null;
    private static GpioPinDigitalOutput red = null;
    private static GpioPinDigitalOutput green = null;

    public static void main(String args[]) throws InterruptedException {
        System.out.println("Car Detector Listener Started.");

        // create gpio controller
        GpioController gpio = GpioFactory.getInstance();
        gpio = GpioFactory.getInstance();
        green = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10, "Green", PinState.LOW);
        red = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "Red", PinState.LOW);

        int red = 0;

        // keep program running until user aborts (CTRL-C)
        while(true) {
            deltaT++; //Increment every 10 Milliseconds
            
            //Every X Seconds reset carCount
            if(deltaT%250==0) {
                red++;
                if(red == 3) {
                    red = 0;
                }          
                changeLED(red);
            }
            //Sleep for 10 Milliseconds
            Thread.sleep(10);
        }
    }

    public static void changeLED(int color) {
        switch(color) {
            case(0):
                //System.out.println("Green On");
                //yellow.low();
                red.low();
                green.high();
                break;
            case(2):
                //System.out.println("Red On");
                green.low();
                //yellow.low();
                red.high();
                break;
            case(1):
                //System.out.println("Yellow On");
                green.high();
                red.high();
                //yellow.high();
                break;
        }
    }
}