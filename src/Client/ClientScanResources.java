package Client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public class ClientScanResources extends Thread {

    private final String RESOURCE_FOLDER = "./resources/"; // Pasta em bin/resources -> rever isso
    private DatagramSocket socket;
    private DatagramPacket packet;
    private ClientP2PApp app;

    public ClientScanResources(ClientP2PApp app) throws IOException {
        if (!Files.isDirectory(Paths.get(RESOURCE_FOLDER))) {
            Files.createDirectory(Paths.get(RESOURCE_FOLDER));
        }
        this.app = app;
        this.socket = this.app.mainSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // ler arquivos da pasta resources
                List<File> filesInFolder;
                filesInFolder = Files.walk(Paths.get(RESOURCE_FOLDER))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                // adiciona no clientresource e registra no server
                for (File file : filesInFolder) {
                    ClientResource resource = new ClientResource(file);
                    this.app.clientResourceSemaphore.acquire();
                    boolean resouceAdd = this.app.clientResources.add(resource);
                    this.app.clientResourceSemaphore.release();
                    if (resouceAdd) {
                        // envia add-resource para o server e set isRegistered true
                        this.app.mainSocketSemaphore.acquire();
                        addResource(resource);
                        this.app.mainSocketSemaphore.release();
                    }
                }
                // verifica os removidos

                for (ClientResource resource : this.app.clientResources) {
                    if (!filesInFolder.contains(resource.getFile())) {
                        // envia remove-resource para o server
                        this.app.mainSocketSemaphore.acquire();
                        removeResource(resource);
                        this.app.mainSocketSemaphore.release();
                    } else if (!resource.isRegistred()) {
                        // envia add-resource para o server
                        this.app.mainSocketSemaphore.acquire();
                        addResource(resource);
                        this.app.mainSocketSemaphore.release();
                    }
                }

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addResource(ClientResource newResource) throws IOException {

        String messege = "add-resource|" + newResource.toString();
        this.packet = new DatagramPacket(messege.getBytes(), messege.length(), this.app.serverAddress,
                this.app.serverPort);
        this.socket.send(packet);

        try {
            socket.setSoTimeout(5000);
            byte[] response = new byte[1024];
            this.packet = new DatagramPacket(response, response.length, this.app.serverAddress, this.app.serverPort);
            socket.receive(packet);

            String content = new String(packet.getData()).trim();
            if (content.equals("OK")) {
                this.app.clientResourceSemaphore.acquire();
                newResource.setRegistred(true);
                this.app.clientResourceSemaphore.release();
            }
        } catch (Exception e) {
        }
    }

    private void removeResource(ClientResource removedResource) throws IOException {
        String messege = "remove-resource|" + removedResource.toString();
        this.packet = new DatagramPacket(messege.getBytes(), messege.length(), this.app.serverAddress,
                this.app.serverPort);
        this.socket.send(packet);

        try {
            socket.setSoTimeout(5000);
            byte[] response = new byte[1024];
            this.packet = new DatagramPacket(response, response.length, this.app.serverAddress, this.app.serverPort);
            socket.receive(packet);

            String content = new String(packet.getData()).trim();
            if (content.equals("OK")) {
                this.app.clientResourceSemaphore.acquire();
                this.app.clientResources.remove(removedResource);
                this.app.clientResourceSemaphore.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}