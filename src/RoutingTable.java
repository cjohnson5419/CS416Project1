import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoutingTable {
    private Map<String, String> forwardingTable = new HashMap<>();
    private Parser parser;
    private String ID;

    public RoutingTable(Parser parser, String ID) {
        this.ID = ID;
        this.parser = parser;
        getTable();
    }

    private void getTable() {
        String destinations = null;
        Set<Map.Entry<String, InetSocketAddress>> neighbors = parser.getNeighbors().entrySet();
        String[] virtualIPInfo = parser.getDevice(ID).getVirtualIP().split(",");

        for (Map.Entry<String, InetSocketAddress> neighbor : neighbors) {
            String neighborVIPInfo = parser.getDevice(neighbor.getKey()).getVirtualIP();

            for (int i = 0; i < virtualIPInfo.length; i++) {
                String subNet = virtualIPInfo[i].split("\\.")[0];

                if (neighborVIPInfo == null || neighborVIPInfo.contains(subNet)) {
                    destinations = subNet;
                    break;
                }
            }
            forwardingTable.putIfAbsent(destinations, neighbor.getKey() + neighbor.getValue());
        }
        System.out.println(forwardingTable.toString());
    }
}