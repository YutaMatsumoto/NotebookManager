import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.FileWriter.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;

import java.sql.*;
import java.sql.*;
import java.util.Properties;

class resourceLoader{
	static URL get(String name){
		return resourceLoader.class.getResource(name);
	}
}

public class GuiMain extends JPanel implements UndoableEditListener {

	NoteDisplay noteDisplay;
	private static final long serialVersionUID = 1L;
	private NoteDatabase noteDB = new NoteDatabase();
	private JMenuItem undo;
	private JMenuItem redo;
	private JButton jb_undo, jb_redo;
	private UndoManager undoredo;

	// -----------------------------------------------------------------------
	// [ ] notebook display
	public GuiMain()
	{
		Editor editor = new Editor(25,25) ;

		// Save
		SaveAction saveAction = new SaveAction(editor) ;
		JButton saveButton = new JButton(saveAction);
		saveButton.setPreferredSize(new Dimension(50, 50));

		// Create
		CreateNote createAction = new CreateNote(editor);
		JButton createButton = new JButton(createAction);
		createButton.setPreferredSize(new Dimension(50, 50));

		// show notes
		noteDisplay = new NoteDisplay(new Dimension(300,50), noteDB );
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, noteDisplay, editor);

		// Add 
		// add(editor);
		// add(noteDisplay);
		add(sp);
		add(saveButton);
		add(createButton);
	}

	private static void createAndShowGUI()
	{
		// Create the container	
		JFrame frame = new JFrame("Database Project", null);

		// Quit the application when this frame is closed:
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and add the content
		GuiMain panel = new GuiMain();
		frame.setLayout(new BorderLayout());
		frame.add(panel);

		// Display the window
		frame.pack(); // adapt the frame size to its content
		frame.setLocation(300, 20); // in pixels
		frame.setVisible(true); // Now the frame will appear on screen

		// Quit the application when this frame is closed:
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//add a window listener
		frame.addWindowListener( 
			new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					System.exit(0);
				}
			}
		);
	}

	public static void main(String args[])
	{

		// Set the look and feel to that of the system
		try { 
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); 
		}
		catch ( Exception e ) { 
			System.err.println( e ); 
		}

		try { 
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void close()
	{
		// noteDBconnection.close();
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent edit) 
	{
		undoredo.addEdit(edit.getEdit());
	}

	// An action to simply exit the app
	@SuppressWarnings("serial")
	class ExitAction extends AbstractAction 
	{

		public ExitAction() {
			super("Exit");
		}

		public void actionPerformed(ActionEvent ev) {
				System.exit(0);
		}

	}

	//  
	// SQL Actions
	//
	// An action that saves the document to a file : Supports Save and SaveAs actions 
	@SuppressWarnings("serial")
	class SaveAction extends AbstractAction {
		JTextComponent textComponent;
		boolean saveToCurrentFile;
		Editor editor;

		public SaveAction(Editor editor){
			super("Save Note", new ImageIcon(resourceLoader.get("Images/save.png")));
			this.editor = editor;	
		}

		public void actionPerformed(ActionEvent ev) {
			System.out.println("SaveAction Action Called");
		}

	}

	@SuppressWarnings("serial")
	class CreateNote extends AbstractAction {

		Editor editor;
		String noteTitle;

		public CreateNote(Editor editor){
			super("Create Note", new ImageIcon(resourceLoader.get("Images/copy.png")));
			this.editor = editor;	
		}

		public void actionPerformed(ActionEvent ev) {

			final NoteDatabase noteDB = new NoteDatabase();

			System.out.println("CreateNote Action Called");

			//Create and set up the window.
			final JFrame frame = new JFrame("CreateNote");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
			//Create and set up the content pane.
			// frame.setOpaque(true); //content panes must be opaque
	
			//Display the window.
			frame.pack();
			frame.setVisible(true);
			frame.setSize(new Dimension(300, 100));
			frame.setVisible(true);
			final JTextField noteTitleInput = new JTextField("Enter Note Title");

			//
			// note input dialog action listener 
			//
			noteTitleInput.addActionListener(  
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Document doc = noteTitleInput.getDocument();	
						try {
							// Get Title
							noteTitle = doc.getText(0, doc.getLength());
							// Remove diaglog
							frame.dispose();
							editor.clear();

							System.out.println("NoteTitleSet: <" + noteTitle + ">");

							if ( (noteTitle != null) && ! noteTitle.equals("")  ) {
								// Create Note and Update
								if ( noteDB.createNote( noteTitle, "root" ) ) {
									noteDisplay.update( noteDB.getAllNotes() );
									noteDB.close();
									System.out.println("Note Created <"+noteTitle+">");
								} else {
									System.err.println("Note Not Created <"+noteTitle+">");
								}
							}
							else  {
								System.err.println("Note Title is invalid: "+noteTitle);
							}

						} catch (BadLocationException ex) {
							System.out.println("CreatingNote : GettingNoteName : BadLocationException");
							ex.printStackTrace();
						}
					}
				}
			);
			frame.add(noteTitleInput);
		}

	}

	@SuppressWarnings("serial")
	class ShowNotes extends AbstractAction {
		NoteDisplay display;

		public void actionPerformed(ActionEvent ev) 
		{
		}

		
		public ShowNotes(NoteDisplay display/**/)
		{
			this.display = display;
		}

	}

	// public class OpenAction extends AbstractAction {
	// }

}
