package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class ServerP2PApp {

    private InetAddress peerAddr;
    private int peerPort;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private Set<Peer> connectedPeers = new HashSet<>();
    private Semaphore connectedPeersSemaphore = new Semaphore(1);
    private ServerP2PHearthbeat hearthbeat;

    byte[] response = new byte[1024];
    byte[] resource = new byte[1024];

    public ServerP2PApp(String serverPort) throws Exception {
        this.peerPort = Integer.parseInt(serverPort);
        this.socket = new DatagramSocket(this.peerPort);
        hearthbeat = new ServerP2PHearthbeat(connectedPeers, connectedPeersSemaphore);
        hearthbeat.start();
    }

    public void run() {
        while (true) {
            try {
                System.out.println("Esperando novo pacote:");

                byte[] response = new byte[1024];
                byte[] resource = new byte[1024];
                String content = null;
                // recebe datagrama
                this.packet = new DatagramPacket(resource, resource.length);
                socket.receive(this.packet);

                // processa o que foi recebido, adicionando a uma lista
                peerAddr = packet.getAddress();
                peerPort = packet.getPort();
                content = new String(packet.getData()).trim();
                System.out
                        .print(String.format("[ %s:%d ] Pacote Recebido: %s\n", peerAddr.toString(), peerPort,
                                content));

                String vars[] = content.split("\\|");
                // add-resource|texto.txt|AJLKSDH1J23ASDAS
                if (vars[0].equals("add-resource") && vars.length >= 3) {
                    // name|hash
                    String resourceName = vars[1];
                    String resourceHash = vars[2];

                    this.addResource(resourceName, resourceHash);
                }

                // remove-resource|texto.txt|AJLKSDH1J23ASDAS
                if (vars[0].equals("remove-resource") && vars.length >= 3) {
                    // name|hash
                    String resourceName = vars[1];
                    String resourceHash = vars[2];

                    this.removeResource(resourceName, resourceHash);
                }

                if (vars[0].equals("register") && vars.length == 1) {
                    this.registerPeer(new Peer(peerAddr, peerPort));
                }

                if (vars[0].equals("list-resources") && vars.length >= 3) {
                    // list-resources|--name|XX
                    // list-resources|--hash|XX
                    this.listResources(vars[1], vars[2]);
                }

                if (vars[0].equals("heartbeat") && vars.length == 1) {

                    if (!hearthbeat.receivedHearbeat(peerAddr, peerPort)) {
                        response = "Peer not connected".getBytes();
                        packet = new DatagramPacket(response, response.length, peerAddr, peerPort);
                        socket.send(packet);
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void listResources(String searchQuery, String searchTerm) throws IOException, InterruptedException {
        List<String> resourceSearch = new ArrayList<>();

        if (searchQuery.equals("--name")) {
            for (Peer peer : this.connectedPeers) {
                if (peer.getResources().size() > 0
                        && (peer.getIpAddress().getHostAddress() != this.peerAddr.getHostAddress()
                                && peer.getPort() != this.peerPort)) {
                    for (Resource resource : peer.getResources()) {
                        if (resource.getName().contains(searchTerm)) {
                            // nome|hash|ip|port
                            resourceSearch.add(resource.getName() + "|" + resource.getHash() + "|"
                                    + resource.getPeer().getIpAddress().getHostAddress() + "|"
                                    + resource.getPeer().getPort());
                        }
                    }
                }
            }
        }

        if (searchQuery.equals("--hash")) {
            for (Peer peer : this.connectedPeers) {
                if (peer.getResources().size() > 0
                        && (peer.getIpAddress().getHostAddress() != this.peerAddr.getHostAddress()
                                && peer.getPort() != this.peerPort)) {
                    for (Resource resource : peer.getResources()) {
                        if (resource.getHash().equalsIgnoreCase(searchTerm)) {
                            // nome|hash|ip|port
                            resourceSearch.add(resource.getName() + "|" + resource.getHash() + "|"
                                    + resource.getPeer().getIpAddress().getHostAddress() + "|"
                                    + resource.getPeer().getPort());
                        }
                    }
                }
            }
        }

        // enviar size resourcesCount
        response = Integer.valueOf(resourceSearch.size()).toString().getBytes();
        packet = new DatagramPacket(response, response.length, peerAddr, peerPort);

        this.connectedPeersSemaphore.acquire();
        socket.send(packet);
        this.connectedPeersSemaphore.release();

        // Enviando todos os resources.
        if (resourceSearch.size() > 0) {
            for (String resourceString : resourceSearch) {
                response = resourceString.getBytes();
                packet = new DatagramPacket(response, response.length, peerAddr, peerPort);
                socket.send(packet);
            }
        }

    }

    private void registerPeer(Peer newPeer) throws InterruptedException, IOException {
        this.connectedPeersSemaphore.acquire();
        boolean isNewPeer = connectedPeers.add(newPeer); // adicionar novo peer.
        this.connectedPeersSemaphore.release();
        if (!isNewPeer) {
            response = "NOT OK".getBytes();
        } else {
            response = "OK".getBytes();
        }

        packet = new DatagramPacket(response, response.length, peerAddr, peerPort);
        socket.send(packet);
    }

    private void addResource(String resourceName, String resourceHash)
            throws InterruptedException, IOException {
        for (Peer peer : this.connectedPeers) {
            if (peer.getIpAddress().getHostAddress().equals(this.peerAddr.getHostAddress())
                    && peer.getPort() == this.peerPort) {
                this.connectedPeersSemaphore.acquire();
                peer.addResource(new Resource(resourceName, resourceHash, peer));
                this.connectedPeersSemaphore.release();
            }
        }

        response = "OK".getBytes();

        packet = new DatagramPacket(response, response.length, peerAddr, peerPort);
        socket.send(packet);
    }

    private void removeResource(String resourceName, String resourceHash) throws InterruptedException, IOException {
        for (Peer peer : this.connectedPeers) {
            if (peer.getIpAddress().getHostAddress().equals(peerAddr.getHostAddress())) {
                this.connectedPeersSemaphore.acquire();
                peer.removeResource(new Resource(resourceName, resourceHash, peer));
                this.connectedPeersSemaphore.release();
            }
        }

        response = "OK".getBytes();

        packet = new DatagramPacket(response, response.length, peerAddr, peerPort);
        socket.send(packet);
    }
}
