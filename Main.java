
public class Main {

    static Client client;
    public static void main(String[] args) {

        client = new Client();

        while (true) {
            if (client.isClosed) {
                client.stopConnection();
                client = new Client();
            }
            try {
                Thread.sleep(2000);
                client.updateList();
            } catch (Exception e) {}
        }
        
    
    }
}
