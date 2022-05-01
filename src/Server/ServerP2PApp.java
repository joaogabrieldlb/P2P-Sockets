package Server;

import java.net.InetAddress;

public class ServerP2PApp {
    protected InetAddress serverAddress;
    protected int serverPort;


    public ServerP2PApp(String serverPort) throws Exception {
        this.serverAddress = InetAddress.getLocalHost();
        this.serverPort = Integer.parseInt(serverPort);
    }
    
    public void run() {
        System.out.println("Server");
    }
}
