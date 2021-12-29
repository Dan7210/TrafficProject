import java.net.*;
import java.io.*;
import java.math.*;

public class TrafficServerThread extends Thread {
    
    Socket server;
    TrafficServer secureServer;
    String message = "";
    DataInputStream in;
    DataOutputStream out;
    RSA RSA = new RSA();
    AES AES = new AES();
    String secretKey;
    String direction;
    
    //Constructor Method
    public TrafficServerThread(Socket threadSocket, TrafficServer serverRef) {
        server = threadSocket;
        secureServer = serverRef;
    }
    
    //Main method for relaying communication
    public void run() {
        try {
            System.out.println("Connection established with: " + server.getRemoteSocketAddress());
            
            //Define new data i/o streams
            in = new DataInputStream(server.getInputStream());
            out = new DataOutputStream(server.getOutputStream());
            
            //Receive RSA public key and send symmetric key
            RSA.foreignN = new BigInteger(in.readUTF());
            RSA.foreignE = new BigInteger(in.readUTF());
            secretKey = AES.genRandomString(20);
            out.writeUTF(RSA.encrypt(secretKey));

            direction = AES.decrypt(in.readUTF(), secretKey);
            
            //If client has message, relay to server.
            while(true) {
                if(in.available() > 0) {
                    message = AES.decrypt(in.readUTF(), secretKey);
                    secureServer.parseSensorData(message, direction, this);
                }
            }
        }
        //Catch exceptions
        catch(IOException e) {
            System.out.println("Client disconnect.");
            secureServer.parseSensorData("CLIENTDISCONNECTION", direction, this);
        }
    }
    
    //Method for relaying messages from server to client.
    public void toClient(String message) {
        try {
            if(secretKey != null) {
                out.writeUTF(AES.encrypt(message, secretKey));
            }
            else {
                System.out.println("Sending unencrypted message");
                out.writeUTF(message);
            }
        }
        catch(IOException e) {
            System.out.println("Client not available.");
        }
    }
    
}
