import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Router {
    public static void main(String[] args) throws Exception {
        String ID = args[0];
        Parser parser = new Parser(ID);

        Map<String, String> forwardingTable = new HashMap<>();
        int portNum = parser.getPortNum();
        ArrayList<InetSocketAddress> neighbors = parser.getNeighbors();
        DatagramSocket socket = new DatagramSocket(portNum);

        if (ID.equals("R1")) {
            forwardingTable.put("net1", "S1:S1realAdr:5000");
            forwardingTable.put("net2", "R2:R2realAdr:8000");
            forwardingTable.put("net3", "R2:R2realAdr:8000");
        } else if (ID.equals("R2")) {
            forwardingTable.put("net1", "R1:R1realAdr:7000");
            forwardingTable.put("net2", "R1:R1realAdr:7000");
            forwardingTable.put("net3", "S2:S2realAdr:6000");
        }

        System.out.println("Router " + ID + " started on port " + portNum);
        System.out.println("Neighbors:\n");
        for (int i = 0; i < neighbors.size(); i++) {
            System.out.println(neighbors.get(i));
        }
        System.out.println();

        while (true) {
            DatagramPacket packetReceived = receivePacket(socket);
            String originalFrame = new String(packetReceived.getData(), 0, packetReceived.getLength());

            String[] info = originalFrame.split(":");
            String srcMAC = info[0];
            String dstMAC = info[1];
            String srcIP = info[2];
            String dstIP = info[3];
            String message = info[4];

            if (!dstMAC.equals(ID)) {
                continue;
            }

            System.out.println("Frame:\n");
            printFrame(srcMAC, dstMAC, srcIP, dstIP, message);

            srcMAC = ID;
            InetSocketAddress receiver = null;
            String[] dstIPInfo = dstIP.split("\\.");

            if (ID.equals("R1")) {
                 if (dstIP.contains("net1")) {
                     dstMAC = dstIPInfo[1];
                     receiver = buildReceiver(forwardingTable, "net1");
                 } else if (dstIP.contains("net3")) {
                     dstMAC = forwardingTable.get("net3").split(":")[0];
                     receiver = buildReceiver(forwardingTable, "net3");
                 }
            } else if (ID.equals("R2")) {
                if (dstIP.contains("net3")) {
                    dstMAC = dstIPInfo[1];
                    receiver = buildReceiver(forwardingTable, "net3");
                } else if (dstIP.contains("net1")) {
                    dstMAC = forwardingTable.get("net1").split(":")[0];
                    receiver = buildReceiver(forwardingTable, "net1");
                }
            }

            System.out.println("New Frame:");
            printFrame(srcMAC, dstMAC, srcIP, dstIP, message);

            String newFrame = srcMAC + ":" + dstMAC + ":" + srcIP + ":" + dstIP + ":" + message;
            sendPacket(socket, newFrame, receiver);
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

    private static void printFrame(String srcMAC, String dstMAC, String srcIP, String dstIP, String message) {
        System.out.println("srcMAC: " + srcMAC +
                "\ndstMAC: " + dstMAC +
                "\nsrcIP: " + srcIP +
                "\ndstIP: " + dstIP +
                "\nmessage: " + message);
        System.out.println();
    }

    private static InetSocketAddress buildReceiver(Map<String, String> forwardingTable, String nxtIP) {
        String[] nxtPortInfo = forwardingTable.get(nxtIP).split(":");
        return new InetSocketAddress(nxtPortInfo[1], Integer.parseInt(nxtPortInfo[2]));
    }
}