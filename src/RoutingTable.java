import java.net.InetSocketAddress;
import java.util.*;

public class RoutingTable {
    private String ID;
    private Parser parser;
    private Set<String> localNetworks=new HashSet<>();
    private Map<String, Integer> dvTable = new HashMap<>();
    private Map<String, String> nextHopTable = new HashMap<>();
    private Map<String, Map<String, Integer>> neighborDVs = new HashMap<>();

    private static final List<String> ALL_NETWORKS = Arrays.asList(
            "net1", "net2", "net3", "net4", "net5",
            "net6", "net7", "net8", "net9", "net10"
    );

    private final int INFINITY=-1;

    public RoutingTable(Parser parser, String ID) {
        this.ID = ID;
        this.parser = parser;
        initTable();
    }

    private void initTable() {
        Set<Map.Entry<String, InetSocketAddress>> neighbors = parser.getNeighbors().entrySet();
        String[] virtualIPInfo = parser.getDevice(ID).getVirtualIP().split(",");

        for (String vip : virtualIPInfo) {
            String subNet = vip.trim().split("\\.")[0];
            localNetworks.add(subNet);
        }

        for (Map.Entry<String, InetSocketAddress> neighbor : neighbors) {
            String neighborVIPInfo = parser.getDevice(neighbor.getKey()).getVirtualIP();
            for (String vip : virtualIPInfo) {
                String subNet = vip.trim().split("\\.")[0];
                if (neighborVIPInfo != null && neighborVIPInfo.contains(subNet)) {
                    dvTable.put(subNet, 1);
                    nextHopTable.put(subNet, neighbor.getKey());
                }
            }
        }
        for (String net : ALL_NETWORKS) {
            if (!dvTable.containsKey(net)) {
                if (localNetworks.contains(net)) {
                    dvTable.put(net, 1);
                    nextHopTable.put(net, "local");
                } else {
                    dvTable.put(net, INFINITY);
                }
            }
        }

        System.out.println("Initial DV table: " + dvTable);
        System.out.println("Initial Forwarding table: " + nextHopTable);
    }

}