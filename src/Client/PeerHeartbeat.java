package Client;

import java.io.*;
import java.net.*;

public class PeerHeartbeat extends Thread {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress serverAddress;
    private byte[] data = "heartbeat".getBytes();
    private int port;
    private int serverPort;

    public PeerHeartbeat(int port, InetAddress serverAddress, int serverPort)
            throws SocketException {

        // envia um packet
        /*
         * String vars[] = args[1].split("\\s");
         * data = ("heartbeat " + vars[1]).getBytes(); // nickname
         * addr = InetAddress.getByName(args[0]); // serverAddress
         * porta = Integer.parseInt(args[2]) + 100; // port
         */

        // Uso: java p2pPeer <server> \"<message>\" <localport>")

        // cria um socket datagrama
        this.port = port;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress; // serverAddress
        this.socket = new DatagramSocket(this.port);
    }

    // "Uso: java p2pPeer <server> \"<message>\" <localport>");
    public void run() {
        while (true) {
            try {
                packet = new DatagramPacket(this.data, data.length, this.serverAddress, this.serverPort);
                socket.send(packet);
            } catch (IOException e) {
                socket.close();
                // abrir novo se o atual fechar...
            }

            try {
                Thread.sleep(10000); // 10seg
            } catch (InterruptedException e) {
            }
        }
    }
}