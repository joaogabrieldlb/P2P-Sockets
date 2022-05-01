package Server;

import java.net.InetAddress;

public class Resource {
    private String name;
    private String hash;
    private Peer peer;

    public Resource(String name, String hash, InetAddress ip, int port, Peer peer) {
        this.name = name;
        this.hash = hash;
        this.peer = peer;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public Peer getPeer() {
        return peer;
    }
}
