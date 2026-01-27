public class Port {
    private String portName;
    private String neighborId;
    private String neighborAddress;
    private int neighborPort;

    public Port(Device neighborDevice) {
        this.neighborId = neighborDevice.getId();
        this.neighborAddress = neighborDevice.getAddress();
        this.neighborPort = neighborDevice.getPort();
        this.portName = neighborDevice.getAddress() + ":" + neighborDevice.getPort();
    }

    // Getters
    public String getPortName() {
        return portName;
    }

    public String getNeighborId() {
        return neighborId;
    }

    public String getNeighborAddress() {
        return neighborAddress;
    }

    public int getNeighborPort() {
        return neighborPort;
    }

    public String getFullAddress() {
        return neighborAddress + ":" + neighborPort;
    }

    @Override
    public String toString() {
        return String.format("Port[name=%s, neighborId=%s]", portName, neighborId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Port)) return false;
        Port other = (Port) obj;
        return this.portName.equals(other.portName);
    }

    @Override
    public int hashCode() {
        return portName.hashCode();
    }
}