import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseManager {
	
	public static String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
	
	private Connection _connection;
	
	// Constructor 
	public DatabaseManager(Map<String, String> credentials) {
		if (credentials != null) {
			
			try {
				String url = credentials.get("url");
				String username = credentials.get("user");
				String password = credentials.get("password");
				// Load the MySQL database Driver
				Class.forName(DATABASE_DRIVER);
				// Establish connection
				_connection = DriverManager.getConnection(url, username, password);
			} catch(Exception e) {
				System.out.println("Error establishing connection to Database.");
				System.out.println(e);
			}
		} else {
			System.out.println("Credentials not found. Cannot conenct to database.");
		}
	}
	
	// Queries the database for the entry corresponding to entryID and returns it
	public String readEntry(int entryID){
		try {
			// Set up a PreparedStatement
			String readCommand = "SELECT entry_body FROM todo.entries WHERE entry_id = ?";
			PreparedStatement readStatement = _connection.prepareStatement(readCommand);
			// Set entry_id = entryID in the query
			readStatement.setInt(1, entryID);
			ResultSet entry = readStatement.executeQuery();
			// Retrieve the result
			if(entry.next()) {	
				// Successfully read the entry
				String body = entry.getString("entry_body");
				readStatement.close();
				return body;
			}
			// Close the statement and return null if the ResultSet was empty
			readStatement.close();
			return null;
		} catch(Exception e) {
			// Failed to read the entry
			System.out.println(e);
			return null;
		}
	}
	
	// Adds entry into the database, returns the database ID of the entry or -1 if unsuccessful 
	public int addEntry(String entry) {
		try {
			// Set up a PreparedStatement, prevents SQL injections
			String insertCommand = "INSERT INTO todo.entries(entry_body, entry_user) VALUES(?, 'fake_user')";
			PreparedStatement addStatement = _connection.prepareStatement(insertCommand);
			addStatement.setString(1, entry);
			addStatement.executeUpdate();
			addStatement.close();
			
			// Successfully added an entry, return the ID of the entry that was just added
			return getLatestID();
		} catch(Exception e){
			// Could not add entry
			System.out.println(e);
			return -1;
		}
	}
	
	// deletes the entry corresponding to entryID, returns true/false if successful/unsuccessful  
	public boolean deleteEntry(int entryID) {
		try {
			String deleteCommand = "DELETE FROM todo.entries WHERE entry_id = ?";
			PreparedStatement deleteStatement = _connection.prepareStatement(deleteCommand);
			deleteStatement.setInt(1, entryID);
			deleteStatement.executeUpdate();
			deleteStatement.close();
			
			// Successfully deleted the entry
			return true;
		} catch(Exception e) {
			// Could not delete entry
			System.out.println(e);
			return false;
		}
	}
	
	// Edits the entry corresponding to entryID
	// Returns true/false if successful/unsuccessful  
	public boolean editEntry(int entryID, String entry) {
		try {
			// Use a PreparedStatement to prevent SQL injections
			String updateCommand = "UPDATE todo.entries SET entry_body = ? WHERE entry_id = ?";
			PreparedStatement editStatement = _connection.prepareStatement(updateCommand);
			editStatement.setString(1, entry);
			editStatement.setInt(2, entryID);
			editStatement.executeUpdate();
			editStatement.close();
			
			// Successfully edited the entry
			System.out.println("updated an entry");
			return true;
		} catch(Exception e) {
			System.out.println(e);
			
			// Could not edit the entry
			return false;
		}
	}
	
	public Map<Integer, String> getAllEntries() {
		// Use LinkedHashMap to preserve the order of items inserted
		Map<Integer, String> allEntries = new LinkedHashMap<>();
		try {
			String readAllCommand = "SELECT * FROM todo.entries";
			PreparedStatement readAllStatement = _connection.prepareStatement(readAllCommand);
			ResultSet results = readAllStatement.executeQuery();
			
			// Loop through the results set and add them to our LinkedHashMap
			while(results.next()) {
				String body = results.getString("entry_body");
				int entryID = results.getInt("entry_id");		
				// Use the entryID as the key to the Map in order to allow duplicate text entries
				allEntries.put(entryID, body);
			}
			
			// Close the prepared statement and return the LinkedHashMap
			readAllStatement.close();
			return allEntries;
		} catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	// Returns the entryID of the latest entry that still exists in the database
	public int getLatestID() {
		try {
			// Queries the database for the entry with the highest entry_id value
			String latestCommand = "SELECT * FROM todo.entries ORDER BY entry_id DESC LIMIT 1";
			PreparedStatement latestStatement = _connection.prepareStatement(latestCommand);
			ResultSet result = latestStatement.executeQuery();
			
			// Extract the id from the ResultSet and return it
			if(result.next()) {
				int id = result.getInt("entry_id");
				latestStatement.close();
				return id;
			}
			
			latestStatement.close();
			return -1;
		} catch(Exception e){
			System.out.println(e);
			return -1;
		}
	}
	
	// Checks to see if the connection to the server is valid, 2 second timeout
	public boolean isValid() {
		if(_connection != null) {
			try {
				return _connection.isValid(2);
			} catch(Exception e){
				System.out.println(e);
				return false;
			}
		}
		return false;
	}
	
	// Closes the connection to the SQL server
	public void closeConnection() {
		System.out.println("Closing conneciton...");
		try {
			_connection.close();
			System.out.println("Connection closed");
		} catch (SQLException e) {
			System.out.println("Connection failed to close");
			e.printStackTrace();
		}
	}
}
