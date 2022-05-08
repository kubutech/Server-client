package kubutech.javaProjects.serverClient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Window {
    
    JFrame frame, login;
    JPasswordField pass;
    JPanel panelTop, panelBottom;
    JPanel connect;
    JButton send, download, upload, sendLogin;
    JScrollPane sp;
    JTextArea status;
    JTextField input, connectInput, connectStatus;
    JTable table;
    myTableModel model;
    JFileChooser fc_upload, fc_download;
    JMenuBar menubar;
    JMenu menu;
    JMenuItem refresh, restart;

    public Window() {
        frame = new JFrame("Server client");
        login = new JFrame("Connect");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelTop = new JPanel(new BorderLayout());
        panelBottom = new JPanel(new BorderLayout());
        connect = new JPanel(new BorderLayout());
        refresh = new JMenuItem("Refresh List");
        download = new JButton("Download selection");
        send = new JButton("Send command");
        upload = new JButton("Upload");
        sendLogin = new JButton("Connect");
        restart = new JMenuItem("Restart connection");
        status = new JTextArea();
        input = new JTextField();
        connectInput = new JTextField();
        connectStatus = new JTextField();
        model = new myTableModel();
        table = new JTable(model);
        fc_upload = new JFileChooser();
        fc_download = new JFileChooser();
        menu = new JMenu("Connection");
        menubar = new JMenuBar();
        pass = new JPasswordField();

        fc_upload.setApproveButtonText("Upload");
        fc_upload.setMultiSelectionEnabled(true);
        fc_download.setApproveButtonText("Download");
        fc_download.setDialogTitle("Select file to download");

        menu.setMnemonic(KeyEvent.VK_A);
        menubar.add(menu);
        refresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        menu.add(refresh);
        restart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        menu.add(restart);

        table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("File Number");
        table.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("File Name");
        table.getTableHeader().getColumnModel().getColumn(2).setHeaderValue("File Size");
        table.setCellSelectionEnabled(true);

        panelTop.add(input);
        panelTop.add(BorderLayout.EAST,send);
        panelTop.add(BorderLayout.NORTH,status);
        panelBottom.add(download);
        panelBottom.add(BorderLayout.EAST,upload);
        connectInput.setVisible(true);
        
        status.setText("Welcome to Server");
        status.setEditable(false);

        connect.add(connectInput);
        connect.add(BorderLayout.EAST, sendLogin);
        connect.add(BorderLayout.NORTH, connectStatus);

        login.add(BorderLayout.NORTH, connect);
        login.setSize(400,100);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        login.getRootPane().setDefaultButton(sendLogin);

        connectStatus.setEditable(false);

        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(BorderLayout.SOUTH, panelBottom);
        frame.getContentPane().add(BorderLayout.NORTH, panelTop);
        sp = new JScrollPane(table);
        frame.add(sp);
        frame.setJMenuBar(menubar);
    }

}

class myTableModel  extends AbstractTableModel {


    private ArrayList<RemoteFile> files = new ArrayList<RemoteFile>();

    public int getColumnCount() {

        return 3;
    }
    
    public int getRowCount() {

        return this.files.size();
    }

    public Object getValueAt(int row, int column) {

        if (column == 1) {
            return files.get(row).name;
        } else if (column == 2) {
            if (files.get(row).size < 512) {
                return files.get(row).size + " B";
            }
            else if (files.get(row).size < 512 * 1024) {
                return String.format("%.1f" ,(float)files.get(row).size / 1024) + " KB";
            }
            else if (files.get(row).size < 512 * 1024 * 1024) {
                return String.format("%.1f" ,(float)files.get(row).size / (1024 * 1024)) + " MB";
            }
            else {
                return String.format("%.1f" ,(float)files.get(row).size / (1024 * 1024 * 1024)) + " GB";
            }
        } else {
            return row + 1;
        }
    }
    
    public void updateList(ArrayList<RemoteFile> files) {
        
        this.files = files;
    }

}


