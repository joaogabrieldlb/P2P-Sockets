package Server;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class Peer {

    private final InetAddress ipAddress;
    private final Integer port;
    private Set<Resource> resources = new HashSet<>();
    private final int HEARTHBEAT_TIMEOUT = 15; /* 1000ms * 15 = 15s (enough for 10s heartbeat) */
    private int timeOut = HEARTHBEAT_TIMEOUT;

    public Peer(InetAddress ipAddress, Integer port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void resetTimeOut() {
        this.timeOut = HEARTHBEAT_TIMEOUT;
    }

    public int decrementTimeOut() {
        return --this.timeOut;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public boolean addResource(Resource resource) {
        return resources.add(resource);
    }

    public boolean removeResource(Resource resource) {
        return resources.remove(resource);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
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
        return true;
    }

}