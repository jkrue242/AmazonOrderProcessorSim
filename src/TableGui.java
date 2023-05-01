import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * This class handles the gui
 */
public class TableGui {

    /**
     * Constructor
     */
    public TableGui()
    {
        // create gui
        gui = new JFrame();
        gui.setSize(1580,500);
        gui.setTitle("Orders Delivered");

        // columns for table
        String[] columns = {"address" , "city", "state", "zip", "name", "item", "category", "shipping center", "shipping section", "truck"};

        // make table
        table = new DefaultTableModel(null, columns);
        JTable order_table = new JTable(table);

        // make scrollable
        JScrollPane scrollPane = new JScrollPane(order_table);
        gui.add(scrollPane);

        // app exits when gui closed
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }

    /**
     * Adds row to table
     * @param row row data to be added
     */
    public void addRow(String[] row)
    {
        table.addRow(row);
    }

    private JFrame gui;
    DefaultTableModel table;
}
