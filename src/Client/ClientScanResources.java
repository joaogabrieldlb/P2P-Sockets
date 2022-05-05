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
        Set<ClientResource> resourcesInFolder = new HashSet<>();
        while (true) {
            // ler arquivos da pasta resources
            try {
                resourcesInFolder = Files.walk(Paths.get(RESOURCE_FOLDER))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .map(file -> {
                        try {
                            return new ClientResource(file);
                        } catch (NoSuchAlgorithmException e) {
                            System.out.println(e.getMessage());
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                        return null;
                    })
                    .filter(r -> r != null)
                    .collect(Collectors.toSet());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
        
            try {
                Set<ClientResource> resourcesToAdd = new HashSet<>();
                this.app.clientResourceSemaphore.acquire();
                for (ClientResource clientResource : resourcesInFolder) {
                    if (!this.app.clientResources.contains(clientResource)) {
                        resourcesToAdd.add(clientResource);
                    }
                }
                this.app.clientResourceSemaphore.release();

                resourcesToAdd.forEach(resource -> {
                    try {
                        this.app.mainSocketSemaphore.acquire();
                        addResourceInServer(resource);
                        this.app.mainSocketSemaphore.release();
                        if (resource.isRegistred()) {
                            this.app.clientResourceSemaphore.acquire();
                            this.app.clientResources.add(resource);
                            this.app.clientResourceSemaphore.release();
                        }
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                });
                
                Set<ClientResource> resourcesToRemove = new HashSet<>();
                this.app.clientResourceSemaphore.acquire();
                for (ClientResource clientResource : this.app.clientResources) {
                    if (!resourcesInFolder.contains(clientResource)){
                        resourcesToRemove.add(clientResource);
                    }
                }
                this.app.clientResourceSemaphore.release();

                resourcesToRemove.forEach(resource -> {
                    boolean isRemoved;
                    try {
                        this.app.mainSocketSemaphore.acquire();
                        isRemoved = removeResourceInServer(resource);
                        this.app.mainSocketSemaphore.release();
                        if (isRemoved) {
                            this.app.clientResourceSemaphore.acquire();
                            this.app.clientResources.remove(resource);
                            this.app.clientResourceSemaphore.release();
                        }
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
        
    private boolean addResourceInServer(ClientResource newResource) {
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
        return newResource.isRegistred();
    }

    private boolean removeResourceInServer(ClientResource removedResource) {
        boolean result = false;
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
            result = content.equals("OK");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                mainSocket.setSoTimeout(0);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            }
        }
        return result;
    }

}