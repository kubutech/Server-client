import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;



public class Window {
    
    JFrame frame;
    JPanel panelTop, panelBottom;
    JButton send, download, upload;
    JScrollPane sp;
    JTextArea status;
    JTextField input;
    JTable table;
    myTableModel model;
    JFileChooser fc;
    JMenuBar menubar;
    JMenu menu;
    JMenuItem refresh, restart;

    public Window() {
        frame = new JFrame("Server client"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelTop = new JPanel(new BorderLayout());
        panelBottom = new JPanel(new BorderLayout());
        refresh = new JMenuItem("Refresh List");
        download = new JButton("Download selection");
        send = new JButton("Send command");
        upload = new JButton("Upload");
        restart = new JMenuItem("Restart connection");
        status = new JTextArea();
        input = new JTextField();
        model = new myTableModel();
        table = new JTable(model);
        fc = new JFileChooser();
        menu = new JMenu("Connection");
        menubar = new JMenuBar();

        fc.setApproveButtonText("Upload");

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
        
        status.setText("Welcome to Server");
        status.setEditable(false);

        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(BorderLayout.SOUTH, panelBottom);
        frame.getContentPane().add(BorderLayout.NORTH, panelTop);
        sp = new JScrollPane(table);
        frame.add(sp);
        frame.setJMenuBar(menubar);
        frame.setVisible(true);

        
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
                return (float)files.get(row).size / 1024 + " KB";
            }
            else if (files.get(row).size < 512 * 1024 * 1024) {
                return (float)files.get(row).size / (1024 * 1024) + " MB";
            }
            else {
                return (float)files.get(row).size / (1024 * 1024 * 1024) + " GB";
            }
        } else {
            return row + 1;
        }
    }
    
    public void updateList(ArrayList<RemoteFile> files) {
        
        this.files = files;
    }
}


