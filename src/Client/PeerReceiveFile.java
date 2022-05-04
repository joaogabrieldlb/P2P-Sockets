package Client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class PeerReceiveFile extends Thread {

    private InetAddress remotePeerIp;
    private int remotePeerPort;
    private String remoteFileHash;
    private String remoteFileName;
    private final String TEMP_FOLDER = "./temp/"; // Pasta em bin/resources -> rever isso

    public PeerReceiveFile(InetAddress remotePeerIpAddress, String port, String fileHash, String remotePeerFileName)
            throws UnknownHostException {
        this.remotePeerIp = remotePeerIpAddress;
        this.remotePeerPort = Integer.valueOf(port);
        this.remoteFileHash = fileHash;
        this.remoteFileName = remotePeerFileName;
    }

    public void run() {
        try {
            Socket sock;
            sock = new Socket(this.remotePeerIp.getHostAddress(), this.remotePeerPort);
            byte[] buffer = new byte[1024];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream(TEMP_FOLDER + this.remoteFileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                // tratamento do buffer para o último pacote
                if (bytesRead < buffer.length) {
                    buffer = Arrays.copyOf(buffer, bytesRead);
                }
                bos.write(buffer, 0, buffer.length);
            }
            bos.close();
            sock.close();

            // validar hash e mover p/ pasta de resources
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
