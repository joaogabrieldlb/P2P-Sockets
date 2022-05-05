package Client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import Utils.ResourceHash;

public class ClientScanResources extends Thread {

    private final String RESOURCE_FOLDER = "./resources/"; // Pasta em bin/resources -> rever isso
    private DatagramSocket mainSocket;
    private DatagramPacket packet;
    private ClientP2PApp app;

    public ClientScanResources(ClientP2PApp app) throws IOException {
        if (!Files.isDirectory(Paths.get(RESOURCE_FOLDER))) {
            Files.createDirectory(Paths.get(RESOURCE_FOLDER));
        }
        this.app = app;
        this.mainSocket = this.app.mainSocket;
    }

    @Override
    public void run() {
        List<File> filesInFolder = new ArrayList<>();
        while (true) {
            // ler arquivos da pasta resources
            try {
                filesInFolder = Files.walk(Paths.get(RESOURCE_FOLDER))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            
            // adiciona no clientresource e registra no server
            for (File file : filesInFolder) {
                ClientResource resource;
                try {
                    resource = new ClientResource(file);
                    boolean resouceAdded = false;
                    try {
                        this.app.clientResourceSemaphore.acquire();
                        resouceAdded = this.app.clientResources.add(resource);
                        this.app.clientResourceSemaphore.release();
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                    if (resouceAdded) {
                        // envia add-resource para o server e set isRegistered true
                        try {
                            this.app.clientResourceSemaphore.acquire();
                            addResource(resource);
                            this.app.clientResourceSemaphore.release();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                } catch (NoSuchAlgorithmException | IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            // verifica os removidos
            List<String> filesInFolderHashes = filesInFolder.stream().map(t -> {
                        try {
                            return ResourceHash.computeMD5(t);
                        } catch (NoSuchAlgorithmException e) {
                            System.out.println(e.getMessage());
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                        return null;
                    })
                .filter(h -> h != null)
                .toList(); 
            
                
            try {
                Set<ClientResource> resourcesToRemove = new HashSet<>();
                this.app.clientResourceSemaphore.acquire();
                for (ClientResource resource : this.app.clientResources) {
                    if (!filesInFolderHashes.contains(resource.getHash())) {
                        // envia remove-resource para o server
                        this.app.mainSocketSemaphore.acquire();
                        if (removeResource(resource)) {
                            resourcesToRemove.add(resource);
                        }
                        this.app.mainSocketSemaphore.release();
                    } else if (!resource.isRegistred()) {
                        // envia add-resource para o server
                        this.app.mainSocketSemaphore.acquire();
                        addResource(resource);
                        this.app.mainSocketSemaphore.release();
                    }
                }
                this.app.clientResources.removeAll(resourcesToRemove);
                this.app.clientResourceSemaphore.release();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
        
    private void addResource(ClientResource newResource) {
        try {
            String messege = "add-resource|" + newResource.toString();
            this.packet = new DatagramPacket(messege.getBytes(), messege.length(), this.app.serverAddress,
                    this.app.serverPort);
            this.mainSocket.send(packet);

            mainSocket.setSoTimeout(1000);
            byte[] response = new byte[1024];
            this.packet = new DatagramPacket(response, response.length, this.app.serverAddress, this.app.serverPort);
            mainSocket.receive(packet);

            String content = new String(packet.getData()).trim();
            if (content.equals("OK")) {
                newResource.setRegistred(true);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                mainSocket.setSoTimeout(0);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean removeResource(ClientResource removedResource) {
        try {
            String messege = "remove-resource|" + removedResource.toString();
            this.packet = new DatagramPacket(messege.getBytes(), messege.length(), this.app.serverAddress,
                    this.app.serverPort);
            this.mainSocket.send(packet);

            mainSocket.setSoTimeout(1000);
            byte[] response = new byte[1024];
            this.packet = new DatagramPacket(response, response.length, this.app.serverAddress, this.app.serverPort);
            mainSocket.receive(packet);

            String content = new String(packet.getData()).trim();
            return content.equals("OK");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                mainSocket.setSoTimeout(0);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

}