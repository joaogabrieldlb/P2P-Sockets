package Client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class ClientP2PApp {

    protected DatagramSocket mainSocket;
    protected int localPort;
    protected InetAddress serverAddress;
    protected int serverPort;
    protected Set<ClientResource> clientResources = new HashSet<>();
    protected Semaphore mainSocketSemaphore = new Semaphore(1);
    protected Semaphore clientResourceSemaphore = new Semaphore(1);

    public ClientP2PApp(String localPort, String serverAddress, String serverPort) throws Exception {
        this.localPort = Integer.parseInt(localPort);
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = Integer.parseInt(serverPort);
        this.mainSocket = new DatagramSocket(this.localPort);
    }

    public void run() throws Exception {
        new PeerConsole(this).run(); // Interações com usuário via console
        // comunicando-se com o servidor.
        // Solicita registro no servidor
        // new DirectoryTrack(newPort)
        // - verifica o diretório a cada x segundo para realizar operações de remov e ou
        // add no server.
        // imprime comandos disponiveis no PeerConsole
        // aguarda input do usuário

        // 1. usuario deseja listar recursos por um critério de busca e por fileName
        // (list-resources)
        // list-resources --name XX
        // list-resources --hash XX

        // 2. usuario deseja "baixar" recurso de outro peer
        // get-resource <hash> <ip_address> <port>

        /*
         * new PeerHeartbeat((this.localPort), this.serverAddress, this.serverPort,
         * InetAddress.getLocalHost().getHostAddress())
         * .start();
         */
        // alterar logica servidor porta recebida do heartbeat - 1 para identificar o
        // peer -> DONE

        new PeerListener(this, this.localPort + 2).start(); // Fica esperando conexões diretos de P2P
        // aguarda receber pacote de outro peer
        // - new PeerThread(newPort).start();
        // - recebe o arquivo
        // - calcula a hash
        // - verifica se a hash bate
        // volta para aguardar ln 27

    }

}
