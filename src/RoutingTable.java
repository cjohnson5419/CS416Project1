import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoutingTable {
    private Map<String, InetSocketAddress> neighbors;
    private Map<String, String> forwardingTable = new HashMap<>();
    private Map<String, Integer> DVTable = new HashMap<>();

    private final int distance = 1;
    private Parser parser;
    private String ID;
    private String[] virtualIPInfos;

    public RoutingTable(Parser parser, String ID) {
        this.ID = ID;
        this.parser = parser;
        this.neighbors = parser.getNeighbors();
        setIniTables();
    }

    private void setIniTables() {
        String destinations = null;
        Set<Map.Entry<String, InetSocketAddress>> neighborSet = neighbors.entrySet();
        virtualIPInfos = parser.getDevice(ID).getVirtualIP().split(",");

        for (Map.Entry<String, InetSocketAddress> neighbor : neighborSet) {
            String neighborVIPInfo = parser.getDevice(neighbor.getKey()).getVirtualIP();

            for (String virtualIPInfo : virtualIPInfos) {
                String subnet = virtualIPInfo.split("\\.")[0].trim();

                if (neighborVIPInfo == null || neighborVIPInfo.contains(subnet)) {
                    destinations = subnet;
                    break;
                }
            }

            if (destinations != null) {
                forwardingTable.putIfAbsent(destinations, neighbor.getKey() + neighbor.getValue());
                DVTable.putIfAbsent(destinations, distance);
            }
        }
    }

    public String getDVTable() {
        return DVTable.toString();
    }

    public Map getForwardingTable() {
        return forwardingTable;
    }

    public void updateForwardingTable(String srcMAC, String message) {
        System.out.println(DVTable);
        String neighborDVTableInfo = message.substring(3);
        String[] neighborDVTable = neighborDVTableInfo.substring(1, neighborDVTableInfo.length() - 1).trim().split(",");

        for (String neighborRoute : neighborDVTable) {
            String neighborSubnet = neighborRoute.split("=")[0].trim();
            int neighborDistance = Integer.parseInt(neighborRoute.split("=")[1].trim());
            int newCost = neighborDistance + 1;

            if (!DVTable.containsKey(neighborSubnet) || DVTable.get(neighborSubnet) > newCost) {
                DVTable.put(neighborSubnet, newCost);
                forwardingTable.put(neighborSubnet, srcMAC + neighbors.get(srcMAC));
            }
        }


    }
}
