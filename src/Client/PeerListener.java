package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.Semaphore;

import javax.management.relation.RelationSupport;

import Server.Resource;

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
        System.out.println("Escutando na porta: " + port);

        byte[] response = new byte[1024];
        byte[] resource = new byte[1024];
        String content = null;
        while (true) {
                try {
                // recebe datagrama
                this.packet = new DatagramPacket(resource, resource.length);
                socketListen.receive(this.packet);
                    
                // processa o que foi recebido, adicionando a uma lista
                clientAddress = packet.getAddress();
                clientPort = packet.getPort();
                System.out.print(String.format("[ %s:%d ] Pacote recebido.", clientAddress.toString(), clientPort));

                content = new String(packet.getData()).trim();
                System.out.println("\nContent: " + content);
                String vars[] = content.split("\\|");
                System.out.println("Size: " + vars.length);

                // get-resouce|AJLKSDH1J23ASDAS
                if (vars[0].equals("get-resource") && vars.length >= 2) {
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
                    new PeerReplyFile(nextFilePort++, clientAddress, clientPort, localResource).start();

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
            for(ClientResource resource : this.app.clientResources) {
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