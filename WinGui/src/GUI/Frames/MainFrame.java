/**
 * 
 */
package GUI.Frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import GUI.Frames.Network.ClientFrame;
import GUI.Frames.Network.ServerFrame;
import Game.Gameplay;
import Game.Player;


/**
 * @author Gilad
 *
 */
public class MainFrame extends JFrame {
	
	public static final int SPACE = 10;
	public static final int WINDOW_POS = 300;
	public static final int BUTTON_HEIGHT = 40;
	public static final int BUTTON_WIDTH = 250;
			

	// **** START HERE ****
	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
	}

	public MainFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Welcome to S.E.T!");
		this.setLocation(WINDOW_POS, WINDOW_POS);
		
		GridBagLayout gridLayout = new GridBagLayout();
		
		this.getContentPane().setLayout(gridLayout);
		
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.fill = GridBagConstraints.BOTH;
		constraint.weightx = 1;
		constraint.weighty = 1;
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.insets = new Insets(SPACE, SPACE, SPACE, SPACE);
		
		// Set the buttons
		// Single Game
		JButton singleButton = new JButton("Single game");
		singleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameFrame gameFrame = new GameFrame();
				ArrayList<Player> players = new ArrayList<Player>();
				players.add(new Player(gameFrame, "Player", Color.BLACK.getRGB()));
				gameFrame.startGame(new Gameplay(players));
				dispose();
			}
		});
		
		// Server
		JButton serverButton = new JButton("Server");
		serverButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ServerFrame server = new ServerFrame();
				dispose();
			}
		});
		
		// Client
		JButton clientButton = new JButton("Client");
		clientButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ClientFrame client = new ClientFrame();
				dispose();
			}
		});
		
		singleButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		serverButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		clientButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		
		this.getContentPane().add(singleButton, constraint);
		constraint.gridy++;
		this.getContentPane().add(serverButton, constraint);
		constraint.gridy++;
		this.getContentPane().add(clientButton, constraint);
		
		this.pack();
		this.setVisible(true);
	}
}