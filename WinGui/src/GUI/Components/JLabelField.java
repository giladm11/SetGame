package GUI.Components;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class JLabelField extends JPanel
{	
	private JLabel label;
	private JTextField textField;
	
	public JLabelField(String fieldName, String defaultText) {
		super();
	
		this.label = new JLabel(fieldName);
		this.textField = new JTextField(defaultText == null ? "" : defaultText);
		
		SpringLayout myLayout = new SpringLayout();
		this.setLayout(myLayout);
		
		this.add(this.label);
		this.add(this.textField);
		
		myLayout.putConstraint(SpringLayout.WEST, this.label, 0, SpringLayout.WEST, this);
		myLayout.putConstraint(SpringLayout.WEST, this.textField, 0, SpringLayout.EAST, this.label);
		myLayout.putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, this.textField);
		myLayout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, this.textField);
	}
	
	public String getText()
	{
		return this.textField.getText();
	}
	
	public JLabel getLabel() {
		return this.label;
	}
	
	public JTextField getTextField() {
		return this.textField;
	}
}
