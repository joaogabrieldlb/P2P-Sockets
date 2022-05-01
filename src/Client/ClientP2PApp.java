package Client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientP2PApp extends Thread {
    
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected InetAddress address;
    protected int port;
    protected int peerPort;

    public ClientP2PApp(String localPort, String serverAddress, String serverPort) throws SocketException {
        try {
            this.address = InetAddress.getByName(serverAddress);
            this.port = Integer.parseInt(localPort) + 101;
        } catch(UnknownHostException ex) {
            System.out.println("\n\tERROR: Invalid machine address!\n");
        } catch(NumberFormatException ex) {
            System.out.println("\n\tERROR: Invalid local port!\n");
        }

        socket = new DatagramSocket(this.port);
    }
    
    public void run() {

    }
}
