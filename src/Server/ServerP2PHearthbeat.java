package Server;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class ServerP2PHearthbeat extends Thread {

    private Set<Peer> connectedPeers;
    private Semaphore connectedPeersSemaphore;

    public ServerP2PHearthbeat(Set<Peer> connectedPeers, Semaphore connectedPeersSemaphore) {
        this.connectedPeers = connectedPeers;
        this.connectedPeersSemaphore = connectedPeersSemaphore;
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
                        System.out.println("Peer removido por inatividade: " + peer.toString());
                        this.connectedPeers.remove(peer);
                    }
                }
                connectedPeersSemaphore.release();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean receivedHearbeat(InetAddress peerAddr, int heartbeatPort) {
        int peerPort = heartbeatPort - 1; // Porta recebeida é diferente da porta registrada no Peer (+1).
        boolean reset = false;
        try {
            connectedPeersSemaphore.acquire();
            for (Peer peer : this.connectedPeers) {
                if (peer.getIpAddress().equals(peerAddr) && peer.getPort().equals(peerPort)) {
                    peer.resetTimeOut();
                    reset = true;
                    break;
                }
            }
            connectedPeersSemaphore.release();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reset;
    }
}
