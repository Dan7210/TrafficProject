import java.net.*;
import java.io.*;

public class TrafficNode {
    Socket client;
    String secretKey;
    //These variables should be read from a settings file later on.
    String direction = "0"; //0 is North, 1 is East, etc... 
    String serverIP = "127.0.0.1";
    int serverPort = 34040;
    
    public void main() {
        //Define session variables
        String sensorMessage = ""; //Should be deprecated later, sensors don't have specific messages.
        String incomingMessage = "";
        RSA RSA = new RSA();
        AES AES = new AES();
        
        //Try to connect with the server and communicate
        try {
            //Attempt to create new socket connection with server
            System.out.println("Connecting to: " + serverIP + ":" + serverPort);
            client = new Socket(serverIP, serverPort);
            System.out.println("Connected to: " + client.getRemoteSocketAddress());
            
            //Create new data io streams
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            
            //Send RSA public key to server for exchanging symmetric key, receive symmetric key
            RSA.genKeys(2048);
            out.writeUTF(RSA.n.toString());
            out.writeUTF(RSA.e.toString());
            secretKey = RSA.readMessage(in.readUTF());

            out.writeUTF(AES.encrypt(direction, secretKey));
            
            //Send any signal updates to server.
            while(true) {
                if(!sensorMessage.equals("")) //Replace this later with if sensor detects something
                {
                    out.writeUTF(AES.encrypt(direction + ": " + "VEHICLEDETECTED", secretKey));
                    sensorMessage = "";
                }
                //Parse any instructions from server
                if(in.available() > 0) {
                    incomingMessage = AES.decrypt(in.readUTF(), secretKey);
                    System.out.println(incomingMessage);
                    instructionParser(incomingMessage);
                }
            }
        }
        //Catch exceptions
        catch(IOException e) {
            System.out.println("Oh no... Our IOException... It's broken!");
            e.printStackTrace();
        }
    }

    public void instructionParser(String instruction) {
        if(instruction.equals("STOP")) {
            //Stop logic here
        }
        if(instruction.equals("RED")) {
            //Red light logic here
        }
        if(instruction.equals("GREEN")) {
            //Green light logic here
        }
    }
}