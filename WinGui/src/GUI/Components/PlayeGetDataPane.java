package GUI.Components;

import java.awt.Color;
import java.awt.Dimension;
import java.net.InetAddress;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import GUI.Frames.MainFrame;

public class PlayeGetDataPane extends JPanel
{
	private static final int PLAYER_NAME_WIDTH = 100;
	
	private JLabelField playerName;
	private ChooseColorButton colorSelection;
	
	public PlayeGetDataPane() {
		super();
		
		String strPlayerName = "Player";

		try
		{
			strPlayerName = InetAddress.getLocalHost().getHostName();
		}catch (Exception ex){}
		
		// Initialize compenents
		this.playerName = new JLabelField("Player name:", strPlayerName);
		this.colorSelection = new ChooseColorButton();
		
		// Set the layout
		SpringLayout myLayout = new SpringLayout();
		this.setLayout(myLayout);
		
		// Add components
		this.add(this.playerName);
		this.add(this.colorSelection);
		
		// Set the layout
		//this.colorSelection.setPreferredSize(new Dimension(150,20));
		this.colorSelection.setMaximumSize(new Dimension(2000,2000));
		this.playerName.getTextField().setPreferredSize(new Dimension(PLAYER_NAME_WIDTH, (int)this.playerName.getTextField().getPreferredSize().getHeight()));
		
		myLayout.putConstraint(SpringLayout.WEST, this.playerName, 0, SpringLayout.WEST, this);
		myLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, this.colorSelection, 0, SpringLayout.HORIZONTAL_CENTER, this);
		myLayout.putConstraint(SpringLayout.NORTH, this.colorSelection, MainFrame.SPACE, SpringLayout.SOUTH, this.playerName);
		myLayout.putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, this.playerName);
		myLayout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, this.colorSelection);
	}
	
	// ***** GETTERS *****
	
	public String getPlayerName()
	{
		return this.playerName.getText();
	}
	
	public Color getPlayerColor()
	{
		return this.colorSelection.getSelectedColor();
	}
}
