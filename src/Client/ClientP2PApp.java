package Client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientP2PApp extends Thread {
    
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected InetAddress address;
    protected int port;
    protected int peerPort;

    public ClientP2PApp(String localPort, String serverAddress, String serverPort) throws Exception {
        this.address = InetAddress.getByName(serverAddress);
        this.port = Integer.parseInt(localPort) + 101;

        socket = new DatagramSocket(this.port);
    }
    
    public void run() {
        System.out.println("Client");
    }
}
