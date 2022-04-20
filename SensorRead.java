import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.Date;

//Base code by Robert Savage
//@author Daniel Marquez

public class SensorRead {

    private static int deltaT = 0;
    private static int carCount = 0;

    private static int carMinimum = 0;
    private static boolean stopLight = false;

    private static LedRun led = new LedRun();

    public static void main(String args[]) throws InterruptedException {
        System.out.println("Car Detector Listener Started.");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        myButton.setShutdownOptions(true);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                if(event.getState() == PinState.LOW) {
                    System.out.println("Car detected. Current car count for this minute: " + carCount);
                    carCount++;
                }
            }

        });

        // keep program running until user aborts (CTRL-C)
        while(true) {
            deltaT++; //Increment every 10 Milliseconds
            
            //Every Minute reset carCount
            if(deltaT%6000==0) {
                carCount = 0;               
            }

            //Every 5 seconds check what mode
            if(deltaT%500 == 0) {
                if(carCount >= 3) {
                    System.out.println("Stop light on.");
                    stopLight = true;
                    led.changeLED(0);
                }   
                else {
                    System.out.println("Stop light off.");
                    stopLight = false;
                    led.changeLED(1);
                }
            }

            //Sleep for 10 Milliseconds
            Thread.sleep(10);
        }
    }
}