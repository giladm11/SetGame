package Game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import Game.Interfaces.IGuiUpdater;

public class Gameplay {
	private final int DEFAULT_CARDS = 12;
	private final int MAX_HINT_COUNT = Deck.PROPERTIES_OPTIONS;

	private Stack<Card> deck;
	protected Board board;
	protected ArrayList<Player> players;
	protected ArrayList<BoardLocation[]> boardSets;
	protected boolean isGameActive;
	protected ArrayList<IGuiUpdater> updateGuiEvents;
	protected int maxHintCount;

	public Gameplay(ArrayList<Player> players) {
		this.isGameActive = false;
		this.deck = (new Deck()).Shuffle().ToPile();
		
		// TODO delete
		/*for (int i = 0; i < 70; i++) {
			this.deck.pop();
		}*/
		
		this.board = new Board();
		this.boardSets = new ArrayList<BoardLocation[]>();
		// Set the players
		this.players = players;
		this.updateGuiEvents = new ArrayList<IGuiUpdater>();
		this.maxHintCount = MAX_HINT_COUNT;
		this.prepareGame();
	}

	protected void prepareGame() {
		this.isGameActive = true;

		// Set the cards on the board
		Vector<BoardChange> cardsChanges = new Vector<BoardChange>();
		cardsChanges.addAll(this.popCards(this.DEFAULT_CARDS - 1));
		cardsChanges.addAll(this.popCards(-1));
		
		// Notify players
		this.NotifyPlayerForChanges(
				(BoardChange[]) cardsChanges.toArray(
						new BoardChange[cardsChanges.size()]));
	}

	public Stack<Card> getDeck() {
		return deck;
	}

	public Board getBoard() {
		return this.board;
	}

	public Vector<BoardChange> popCards(int numberOfCards) {
		Vector<BoardChange> popedChanges = new Vector<BoardChange>();

		numberOfCards = numberOfCards > this.board.getAvilableSpace() ? this.board.getAvilableSpace() : numberOfCards;
		numberOfCards = numberOfCards < 0 ? -1 : numberOfCards;

		while ((numberOfCards != 0) && (this.getCardsOnDeck() > 0)
				&& this.board.getAvilableSpace() > 0) {
			// POP CARD
			Card card = this.deck.pop();
			BoardLocation location = this.board.addCard(card);
			popedChanges.add(new BoardChange(location, card));

			// Check if to continue
			if (numberOfCards > 0) {
				numberOfCards--;
			} else {
				// If deal neccery
				if (this.getCardsOnBoards() >= DEFAULT_CARDS) {
					this.calculateAndUpdateBoardSets();
					numberOfCards = (this.getBoardSets().size() == 0) ? (numberOfCards * 2)
									: numberOfCards + 1;
				}
			}
		}

		this.calculateAndUpdateBoardSets();

		// if there is no space available
		return popedChanges;
	}

	// Checks for sets
	public boolean IsSet(BoardLocation[] locations) {
		if (locations.length != Deck.PROPERTIES_OPTIONS) {
			return false;
		} else {
			// First int = color int
			// Secount int = his number
			Hashtable<Integer, Integer> hshColors = new Hashtable<Integer, Integer>();

			for (int i = 0; i < Deck.COLORS.length; i++) {
				hshColors.put(Deck.COLORS[i], i + 1);
			}
			// Get the cards
			Card[] cards = new Card[locations.length];

			// Add the cards
			for (int i = 0; i < cards.length; i++) {
				if (this.board
						.getCard(locations[i].getX(), locations[i].getY()) != null) {
					cards[i] = this.board.getCard(locations[i].getX(),
							locations[i].getY());
				} else {
					return false;
				}
			}

			// Set the properties counter
			int nNumbers = 0;
			int nShapes = 0;
			int nFillings = 0;
			int nColors = 0;

			// Add the cards
			for (int i = 0; i < cards.length; i++) {
				nNumbers += cards[i].getNumber();
				nShapes += cards[i].getShape();
				nFillings += cards[i].getFill().getValue();
				nColors += hshColors.get(cards[i].getColor());
			}

			// Check to zero
			nNumbers = nNumbers % Deck.PROPERTIES_OPTIONS;
			nShapes = nShapes % Deck.PROPERTIES_OPTIONS;
			nFillings = nFillings % Deck.PROPERTIES_OPTIONS;
			nColors = nColors % Deck.PROPERTIES_OPTIONS;

			// Return the result
			return ((nNumbers + nShapes + nFillings + nColors) == 0);
		}
	}

