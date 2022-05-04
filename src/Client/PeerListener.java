package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PeerListener extends Thread {

    private ClientP2PApp app;
    private DatagramSocket socketListen;
    private DatagramPacket packet;
    private int port;
    private InetAddress clientAddress;
    private int clientPort;
    private int nextFilePort;

    public PeerListener(ClientP2PApp app, int port) throws SocketException {
        this.app = app;
        this.port = port;
        this.socketListen = new DatagramSocket(port);
        this.nextFilePort = port + 10;
    }

    @Override
    public void run() {
        byte[] response = new byte[1024];
        byte[] resource = new byte[1024];
        String content = null;

        // laço de escuta
        while (true) {
            try {
                // recebe datagrama
                this.packet = new DatagramPacket(resource, resource.length);
                socketListen.receive(this.packet);

                // processa o que foi recebido, adicionando a uma lista
                clientAddress = packet.getAddress();
                clientPort = packet.getPort();

                content = new String(packet.getData()).trim();
                String vars[] = content.split("\\|");

                // get-resouce|AJLKSDH1J23ASDAS
                if (vars[0].equals("get-resource") && vars.length == 4) {
                    // name|hash
                    String resourceHash = vars[1];

                    // procura resource
                    ClientResource localResource = localizaResource(resourceHash);
                    if (localResource == null) {
                        response = "NOT OK".getBytes();
                        packet = new DatagramPacket(response, response.length, clientAddress, clientPort);
                        socketListen.send(packet);
                    }

                    // envia resource (abre thread de envio com socket stream)
                    PeerSendFile sendFile = null;
                    int retry = 0;
                    while (retry < 3) {
                        try {
                            sendFile = new PeerSendFile(nextFilePort++, clientAddress, clientPort, localResource,
                                    socketListen);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            retry++;
                        }
                    }
                    if (retry >= 3) {
                        response = "NOT OK".getBytes();
                        packet = new DatagramPacket(response, response.length, clientAddress, clientPort);
                        socketListen.send(packet);
                    } else {
                        sendFile.start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ClientResource localizaResource(String hash) {
        ClientResource locatedResource = null;
        try {
            this.app.clientResourceSemaphore.acquire();
            for (ClientResource resource : this.app.clientResources) {
                if (resource.getHash().equals(hash)) {
                    locatedResource = resource;
                    break;
                }
            }
            this.app.clientResourceSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return locatedResource;
    }

}