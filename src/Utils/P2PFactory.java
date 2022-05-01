package Utils;
import java.net.SocketException;

import Client.ClientP2PApp;
import Server.ServerP2PApp;

public class P2PFactory {

    public static ClientP2PApp createP2PClient(String localport, String server) throws SocketException {
        return new ClientP2PApp(localport, server);
    }

    public static ServerP2PApp createP2PServer() {
        return new ServerP2PApp();
    }
}
