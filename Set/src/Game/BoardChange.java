package Game;

import java.io.Serializable;

public class BoardChange implements Serializable{
	private BoardLocation location;
	private Card newCard;
	
	public BoardChange(BoardLocation location, Card newCard) {
		super();
		this.location = location;
		this.newCard = newCard;
	}
	public BoardLocation getLocation() {
		return location;
	}
	public Card getNewCard() {
		return newCard;
	}
	public void setLocation(BoardLocation location) {
		this.location = location;
	}
	public void setNewCard(Card newCard) {
		this.newCard = newCard;
	}
	
	
}
