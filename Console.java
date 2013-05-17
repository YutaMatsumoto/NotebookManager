import javax.swing.*;
import javax.swing.text.*;

@SuppressWarnings("serial")
public class Console extends JTextArea {

	public Console() { 
		super(); 
		this.setFont(UIManager.getDefaults().getFont("TextField.font"));
		setEditable(false);
	}

	/*
	public void update(CitrinObservable o, Object arg)
	{
	}
	*/

}
