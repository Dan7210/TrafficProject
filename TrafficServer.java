import java.net.*;
import java.io.*;
import java.util.*;

public class TrafficServer extends Thread {
    //Networking variables
    ServerSocket serverSocket;
    int numThreads = 3; //Should be later read from settings file. May vary depending on intersection.
    List<TrafficServerThread> chatThreads = new ArrayList<TrafficServerThread>(); //Array of chat threads.
    
    //Traffic variables
    Boolean[] trafficStatus = new Boolean[numThreads];
    long[] lastMilliTime = new long[numThreads];
    long deltaTime = 0;
    long lastSwapTime = 0;
    Boolean nsLastSwap = true;
    Boolean nsHigh = false;
    Boolean ewHigh = false;
    int flowControl = 0; //0 = Stop Sign //1 = Preference towards active traffic // 2 = Alternating Green
    int[] errorList = new int[2]; //0 for connection, 1 for sensor

    public void main() {
        //Creates new serverSocket with port 34040.
        try {
            serverSocket = new ServerSocket(34040);
            System.out.println("New server socket created on port 34040.");
        }
        catch(IOException e) {
            System.out.println("Main IOException");
        }
        
        //Create chat threads for numThreads
        while(true) {
            deltaTime = System.currentTimeMillis();
            try {
                System.out.println();
                if(chatThreads.size() == numThreads) {
                    errorList[0] = 0;
                    decisionLogic();
                }
                else {
                    System.out.println("Not all connections made. Defaulting to stop sign mode.");
                    flowControl = 0;
                    errorList[0] = 1;
                    broadcast("STOP");
                    System.out.println("Ready for connection at: " + serverSocket.getLocalSocketAddress());
                    chatThreads.add(new TrafficServerThread(serverSocket.accept(), this));
                    chatThreads.get(chatThreads.size()-1).start();
                }
            }

            catch(SocketTimeoutException s) {
                System.out.println("Socket timeout exception.");
                s.printStackTrace();
            }
            catch(IOException e) {
                System.out.println("Oh no... Our IOException... It's broken!");
                e.printStackTrace();
            }
        }
    }
    
    //Parse received data.
    public void parseSensorData(String receivedData, String direction, TrafficServerThread thread) {
        if(receivedData.contains("VEHICLEDETECTED")) {
            lastMilliTime[Integer.valueOf(direction)] = System.currentTimeMillis();
        }
        else if(receivedData.contains("SENSORERROR")) {
            flowControl = 0;
            errorList[1] += 1;
            broadcast("STOP");
        }
        else if(receivedData.contains("SENSORFIXED")) {
            errorList[1] -= 1;
        }
        else if(receivedData.contains("CLIENTDISCONNECTION")) {
            chatThreads.remove(thread);
            flowControl = 0;
            errorList[0] = 1;
            broadcast("STOP");
        }
        //Insert logic here for other messages
    }

    //Broadcast message to all nodes.
    public void broadcast(String message) {
        for(TrafficServerThread currentThread : chatThreads) {
            currentThread.toClient(message);
        }
    }

    //Send message to specific node.
    public void sendMessage(String message, String direction) {
        for(TrafficServerThread currentThread : chatThreads) {
            if(currentThread.direction.equals(direction)) {
                currentThread.toClient(message);
            }
        }
    }

    public void decisionLogic() {
        //Set traffic status for each node.
        for(int i=0; i<numThreads; i++) {
            //If the last car was sighted within 5 seconds, high traffic.
            trafficStatus[i] = (Math.abs(lastMilliTime[i]-deltaTime) <= 5000);
        }

        //Replace this later with procedural depending on # of stop signs. Currently uses 3 stop sign config.
        //Boolean logic to decide
        nsHigh = (trafficStatus[0] == true || trafficStatus[2] == true);
        ewHigh = trafficStatus[1] == true;
        Boolean safetyCheck = true;
        for(int i=0; i<errorList.length; i++) {
            if(errorList[i] != 0) {
                safetyCheck = false;
                break;
            }
        }

        //Flow Control Logic
        if((safetyCheck == false) || (nsHigh == false && ewHigh == false)) {
            flowControl = 0;
            broadcast("STOP");
        }
        else if(nsHigh == true && ewHigh == true) {
            flowControl = 2;
        }
        else {
            flowControl = 1;
        }

        //**Light Change Logic** Look, I know this is spaghetti code. But shutup.
        //Alternating, preference to one lane
        if(flowControl == 1) {
            if((nsHigh == false && nsLastSwap == true) && (Math.abs(lastSwapTime-deltaTime) >= 10000)) {
                sendMessage("RED","0");
                sendMessage("GREEN","1");
                sendMessage("RED","2");
                lastSwapTime = System.currentTimeMillis();
                nsLastSwap = false;
            }
            else if((nsHigh == true && nsLastSwap == true) && (Math.abs(lastSwapTime-deltaTime) >= 30000)) {
                sendMessage("RED","0");
                sendMessage("GREEN","1");
                sendMessage("RED","2");
                lastSwapTime = System.currentTimeMillis();
                nsLastSwap = false;
            }
            else if((ewHigh == false && nsLastSwap == false) && (Math.abs(lastSwapTime-deltaTime) >= 10000)) {
                sendMessage("GREEN","0");
                sendMessage("RED","1");
                sendMessage("GREEN","2");
                lastSwapTime = System.currentTimeMillis();
                nsLastSwap = true;
            }
            else if((ewHigh == true && nsLastSwap == false) && (Math.abs(lastSwapTime-deltaTime) >= 30000)) {
                sendMessage("GREEN","0");
                sendMessage("RED","1");
                sendMessage("GREEN","2");
                lastSwapTime = System.currentTimeMillis();
                nsLastSwap = true;
            }
        }
        //Alternating equally
        else if(flowControl == 2) {
            if(nsLastSwap == true && Math.abs(lastSwapTime-deltaTime) >= 30000) {
                sendMessage("RED","0");
                sendMessage("GREEN","1");
                sendMessage("RED","2");
                lastSwapTime = System.currentTimeMillis();
                nsLastSwap = false;
            }
            else if(nsLastSwap == false && Math.abs(lastSwapTime-deltaTime) >= 30000) {
                sendMessage("GREEN","0");
                sendMessage("RED","1");
                sendMessage("GREEN","2");
                lastSwapTime = System.currentTimeMillis();
                nsLastSwap = true;
            }
        }

    }

}