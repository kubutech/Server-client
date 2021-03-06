package kubutech.javaProjects.serverClient;

import javax.swing.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

public class Client {
    
    private Socket connectSocket;
    private OutputStream out;
    private FileOutputStream fileOut;
    private FileInputStream fileIn;
    private InputStream in;
    private int port = 1999;
    private String IP = " ";
    public boolean isClosed = false;
    ArrayList<RemoteFile> files = new ArrayList<RemoteFile>();
    Window window;

    public Client() {
        this.window = new Window();
        try {
            BufferedReader dataIn = new BufferedReader(new FileReader("IPAddress.txt"));
            window.connectInput.setText(dataIn.readLine());
            dataIn.close();
        } catch (IOException e) {}
        window.connectStatus.setText("Enter IP to connect");
        this.startConnection();
        window.sendLogin.addActionListener(e -> {
            if (e.getActionCommand().equals("Connect")) {
                this.startConnection();
            } else if (e.getActionCommand().equals("Login")) {
                this.login();
            }
        });
    }

    public void startConnection() {
        this.IP = window.connectInput.getText();
        try {
            connectSocket = new Socket(this.IP, this.port);
            connectSocket.setSoTimeout(500);
            try {
                out = connectSocket.getOutputStream();
                in = connectSocket.getInputStream();
            } catch (Exception e) {System.out.println("Error");}
            window.connectStatus.setText("Connected successfully!");
            window.send.setText("Send command");
            window.input.setText("");
            window.connectInput.setText("");
            window.connectStatus.setText("Connected! Enter server password");
            window.sendLogin.setText("Login");
            window.connectInput.setVisible(false);
            window.connect.add(window.pass);

            try {
                BufferedWriter fileOut = new BufferedWriter(new FileWriter("IPAddress.txt"));
                fileOut.write(this.IP);
                fileOut.flush();
                fileOut.close();
            } catch (IOException e) {}
        } catch (Exception e) {
            window.connectStatus.setText("Couldn't connect to Server, enter IP address");
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

            String reply = new String(recvbuf,"UTF-16LE");

            this.files = getFiles(reply);

        } catch (Exception e){
            this.window.status.setText("Connection with server interrupted- restart application to connect again!");
        }

        window.model.updateList(this.files);

        window.table.revalidate();
        window.table.repaint();

    }

    ArrayList<RemoteFile> getFiles(String message) {
        
        ArrayList<RemoteFile> files = new ArrayList<RemoteFile>();
        String[] rows = message.split("\n");
        for(String file : rows) {
            if (!file.contains("\t")) {
                continue;
            }
            String [] column = file.split("\t");
            files.add(new RemoteFile(column[0], Long.parseLong(column[1])));
        }
        return files;
    }

    public void sendCommand() {
        String command = window.input.getText();
        byte[] sendbuf = command.getBytes();
        try {
            out.write(sendbuf);
            out.flush();
        } catch (Exception e) {
        }
    }

    /*@Deprecated
    public void testConnection() {
        System.out.println(connectSocket.isConnected());
        String message = "????ka??";
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

    public void downloadFile (RemoteFile file) throws IOException {

        byte[] sendbuf = ("GET " + file.name).getBytes(StandardCharsets.UTF_16LE);
        byte[] recvbuf = new byte[2048];

        window.fc_download.setSelectedFile(new File(file.name));
        int result = window.fc_download.showSaveDialog(window.frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            out.write(sendbuf);
            out.flush();

            in.read(recvbuf);
            String reply = new String(recvbuf, "UTF-16LE");

            String error = reply.substring(0, 17);

            if (!error.equals("File doesnt exist")) {
                fileOut = new FileOutputStream(window.fc_download.getSelectedFile());
                int recvSize = 0;
                while (true) {
                    Arrays.fill(recvbuf, (byte) 0);
                    try {
                        recvSize = in.read(recvbuf, 0, 2048);
                    } catch (IOException e) {
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

    public void uploadFile(File file) throws Exception {
        fileIn = new FileInputStream(file);
        byte[] sendbuf = new byte[2048];
        sendbuf = ("PUSH " + file.getName()).getBytes(StandardCharsets.UTF_16LE);
        out.write(sendbuf);
        out.flush();
        int readSize;
        while (true) {
            Arrays.fill(sendbuf, (byte) 0);
            readSize = fileIn.read(sendbuf);
            if (readSize <= 0) {
                break;
            }
            out.write(sendbuf);
            out.flush();
        }
        out.write(0);
        fileIn.close();
        this.updateList();
    }

    public void login() {
        String login = new String(window.pass.getPassword());
        System.out.println(login);
        byte sendbuf[] = login.getBytes();
        byte recvbuf[] = new byte[32];
        try {
            out.write(sendbuf);
            out.flush();
        } catch (Exception e) {
        }
        try {
            in.read(recvbuf);
            String response = new String(recvbuf).substring(0,9);
            if (response.equals("Connected")) {
                this.updateList();
                window.refresh.addActionListener(a -> {
                    this.updateList();
                });

                window.download.addActionListener(a -> {
                    RemoteFile dFile;
                    try {
                        dFile = this.files.get(window.table.getSelectedRow());
                        this.downloadFile(dFile);
                        window.status.setText("Downloaded file " + dFile.name);
                    } catch (IOException e) {
                        if (e.getMessage().equals("Read timed out")) {
                            dFile = this.files.get(window.table.getSelectedRow());
                            window.status.setText("Downladed file " + dFile.name);
                        } else {
                            window.status.setText(e.getMessage());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        window.status.setText("Select file to download!");
                    }
                });

                window.restart.addActionListener(a -> {
                    if (!this.isClosed) {
                        this.isClosed = true;
                    }
                });

                window.upload.addActionListener(a -> {
                    window.fc_download.setMultiSelectionEnabled(true);
                    int result = window.fc_upload.showOpenDialog(window.frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File[] selectedFiles = window.fc_upload.getSelectedFiles();
                        for (File selectedFile:selectedFiles) {
                            try {
                                uploadFile(selectedFile);
                                window.status.setText("Uploaded file " + selectedFile.getName());
                            } catch (Exception e) {
                                window.status.setText("Couldn't upload file " + selectedFile.getName());
                            }
                        }
                    }
                });

                window.send.addActionListener(a -> {
                    this.sendCommand();
                });

                window.login.setVisible(false);
                window.frame.setVisible(true);
            } else if (response.equals("Wrong log")) {
                window.connectStatus.setText("Wrong password, try again!");
            }
        } catch (Exception e) {}

    }

}

class RemoteFile {

    String name;
    long size;

    public RemoteFile(String name, long size) {
        this.name = name;
        this.size = size;
    }
}
