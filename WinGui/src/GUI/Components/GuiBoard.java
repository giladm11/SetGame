package GUI.Components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import GUI.Frames.MainFrame;
import Game.Board;
import Game.BoardChange;
import Game.BoardLocation;
import Game.Gameplay;
import Game.Player;
import Game.Interfaces.IGuiUpdater;
import Game.Interfaces.NotifyChangesListener;

public class GuiBoard extends JPanel implements NotifyChangesListener, IGuiUpdater
{
	private static final int SPACE = MainFrame.SPACE;
	private static final Color BOARD_COLOR = Color.LIGHT_GRAY;
	
	private GuiCard[][] cardsHolder;
	private Gameplay game;
	
	public GuiBoard(MouseListener cardsMouseListiener)
	{
		this.setBackground(BOARD_COLOR);
		
		// Set the gui cards
		this.cardsHolder = new GuiCard[Board.COLUMS][Board.CARD_IN_COLUMN];
		
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.insets = new Insets(SPACE, SPACE, SPACE, SPACE);
		constraint.weightx = 1;
		constraint.weighty = 1;
		constraint.fill = GridBagConstraints.BOTH;
		
		for (int i = 0; i < this.cardsHolder.length; i++) {
			for (int j = 0; j < this.cardsHolder[i].length; j++) {
				this.cardsHolder[i][j] = new GuiCard(null, new BoardLocation(i, j));

				// Add select listiner
				this.cardsHolder[i][j].addMouseListener(cardsMouseListiener);
				
				// add the card
				constraint.gridx = j;
				constraint.gridy = i;
				this.add(this.cardsHolder[i][j], constraint);
			}
		}
	}
	
	public void setGame(Gameplay game) {
		this.game = game;
		this.game.AddGuiUpdater(this);
	}

	public ArrayList<GuiCard> getSelectedCards() {
		ArrayList<GuiCard> selectedCards = new ArrayList<GuiCard>();

		for (int i = 0; i < this.cardsHolder.length; i++) {
			for (int j = 0; j < this.cardsHolder[i].length; j++) {
				if (this.cardsHolder[i][j] != null) {
					if (this.cardsHolder[i][j].IsSelected()) {
						selectedCards.add(this.cardsHolder[i][j]);
					}
				}
			}
		}

		return selectedCards;
	}

	public void clearSelectionsOnBoard() {
		// Clear selection
		for (int i = 0; i < this.cardsHolder.length; i++) {
			for (int j = 0; j < this.cardsHolder[i].length; j++) {
				this.cardsHolder[i][j].ClearSelect();
			}
		}
	}
	
	public GuiCard getCard(BoardLocation location)
	{
		return this.getCard(location.getX(), location.getY());
	}
	
	public GuiCard getCard(int x, int y)
	{
		return this.cardsHolder[x][y];
	}
	
	@Override
	public void notifyChanges(BoardChange[] changes) {
		if (this.game != null)
		{
			for (BoardChange boardChange : changes) {
				this.cardsHolder[boardChange.getLocation().getX()]
								[boardChange.getLocation().getY()]
									.setCard(boardChange.getNewCard());
			}
		}
	}

	@Override
	public void updateGameGui() {
		for (int i = 0; i < this.cardsHolder.length; i++) {
			for (int j = 0; j < this.cardsHolder[i].length; j++) {
				this.cardsHolder[i][j].setCard(this.game.getBoard().getCard(i, j));
			}
		}
	}

	@Override
	public void callSetUpdateGameGui(BoardLocation[] location, Player player) {
		for (int i = 0; i < location.length; i++) {
			this.cardsHolder[location[i].getX()][location[i].getY()]
					.SetPlayerSetColor(player);
		}
	}

	@Override
	public void gameOver(Player[] winners) {
	}
}
