import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class MyToDoList extends JPanel{
	// Globals
	public static final int TEXT_COLUMN_INDEX = 0;
	public static final int ID_COLUMN_INDEX = 1;
	
	private String[] _columnNames;
	private Object[][] _listEntries;
	
	private JTable _table;
	private JScrollPane _scrollTable;
	private DefaultTableModel _tableModel;
	private int _currentRow;
	private int _currentDatabaseID;
	private ListSelectionModel _select;
	
	public MyToDoList(String[] columnNames, Object[][] listEntries) {
		// Layout settings for the list panel
		setBorder(new EmptyBorder(5,5,5,5));
		setLayout(new BorderLayout(0,0));
		
		_currentRow = -1;
		_columnNames = columnNames;
		if(listEntries == null) {
			// Default entries when using the application without a database/offline mode
			_listEntries = new Object[][]{
				{"Make dentist appointment", 0},
				{"Buy eggs and milk", 1},
				{"Check mailbox", 2},
				{"Oil change on Friday", 3}
			};;
		} else {
			// Use the entries provided, could be empty
			_listEntries = listEntries;
		}

		// Create new JTable 
		// Using DefaultTableModel because it allows you to add and remove rows without refreshing
		_table = new JTable(new DefaultTableModel(_listEntries, _columnNames) {
			// Lock entries in the list so they are not editable by double clicking them
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		// tableModel used by other methods to add/remove/edit entries
		_tableModel = (DefaultTableModel) _table.getModel();

		
		// Get column model in order to remove/hide the database id column from the user
		TableColumnModel columnModel = _table.getColumnModel();
		// The column data still exists when it is removed
		columnModel.removeColumn(columnModel.getColumn(ID_COLUMN_INDEX));
		
		
		_table.setCellSelectionEnabled(true);
		_select = _table.getSelectionModel();
		// Only allow one entry to be selected at a time
		_select.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Add a listener to determine which entry is currently selected
		_select.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				// valueChanged fires twice on click, only run once
				if(! event.getValueIsAdjusting()) {
					// Evaluates to a list with a single item: the currently selected row
					int[] row = _table.getSelectedRows();
					// Prevents out of bounds error if first row is deleted
					if(row.length != 0) {
						// Update globals to reflect the current selected row
						_currentRow = row[0];
						_currentDatabaseID = (int) _table.getModel().getValueAt(row[0], ID_COLUMN_INDEX);
					}
				}
			}
		});
		
		// Add the table to a scroll pane, user can scroll if their list is too long
		_scrollTable = new JScrollPane(_table);
		add(_scrollTable);
	}
	
	// Add an entry
	public void addEntry(String entryText, int hiddenID) {
		_tableModel.addRow(new Object[]{entryText, hiddenID});
	}
	
	// Remove an entry
	public void removeSelectedEntry() {
		// Check to make sure there are rows left to delete and there is a row selected
		if(_tableModel.getRowCount() > 0 && _currentRow != -1) {
			_tableModel.removeRow(_currentRow);
		}
		// Clear the selected row 
		_select.clearSelection();
		_currentRow = -1;
	}
	
	// Edit an entry
	public void editSelectedEntry(String entry) {
		// Make sure there is a row selected
		if(_currentRow != -1) {
			_tableModel.setValueAt(entry, _currentRow, TEXT_COLUMN_INDEX);
		}
	}
	
	// Returns the index of the currently selected row
	public int getCurrentRow() {
		return _currentRow;
	}
	
	// Returns the text of the currently selected row
	public String getCurrentEntry() {
		// Make sure there is a row selected
		if(_currentRow != -1) {	
			return (String) _table.getModel().getValueAt(_currentRow, TEXT_COLUMN_INDEX);
		} else {
			return null;
		}
	}
	
	// Returns the database ID of the current selected row
	public int getCurrentDatabaseID() {
		return _currentDatabaseID;
	}

}
