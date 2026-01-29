import java.io.File;
import java.net.InetSocketAddress;
import java.util.*;

public class Parser {
    private String specificID;
    private String ID;
    private int portNum;
    private String address;
    private ArrayList<InetSocketAddress> neighbors = new ArrayList<>();
    private static Map<String, Device> devices = new HashMap<>();


    public Parser(String specificID) {
        this.specificID = specificID;
        fetchInfo();
    }

    /* Parse the config file's info */
    private void fetchInfo() {
        try {
            File config = new File("src\\config_file");
            Scanner scanner = new Scanner(config);
            String currLine;

            while (scanner.hasNextLine()) {
                currLine = scanner.nextLine();
                if (currLine.isBlank()) {
                    while (scanner.hasNextLine()) {
                        currLine = scanner.nextLine();
                        parseLink(scanner, currLine);
                    }
                } else if (currLine.length() <= 2) {
                    parseDevice(scanner, currLine);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Fetch the info of all devices and store them into Map: devices */
    private void parseDevice(Scanner scanner, String currLine) {
        this.ID = currLine;
        this.portNum = Integer.parseInt(scanner.nextLine().split(": ")[1]);
        this.address= scanner.nextLine().split(": ")[1];

        Device device = new Device(ID, address, portNum);
        devices.putIfAbsent(ID, device);
    }

    /* Find the link relation of specific device and store relevant device's info into ArrayList: neighbors */
    private void parseLink(Scanner scanner, String currLine) {
        String neighborID;
        int neighborPort;
        String neighborAdr;
        InetSocketAddress neighbor;
        String[] parts = currLine.split(":");

        if (!parts[0].equals("Links")) {
            if (parts[0].equals(specificID) || parts[1].equals(specificID)) {
                neighborID = parts[1].equals(specificID) ? parts[0] : parts[1];
                neighborPort = devices.get(neighborID).getPort();
                neighborAdr = devices.get(neighborID).getAddress();

                neighbor = new InetSocketAddress(neighborAdr, neighborPort);
                neighbors.add(neighbor);
            }
        }
    }

    public ArrayList getNeighbors() {
        return neighbors;
    }

    public int getPortNum() {
        return devices.get(specificID).getPort();
    }
}
