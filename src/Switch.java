import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Switch {
    public static void main(String[] args) throws Exception {
        Map<String, InetSocketAddress> table = new HashMap<>();
        String ID = args[0];
        Parser parser = new Parser(ID);
        int portNum = parser.getPortNum();
        InetSocketAddress[] neighbors = parser.getNeighbors();
        DatagramSocket socket = new DatagramSocket(portNum);

        while (true) {
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
                for (int i = 0; i < neighbors.length; i++) {
                    if (!neighbors[i].equals(sender)) {
                        sendPacket(socket, frame, neighbors[i]);
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