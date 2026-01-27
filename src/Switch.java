import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Switch {
    public static void main(String[] args) throws Exception {
        Map<String, String[]> table = new HashMap<>();
        String ID = args[0];
        Parser parser = new Parser(ID);
        int portNum = parser.getPortNum();
        InetAddress IP = InetAddress.getByName(parser.getAddress());

        while (true) {
            String frame = receivePacket(portNum);
            sendPacket(frame);
        }
    }

    private static void sendPacket(String frame) throws Exception {
    }

    private static String receivePacket(int portNum) throws Exception {
        DatagramSocket socket = new DatagramSocket(portNum);
        byte[] buffer = new byte[1024];

        DatagramPacket packetReceived = new DatagramPacket(buffer, buffer.length);
        socket.receive(packetReceived);

        return new String(packetReceived.getData(), 0, packetReceived.getLength());
    }
}