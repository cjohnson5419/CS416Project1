import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java Host <ID>");
            return;
        }

        String ID = args[0];
        Parser parser = new Parser(ID);

        int realPort = parser.getPortNum();
        ArrayList<InetSocketAddress> neighbors = parser.getNeighbors();
        DatagramSocket socket = new DatagramSocket(realPort);

        String myVirtualIP = Parser.getDevice(ID).getVirtualIP();
        String myGateway = Parser.getDevice(ID).getGateway();

        System.out.println("Host " + ID + " (IP: " + myVirtualIP + ") started on port " + realPort);
        System.out.println("Default Gateway: " + myGateway);

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
                        System.out.println("\n[Received] From " + srcIP + " (MAC: " + srcMAC + "): " + message);
                    } else {
                        System.out.println("\n[DEBUG] MAC Mismatch. Destination was " + dstMAC + ". (Flooded frame)");
                    }
                    System.out.print("Enter <DestIP> <Message>: ");
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter <DestIP> <Message>: ");
            String destIP = scanner.next();
            String message = scanner.nextLine().trim();

            String destMAC;
            String myNetwork = myVirtualIP.split("\\.")[0];
            if (destIP.startsWith(myNetwork)) {
                destMAC = destIP.split("\\.")[1];
            } else {
                destMAC = myGateway.split("\\.")[1];
            }

            String frame = ID + ":" + destMAC + ":" + myVirtualIP + ":" + destIP + ":" + message;

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