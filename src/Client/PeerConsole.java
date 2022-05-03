package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PeerConsole implements Runnable {

    private int localPort;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    private final String DEFAULT_DIRECTORY_PATH = "./Resource/";

    public PeerConsole(int localPort, InetAddress serverAddress, int serverPort) throws Exception {
        this.localPort = localPort;
        this.socket = new DatagramSocket(localPort);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void run() {
        // Solicita registro no servidor
        byte[] data = "register".getBytes();
        this.packet = new DatagramPacket(data, data.length, this.serverAddress, this.serverPort);
        try {
            this.socket.send(packet); // Tenta se registrar no servidor.

            byte[] response = new byte[1024];
            var responsePacket = new DatagramPacket(response, response.length);
            socket.receive(responsePacket); // Aguarda resposta do server para op. de registro.
            String content = new String(responsePacket.getData(), 0, responsePacket.getLength());

            if (content.equals("OK")) {
                // new DirectoryTrack(socket)
                // - verifica o diretório a cada x segundo para realizar operações de remove ou
                // add no server.
                // Ler os arquivos e dar uma add-resource para cada um deles.
            } else {
                System.out.println("\t\nERROR: This client is already registered.\n");
                throw new IOException();
            }

        } catch (IOException e) {
            System.out.println("\n\tERROR: Register to the server was not successful!\n");
            System.exit(1);
        }

        // imprime comandos disponiveis no PeerConsole
        // aguarda input do usuário

        // 1. usuario deseja listar recursos por um critério de busca e por fileName
        // (list-resources)
        // list-resources --name XX
        // list-resources --hash XX

        // 2. usuario deseja "baixar" recurso de outro peer
        // get-resource <hash> <ip_address> <port>
        // new PeerRequestFile(this.localport + 3).start();

    }

}
