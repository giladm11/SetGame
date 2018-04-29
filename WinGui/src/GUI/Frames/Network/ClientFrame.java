package GUI.Frames.Network;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;

import GUI.Components.JLabelField;
import GUI.Components.PlayeGetDataPane;
import GUI.Frames.GameFrame;
import GUI.Frames.MainFrame;
import Game.Player;
import Game.Network.ClientGameplay;
import Game.Network.ServerGameplay;

public class ClientFrame extends JFrame implements ActionListener {
	
	private static final int SPACE = (int)(1.5 * MainFrame.SPACE);
	
	private JLabelField serverAdress;
	private JButton connectButton;
	private PlayeGetDataPane playerData;

	public ClientFrame() {
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Connect to a server");
		this.setLocation(MainFrame.WINDOW_POS, MainFrame.WINDOW_POS);

		SpringLayout myLayout = new SpringLayout();

		this.getContentPane().setLayout(myLayout);

		// Set the server adress
		this.serverAdress = new JLabelField("Enter server adress:", "localhost");

		// Set the player name
		this.playerData = new PlayeGetDataPane();

		// Set the connect button
		this.connectButton = new JButton("Connect!");
		this.connectButton.addActionListener(this);

		// Add the components
		this.getContentPane().add(this.serverAdress);
		this.getContentPane().add(this.playerData);
		this.getContentPane().add(this.connectButton);

		// Locate the components
		// serverAdress
		myLayout.putConstraint(SpringLayout.WEST, this.serverAdress,
				SPACE, SpringLayout.WEST, this.getContentPane());
		myLayout.putConstraint(SpringLayout.NORTH, this.serverAdress,
				SPACE, SpringLayout.NORTH, this.getContentPane());

		// player data
		myLayout.putConstraint(SpringLayout.WEST, this.playerData,
				SPACE, SpringLayout.WEST, this.getContentPane());
		myLayout.putConstraint(SpringLayout.EAST, this.playerData,
				SPACE * -1, SpringLayout.EAST, this.getContentPane());
		myLayout.putConstraint(SpringLayout.NORTH, this.playerData,
				SPACE, SpringLayout.SOUTH, this.serverAdress);
		
		// Connect button
		myLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, this.connectButton,
				SPACE, SpringLayout.HORIZONTAL_CENTER, this.getContentPane());
		myLayout.putConstraint(SpringLayout.NORTH, this.connectButton,
				SPACE, SpringLayout.SOUTH, this.playerData);
		
		// Set the right buttom point
		myLayout.putConstraint(SpringLayout.EAST, this.getContentPane(), SPACE,
				SpringLayout.EAST, this.serverAdress);
		myLayout.putConstraint(
				SpringLayout.SOUTH,
				this.getContentPane(),
				SPACE,
				SpringLayout.SOUTH,
				this.connectButton);
		
		this.pack();
		this.setSize(this.getWidth() + SPACE * 2, this.getHeight());
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ConnectClient(this.serverAdress.getText(),
				this.playerData.getPlayerName(),
				this.playerData.getPlayerColor());

		this.dispose();
	}

	public static void ConnectClient(String server, String name, Color color) {
		try {

			Socket socket = new Socket(server, ServerGameplay.PORT);

			GameFrame gameFrame = new GameFrame();

			ArrayList<Player> players = new ArrayList<Player>();
			players.add(new Player(gameFrame, name, color.getRGB()));

			ClientGameplay gamePlay = new ClientGameplay(players,
					players.get(0), socket);

			gameFrame.addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosing(WindowEvent e) {
					gamePlay.dispose();
				}

				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub

				}
			});

			gameFrame.startGame(gamePlay);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1);
		}
	}
}
