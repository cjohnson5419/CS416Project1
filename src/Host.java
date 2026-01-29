import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) throws Exception {
        String ID = args[0];
        Parser parser = new Parser(ID);
        int portNum = parser.getPortNum();
        ArrayList<InetSocketAddress> neighbors = parser.getNeighbors();
        DatagramSocket socket = new DatagramSocket(portNum);

        System.out.println("Host " + ID + " started on port " + portNum);

        new Thread(() -> {
            try {
                while (true) {
                    DatagramPacket packet = receivePacket(socket);
                    String frame = new String(packet.getData(), 0, packet.getLength());
                    String[] info = frame.split(":");

                    String srcMAC = info[0];
                    String dstMAC = info[1];
                    String message = info[2];

                    System.out.println("\n[Received] From " + srcMAC + ": " + message);

                    if (!dstMAC.equals(ID)) {
                        System.out.println("DEBUG: MAC mismatch. Destination was " + dstMAC);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter <DestinationID> <Message>: ");
            String destID = scanner.next();
            String message = scanner.nextLine().trim();

            String frame = ID + ":" + destID + ":" + message;

            for (InetSocketAddress neighbor : neighbors) {
                sendPacket(socket, frame, neighbor);
            }
        }
    }

    private static void sendPacket(DatagramSocket socket, String frame, InetSocketAddress target) throws Exception {
        byte[] data = frame.getBytes();
        socket.send(new DatagramPacket(data, data.length, target));
    }

    private static DatagramPacket receivePacket(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet;
    }
}