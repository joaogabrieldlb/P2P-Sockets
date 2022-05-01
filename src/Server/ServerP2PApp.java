package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class ServerP2PApp {

    InetAddress addr;
    int peerPort;
    DatagramSocket socket;
    Set<Peer> connectedPeers = new HashSet<>();
    DatagramPacket packet;

    byte[] resource = new byte[1024];
    byte[] response = new byte[1024];

    public ServerP2PApp(String serverPort) throws Exception {
        this.peerPort = Integer.parseInt(serverPort);
        this.socket = new DatagramSocket(this.peerPort);

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
                addr = packet.getAddress();
                peerPort = packet.getPort();
                System.out.print(String.format("[ %s:%d ] Recebi um pacote!", addr.toString(), peerPort));

                content = new String(packet.getData(), 0, packet.getLength());
                String vars[] = content.split("\\s");

                // add-resource texto.txt AJLKSDH1J23ASDAS
                if (vars[0].equals("add-resource") && vars.length == 3) {
                    // name e hash
                    String resourceName = vars[1];
                    String resourceHash = vars[2];

                    for (Peer peer : this.connectedPeers) {
                        if (peer.getIpAddress().getHostAddress().equals(addr.getHostAddress())) {
                            peer.addResource(new Resource(resourceName, resourceHash, peer));
                        }
                    }

                    response = "OK".getBytes();

                    packet = new DatagramPacket(response, response.length, addr, peerPort);
                    socket.send(packet);
                }

                // add-resource texto.txt AJLKSDH1J23ASDAS
                if (vars[0].equals("remove-resource") && vars.length == 3) {
                    // name e hash
                    String resourceName = vars[1];
                    String resourceHash = vars[2];

                    for (Peer peer : this.connectedPeers) {
                        if (peer.getIpAddress().getHostAddress().equals(addr.getHostAddress())) {
                            peer.removeResource(new Resource(resourceName, resourceHash, peer));
                        }
                    }

                    response = "OK".getBytes();

                    packet = new DatagramPacket(response, response.length, addr, peerPort);
                    socket.send(packet);
                }

                if (vars[0].equals("register") && vars.length > 1) {
                    Peer newPeer = new Peer(addr, peerPort);
                    System.out.println(newPeer.toString());

                    boolean isNewPeer = connectedPeers.add(newPeer); // adicionar novo peer.
                    if (!isNewPeer) {
                        response = "NOT OK".getBytes();
                    } else {
                        response = "OK".getBytes();
                    }

                    packet = new DatagramPacket(response, response.length, addr, peerPort);
                    socket.send(packet);
                }

                /*
                 * if (vars[0].equals("list") && vars.length > 1) { //lista de n usuários
                 * (index)
                 * for (int j = 0; j < resourceList.size(); j++) {
                 * if (resourceList.get(j).equals(vars[1])) {
                 * for (int i = 0; i < resourceList.size(); i++) {
                 * String data = new String(resourceList.get(i) + " " +
                 * resourceAddr.get(i).toString() + " " + resourcePort.get(i).toString());
                 * response = data.getBytes();
                 * 
                 * packet = new DatagramPacket(response, response.length, addr, port);
                 * socket.send(packet);
                 * }
                 * break;
                 * }
                 * }
                 * }
                 * 
                 * if (vars[0].equals("heartbeat") && vars.length > 1) { //ping de um peer.
                 * System.out.print("\nheartbeat: " + vars[1]);
                 * for (int i = 0; i < resourceList.size(); i++) {
                 * if (resourceList.get(i).equals(vars[1]))
                 * timeoutVal.set(i, 15);
                 * }
                 * }
                 */
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
            }
        }
    }
}
