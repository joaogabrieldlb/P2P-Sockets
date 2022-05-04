package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class PeerConsole implements Runnable {

    private ClientP2PApp app;
    private int localPort;
    private DatagramPacket packet;
    private DatagramSocket socket;

    public PeerConsole(ClientP2PApp app) throws Exception {
        this.app = app;
        this.localPort = this.app.localPort;
        this.socket = this.app.mainSocket;
    }

    public void run() {
        // Solicita registro no servidor
        try {
            byte[] data = "register".getBytes();
            this.packet = new DatagramPacket(data, data.length, app.serverAddress, app.serverPort);
            this.socket.send(packet); // Tenta se registrar no servidor.

            byte[] response = new byte[1024];
            var responsePacket = new DatagramPacket(response, response.length);
            socket.receive(responsePacket); // Aguarda resposta do server para op. de registro.
            String content = new String(responsePacket.getData(), 0, responsePacket.getLength());

            if (content.equals("OK")) {
                new PeerHeartbeat(this.localPort + 1, app.serverAddress, app.serverPort, this.app.mainSocketSemaphore)
                        .start();
                new ClientScanResources(this.app).start();
                new PeerListener(app, this.localPort + 2).start();

                // iniciar loop do console para interação com user.
                this.startConsoleInterface();
            } else {
                System.out.println("\t\nERROR: This client IP is already registered.\n");
                throw new IOException();
            }

        } catch (IOException e) {
            System.out.println("\n\tERROR: Register to the server was not successful!\n");
            System.exit(1);
        }
    }

    private void startConsoleInterface() {
        BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
        String str = "";

        while (true) {
            System.out.println("\nAvailable Operations");
            System.out.println("- list-resources --name XXX | list-resources --hash XXX");
            System.out.println("- get-resource <hash> <ip_address> <port>");

            try {
                str = obj.readLine();
                String vars[] = str.split("\\s");

                switch (vars[0]) {
                    case "list-resources":
                        this.listResources(vars);
                        break;
                    case "get-resource":
                        this.getResource(vars);
                        break;
                    default:
                        System.out.println("Invalid operation.");
                        break;
                }
            } catch (IOException | InterruptedException e) {
            }
        }
    }

    private void getResource(String[] vars) throws IOException {
        String operation = vars[0];
        String hash = vars[1];
        String ip = vars[2];
        String port = vars[3];
        byte[] request = new byte[1024];
        InetAddress remotePeerIpAddress = InetAddress.getByName(ip);

        request = String.join("|", operation, hash, ip, port).getBytes();
        var packet = new DatagramPacket(request, request.length, remotePeerIpAddress, Integer.valueOf(port) + 2);
        // Envia uma solicitação de recurso.
        this.socket.send(packet);

        // Recebe o status do arquivo solicitado e a porta p/conectar (Ex:
        // OK|7895|file.txt)
        byte[] response = new byte[1024];
        var responsePacket = new DatagramPacket(response, response.length);
        socket.receive(responsePacket);
        String content = new String(responsePacket.getData(), 0, responsePacket.getLength()).trim();

        String varsResponse[] = content.split("\\|");

        if (varsResponse[0].equals("OK")) {
            new PeerReceiveFile(remotePeerIpAddress, varsResponse[1], hash, varsResponse[2]).start();
        } else {
            System.out.println("File not available.");
        }
    }

    private void listResources(String[] vars) throws IOException, InterruptedException {
        String searchType = vars[1];
        String searchContent = String.join(" ", Arrays.copyOfRange(vars, 2, vars.length));
        byte[] request = new byte[1024];
        request = String.join("|", "list-resources", searchType, searchContent).getBytes();
        var packet = new DatagramPacket(request, request.length, this.app.serverAddress, this.app.serverPort);
        this.app.mainSocketSemaphore.acquire();
        this.socket.send(packet);
        this.app.mainSocketSemaphore.release();

        // Recebe o número de registros encontrados.
        byte[] response = new byte[1024];
        var responsePacket = new DatagramPacket(response, response.length);
        socket.receive(responsePacket);
        int resourcesCount = Integer.valueOf(new String(responsePacket.getData(), 0, responsePacket.getLength()));

        if (resourcesCount > 0) {
            System.out.println("\n File Name | HASH | Peer IP | Port");
            System.out.println();
            for (int i = 0; i < resourcesCount; i++) {
                // Aguarda resposta do server para X resources encontrados.
                responsePacket = new DatagramPacket(response, response.length);
                socket.receive(responsePacket);
                String resource = new String(responsePacket.getData(), 0, responsePacket.getLength())
                        .replace("|", " ");
                System.out.println(resource);
            }
        } else {
            System.out.println("Could not find files with this name or hash.\n");
        }

    }

}
