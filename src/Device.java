import java.util.ArrayList;
import java.util.List;

public class Device {
    private String id;
    private String realIP;
    private int realPort;
    private String virtualIP;           // for hosts: e.g. net1.A
    private String gateway;             // for hosts: e.g. net1.R1
    private List<String> virtualPorts;  // for routers: e.g. [net1.R1, net2.R1]

    public Device(String id, String realIP, int realPort) {
        this.id = id;
        this.realIP = realIP;
        this.realPort = realPort;
        this.virtualPorts = new ArrayList<>();
    }

    public String getId()            { return id; }
    public String getRealIP()        { return realIP; }
    public int getRealPort()         { return realPort; }

    public String getVirtualIP()     { return virtualIP; }
    public void setVirtualIP(String v) { this.virtualIP = v; }

    public String getGateway()       { return gateway; }
    public void setGateway(String g) { this.gateway = g; }

    public List<String> getVirtualPorts()          { return virtualPorts; }
    public void setVirtualPorts(List<String> vp)   { this.virtualPorts = vp; }
}