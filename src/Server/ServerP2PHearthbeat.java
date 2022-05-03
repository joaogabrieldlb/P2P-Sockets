package Server;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class ServerP2PHearthbeat implements Runnable {

    private Set<Peer> connectedPeers;
    private Semaphore connectedPeersSemaphore;

    public ServerP2PHearthbeat(Set<Peer> connectedPeers, Semaphore connectedPeersSemaphore) {
        this.connectedPeers = connectedPeers;
    }

    @Override
    public void run() {
        while (true) {
            // decrementa timeouts
            try {
                // for(Iterator<Peer> i = this.connectedPeers.iterator(); i.hasNext(); ) {
                // String item = i.next();
                // System.out.println(item);
                // }
                connectedPeersSemaphore.acquire();
                for (Peer peer : this.connectedPeers) {
                    int timeout = peer.decrementTimeOut();
                    if (timeout == 0) {
                        this.connectedPeers.remove(peer);
                    }
                }
                connectedPeersSemaphore.release();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean receivedHearbeat(InetAddress peerAddr, int peerPort) {
        peerPort--; // Porta recebeida é diferente da porta registrada no Peer (+1).
        boolean reset = false;
        for (Peer peer : connectedPeers) {
            if (peer.getIpAddress().equals(peerAddr) && peer.getPort().equals(peerPort)) {
                try {
                    connectedPeersSemaphore.acquire();
                    peer.resetTimeOut();
                    reset = true;
                    connectedPeersSemaphore.release();
                } catch (InterruptedException e) {
                }
                break;
            }
        }
        return reset;
    }
}
