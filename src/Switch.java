import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Switch {
    public static void main(String[] args) throws Exception {
        String ID = args[0];
        Parser parser = new Parser(ID);

        Map<String, InetSocketAddress> table = new HashMap<>();
        int portNum = parser.getPortNum();
        ArrayList<InetSocketAddress> neighbors = parser.getNeighbors();
        DatagramSocket socket = new DatagramSocket(portNum);

        System.out.println("Switch " + ID + " started on port " + portNum);
        System.out.println("Neighbors:\n");
        for (int i = 0; i < neighbors.size(); i++) {
            System.out.println(neighbors.get(i));
        }
        System.out.println();

        while (true) {
            System.out.println("\nTable");
            System.out.println(table);
            System.out.println();

            DatagramPacket packetReceived = receivePacket(socket);
            String frame = new String(packetReceived.getData(), 0, packetReceived.getLength());

            InetSocketAddress sender = new InetSocketAddress(packetReceived.getAddress(), packetReceived.getPort());

            String[] info = frame.split(":");
            String srcMAC = info[0];
            String dstMAC = info[1];

            table.putIfAbsent(srcMAC, sender);
            if (table.containsKey(dstMAC)) {
                InetSocketAddress target = table.get(dstMAC);
                sendPacket(socket, frame, target);
            }
            else {
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!neighbors.get(i).equals(sender)) {
                        sendPacket(socket, frame, neighbors.get(i));
                    }
                }
            }
        }
    }

    private static void sendPacket(DatagramSocket socket, String frame, InetSocketAddress target) throws Exception {
        byte[] data = frame.getBytes();

        DatagramPacket packetSent = new DatagramPacket(data, data.length, target.getAddress(), target.getPort());
        socket.send(packetSent);
    }

    private static DatagramPacket receivePacket(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[1024];

        DatagramPacket packetReceived = new DatagramPacket(buffer, buffer.length);
        socket.receive(packetReceived);

        return packetReceived;
    }
}