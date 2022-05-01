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

    public ClientP2PApp(String localport, String server) throws SocketException {
        try {
            this.address = InetAddress.getByName(server);
            this.port = Integer.parseInt(localport) + 101;
        } catch(UnknownHostException ex) {
            System.out.println("ERROR: Invalid machine address!");
        } catch(NumberFormatException ex) {
            System.out.println("ERROR: Invalid local port!");
        }

        socket = new DatagramSocket(this.port);
    }
    
    public void run() {

    }
}
