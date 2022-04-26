import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.*;
import javax.swing.*;
import java.util.*;

public class Client {
    
    private Socket connectSocket;
    private OutputStream out;
    private FileOutputStream fileOut;
    private InputStream in;
    private int port = 1999;
    private String IP = " ";
    ArrayList<File> files = new ArrayList<File>();
    Window window;

    public Client() {
        this.window = new Window();
        try {
            BufferedReader dataIn = new BufferedReader(new FileReader("IPAddress.txt"));
            window.input.setText(dataIn.readLine());
            dataIn.close();
        } catch (IOException e) {}
        window.send.setText("Connect");
        window.status.setText("Enter IP to connect");
        window.send.addActionListener(e -> {
            if (e.getActionCommand().equals("Connect")) {
                this.startConnection();
            } else if (e.getActionCommand().equals("Send command")) {
                this.sendCommand();
            }
        });
    }

    public void startConnection() {
        this.IP = window.input.getText();
        try {
            connectSocket = new Socket(this.IP, this.port);
            try {
                out = connectSocket.getOutputStream();
                in = connectSocket.getInputStream();
            } catch (Exception e) {System.out.println("Error");}
            window.status.setText("Connected succefully!");
            this.updateList();
            window.refresh.addActionListener(e -> {
                this.updateList();
            });
            window.download.addActionListener(e -> {
                try {
                    this.downloadFile(this.files.get(window.table.getSelectedRow()));
                    window.status.setText("Downladed file!");
                } catch (IOException f) {
                    window.status.setText("couldn't download file!");
                }
            });
            window.send.setText("Send command");
            try {
                BufferedWriter fileOut = new BufferedWriter(new FileWriter("IPAddress.txt"));
                fileOut.write(this.IP);
                fileOut.flush();
                fileOut.close();
            } catch (IOException e) {}
        } catch (Exception e) {
            window.status.setText("Couldn't connect to Server on provided addrress");
        }
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

    public void updateList() {
        this.files.clear();
        String message = "LIST FILES";
        byte[] sendbuf = message.getBytes(StandardCharsets.UTF_16LE);
        byte[] recvbuf = new byte[2048];
        try {
            out.write(sendbuf);
            out.flush();
            in.read(recvbuf);
            
            System.out.println(recvbuf.length);

            String reply = new String(recvbuf,"UTF-16LE");

            this.files = getFiles(reply);

        } catch (Exception e){
            this.window.status.setText("Connection with server interrupted- restart application to connect again!");
        }
        
        window.model.updateList(this.files);

        window.table.repaint();

    }

    ArrayList<File> getFiles(String message) {
        
        ArrayList<File> files = new ArrayList<File>();
        String[] rows = message.split("\n");
        for(String file : rows) {
            if (!file.contains("\t")) {
                continue;
            }
            String [] column = file.split("\t");
            files.add(new File(column[0], Long.parseLong(column[1])));
        }
        System.out.println(files.size());
        return files;
    }

    public void sendCommand() {
        String command = this.window.input.getText();
        byte[] sendbuf = command.getBytes();
        try {
            out.write(sendbuf);
            out.flush();
        } catch (Exception e) {}
    }

    @Deprecated
    public void testConnection() {
        System.out.println(connectSocket.isConnected());
        String message = "Łąkaӽ";
        byte[] sendbuf = message.getBytes(Charset.forName("UTF_16LE"));
        try {
            out.write(sendbuf);
            out.flush();
        }catch (Exception e){}
        try {
            byte[] recvbuf = in.readAllBytes();
            for (int i = 0; i < recvbuf.length; i++) {
                System.out.println(recvbuf[i]);
            }
        } catch (IOException e) {}
        System.out.println(connectSocket.isConnected());
    }

    public void downloadFile (File file) throws IOException {

        byte[] sendbuf = ("GET " + file.name).getBytes(StandardCharsets.UTF_16LE);
        byte[] recvbuf = new byte[2048];
        
        out.write(sendbuf);
        out.flush();

        in.read(recvbuf);
        String reply = new String(recvbuf,"UTF-16LE");
        if (reply.equals("Sending FILE " + file.name)) {
            fileOut = new FileOutputStream(file.name);
            System.out.println(reply);
            int recvSize;
            while (true) {
                Arrays.fill((byte[])recvbuf,(byte)0);
                recvSize = in.read(recvbuf);
                if (recvSize == 0 || recvSize == 1 || recvSize == -1) {
                    break;
                }
                fileOut.write(recvbuf);
                fileOut.flush();
            }
            fileOut.close();
        }

    }

}

class File {

    String name;
    long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }
}
