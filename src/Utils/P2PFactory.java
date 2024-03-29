package Utils;

import Client.ClientP2PApp;
import Server.ServerP2PApp;

public class P2PFactory {

    public static ClientP2PApp createP2PClient(String localPort, String serverAddress, String serverPort)
            throws Exception {
        return new ClientP2PApp(localPort, serverAddress, serverPort);
    }

    public static ServerP2PApp createP2PServer(String serverPort) throws Exception {
        return new ServerP2PApp(serverPort);
    }
}
