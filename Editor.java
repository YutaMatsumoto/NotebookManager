import java.awt.*;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
public class Editor extends JTextArea {

	String name;

	public Editor(int row, int column) {
		super(row, column);
		setLineWrap(true);
		setEditable(true);
		setVisible(true);
		setPreferredSize(new Dimension(row, column));
	}

	public void clear()
	{
		setText("");
	}

}

