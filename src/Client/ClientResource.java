package Client;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import Utils.ResourceHash;

public class ClientResource {

    private String name;
    private String hash;
    private boolean isRegistred;
    private File file;

    public ClientResource(File file) throws NoSuchAlgorithmException, IOException {
        this.file = file;
        this.name = this.file.getName();
        this.hash = ResourceHash.computeMD5(this.file);
        this.isRegistred = false;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public boolean isRegistred() {
        return isRegistred;
    }

    public void setRegistred(boolean isRegistred) {
        this.isRegistred = isRegistred;
    }

    public File getFile() {
        return file;
    }

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
        ClientResource other = (ClientResource) obj;
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

    @Override
    public String toString() {
        return this.name + "|" + this.hash;
    }

}
