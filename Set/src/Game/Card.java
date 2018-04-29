/**
 * 
 */
package Game;

import java.io.Serializable;

/**
 * @author Chencha
 * Set Game Card
 */
public class Card implements Serializable
{	
	private int number;
	private Filling fill;
	private int color;
	private int shape;
	
	public Card(int number, Filling fill, int color, int shape) {
		super();
		this.number = number;
		this.fill = fill;
		this.color = color;
		this.shape = shape;
	}
	
	public Card(Card newCard) {
		this(newCard.getNumber(),
			 newCard.getFill(),
			 newCard.getColor(),
			 newCard.getShape());
	}

	public int getColor() {
		return color;
	}
	
	public Filling getFill() {
		return fill;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getShape() {
		return shape;
	}
	
}
