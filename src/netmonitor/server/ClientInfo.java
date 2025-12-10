package netmonitor.server;

import java.net.InetAddress;

public class ClientInfo {
    public InetAddress adress;
    public int udpPort;

    public ClientInfo(InetAddress adress, int udpPort) {
        this.adress = adress;
        this.udpPort = udpPort;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClientInfo otro = (ClientInfo) obj;
        return udpPort == otro.udpPort && adress.equals(otro.adress);
    }

    @Override
    public int hashCode() {
        return adress.hashCode() + udpPort;
    }
}