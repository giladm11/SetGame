package GUI.Components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import GUI.Frames.MainFrame;
import Game.BoardLocation;
import Game.Gameplay;
import Game.Player;
import Game.Interfaces.IGuiUpdater;

public class GameDataPane extends JPanel implements IGuiUpdater {

	private final static int SPACE = MainFrame.SPACE / 2;
	private final static Color DISCONNECTED_COLOR = Color.lightGray;
	private final static Color DEFAULT_COLOR = Color.WHITE;
	private final static int COLOR_MAX = 255;
	private final static int SECOND = 1000;

	private Gameplay game;
	private JTextArea gameStatus;
	private Timer timer;
	private Calendar calendar;
	private SimpleDateFormat dateFormat;
	private JPanel statusPanel;
	private Hashtable<String, JTextArea> activePlayers;
	private Hashtable<String, JTextArea> disconnectedPlayers;
	private boolean isGameOver;
	private String strGameOverText = "";
	private JLabel lblTime;

	public GameDataPane(JButton[] actionButtons) {
		this.isGameOver = true;

		this.activePlayers = new Hashtable<String, JTextArea>();
		this.disconnectedPlayers = new Hashtable<String, JTextArea>();

		this.statusPanel = new JPanel(new GridBagLayout());
		this.statusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		this.gameStatus = new JTextArea("Game not started yet.");
		this.gameStatus.setEditable(false);

		this.lblTime = new JLabel();
		this.lblTime.setText("Time: --:--");

		GridBagConstraints constraint = new GridBagConstraints();
		constraint.insets = new Insets(SPACE, SPACE, SPACE, SPACE);
		constraint.fill = GridBagConstraints.BOTH;
		constraint.weightx = 1;
		constraint.weighty = 1;
		constraint.gridx = 0;
		constraint.gridy = 0;

		statusPanel.add(gameStatus, constraint);

		constraint.weighty = 0;
		constraint.fill = GridBagConstraints.HORIZONTAL;

		// Add the time label
		constraint.gridy++;
		statusPanel.add(lblTime, constraint);

		if (actionButtons != null) {
			for (JButton currentButton : actionButtons) {
				constraint.gridy++;
				statusPanel.add(currentButton, constraint);
			}
		}

		this.setLayout(new GridLayout(1, 1));

		this.calendar = Calendar.getInstance();
		this.calendar.setTimeInMillis(0);

		this.dateFormat = new SimpleDateFormat("mm:ss");

		this.timer = new javax.swing.Timer(SECOND, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				calendar.add(Calendar.SECOND, 1);
				updateTimeLabel();
			}
		});

		this.add(statusPanel);
	}

	public void setGame(Gameplay game) {
		this.game = game;
		this.isGameOver = false;
		this.game.AddGuiUpdater(this);
		while (this.game.getPlayers().size() != this.activePlayers.size()) {
			try {
				for (Player currentPlayer : this.game.getPlayers()) {
					this.AddPlayer(currentPlayer);
				}
			} catch (ConcurrentModificationException ex) {
			}
		}
		this.updateGameGui();
	}

	@Override
	public void updateGameGui() {
		if (this.game == null) {
			this.gameStatus.setText("Game not on.");
		} else if (this.isGameOver) {
			this.gameStatus.setText(strGameOverText);
		} else {
			synchronized (this) {
				ArrayList<Player> gamePlayers = this.game.getPlayers();
				Hashtable<String, Player> checkAvailable = new Hashtable<String, Player>();

				for (int i = 0; i < gamePlayers.size(); i++) {
					try {
						Player currentPlayer = gamePlayers.get(i);
						if (this.activePlayers.containsKey(currentPlayer
								.getName())) {
							this.updatePlayer(this.activePlayers
									.get(currentPlayer.getName()),
									currentPlayer);
						} else {
							this.AddPlayer(currentPlayer);
						}

						checkAvailable.put(currentPlayer.getName(),
								currentPlayer);
					} catch (java.util.ConcurrentModificationException ex) {
						i = -1;
						checkAvailable.clear();
					}
				}

				// If there is more active players here than in the game
				if (checkAvailable.size() != activePlayers.size()) {
					ArrayList<String> disconnected = new ArrayList<String>();

					// Find him
					for (String currentPlayerName : this.activePlayers.keySet()) {
						if (!(checkAvailable.containsKey(currentPlayerName))) {
							disconnected.add(currentPlayerName);
						}
					}

					for (String player : disconnected) {
						disconnectPlayer(player);
					}
				}
			}
			this.updateGameStatus();
		}
	}

	@Override
	public void callSetUpdateGameGui(BoardLocation[] location, Player player) {
		this.updateGameGui();
		this.highlightPlayer(player);
		this.gameStatus.setText(this.gameStatus.getText() + "\n"
				+ player.getName() + " Called set!");
	}

	private void AddPlayer(Player player) {
		synchronized (this) {
			if (!this.activePlayers.containsKey(player.getName())) {
				JTextArea playerText = new JTextArea();
				this.activePlayers.put(player.getName(), playerText);
				playerText.setEditable(false);
				playerText.setBorder(BorderFactory
						.createLineBorder(Color.BLACK));
				this.updatePlayer(playerText, player);
				((GridLayout) this.getLayout()).setRows(((GridLayout) this
						.getLayout()).getRows() + 1);
				this.add(playerText);
			}
		}
	}

	private void disconnectPlayer(String playerName) {
		JTextArea playerText = this.activePlayers.get(playerName);
		this.disconnectedPlayers.put(playerName, playerText);
		this.activePlayers.remove(playerName);
		playerText.setBackground(DISCONNECTED_COLOR);
		playerText.setFont(playerText.getFont().deriveFont(Font.ITALIC));
		playerText.append("\n(Disconnected)");
	}

	private void updatePlayer(JTextArea text, Player p) {
		text.setBackground(DEFAULT_COLOR);
		text.setForeground(new Color(p.getColor()));
		text.setText(p.toString());
	}

	public void updateGameStatus() {
		this.gameStatus.setText("Cards on deck: " + this.game.getCardsOnDeck()
				+ "\nCards on board: " + this.game.getCardsOnBoards()
				+ "\nNumber of players: " + this.game.getPlayers().size());
		if ((this.timer.isRunning() == false)
				&& (this.game.getCardsOnBoards() > 0)
				&& (this.isGameOver == false)) {
			this.updateTimeLabel();
			this.timer.start();
		}
	}

	@Override
	public void gameOver(Player[] winners) {
		this.isGameOver = true;
		this.timer.stop();
		String text = "The game is over\nafter "
				+ this.dateFormat.format(calendar.getTime()) + " minutes.";
		// If there was lost connection
		if (winners == null) {
			text += "\nThe connection to\n the server lost!";
		} else {

			for (Player player : winners) {
				this.updatePlayer(this.activePlayers.get(player.getName()),
						player);
				this.highlightPlayer(player);
			}

			if (winners.length > 0) {
				String playersName = "";

				for (Player player : winners) {
					playersName = (playersName == "") ? player.getName()
							: playersName + " and " + player.getName();
				}

				text += "\nThe player(s) " + playersName
						+ "\nWin with the score " + winners[0].getScore();

			}
		}

		this.strGameOverText = text;

		String title = "Game is over!";
		JOptionPane.showMessageDialog(null, text, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void highlightPlayer(Player player) {
		this.activePlayers.get(player.getName()).setForeground(
				this.getOppositeColor(new Color(player.getColor())));
		this.activePlayers.get(player.getName()).setBackground(
				new Color(player.getColor()));
	}

	private Color getOppositeColor(Color color) {
		return new Color(COLOR_MAX - color.getRed(), COLOR_MAX
				- color.getGreen(), COLOR_MAX - color.getBlue());
	}

	private void updateTimeLabel() {
		lblTime.setText("Time: " + dateFormat.format(calendar.getTime()));
	}
}
