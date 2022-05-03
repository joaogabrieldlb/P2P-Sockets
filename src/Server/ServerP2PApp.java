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
    private Set<Peer> connectedPeers = new HashSet<>();
    private DatagramPacket packet;
    private Semaphore connectedPeersSemaphore = new Semaphore(1);
    private ServerP2PHearthbeat hearthbeat;

    byte[] resource = new byte[1024];
    byte[] response = new byte[1024];

    public ServerP2PApp(String serverPort) throws Exception {
        this.peerPort = Integer.parseInt(serverPort);
        this.socket = new DatagramSocket(this.peerPort);
        hearthbeat = new ServerP2PHearthbeat(connectedPeers, connectedPeersSemaphore);
        hearthbeat.run();
    }

    public void run() {
        while (true) {
            try {
                String content = null;
                // recebe datagrama
                packet = new DatagramPacket(resource, resource.length);
                socket.setSoTimeout(500);
                socket.receive(packet);

                // processa o que foi recebido, adicionando a uma lista
                peerAddr = packet.getAddress();
                peerPort = packet.getPort();
                System.out.print(String.format("[ %s:%d ] Recebi um pacote!", peerAddr.toString(), peerPort));

                content = new String(packet.getData(), 0, packet.getLength());
                String vars[] = content.split("\\s");

                // add-resource texto.txt AJLKSDH1J23ASDAS
                if (vars[0].equals("add-resource") && vars.length > 3) {
                    // name e hash
                    String resourceName = vars[1];
                    String resourceHash = vars[2];

                    this.addResource(resourceName, resourceHash);
                }

                // add-resource texto.txt AJLKSDH1J23ASDAS
                if (vars[0].equals("remove-resource") && vars.length > 3) {
                    // name e hash
                    String resourceName = vars[1];
                    String resourceHash = vars[2];

                    this.removeResource(resourceName, resourceHash);
                }

                if (vars[0].equals("register") && vars.length > 1) {
                    this.registerPeer(new Peer(peerAddr, peerPort));
                }

                if (vars[0].equals("list-resources") && vars.length > 3) {
                    // list-resources --name XX
                    // list-resources --hash XX
                    this.listResources(vars[1], vars[2]);
                }

                if (vars[0].equals("heartbeat") && vars.length > 1) {
                    if (!hearthbeat.receivedHearbeat(peerAddr, peerPort)) {
                        response = "Peer not connected".getBytes();
                        packet = new DatagramPacket(response, response.length, peerAddr, peerPort);
                        socket.send(packet);
                    }
                }
            } catch (IOException e) {
                // decrementa os contadores de timeout a cada 500ms (em função do receive com
                // timeout)
                // se alguem mandar ping, reseta o timeout.
                /*
                 * for (int i = 0; i < timeoutVal.size(); i++) {
                 * timeoutVal.set(i, timeoutVal.get(i) - 1);
                 * if (timeoutVal.get(i) == 0) { //Perdeu o tempo de 5s p/ enviar ping, sai do
                 * index.
                 * System.out.println("\nuser " + resourceList.get(i) + " is dead.");
                 * resourceList.remove(i);
                 * resourceAddr.remove(i);
                 * resourcePort.remove(i);
                 * timeoutVal.remove(i);
                 * }
                 * }
                 * System.out.print(".");
                 */
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void listResources(String searchQuery, String searchTerm) throws IOException {
        List<String> resourceSearch = new ArrayList<>();

        if (searchQuery.equals("--name")) {
            for (Peer peer : this.connectedPeers) {
                if (peer.getResources().size() > 0) {
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
                if (peer.getResources().size() > 0) {
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
        socket.send(packet);
        // Enviando todos os resources.
        for (String resourceString : resourceSearch) {
            response = resourceString.getBytes();
            packet = new DatagramPacket(response, response.length, peerAddr, peerPort);
            socket.send(packet);
        }
    }

    private void registerPeer(Peer newPeer) throws InterruptedException, IOException {
        System.out.println(newPeer.toString());

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

    private void addResource(String resourceName, String resourceHash) throws InterruptedException, IOException {
        for (Peer peer : this.connectedPeers) {
            if (peer.getIpAddress().getHostAddress().equals(peerAddr.getHostAddress())) {
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
