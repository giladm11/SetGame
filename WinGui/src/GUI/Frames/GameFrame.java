package GUI.Frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import GUI.Components.GameDataPane;
import GUI.Components.GuiBoard;
import GUI.Components.GuiCard;
import Game.BoardChange;
import Game.BoardLocation;
import Game.Deck;
import Game.Gameplay;
import Game.Player;
import Game.Interfaces.IGuiUpdater;
import Game.Interfaces.NotifyChangesListener;

/**
 * @author Gilad
 *
 */
public class GameFrame extends JFrame implements IGuiUpdater, MouseListener,
		NotifyChangesListener, WindowListener {

	private GuiBoard GuiBoard;
	private GameDataPane dataPanel;
	private Gameplay game;
	private JLabel status;
	private int clueIndex;
	private JButton clueButton;
	private JButton addCardButton;
	private JButton playAgainButton;

	/**
	 * @param args
	 */

	public GameFrame() {
		super("Welcome to the game set");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(MainFrame.WINDOW_POS, MainFrame.WINDOW_POS);

		this.clueIndex = 0;
		this.addWindowListener(this);

		// Create the gui board
		this.GuiBoard = new GuiBoard(this);

		// Set clue button
		this.clueButton = new JButton("Get clue");
		this.clueButton.setEnabled(false);
		this.clueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiBoard.clearSelectionsOnBoard();
				ArrayList<BoardLocation[]> clues = game.getBoardSets();

				if (clues.size() > 0) {
					clueIndex = (clueIndex % game.getMaxHintCount()) + 1;
					status.setText(clueIndex + " cards clue.");

					for (int i = 0; i < clueIndex; i++) {
						GuiBoard.getCard(clues.get(0)[i]).SetClueColor();
					}
				} else {
					status.setText("No clues.");
				}
			}
		});

		// Set add card button
		this.addCardButton = new JButton("Add card");
		this.addCardButton.setEnabled(false);
		this.addCardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<BoardChange> cards = game.popCards(1);

				if ((cards != null) && (cards.size() != 0)) {
					dataPanel.updateGameStatus();
					GuiBoard.updateGameGui();
					updateStatus("A card has been added.");
				} else {
					updateStatus("Unable to open card.");
				}
			}
		});

		// Play again button
		playAgainButton = new JButton();
		playAgainButton.setText("Play again");
		playAgainButton.setEnabled(false);
		playAgainButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					GameFrame gameFrame = new GameFrame();
					ArrayList<Player> players = new ArrayList<Player>();
					players.add(new Player(gameFrame, "Player", Color.BLACK.getRGB()));
					gameFrame.startGame(new Gameplay(players));
					dispose();
				}
		});

		// Set status label
		status = new JLabel();
		status.setVisible(true);

		// Set the data panel
		this.dataPanel = new GameDataPane(new JButton[] { clueButton,
				addCardButton, playAgainButton });

		// Add gui stuff
		this.getContentPane().add(this.GuiBoard);
		this.getContentPane().add(this.status, BorderLayout.PAGE_END);
		this.getContentPane().add(this.dataPanel, BorderLayout.LINE_END);

		this.updateStatus("Waiting for the game to start.");

		// Show form
		this.pack();
		this.setVisible(true);
	}

	private ArrayList<GuiCard> getSelectedCards() {
		return this.GuiBoard.getSelectedCards();
	}

	private void updateStatus(String text) {
		this.status.setText(text);
	}

	public void startGame(Gameplay game) {
		// Set the game
		this.game = game;
		this.game.AddGuiUpdater(this);
		this.GuiBoard.setGame(this.game);
		this.GuiBoard.updateGameGui();
		this.dataPanel.setGame(this.game);
		this.clueIndex = 0;
		this.clueButton.setEnabled(this.game.getMaxHintCount() > 0);
		this.addCardButton.setEnabled(this.game.canDrawCards());
		// Update status
		this.updateStatus("Game is on.");
	}

	// Updates the gui

	@Override
	public void updateGameGui() {
	}

	@Override
	public void gameOver(Player[] winners) {
		if (this.game.canPlayAgain())
		{
			this.playAgainButton.setEnabled(true);
		}
		
		if (winners == null)
		{
			this.game.RemoveThisPlayer();
			this.game.ClearBoard();
			this.dataPanel.updateGameGui();
			this.GuiBoard.updateGameGui();
		}
	}

	@Override
	public void callSetUpdateGameGui(BoardLocation[] location, Player player) {
		this.status.setText(player.getName() + " called set!");
		this.clueIndex = 0;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		ArrayList<GuiCard> selectedCards = getSelectedCards();

		if (selectedCards.size() >= Deck.PROPERTIES_OPTIONS) {
			BoardLocation[] boardLocation = new BoardLocation[selectedCards
					.size()];

			for (int i = 0; i < boardLocation.length; i++) {
				boardLocation[i] = selectedCards.get(i).getBoardLocation();
			}

			if (game.CallSet(game.getCurrentPlayer(), boardLocation)) {
				status.setText("Set!!");
			} else {
				status.setText("No set.");
			}

			this.GuiBoard.clearSelectionsOnBoard();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void notifyChanges(BoardChange[] changes) {
		this.GuiBoard.notifyChanges(changes);
		this.dataPanel.updateGameGui();
	}

	// ****** Window events ********

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (this.game != null) {
			try {
				this.game.RemoveThisPlayer();
			} catch (Exception ex) {
			}
		}
		this.dispose();
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
}
