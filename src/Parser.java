import java.io.File;
import java.util.*;

public class Parser {
    public static void main(String[] args) {
        new Parser("s3");

    }
    private String ID;
    private int portNum;
    private String address;


    public Parser(String ID) {
        try {
            File config = new File("C:\\Users\\fashi\\Documents\\CS416Project1\\src\\config_file");
            Scanner scanner = new Scanner(config);
            Map<String, String[]> table = new HashMap<>();

            while (scanner.hasNextLine()) {
                if (scanner.nextLine().equals(ID)) {
                    this.ID = ID;
                    this.portNum = Integer.parseInt(scanner.nextLine().split(": ")[1]);
                    this.address= scanner.nextLine().split(":")[1];
                    table.put(ID, new String[]{Integer.toString(this.portNum), this.address});
                    System.out.println(Arrays.toString(table.get("s2")));
                    break;
                }
                else {
                    System.out.println("lol");

                }
            }

        }
        catch (Exception e){ e.printStackTrace();
        }
    }



    private void fetchInfo() {
        try {
            File config = new File("C:\\Users\\fashi\\Documents\\CS416Project1\\src\\config_file");
            Scanner scanner = new Scanner(config);



            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.equals("Links")) break;
                if (line.isEmpty()) continue;

                String nodeId = this.ID;
                String portNum = this.ID + ":" + line.split(":")[0];
                String address = scanner.nextLine().split(":")[1];



            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAddress() {
        return address;
    }

    public int getPortNum() {
        return portNum;
    }
}
