import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.Thread;
import java.sql.*;
import java.util.*;
import java.util.Observer;
import java.util.Properties;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class NoteDisplay extends JPanel {

	boolean DEBUG = true;

	// Data for Table
	private String[] columnNames = {
		"ID",
		"Title",
		"Tag",
		"First Line"
	};
	private Object[][] data = {};
	private Dimension dim;

	NoteDatabase noteDB;

	// private List<JTable> tables = new ArrayList<JTable>();
	private JTable table = new JTable();
	private List<JComponent> tableContainers = new ArrayList<JComponent>();
	private int tableCount = 0;

	// -----------------------------------------------------------------------

	/** Instantiate a table within a pane with the specified dimension.
	 */
	public NoteDisplay(Dimension dim, NoteDatabase db)
	{
		// Set dimenstion of the table
		this.dim = dim;
		this.noteDB = db;

		// Add Title
		Box box1 = Box.createHorizontalBox();
		add(box1, BorderLayout.NORTH);

		addTable();
		update(db.getAllNotes());

		//
		// debug
		//
		// Print contents of the table on click
		//
		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					printDebugData(table);
				}
			});
		}
	}

	void setDimension(Dimension dim)
	{
		this.dim = dim;
	}

	private void addTable()
	{
		// Create new table for the new scope
		table.setModel( new DefaultTableModel(data,columnNames) );
		table.setPreferredScrollableViewportSize(dim);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
		tableContainers.add(scrollPane);

        //Add the scroll pane to this panel.
        add(scrollPane, BorderLayout.SOUTH);
		revalidate();
		repaint();
	}

	public void update(ArrayList<String[]> noteList) 
	{
		// clear table
		table.setModel(new DefaultTableModel(data, columnNames));

		// 
		System.out.println("================ Notes");
		DefaultTableModel tmodel = (DefaultTableModel) table.getModel();
		for ( String[] noteInfo : noteList ) {
			final Object[][] row = { { 
				noteInfo[0],
				noteInfo[1],
				noteInfo[2],
				noteInfo[3]
			} }; 
			
			tmodel.addRow(row[0]);
			System.out.println(noteInfo[1]);
		}

		revalidate();
		repaint();
	}


	// -----------------------------------------------------------------------
	// Debugging
	
	// Print all the data in the table to stdout
	private void printDebugData(JTable table) {

		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

}
