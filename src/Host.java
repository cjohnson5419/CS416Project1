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

        String virtualIP = "net1." + ID;

        System.out.println("Host " + ID + " (IP: " + virtualIP + ") started on port " + portNum);

        new Thread(() -> {
            try {
                while (true) {
                    DatagramPacket packet = receivePacket(socket);
                    String frame = new String(packet.getData(), 0, packet.getLength());

                    String[] info = frame.split(":", 5);
                    if (info.length < 5) continue;

                    String srcMAC = info[0];
                    String dstMAC = info[1];
                    String srcIP  = info[2];
                    String message = info[4];

                    if (dstMAC.equals(ID)) {
                        System.out.println("\n[Message Received]");
                        System.out.println("From Source Host: " + srcMAC + " (IP: " + srcIP + ")");
                        System.out.println("Message: " + message);
                    } else {
                        System.out.println("\n[DEBUG] Flooded frame received. Destination MAC (" + dstMAC + ") " +
                                "does not match my MAC (" + ID + "). Ignoring payload.");
                    }

                    System.out.print("\nEnter <DestMAC> <DestIP> <Message>: ");
                }
            } catch (Exception e) {
                System.err.println("Receiver error: " + e.getMessage());
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter <DestMAC> <DestIP> <Message> (e.g., R1 net3.D hello!): ");

            String destMAC = scanner.next();   // R1
            String destIP = scanner.next();    // net3.D
            String message = scanner.nextLine().trim(); // hello!

            String frame = ID + ":" + destMAC + ":" + virtualIP + ":" + destIP + ":" + message;

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