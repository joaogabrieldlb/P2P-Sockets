package Client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientP2PApp extends Thread {
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected InetAddress localAddress;
    protected int localPort;
    protected InetAddress serverAddress;
    protected int serverPort;

    public ClientP2PApp(String localPort, String serverAddress, String serverPort) throws Exception {
        this.localAddress = InetAddress.getLocalHost();
        this.localPort = Integer.parseInt(localPort);
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = Integer.parseInt(serverPort);

        socket = new DatagramSocket(this.localPort);
    }
    
    public void run() {
        System.out.println("Client");
    }
}
