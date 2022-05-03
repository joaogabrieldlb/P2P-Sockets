package Client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientScanResources implements Runnable {
    private String resourceFolder = "./resources/";
    private DatagramSocket socket;

    public ClientScanResources(DatagramSocket mainSocket) throws IOException {
        if (!Files.isDirectory(Paths.get(resourceFolder))) {
            Files.createDirectory(Paths.get(resourceFolder));
        }

        socket = mainSocket;

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}