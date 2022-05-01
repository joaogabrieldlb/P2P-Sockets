package Server;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class Peer {

    private final InetAddress ipAddress;
    private final Integer port;
    private Set<Resource> resources = new HashSet<>();
    private int timeOut = 15; /* 500ms * 15 = 7.5s (enough for 5s heartbeat) */

    public Peer(InetAddress ipAddress, Integer port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void resetTimeOut() {
        this.timeOut = 15;
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