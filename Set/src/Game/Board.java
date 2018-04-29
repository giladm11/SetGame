package Game;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.Vector;

public class Board
{
	public static final int CARD_IN_COLUMN = 4;
	public static final int COLUMS = 4;
	
	private Hashtable<Integer, Card> boardCards;
	private Vector<Integer> boardEmpty;
	private int rows;
	private int columns;
	
	public Board(int rows, int columns)
	{			
		this.rows = rows;
		this.columns = columns;
		
		this.boardCards = new Hashtable<Integer, Card>();
		this.boardEmpty = new Stack<Integer>();
		
		int maxOfCards = rows * columns;
		
		for (int i = 0; i < maxOfCards; i++) {
			this.boardEmpty.add(i);
		}
	}
	
	public Board()
	{
		this(CARD_IN_COLUMN, COLUMS);
	}
	
	public Card getCard(int location)
	{
		return (this.boardCards.containsKey(location)) ?
				this.boardCards.get(location) : null;
	}
	
	public Card getCard(int row, int column)
	{
		return this.getCard(this.getLocation(row, column));
	}
	
	private int getLocation(int row, int col)
	{
		return (row * this.getColumns()) + col;
	}
	
	public BoardLocation getLocation(int location)
	{
		return new BoardLocation(this.getRow(location), this.getColumn(location));
	}
	
	private int getRow(int location)
	{
		return (location / this.getColumns());
	}
	
	private int getColumn(int location)
	{
		return (location % this.getColumns());
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public int getCardsOnBoardCount() {
		return this.boardCards.size();
	}
	
	
	public void removeCards(BoardLocation[] locations)
	{
		for (BoardLocation boardLocation : locations) {
			int location = this.getLocation(boardLocation.getX(), boardLocation.getY());
			if (this.boardCards.containsKey(location))
			{
				this.boardCards.remove(location);
				this.boardEmpty.add(location);
			}
		}
		
		Collections.sort(this.boardEmpty);
	}
	
	public BoardLocation addCard(Card c)
	{
		if (this.boardEmpty.size() > 0)
		{
			int location = this.boardEmpty.remove(0);
			this.boardCards.put(location, c);
			return this.getLocation(location);
		}
		
		return null;
	}
	
	public Card[][] getMatrixBoard()
	{
		Card[][] metrixCards = new Card[this.getRows()][this.getColumns()];
		
		for (Entry<Integer, Card> cardEntry : this.boardCards.entrySet()) {
			metrixCards[this.getRow(cardEntry.getKey())][this.getColumn(cardEntry.getKey())] = 
					cardEntry.getValue();
		}
		
		return metrixCards;
	}
	
	public int getMaxCards()
	{
		return this.boardCards.size() + this.boardEmpty.size();
	}
	
	public int getAvilableSpace()
	{
		return this.boardEmpty.size();
	}
	
	public Card[] getBoardArray()
	{
		return (Card[]) boardCards.values().toArray(new Card[boardCards.values().size()]);
	}
	
	public int getLocation(Card card)
	{
		for (Entry<Integer, Card> cardEntry : this.boardCards.entrySet()) {
			if (cardEntry.getValue() == card)
			{
				return cardEntry.getKey();
			}
		}
		
		return -1;
	}
}
