import java.util.*;

public class Launcher {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Server, Client, Exit?");
            String response = sc.nextLine().toLowerCase();
            if(response.equals("server")) {
                new TrafficServer().main();
            }
            else if(response.equals("client")) {
                new TrafficNode().main();
            }
            else if(response.equals("exit")) {
                System.exit(0);
            }
            else {
                System.out.println("Invalid Response. Please try again.");
            }
        }
    }
}
