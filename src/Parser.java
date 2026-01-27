public class Parser {
    private String ID;
    private int portNum;
    private String address;


    public Parser(String ID) {
        this.ID = ID;
    }

    private void fetchInfo() {

    }

    public String getAddress() {
        return address;
    }

    public int getPortNum() {
        return portNum;
    }
}
