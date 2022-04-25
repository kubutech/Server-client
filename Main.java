

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.startConnection("192.168.8.139");
        client.listFiles();
        client.stopConnection();
    }
}
