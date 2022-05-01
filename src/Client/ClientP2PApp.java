package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientP2PApp {
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected InetAddress localAddress;
    protected int localPort;
    protected InetAddress serverAddress;
    protected int serverPort;
    protected String nickName;

    public ClientP2PApp(String localPort, String serverAddress, String serverPort) throws Exception {
        this.localAddress = InetAddress.getLocalHost();
        this.localPort = Integer.parseInt(localPort);
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = Integer.parseInt(serverPort);

        socket = new DatagramSocket(this.localPort);
    }

    public void run() throws IOException {
        new PeerClient().start();
        new PeerHeartbeat(this.serverAddress, this.serverPort, this.localAddress.getHostAddress()).start();
        new PeerThread().start();
    }
}
