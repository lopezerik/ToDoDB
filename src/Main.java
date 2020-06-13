import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
	
	public static int arrayIndex;

	public static void main(String[] args) {

		CredentialsParser parser = new CredentialsParser();
		Map<String, String> map = parser.parseDatabase("credentials.ini");
		
		DatabaseManager db = new DatabaseManager(map);

		// Check if we can connect to the database, else run in offline mode
		if(db.isValid()) {
			System.out.println("Database is online; Starting application in online mode");		
			
			System.out.println(db.isValid());
			
			// Retrieve all the entries currently in the database and store them in a Map
			Map<Integer, String> allEntries = db.getAllEntries();
			// Check for an error
			if(allEntries != null) {
				int totalEntries = allEntries.size();
				// Convert the entry Map into an array to pass to the MyToDoList
				Object[][] entryArray = new Object[totalEntries][2];
				arrayIndex = 0;
				// 'forEach' added to the Map class in Java 8
				allEntries.forEach((id, body) -> {
					// Add the body of the entry and the id to our array
					entryArray[arrayIndex][0] = body; 
					entryArray[arrayIndex][1] = id; 
					arrayIndex++;
				});
				// Create our application window with our MainWindow class
				MainWindow window = new MainWindow(db, entryArray, true);		
				window.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "An error occured with the SQL server. "
						+ "Please check your connection and restart the application.", 
						"SQL Server Error", JOptionPane.ERROR_MESSAGE);
			}
			
		} else {
			System.out.println("Database is offline; Starting application in offline mode");
			MainWindow window = new MainWindow(null, null, false);
			window.setVisible(true);
		}
	}
}
