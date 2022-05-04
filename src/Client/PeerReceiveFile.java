package Client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import Utils.ResourceHash;

public class PeerReceiveFile extends Thread {

    private InetAddress remotePeerIp;
    private int remotePeerPort;
    private String remoteFileHash;
    private String remoteFileName;
    private final String TEMP_FOLDER = "./temp/"; // Pasta em bin/resources -> rever isso

    public PeerReceiveFile(InetAddress remotePeerIpAddress, String port, String fileHash, String remotePeerFileName)
            throws IOException {
        this.remotePeerIp = remotePeerIpAddress;
        this.remotePeerPort = Integer.valueOf(port);
        this.remoteFileHash = fileHash;
        this.remoteFileName = remotePeerFileName;
        if (!Files.isDirectory(Paths.get(TEMP_FOLDER))) {
            Files.createDirectory(Paths.get(TEMP_FOLDER));
        }
    }

    public void run() {
        try {
            Socket sock = new Socket(this.remotePeerIp.getHostAddress(), this.remotePeerPort);
            byte[] mybytearray = new byte[1024];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream(this.remoteFileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead;
            while ((bytesRead = is.read(mybytearray, 0, mybytearray.length)) != -1) {
                if (bytesRead < mybytearray.length) {
                    mybytearray = Arrays.copyOf(mybytearray, bytesRead);
                }
                bos.write(mybytearray, 0, bytesRead);
            }
            bos.close();
            sock.close();

            File receivedFile = new File(this.remoteFileName);
            String receivedFileHash = ResourceHash.computeMD5(receivedFile);
            if (remoteFileHash.equals(receivedFileHash)) {
                System.out.println("File transfer successful.");
                receivedFile.renameTo(new File("./resources/", receivedFile.getName()));
            } else {
                System.out.println("File transfer failed. Hash mismatch.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
