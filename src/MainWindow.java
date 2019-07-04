import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

public class MainWindow extends JFrame {

	private JPanel _contentPane;
	private MyToDoList _todoList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		// Setup frame options
		super("ToDo DB");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 800);
		setLayout(new BorderLayout());
		
		// Create a MyToDoList
		String[] columnNames = {"ToDo List", "Hidden ID"};
		_todoList = new MyToDoList(columnNames, null);
		// Set our ToDo list as the main panel of the frame
		_contentPane =_todoList;
		setContentPane(_contentPane);

		// Button for adding new entries to the list
		JButton addButton = new JButton("Add");
		addButton.setBackground(Color.LIGHT_GRAY);
		addButton.setToolTipText("Add a new entry");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// Launch the UserEntry dialog
				UserEntry userInput = new UserEntry();
				userInput.setVisible(true);
				// Retrieve and check user input
				String content = userInput.getUserInput();
				if(content != null && content.length() > 0) {
					// User input was not null or empty, add it to our list
					System.out.println("Returned user input: " + content);
					_todoList.addEntry(content);
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
				// If currentRow is -1, then there is no row selected, nothing will be deleted
				if(_todoList.getCurrentRow() != -1) {
					// Open confirmation dialog and save response
					int delConfirmation = JOptionPane.showConfirmDialog(_contentPane,
							"Are you sure you want to delete this entry?", "Delete?", 
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					// if user selected 'Yes' remove entry, else do nothing
					if(delConfirmation == 0) {
						// User selected 'Yes', delete entry
						_todoList.removeSelectedEntry();
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
				// Get the currently selected row, if no row is selected do nothing
				String currentEntry =_todoList.getCurrentEntry();
				if(currentEntry != null) {
					// Open UserEntry dialog and populate the text box with the current entry text
					UserEntry userInput = new UserEntry();
					userInput.setTextArea(currentEntry);
					userInput.setVisible(true);
					// Retrieve the edited entry and check for empty or null values
					String content = userInput.getUserInput();
					if(content != null && content.length() > 0) {
						System.out.println("Edited content: " + content);
						_todoList.editSelectedEntry(content);
					}
				}
			}
		});
		
		// Add the buttons to a toolbar with separators in between
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(Color.LIGHT_GRAY);
		toolBar.add(addButton);
		toolBar.add(new JSeparator());
		toolBar.add(editButton);
		toolBar.add(new JSeparator());
		toolBar.add(deleteButton);
		add(toolBar, BorderLayout.NORTH);
		toolBar.setFloatable(false);
		
	}

}
