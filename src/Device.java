import java.net.InetSocketAddress;

public class Device {
    private String id;           // Device ID ( "A", "S1")
    private String address;      // IP address
    private int port;


    public Device(String id, String address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }


    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}