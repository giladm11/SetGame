package GUI.Components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;

public class ChooseColorButton extends JButton implements ActionListener, IColorChooser
{
	private Color selectedColor;
	
	public ChooseColorButton() {
		super("Choose color");
		
		Random rnd = new Random();
		
		this.setSelectedColor(new Color(rnd.nextInt()));
		this.addActionListener(this);
	}
	
	private void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
		this.setForeground(selectedColor);
	}

	@Override
	public void chooseColor(Color c) {
		this.setSelectedColor(c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ColorChooserFrame colorChooser = new ColorChooserFrame(this);
		colorChooser.setVisible(true);
	}
	
	public Color getSelectedColor() {
		return selectedColor;
	}
}
