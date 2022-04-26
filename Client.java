import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

public class Client {
    
    private Socket connectSocket;
    private OutputStream out;
    private FileOutputStream fileOut;
    private InputStream in;
    private int port = 1999;
    private String IP = " ";
    public boolean isClosed = false;
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
        this.startConnection();
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
            connectSocket.setSoTimeout(500);
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
                File dFile = this.files.get(window.table.getSelectedRow());
                try {
                    window.status.setText("Downloading file " + dFile.name);
                    this.downloadFile(dFile);
                    window.status.setText("Downladed file " + dFile.name);
                } catch (IOException f) {
                    if (f.getMessage().equals("Read timed out")) {
                        window.status.setText("Downladed file " + dFile.name);
                    }
                } catch (IndexOutOfBoundsException f) {
                    window.status.setText("Select file to download!");
                }
            });

            window.restart.addActionListener(e -> {
                if (!this.isClosed) {
                    this.isClosed = true;
                }
            });

            window.send.setText("Send command");
            window.input.setText("");
            window.sp.revalidate();
            window.sp.repaint();
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

        window.frame.dispose();
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

        window.sp.revalidate();
        window.sp.repaint();

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

    /*@Deprecated
    public void testConnection() {
        System.out.println(connectSocket.isConnected());
        String message = "Łąkaӽ";
        byte[] sendbuf = message.getBytes(Charset.forName("UTF_16LE"));
        try {
            out.write(sendbuf);
            out.flush();
        }catch (Exception e){}
        try {
            byte[] recvbuf = in.read();
            for (int i = 0; i < recvbuf.length; i++) {
                System.out.println(recvbuf[i]);
            }
        } catch (IOException e) {}
        System.out.println(connectSocket.isConnected());
    }*/

    public void downloadFile (File file) throws IOException {

        byte[] sendbuf = ("GET " + file.name).getBytes(StandardCharsets.UTF_16LE);
        byte[] recvbuf = new byte[2048];
        
        out.write(sendbuf);
        out.flush();

        in.read(recvbuf);
        String reply = new String(recvbuf,"UTF-16LE");

        String error = reply.substring(0,17);

        if (!error.equals("File doesnt exist")) {
            fileOut = new FileOutputStream(file.name);
            System.out.println(reply);
            int recvSize = 0;
            while (true) {
                Arrays.fill((byte[])recvbuf,(byte)0);
                try {
                    recvSize = in.read(recvbuf, 0, 2048);
                } catch(IOException e) {
                    System.out.println("Receive timeout!");
                    break;
                }
                if (recvSize == 0 || recvSize == 1 || recvSize == -1) {
                    break;
                }
                fileOut.write(recvbuf, 0, recvSize);
                fileOut.flush();
            }
            fileOut.close();
        } else {
            throw new IOException("File not available on server");
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
