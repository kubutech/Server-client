import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class Client {
    
    private Socket connectSocket;
    private OutputStream out;
    private InputStream in;
    private int port = 1999;
    byte[] recvbuf = new byte[2048];

    public Client() {

    }

    public int startConnection(String ip) {
        try {
            connectSocket = new Socket(ip, this.port);
        } catch (Exception e){
            System.out.println("Couldn't connect to Server on provided addrress");
            return 1;
        }
        try {
            out = connectSocket.getOutputStream();
            in = connectSocket.getInputStream();
        } catch (Exception e) {System.out.println("Error");}
        System.out.println(connectSocket.isConnected());
        return 0;
    }

    public void stopConnection() {
        try {
            out.close();
        } catch (Exception e) {
            System.out.println("Error out");
            System.out.println(e.getMessage());
        }

        try {
            in.close();
        } catch (Exception e) {
            System.out.println("Error in");
            System.out.println(e.getMessage());
        }

        try {
            connectSocket.close();
        } catch (Exception e) {
            System.out.println("Error socket");
        }
    }

    public void ListFiles() {
        System.out.println(connectSocket.isConnected());
        String message = "";
        byte[] sendbuf = message.getBytes(StandardCharsets.UTF_16LE);
        int size = sendbuf.length;
        int[] unsignedByte = new int[sendbuf.length];
        for (int i = 0; i < sendbuf.length; i++) {
            unsignedByte[i] = sendbuf[i] & 0xff;
            System.out.println(unsignedByte[i]);
        }
        try {
        out.write(sendbuf);
        out.flush();
        }catch (Exception e){}
        
        System.out.println(size);
        System.out.println(connectSocket.isConnected());
    }
}
