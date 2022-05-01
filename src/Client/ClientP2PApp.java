package Client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientP2PApp extends Thread {
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected InetAddress peerAddress;
    protected int peerPort;
    protected InetAddress serverAddress;
    protected int serverPort;

    public ClientP2PApp(String localPort, String serverAddress, String serverPort) throws SocketException {
        try {
            this.peerAddress = InetAddress.getLocalHost();
            this.peerPort = Integer.parseInt(localPort);
            this.serverAddress = InetAddress.getByName(serverAddress);
            this.serverPort = Integer.parseInt(serverPort);
        } catch(UnknownHostException ex) {
            System.out.println("\n\tERROR: Invalid machine address!\n");
        } catch(NumberFormatException ex) {
            System.out.println("\n\tERROR: Invalid local port!\n");
        }

        socket = new DatagramSocket(this.peerPort);
    }
    
    public void run() {

    }
}
