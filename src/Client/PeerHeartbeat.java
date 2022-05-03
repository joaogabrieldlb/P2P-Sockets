package Client;

import java.io.*;
import java.net.*;

public class PeerHeartbeat extends Thread {
    protected DatagramSocket socket = null;
    protected DatagramPacket packet = null;
    protected InetAddress serverAddress = null;
    protected byte[] data = new byte[1024];
    protected int port;
    protected int serverPort;

    public PeerHeartbeat(int localPort, InetAddress serverAddress, int serverPort, String localAddressIp)
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
        this.serverPort = serverPort;
        data = ("heartbeat " + localAddressIp).getBytes(); // nickname
        this.serverAddress = serverAddress; // serverAddress
        socket = new DatagramSocket(serverPort);
    }

    // "Uso: java p2pPeer <server> \"<message>\" <localport>");
    public void run() {
        while (true) {
            try {
                packet = new DatagramPacket(data, data.length, serverAddress, this.serverPort);
                socket.send(packet);
            } catch (IOException e) {
                socket.close();
            }

            try {
                Thread.sleep(10000); // 10seg
            } catch (InterruptedException e) {
            }

        }
    }
}