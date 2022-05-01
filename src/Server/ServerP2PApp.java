package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class ServerP2PApp {

    InetAddress addr;
    int port;
    DatagramSocket socket;
    Set<Peer> connectedPeers = new HashSet<>();
    DatagramPacket packet;

    byte[] resource = new byte[1024];
    byte[] response = new byte[1024];

    public ServerP2PApp(int socketPort) {
        try {
            this.socket = new DatagramSocket(socketPort);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Server");

        while (true) {
            try {
                String content = null;
                // recebe datagrama
                packet = new DatagramPacket(resource, resource.length);
                socket.setSoTimeout(500);
                socket.receive(packet);
                System.out.print("Recebi!");

                // processa o que foi recebido, adicionando a uma lista
                content = new String(packet.getData(), 0, packet.getLength());
                addr = packet.getAddress();
                port = packet.getPort();
                String vars[] = content.split("\\s");

                if (vars[0].equals("add") && vars.length > 1) {
                    // verificar se a hash já está vinculada ao peer solicitante.

                    // adicionar hash a lista de recursos do peer.

                }

                if (vars[0].equals("create") && vars.length > 1) {
                    int j;
                    Peer newPeer = new Peer(vars[1], addr, port);
                    System.out.println(newPeer.toString());

                    boolean isNewPeer = connectedPeers.add(newPeer); // adicionar novo peer.
                    if (!isNewPeer) {
                        response = "NOT OK".getBytes();
                    } else {
                        response = "OK".getBytes();
                    }

                    packet = new DatagramPacket(response, response.length, addr, port);
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
