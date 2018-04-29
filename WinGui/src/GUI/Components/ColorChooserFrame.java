package GUI.Components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorChooserFrame extends JFrame {
	private IColorChooser actionChooser;
	private JColorChooser colorChooser;
	private JPanel panel;
	private JButton okButton;
	private JButton cancelButton;
	
	public ColorChooserFrame(IColorChooser chooser) {
		super("Colors are fun if you high");
		this.actionChooser = chooser;
		this.colorChooser = new JColorChooser();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 400);

		this.panel = new JPanel();
		
		this.okButton = new JButton("Choose");
		this.okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				actionChooser.chooseColor(colorChooser.getColor());
				dispose();
			}
		});
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		this.panel.add(this.okButton,BorderLayout.CENTER);
		
		this.panel.add(this.cancelButton, BorderLayout.CENTER);
		
		this.add(colorChooser);
		this.add(panel, BorderLayout.PAGE_END);
		this.setVisible(true);
	}
}
