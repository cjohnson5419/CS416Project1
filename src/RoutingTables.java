import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RoutingTables {

    String[] net;
    Map<String, String> fowardingTable = new HashMap<>();
    Map<String, InetSocketAddress> neighbors = new HashMap<>();

    public RoutingTables(Parser parser) {
        neighbors = parser.getNeighbors();
        String[] netID = parser.getVirtualIP().split(",");
        for(int i = 0; i < netID.length; i++) {
            System.out.println(netID[i]);
        }

    }

    public void printTable() {
        System.out.println(net[0]);
    }
}
