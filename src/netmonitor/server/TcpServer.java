package netmonitor.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TcpServer {
    private static final int PUERTO_TCP = 5000;
    private static final int PUERTO_UDP = 6000;

    private final ServerSocket serverSocket;
    private final List<ClientInfo> clientes = Collections.synchronizedList(new ArrayList<>());
    private final UdpBroadcaster emisor;

    public TcpServer() throws IOException {
        serverSocket = new ServerSocket(PUERTO_TCP);
        emisor = new UdpBroadcaster(PUERTO_UDP);
        System.out.println("Servidor iniciado en el puerto " + PUERTO_TCP);
    }

    public void start() {
        while (true) {
            try {
                Socket socketCliente = serverSocket.accept();
                ClientHandler manejador = new ClientHandler(socketCliente, this);
                new Thread(manejador).start();
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    System.out.println("Servidor apagado.");
                    break;
                }
                System.out.println("Error en el servidor.");
            }
        }
    }

    public void addCliente(ClientInfo infoCliente) {
        clientes.add(infoCliente);
    }

    public void deleteCliente(ClientInfo infoCliente) {
        clientes.remove(infoCliente);
    }

    public List<ClientInfo> getClientes() {
        return clientes;
    }

    public int getClientesCount() {
        return clientes.size();
    }

    public UdpBroadcaster getEmisor() {
        return emisor;
    }

    public void apagar() throws IOException {
        serverSocket.close();
    }

    static void main() {
        try {
            TcpServer servidor = new TcpServer();
            servidor.start();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor.");
        }
    }
}