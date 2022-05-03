package Client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.security.NoSuchAlgorithmException;

public class ClientScanResources implements Runnable {
    private final String RESOURCE_FOLDER = "./resources/";
    private DatagramSocket socket;
    private Set<ClientResource> clientResources;

    public ClientScanResources(DatagramSocket mainSocket, Set<ClientResource> clientResources) throws IOException {
        if (!Files.isDirectory(Paths.get(RESOURCE_FOLDER))) {
            Files.createDirectory(Paths.get(RESOURCE_FOLDER));
        }
        this.socket = mainSocket;
        this.clientResources = clientResources;
    }

    @Override
    public void run() {
        // ler arquivos da pasta resources
        Set<File> files = new HashSet<>();
        List<File> filesInFolder;
        try {
            filesInFolder = Files.walk(Paths.get(RESOURCE_FOLDER))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            for (File file : filesInFolder) {
                ClientResource resource = new ClientResource(file);
                if (files.add(file)) {
                    // add server
                }

            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

}