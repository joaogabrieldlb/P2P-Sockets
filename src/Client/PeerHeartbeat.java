package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PeerHeartbeat extends Thread {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress serverAddress;
    private final byte[] MESSAGE = "heartbeat".getBytes();
    private int port;
    private int serverPort;

    public PeerHeartbeat(int port, InetAddress serverAddress, int serverPort)
            throws SocketException {

        // cria um socket datagrama
        this.port = port;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress; // serverAddress
        this.socket = new DatagramSocket(this.port);
    }

    // "Uso: java p2pPeer <server> \"<message>\" <localport>");
    @Override
    public void run() {
        while (true) {
            try {
                this.packet = new DatagramPacket(this.MESSAGE, MESSAGE.length, this.serverAddress, this.serverPort);
                socket.send(packet);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                // socket.close();
                // abrir novo se o atual fechar...
            }

            try {
                Thread.sleep(10000); // 10seg
            } catch (InterruptedException e) {
            }
        }
    }
}