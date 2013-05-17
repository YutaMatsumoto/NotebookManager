import java.sql.*;
import java.sql.*;
import java.util.Properties;
import java.util.*;

class NoteDatabase {

	// The JDBC Connector Class.
	private static final String dbClassName = "com.mysql.jdbc.Driver";
	private static String db = "127.0.0.1";
	private static String tab = "cs457";

	// You can include user and password after this by adding (say)
	// ?user=paulr&password=paulr. Not recommended!
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/cs457";

	private static Connection noteDBconnection = null;

	NoteDatabase() 
	{
		try {
			connectToNoteDatabase();
		} catch (ClassNotFoundException e) {
			System.err.println("NoteDatabase : Creating Connectioin: ClassNotFoundException");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("NoteDatabase : Creating Connectioin: SQLException");
			e.printStackTrace();
		}
	}

	public void connectToNoteDatabase() throws ClassNotFoundException,SQLException
	{
		// from [JDBC and MySQL - ArchWiki](https://wiki.archlinux.org/index.php/JDBC_and_MySQL)

		System.out.println(dbClassName);
		// Class.forName(xxx) loads the jdbc classes and
		// creates a drivermanager class factory
		Class.forName(dbClassName);
		// Properties for user and password. Here the user and password are both 'paulr'
		Properties p = new Properties();
		p.put("user","root");
		p.put("password","mysql");

		// Now try to connect
		noteDBconnection = DriverManager.getConnection(CONNECTION,p);
		System.out.println("Connected to Note Database!");
	}

	boolean createNote(String noteTitle, String author) 
	{
		boolean noteCreated = true;	
		PreparedStatement createNote = null;
		String createNoteQuery = "call " + " createNote(?, ?)";

		// 
		// from [Using Transactions (The Java™ Tutorials > JDBC(TM) Database Access > JDBC Basics)](http://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html)
		//
		try {
			System.out.println("Tyring to create note with <"+createNoteQuery+">");
			noteDBconnection.setAutoCommit(false);

			// prepare statement
			createNote = noteDBconnection.prepareStatement(createNoteQuery);
			createNote.setString(1, noteTitle);
			createNote.setString(2, author);
			createNote.executeUpdate();
			noteDBconnection.commit();

		} catch (SQLException e) {
			if (noteDBconnection != null) {
				e.printStackTrace();
				try {
					System.err.print("Transaction is being rolled back");
					noteDBconnection.rollback();
				} catch(SQLException excep) {
					e.printStackTrace();
				}
			}
			noteCreated = false;
		} finally {
			if (createNote != null) {
				try {
					createNote.close();
				} catch(SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				noteDBconnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return noteCreated;
	}

	void close()
	{
		if ( noteDBconnection != null )  {
			try { 
				noteDBconnection.close();
			} catch ( SQLException e ) {
				System.err.println("NoteDatabase SQLException ");
				e.printStackTrace();
			}
		}
	}

	ArrayList<String[]> getAllNotes()
	{
		System.out.println("ShowNotes Actoin Called");

		ArrayList<String[]> noteList = new ArrayList<String[]>();

		// TODO pull in author

		Statement stmt = null;
		String query =
			"select id,title,tagname,content " +
			"from " + tab + ".note";

		try {
			stmt = noteDBconnection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {

				String noteID = new Integer( rs.getInt("id") ).toString();
				String title = rs.getString("title");
				String tagname = rs.getString("tagname");
				String content = rs.getString("content");

				String[] noteInfo = new String[4];
				noteInfo[0]=noteID ;
				noteInfo[1]=title ;
				noteInfo[2]=tagname ;
				noteInfo[3]=content ;

				noteList.add(noteInfo);
			}
		} catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) { stmt.close(); }
			} catch (SQLException e ) {
				e.printStackTrace();
			}
		}

		return noteList;
	}


}
