package netmonitor.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class UdpBroadcaster {
    private final DatagramSocket socket;

    public UdpBroadcaster(int puerto) throws IOException {
        socket = new DatagramSocket(puerto);
    }

    public void broadcast(String mensaje, List<ClientInfo> clientes) throws IOException {
        broadcast(mensaje, clientes, null);
    }

    public void broadcast(String mensaje, List<ClientInfo> clientes, ClientInfo excluir) throws IOException {
        byte[] buffer = mensaje.getBytes();
        synchronized (clientes) {
            for (ClientInfo ci : clientes) {
                if (ci.equals(excluir)) continue;
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, ci.adress, ci.udpPort);
                socket.send(paquete);
            }
        }
    }
}