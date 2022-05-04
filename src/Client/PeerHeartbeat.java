package Client;

import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;

public class PeerHeartbeat extends Thread {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress serverAddress;
    private final byte[] MESSAGE = "heartbeat".getBytes();
    private int port;
    private int serverPort;
    protected Semaphore mainSocketSemaphore;

    public PeerHeartbeat(int port, InetAddress serverAddress, int serverPort, Semaphore mainsockSemaphore)
            throws SocketException {

        // cria um socket datagrama
        this.mainSocketSemaphore = mainsockSemaphore;
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
                this.mainSocketSemaphore.acquire();
                socket.send(packet);
                this.mainSocketSemaphore.release();
            } catch (IOException | InterruptedException e) {
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