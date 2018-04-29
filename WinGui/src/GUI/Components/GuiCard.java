package GUI.Components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import GUI.Components.Drawing.WinCardPainter;
import Game.BoardLocation;
import Game.Card;
import Game.Player;

public class GuiCard extends JComponent {
	private Card card;
	private boolean isSelected;
	private Color backColor;
	private BoardLocation boardLocation;
	
	private final Color DEFAULT_COLOR = Color.WHITE;
	private final Color SELECTED_COLOR = Color.BLACK;
	private final Color CLUE_COLOR = Color.PINK;
	private final Color EMPTY_COLOR = Color.GRAY;
	private static final WinCardPainter CARD_PAINTER = new WinCardPainter();
	public static final int WIDTH = CARD_PAINTER.CARD_WIDTH + 1;
	public static final int HEIGHT = CARD_PAINTER.CARD_HEIGHT + 1;

	public GuiCard(Card card, BoardLocation boardLocation) {
		super();
		this.card = card;
		this.boardLocation = boardLocation;
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.SetIsSelected(false);
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				SetIsSelected(!IsSelected());
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		this.setVisible(true);
	}
	
	public Card getCard() {
		return card;
	}
	
	public void setCard(Card card) {
		this.card = card;
		this.SetBackColor();
	}
	
	public boolean IsSelected() {
		return this.isSelected;
	}
	
	public void ClearSelect()
	{
		this.SetIsSelected(false);
	}
	
	private void SetIsSelected(boolean value)
	{
		this.isSelected = (this.card != null) ? value : false;
		this.SetBackColor();
	}
	
	public void SetClueColor()
	{
		this.SetBackColor(CLUE_COLOR);
	}
	
	public void SetPlayerSetColor(Player p)
	{
		this.SetBackColor(new Color(p.getColor()));
	}
	
	private void SetBackColor(Color color)
	{
		this.backColor = color;
		this.repaint();
	}
	
	private void SetBackColor()
	{
		this.SetBackColor(this.getBackColor());
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(this.backColor);
		g.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 5, 5);
			
		if (this.card != null)
		{
			CARD_PAINTER.DrawCard(g, 0, 0, this.getWidth() - 1, this.getHeight() - 1, this.getCard());
		}
		
		g.setColor(DEFAULT_COLOR);
	}
	
	public BoardLocation getBoardLocation() {
		return boardLocation;
	}
	
	private Color getBackColor()
	{
		return (this.card == null) ? EMPTY_COLOR :
				(this.isSelected ? SELECTED_COLOR : DEFAULT_COLOR);
	}
}
