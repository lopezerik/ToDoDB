import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

public class UserEntry extends JDialog {
	
	public static final int MAX_ENTRY_SIZE = 256;

	private final JPanel _contentPanel = new JPanel();
	
	private JTextArea _textArea;
	private String _userInput;

	public static void main(String[] args) {
		try {
			UserEntry dialog = new UserEntry();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Create the window
	public UserEntry() {
		// User input set to null when initially creating the dialog
		_userInput = null;
		setBounds(100, 100, 450, 300);
		setTitle("Enter and Edit Text");
		// Default modality to block the user from interacting with the main window
		setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		// Setup layout options
		getContentPane().setLayout(new BorderLayout());
		_contentPanel.setLayout(new FlowLayout());
		_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(_contentPanel, BorderLayout.CENTER);
		
		// Add the text box to our dialog
		_textArea = new JTextArea();
		_textArea.setLineWrap(true);
		_textArea.setRows(11);
		_textArea.setColumns(30);
		_textArea.setSize(new Dimension(400, 250));
		_contentPanel.add(_textArea);
		
		// Button area
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	
		// OK button, saves entry and closes dialog
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String userInput = _textArea.getText().trim();
				int contentLength = userInput.length();
				// Cannot have entries greater than 256 characters
				if(contentLength > MAX_ENTRY_SIZE) {
					// Display warning message dialog
					JOptionPane.showMessageDialog(_contentPanel, 
							"Entries cannot exceed " + MAX_ENTRY_SIZE + " characters.\n Current character count: " 
							+ contentLength, "Error", JOptionPane.INFORMATION_MESSAGE);
				} else {
					_userInput = userInput;
					// Hide the dialog box in order for the calling class to retrieve the user input
					setVisible(false);
				}
		
			}
		});
		
		// Cancel button, discards the entry and closes dialog
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		
		// Add buttons to the panel
		buttonPane.add(okButton);		
		buttonPane.add(cancelButton);
		
	}
	
	// Populate the text box, used when editing an existing entry
	public void setTextArea(String text) {
		_textArea.setText(text);
	}
	
	// Return current user input, disposes of dialog resources 
	public String getUserInput() {		
		dispose();
		
		return _userInput;
	}
	
}
