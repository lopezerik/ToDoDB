import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

public class MainWindow extends JFrame {

	private JPanel _contentPane;
	private MyToDoList _toDoList;
	private DatabaseManager _db;
	private boolean _online;
	
	/**
	 * Create the frame.
	 */
	public MainWindow(DatabaseManager db, Object[][] entryArray, boolean online) {
		// Display proper connection status in the title of the frame
		super((online) ? "To Do DB (Online)" : "To Do DB (Offline)");
		
		// Set globals
		_db = db;
		_online = online;
		
		// Setup frame options
		setBounds(100, 100, 450, 600);
		setLayout(new BorderLayout());
		// Set to do nothing or else it will override the window listener
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// Fires when user closes the application, releases resources and closes connections
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Closing main window");
				if(_online) {
					_db.closeConnection();
				}
				dispose();
				System.exit(0);
			}
		});
	
		// Create a MyToDoList instance
		String[] columnNames = {"ToDo List", "Hidden ID"};
		_toDoList = new MyToDoList(columnNames, entryArray);
		// Set our ToDo list as the main panel of the frame
		_contentPane = _toDoList;
		setContentPane(_contentPane);

		// Button for adding new entries to the list
		JButton addButton = new JButton("Add");
		addButton.setBackground(Color.LIGHT_GRAY);
		addButton.setToolTipText("Add a new entry");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				// If the application is in online mode but the database is offline, warn the user
				if( _online && !(_db.isValid()) ) {
					// Warn the user about the database error, return and do not open input dialog
					openWarningDialog();
					return;
				}
				
				// Launch the UserEntry dialog
				UserEntry userInput = new UserEntry();
				userInput.setVisible(true);
				
				// Retrieve and check user input
				String content = userInput.getUserInput();
				if(content != null && content.length() > 0) {
					// if the application is in online mode, add content to our database
					if(_online) {
						// Add 'content' to our DatabaseManager and save the entry ID
						int entryID = _db.addEntry(content);
						// Check to see if the entry was successfully added to the database
						if(entryID != -1) {
							// Add the entry to the toDoList with the respective ID
							_toDoList.addEntry(content, entryID);
						} else {
							// Warn the user about the error
							openWarningDialog();
						}
						
					} else {
						// Add the entry to the toDoList with a fake entry ID
						_toDoList.addEntry(content, 0);
					}
				}
				
			}
		}); 
		
		// Button for deleting entries from our list
		JButton deleteButton = new JButton("Delete");
		deleteButton.setBackground(Color.LIGHT_GRAY);
		deleteButton.setToolTipText("Delete selected entry");
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				// If the application is in online mode but the database is offline, warn the user
				if( _online && !(_db.isValid()) ) {
					// Warn the user about the database error
					openWarningDialog();
					return;
				}
				
				// If currentRow is -1, then there is no row selected, nothing will be deleted
				if(_toDoList.getCurrentRow() != -1) {
					// Open confirmation dialog and save response
					int delConfirmation = JOptionPane.showConfirmDialog(_contentPane,
							"Are you sure you want to delete this entry?", "Delete?", 
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					// Remove entry if user selected 'Yes' (0 = Yes option), else do nothing
					if(delConfirmation == 0) {
						// if the application is in online mode, delete the entry from the database
						if(_online) {
							int currentDatabaseID = _toDoList.getCurrentDatabaseID();
							// Make sure the entry was deleted in the database
							if(_db.deleteEntry(currentDatabaseID)) {
								_toDoList.removeSelectedEntry();
							} else {
								// Warn the user if the entry was not deleted
								openWarningDialog();
							}
						} else {
							_toDoList.removeSelectedEntry();
						}
					}
				}
				
			}
		});
		
		// Button for editing an entry
		JButton editButton = new JButton("Edit");
		editButton.setBackground(Color.LIGHT_GRAY);
		editButton.setToolTipText("Edit selected entry");
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				// If the application is in online mode but the database is offline, warn the user
				if( _online && !(_db.isValid()) ) {
					// Warn the user about the database error
					openWarningDialog();
					return;
				}
				
				// Get the selected row, if no row is selected do nothing
				String currentEntry =_toDoList.getCurrentEntry();
				if(currentEntry != null) {
					// Open UserEntry dialog and populate the text box with the current entry text
					UserEntry userInput = new UserEntry();
					userInput.setTextArea(currentEntry);
					userInput.setVisible(true);
					// Retrieve the edited entry and check for empty or null values
					String newContent = userInput.getUserInput();
					if(newContent != null && newContent.length() > 0) {
						// if the application is in online mode, edit the entry in the database
						if(_online){
							int currentDatabaseID = _toDoList.getCurrentDatabaseID();
							// Make sure the entry was successfully edited in the database
							if(_db.editEntry(currentDatabaseID, newContent)) {
								_toDoList.editSelectedEntry(newContent);
							} else {
								// Warn the user if the entry was not edited in the database
								openWarningDialog();
							}
						} else {
							_toDoList.editSelectedEntry(newContent);
						}
					}
				}
			}
		});
		
		// Add the buttons to a tool bar with separators in between
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(Color.LIGHT_GRAY);
		
		toolBar.add(addButton);
		toolBar.add(new JSeparator());
		toolBar.add(editButton);
		toolBar.add(new JSeparator());
		toolBar.add(deleteButton);
		
		// Add the tool bar to our main frame
		add(toolBar, BorderLayout.NORTH);
		// Remove the ability to drag and remove the tool bar
		toolBar.setFloatable(false);
		
	}
	
	// Opens a message dialog to inform the user that an error has occurred
	public static void openWarningDialog() {
		JOptionPane.showMessageDialog(null, "An error occured with the SQL server. "
				+ "Please check your connection and restart the application.", 
				"SQL Server Error", JOptionPane.ERROR_MESSAGE);
	}

}
