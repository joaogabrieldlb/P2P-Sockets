package Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResourceHash {

    public static String computeMD5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(file.toPath()));
        byte[] digest = md.digest();
        return byteArrayToHex(digest);
    }

    private static String byteArrayToHex(byte[] array) {
        StringBuilder hashString = new StringBuilder(array.length * 2);
        for (byte b : array)
            hashString.append(String.format("%02x", b));
        return hashString.toString().toUpperCase();
    }
}