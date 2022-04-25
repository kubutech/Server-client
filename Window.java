import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.util.*;
import java.time.*;


public class Window {
    
    JFrame frame;
    JPanel panel;
    JButton refresh;
    JButton send;
    JTextArea status;
    JTextField input;
    JTable table;
    myTableModel model;


    public Window() {
        frame = new JFrame("Server client"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel(new BorderLayout());
        refresh = new JButton("Refresh List");
        status = new JTextArea();
        input = new JTextField();
        send = new JButton("Send command");
        model = new myTableModel();
        table = new JTable(model);

        table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("File Number");
        table.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("File Name");
        table.getTableHeader().getColumnModel().getColumn(2).setHeaderValue("File Size");

        panel.add(input);
        panel.add(BorderLayout.EAST,send);
        panel.add(BorderLayout.AFTER_LAST_LINE,refresh);
        
        status.setText("Welcome to Server");
        status.setEditable(false);

        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, status);
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
        
    }

}

class myTableModel  extends AbstractTableModel {


    private ArrayList<File> files = new ArrayList<File>();

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
    
    public void updateList(ArrayList<File> files) {
        
        this.files = files;
    }
}
