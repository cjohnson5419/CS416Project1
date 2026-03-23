import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Router {
    public static void main(String[] args) throws Exception {
        String ID = args[0];
        Parser parser = new Parser(ID);
        RoutingTable routingTables = new RoutingTable(parser, ID);

        Map<String, String> forwardingTable;
        Set<Map.Entry<String, String>> forwardingTableSet;
        int portNum = parser.getPortNum();
        Set<Map.Entry<String, InetSocketAddress>> neighbors = parser.getNeighbors().entrySet();
        DatagramSocket socket = new DatagramSocket(portNum);

        System.out.println("Router " + ID + " started on port " + portNum);
        System.out.println("Neighbors:\n");
        for (Map.Entry<String, InetSocketAddress> neighbor : neighbors) {
            System.out.println(neighbor.getKey() + ": " + neighbor.getValue());
        }
        System.out.println();

        new Thread(() -> {
            try {
                while (true) {
                    String DVTable = routingTables.getDVTable();

                    for (Map.Entry<String, InetSocketAddress> neighbor : neighbors) {
                        if (neighbor.getKey().contains("S")) {
                            continue;
                        }

                        InetSocketAddress neighborAddress = neighbor.getValue();
                        String message =  "DV" + "/" + DVTable;
                        String DVFrame = ID + ":" + neighbor.getKey() + ":0.0.0.0:0.0.0.0:" + message;

                        sendPacket(socket, DVFrame, neighborAddress);
                    }

                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        while (true) {
            DatagramPacket packetReceived = receivePacket(socket);
            String originalFrame = new String(packetReceived.getData(), 0, packetReceived.getLength());

            String[] info = originalFrame.split(":", 5);
            String srcMAC = info[0];
            String dstMAC = info[1];
            String srcIP = info[2];
            String dstIP = info[3];
            String message = info[4];

            if (!dstMAC.equals(ID)) {
                System.out.println("Error: IP mismatch!");
                continue;
            }

            if (message.startsWith("DV/")) {
                routingTables.updateForwardingTable(srcMAC, message);
                continue;
            }

            System.out.println("Frame:\n");
            printFrame(srcMAC, dstMAC, srcIP, dstIP, message);

            srcMAC = ID;
            InetSocketAddress receiver = null;
            String[] dstIPInfo = dstIP.split("\\.");

            forwardingTable = routingTables.getForwardingTable();
            forwardingTableSet = forwardingTable.entrySet();

            for (Map.Entry<String, String> row : forwardingTableSet) {
                if (dstIP.contains(row.getKey())) {
                    String[] value = row.getValue().split("/");
                    dstMAC = value[0].trim();

                    if (dstMAC.contains("S")) {
                        dstMAC = dstIPInfo[1];
                    }

                    receiver = buildReceiver(forwardingTable, row.getKey());
                    break;
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
        String[] nxtPortInfo = forwardingTable.get(nxtIP).split("/")[1].split(":");
        return new InetSocketAddress(nxtPortInfo[0], Integer.parseInt(nxtPortInfo[1]));
    }
}