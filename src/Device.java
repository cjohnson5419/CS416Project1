public class Device {
    private String id;
    private String realIP;
    private int realPort;
    private String virtualIP;
    private String gateway;

    public Device(String id, String realIP, int realPort, String virtualIP, String gateway) {
        this.id = id;
        this.realIP = realIP;
        this.realPort = realPort;
        this.virtualIP = virtualIP;
        this.gateway = gateway;
    }

    public String getId() {
        return id;
    }

    public String getRealIP() {
        return realIP;
    }

    public int getRealPort() {
        return realPort;
    }

    public String getVirtualIP() {
        return virtualIP;
    }

    public String getGateway() {
        return gateway;
    }
}