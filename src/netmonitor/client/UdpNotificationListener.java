package netmonitor.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpNotificationListener implements Runnable {
    private final DatagramSocket socket;
    private volatile boolean ejecutando = true;

    public UdpNotificationListener(DatagramSocket socket) {
        this.socket = socket;
    }

    public void stopEscucha() {
        ejecutando = false;
        socket.close();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (ejecutando) {
            try {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                System.out.println("\nNotificación: " + mensaje);
                System.out.print("Elige opción: ");
            } catch (IOException e) {
                if (ejecutando) {
                    System.out.println("Error en el servidor");
                }
            }
        }
    }
}