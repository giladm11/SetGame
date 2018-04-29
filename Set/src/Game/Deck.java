package Game;

import java.util.Random;
import java.util.Stack;

public class Deck {
	public static final int PROPERTIES_OPTIONS = 3;
	private final int NUM_OF_PROPERTIES = 4;
												// Red, Green, Blue
	public static final int[] COLORS = new int[] {-65536, -16711936, -16776961};
	private final Filling[] FILLINGS = new Filling[]{Filling.EMPTY, Filling.HALF, Filling.FULL};
	
	private Card[] cards;
	
	public Deck()
	{
		cards = new Card[(int)Math.pow(PROPERTIES_OPTIONS, NUM_OF_PROPERTIES)];
		
		for (int i = 1; i <= cards.length; i++)
		{
			cards[i - 1] = new Card((i % PROPERTIES_OPTIONS) + 1,
									FILLINGS[((i / PROPERTIES_OPTIONS) % PROPERTIES_OPTIONS)],
									COLORS[(int)(i / Math.pow(PROPERTIES_OPTIONS, 2)) % PROPERTIES_OPTIONS],
									(int)((i / Math.pow(PROPERTIES_OPTIONS, 3)) % PROPERTIES_OPTIONS) + 1);
		}
	}

	public Card[] getCards() {
		return cards;
	}
	
	public Deck Shuffle(int times)
	{
		for (int i = 0; i < this.cards.length; i++)
		{
			Random rnd = new Random();
			int pos = rnd.nextInt(this.cards.length);
			
			if (i != pos)
			{
				Card tmp = this.cards[i];
				this.cards[i] = this.cards[pos];
				this.cards[pos] = tmp;
			}
		}
		
		return this;
	}
	
	public Deck Shuffle()
	{
		return this.Shuffle(5);
	}
	
	public Stack<Card> ToPile()
	{
		Stack<Card> pile = new Stack<Card>();
		
		for (int i = 0; i < this.cards.length; i++) {
			pile.push(this.cards[i]);
		}
		
		return pile;
	}
}
