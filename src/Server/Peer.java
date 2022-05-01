package Server;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class Peer {

    private final String nickname;
    private final InetAddress ipAddress;
    private final Integer port;
    private Set<Resource> resourcesHash = new HashSet<>();
    private int timeOut = 15; /* 500ms * 15 = 7.5s (enough for 5s heartbeat) */

    public Peer(String nickname, InetAddress ipAddress, Integer port) {
        this.nickname = nickname;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void resetTimeOut() {
        this.timeOut = 15;
    }

    public int decrementTimeOut() {
        return --this.timeOut;
    }

    public String getNickname() {
        return nickname;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public Set<Resource> getResourcesHash() {
        return resourcesHash;
    }

    public boolean addResourceHash(Resource resourceHash) {
        return resourcesHash.add(resourceHash);
    }

    @Override
    public String toString() {
        return "Peer [ipAddress=" + ipAddress + ", nickname=" + nickname + ", port=" + port + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Peer other = (Peer) obj;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (nickname == null) {
            if (other.nickname != null)
                return false;
        } else if (!nickname.equals(other.nickname))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        return true;
    }

}