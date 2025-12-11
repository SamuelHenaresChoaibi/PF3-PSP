package netmonitor.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final TcpServer tcpServer;
    private PrintWriter salida;
    private ClientInfo infoCliente;

    public ClientHandler(Socket socket, TcpServer tcpServer) {
        this.socket = socket;
        this.tcpServer = tcpServer;
    }

    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            String lineaPuertoUdp = entrada.readLine();
            if (lineaPuertoUdp != null && lineaPuertoUdp.startsWith("PUERTO_UDP")) {
                int puertoUdp = Integer.parseInt(lineaPuertoUdp.substring(11));
                InetAddress direccion = socket.getInetAddress();
                infoCliente = new ClientInfo(direccion, puertoUdp);
                tcpServer.addCliente(infoCliente);

                String ip = direccion.getHostAddress();
                tcpServer.getEmisor().broadcast("NUEVO_CLIENTE " + ip + " se ha conectado", tcpServer.getClientes());
                tcpServer.getEmisor().broadcast("TOTAL|Actualmente hay " + tcpServer.getClientesCount() + " clientes conectados", tcpServer.getClientes());
            } else {
                throw new IOException("Falta PUERTO_UDP");
            }

            String comando;
            while ((comando = entrada.readLine()) != null) {
                if (comando.equals("HORA")) {
                    salida.println(new Date());
                } else if (comando.startsWith("ECHO")) {
                    String texto = comando.substring(4);
                    tcpServer.getEmisor().broadcast(texto, tcpServer.getClientes(), infoCliente);
                    salida.println("OK");
                } else if (comando.equals("CONTAR")) {
                    salida.println(tcpServer.getClientesCount());
                } else if (comando.equals("ADIÓS")) {
                    manejarAdios();
                    break;
                } else if (comando.equals("APAGAR")) {
                    manejarApagado();
                } else {
                    salida.println("Comando inválido");
                }
            }
        } catch (IOException e) {
            System.err.println("Error en la conexión con el cliente");
        } finally {
            if (infoCliente != null) {
                try {
                    manejarAdios();
                } catch (IOException e) {
                    System.err.println("Error en la conexión con el cliente");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el servidor");
            }
        }
    }

    private void manejarAdios() throws IOException {
        if (infoCliente != null) {
            String ip = infoCliente.adress.getHostAddress();
            tcpServer.getEmisor().broadcast("CLIENTE_SALIO " + ip + " se ha desconectado", tcpServer.getClientes());
            tcpServer.getEmisor().broadcast("TOTAL|Actualmente hay " + (tcpServer.getClientesCount() - 1) + " clientes conectados", tcpServer.getClientes());
            tcpServer.deleteCliente(infoCliente);
            infoCliente = null;
        }
    }

    private void manejarApagado() throws IOException {
        if (socket.getInetAddress().isLoopbackAddress()) {
            salida.println("Apagando el servidor");
            tcpServer.apagar();
        } else {
            salida.println("No autorizado");
        }
    }
}