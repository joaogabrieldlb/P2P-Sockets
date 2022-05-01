package Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerP2PApp {
    protected InetAddress serverAddress;
    protected int serverPort;


    public ServerP2PApp(String serverPort) {
        try {
            this.serverAddress = InetAddress.getLocalHost();
            this.serverPort = Integer.parseInt(serverPort);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    public void run() {
        System.out.println("Server");
    }
}
