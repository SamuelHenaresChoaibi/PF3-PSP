package netmonitor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class TcpClient {
    private static final String HOST = "localhost";
    private static final int PUERTO_TCP = 5000;

    static void main() {
        try {
            Socket socket = new Socket(HOST, PUERTO_TCP);
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Abrir socket UDP
            DatagramSocket socketUdp = new DatagramSocket();
            int puertoLocal = socketUdp.getLocalPort();
            salida.println("PUERTO_UDP " + puertoLocal);

            // Iniciar listener UDP
            UdpNotificationListener listener = new UdpNotificationListener(socketUdp);
            Thread hiloListener = new Thread(listener);
            hiloListener.start();

            // Menú
            Scanner entradaUsuario = new Scanner(System.in);
            boolean ejecutando = true;
            while (ejecutando) {
                System.out.println("Menú:");
                System.out.println("1. HORA");
                System.out.println("2. ECO");
                System.out.println("3. CONTAR");
                System.out.println("4. ADIÓS");
                System.out.println("5. APAGAR");
                System.out.print("Elige opción: ");
                int opcion = entradaUsuario.nextInt();
                entradaUsuario.nextLine(); // Consumir salto de línea

                String respuesta;
                switch (opcion) {
                    case 1:
                        salida.println("HORA");
                        respuesta = entrada.readLine();
                        System.out.println("Hora del servidor: " + respuesta);
                        break;
                    case 2:
                        System.out.print("Introduce texto para eco: ");
                        String texto = entradaUsuario.nextLine();
                        salida.println("ECO " + texto);
                        respuesta = entrada.readLine();
                        System.out.println("Respuesta del servidor: " + respuesta);
                        break;
                    case 3:
                        salida.println("CONTAR");
                        respuesta = entrada.readLine();
                        System.out.println("Número de clientes: " + respuesta);
                        break;
                    case 4:
                        salida.println("ADIÓS");
                        ejecutando = false;
                        break;
                    case 5:
                        salida.println("APAGAR");
                        respuesta = entrada.readLine();
                        System.out.println("Respuesta del servidor: " + respuesta);
                        ejecutando = false;
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            }

            listener.stopEscucha();
            hiloListener.join();
            socket.close();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}