package Server;

public class Resource {
    private String name;
    private String hash;
    private Peer peer;

    public Resource(String name, String hash, Peer peer) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Resource other = (Resource) obj;
        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.equals(other.hash))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
