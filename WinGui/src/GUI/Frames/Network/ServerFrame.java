package GUI.Frames.Network;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GUI.Components.PlayeGetDataPane;
import GUI.Frames.MainFrame;
import Game.BoardLocation;
import Game.Player;
import Game.Interfaces.IGuiUpdater;
import Game.Network.NetworkPlayer;
import Game.Network.ServerGameplay;

public class ServerFrame extends JFrame implements Runnable, WindowListener,
		ActionListener, IGuiUpdater {
	private static final int SPACE = MainFrame.SPACE;

	private Thread getPlayersThread;
	private ServerSocket server;
	private JLabel ipLabel;
	private JLabel playersLabel;
	private JButton startGame;
	private JPanel clientsPanel;
	private ServerGameplay game;
	private ArrayList<Player> players;
	private boolean keepWaiting;
	private PlayeGetDataPane platerData;

	public ServerFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Starting a server");
		this.setLocation(MainFrame.WINDOW_POS, MainFrame.WINDOW_POS);

		this.keepWaiting = true;

		// Set the game
		this.players = new ArrayList<Player>();

		this.game = new ServerGameplay(new ArrayList<Player>(), server);
		this.game.AddGuiUpdater(this);

		// Set label
		this.ipLabel = new JLabel();
		this.playersLabel = new JLabel("Game players: ");

		// Set the start game button
		this.startGame = new JButton("Start game");
		this.startGame.addActionListener(this);

		// Set the players panel
		this.clientsPanel = new JPanel();
		this.clientsPanel.setBackground(Color.WHITE);
		this.clientsPanel.setLayout(new BoxLayout(this.clientsPanel, BoxLayout.Y_AXIS));
		this.clientsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,
				1));

		// Set this player Data
		this.platerData = new PlayeGetDataPane();

		this.getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();

		// Set default contraint
		constraints.fill = GridBagConstraints.CENTER;
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(SPACE / 2, SPACE, SPACE / 2, SPACE);

		this.getContentPane().add(this.ipLabel, constraints);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridy++;
		this.getContentPane().add(this.platerData, constraints);

		constraints.fill = GridBagConstraints.CENTER;
		constraints.gridy++;
		this.getContentPane().add(this.playersLabel, constraints);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridy++;
		this.getContentPane().add(this.clientsPanel, constraints);

		constraints.gridy++;
		this.getContentPane().add(this.startGame, constraints);

		try {
			// Start server
			this.server = new ServerSocket(ServerGameplay.PORT);
			this.ipLabel.setText("My ip adress: "
					+ InetAddress.getLocalHost().getHostAddress()
					+ ". My player: ");
			getPlayersThread = new Thread(this);
			getPlayersThread.start();

			this.addWindowListener(this);
			this.pack();
			this.setVisible(true);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e);
			this.dispose();
		}
	}

	private void stopWating() {
		this.keepWaiting = false;
		this.getPlayersThread.stop();
	}

	@Override
	public void run() {
		while (this.keepWaiting) {
			try {
				this.GetClient();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void GetClient() throws IOException {
		try {
			Socket playerSocket = this.server.accept();
			NetworkPlayer player = new NetworkPlayer(playerSocket, playerSocket
					.getRemoteSocketAddress().toString(), Color.BLACK.getRGB(),
					this.game);
			this.players.add(player);

			this.updateClientPanel(this.players);
		} catch (Exception e) {
		}
	}

	// // *********** WINDOW LISTENER ***************

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (this.keepWaiting) {
			try {
				this.game.dispose();
			} catch (Exception ex) {
				for (Player player : players) {
					((NetworkPlayer) player).dispose();
				}

				try {
					server.close();
				} catch (Exception e1) {
				}
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Start the game
		// This player connect
		ClientFrame.ConnectClient("localhost",
								  this.platerData.getPlayerName(),
								  this.platerData.getPlayerColor());

		// Stop thread
		this.stopWating();

		this.game.StartGame(this.players.size());
	}

	@Override
	public void updateGameGui() {
		this.updateClientPanel(this.game.getPlayers());
	}
	
	private void updateClientPanel(ArrayList<Player> currentPlayers)
	{
		this.clientsPanel.removeAll();

		for (int i = 0; i < currentPlayers.size(); i++) {
			JLabel playerLabel = new JLabel();
			playerLabel.setText(players.get(i).getName());
			playerLabel.setForeground(new Color(players.get(i).getColor()));
			playerLabel.setOpaque(true);
			playerLabel.setAlignmentX(CENTER_ALIGNMENT);
			this.clientsPanel.add(playerLabel);
		}
		
		this.clientsPanel.updateUI();
		this.pack();
	}

	@Override
	public void callSetUpdateGameGui(BoardLocation[] location, Player Player) {
	}

	@Override
	public void gameOver(Player[] winners) {
		
	}

}
