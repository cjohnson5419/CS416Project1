public class Device {
    private String id;           // Device ID ( "A", "S1")
    private String address;      // IP address
    private int port;
    private String deviceType;   // "HOST" or "SWITCH"


    public Device(String id, String address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.deviceType = id.startsWith("S") ? "SWITCH" : "HOST";
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public boolean isSwitch() {
        return deviceType.equals("SWITCH");
    }

    public boolean isHost() {
        return deviceType.equals("HOST");
    }

    public String getNetworkAddress() {
        return address + ":" + port;
    }

    @Override
    public String toString() {
        return String.format("Device[id=%s, type=%s, address=%s, port=%d]",
                id, deviceType, address, port);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Device)) return false;
        Device other = (Device) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}