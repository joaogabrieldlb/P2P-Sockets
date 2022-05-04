package Client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerSendFile extends Thread {

    private DatagramPacket packet;
    private DatagramSocket socketListen;

    private ServerSocket serverSocket;

    private InetAddress clientAddress;
    private int clientPort;

    private int localPort;
    private ClientResource localResource;

    public PeerSendFile(int localPort, InetAddress clientAddress, int clientPort, ClientResource localResource,
            DatagramSocket socketListen) throws IOException {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.localPort = localPort;
        this.localResource = localResource;
        this.socketListen = socketListen;
        this.serverSocket = new ServerSocket(localPort);
    }

    public void run() {
        try {
            // enviar OK|porta|filename
            byte[] response = ("OK|" + this.localPort + "|" + this.localResource.getName()).getBytes();
            this.packet = new DatagramPacket(response, response.length, this.clientAddress, this.clientPort);
            socketListen.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Aguarda uma conexão.
        try {
            /*
             * Socket clientSocket = serverSocket.accept();
             * byte[] buffer = new byte[1024];
             * BufferedInputStream bufferedInputStream = new BufferedInputStream(
             * new FileInputStream(localResource.getFile()));
             * OutputStream outputStream = clientSocket.getOutputStream();
             * 
             * int bytesRead;
             * while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
             * // tratamento do buffer para o último pacote
             * if (bytesRead < buffer.length) {
             * buffer = Arrays.copyOf(buffer, bytesRead);
             * }
             * bufferedInputStream.read(buffer, 0, buffer.length);
             * outputStream.write(buffer, 0, buffer.length);
             * }
             * outputStream.flush();
             * clientSocket.close();
             */
            Socket clientSocket = serverSocket.accept();
            byte[] buffer = new byte[(int) localResource.getFile().length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localResource.getFile()));
            bis.read(buffer, 0, buffer.length);
            OutputStream os = clientSocket.getOutputStream();
            os.write(buffer, 0, buffer.length);
            os.flush();
            clientSocket.close();
        } catch (IOException ex) {
            System.out.println("Connection error.");
        }

    }
}