	public boolean CallSet(Player player, BoardLocation[] locations) {
		if (locations.length != Deck.PROPERTIES_OPTIONS) {
			return false;
		} else {
			if (this.IsSet(locations)) {
				this.updateSetAllGameGuis(locations, player);

				// Remove cards
				this.board.removeCards(locations);

				BoardChange[] changes = new BoardChange[locations.length];

				for (int i = 0; i < locations.length; i++) {
					changes[i] = new BoardChange(locations[i], null);
				}

				this.NotifyPlayerForChanges(changes);

				// Add player score
				player.addScore();

				// Add the cards that neccery
				Vector<BoardChange> addedCards = this.popCards(-1);

				this.NotifyPlayerForChanges(
						(BoardChange[]) addedCards.toArray(
								new BoardChange[addedCards.size()]));				
				return true;
			} else {
				return false;
			}
		}
	}

	protected void RemoveCards(BoardLocation[] locations) {
		this.board.removeCards(locations);
	}

	public int getCardsOnBoards() {
		return this.board.getCardsOnBoardCount();
	}

	protected void calculateAndUpdateBoardSets() {
		ArrayList<BoardLocation[]> lstSets = this.boardSets;
		lstSets.clear();

		for (int i = 0; i < this.getCardsOnBoards(); i++) {
			for (int j = (i + 1); j < this.getCardsOnBoards(); j++) {
				for (int k = (j + 1); k < this.getCardsOnBoards(); k++) {
					// Set the board locations
					BoardLocation[] locations = new BoardLocation[] {
							this.board.getLocation(i),
							this.board.getLocation(j),
							this.board.getLocation(k) };

					if (this.IsSet(locations)) {
						lstSets.add(locations);
					}
				}
			}
		}

		// Check if the game is over
		if (this.boardSets.size() == 0 &&
			this.getCardsOnDeck() == 0
			&& this.isGameActive) {
			this.DoGameOver();
		}
	}

	public Card[] boardToArray() {

		return this.boardToArray();
	}

	public ArrayList<BoardLocation[]> getBoardSets() {
		return boardSets;
	}

	public Player getCurrentPlayer() {
		return this.players.get(0);
	}

	protected void NotifyPlayerForChanges(BoardChange[] changes) {
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).InvokeChangesNotification(changes);
		}
	}

	public boolean isGameActive() {
		return isGameActive;
	}

	public void AddGuiUpdater(IGuiUpdater gui) {
		this.updateGuiEvents.add(gui);
	}

	// Gui stuff

	protected void updateAllGameGuis() {
		for (IGuiUpdater updateGui : this.updateGuiEvents) {
			updateGui.updateGameGui();
		}
	}

	protected void updateSetAllGameGuis(BoardLocation[] locations, Player p) {
		for (IGuiUpdater updateGui : this.updateGuiEvents) {
			updateGui.callSetUpdateGameGui(locations, p);
		}
	}

	protected void updateAllGameOver(Player[] winners) {
		for (IGuiUpdater updateGui : this.updateGuiEvents) {
			updateGui.gameOver(winners);
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getCardsOnDeck() {
		return this.deck.size();
	}

	public int getMaxHintCount() {
		return maxHintCount;
	}

	public Card getCard(int nRow, int nCol) {
		return this.board.getCard(nRow, nCol);
	}
	
	public void RemoveThisPlayer()
	{
		this.players.remove(0);
	}
	
	private void DoGameOver()
	{
		this.isGameActive = false;

		ArrayList<Player> winners = new ArrayList<Player>();

		// Get winners
		for (Player currentPlayer : players) {
			if (winners.size() == 0) {
				winners.add(currentPlayer);
			} else {
				if (currentPlayer.getScore() >= winners.get(0).getScore()) {
					if (currentPlayer.getScore() > winners.get(0)
							.getScore()) {
						winners.clear();
					}

					winners.add(currentPlayer);
				}
			}
		}

		this.updateAllGameOver((Player[]) winners
				.toArray(new Player[winners.size()]));
	}
	
	public boolean canDrawCards()
	{
		return true;
	}
	
	public boolean canPlayAgain()
	{
		return true;
	}
	
	public void ClearBoard()
	{
		this.board = new Board();
	}
}
