package GUI.Drawing;

import java.awt.Color;

import Game.Card;

public abstract class CardPainter {
	private final int SPACE = 10;
	private final int SHAPE_SIZE = 30;
	public final int CARD_WIDTH = SHAPE_SIZE + (SPACE * 4);
	public final int CARD_HEIGHT = (SHAPE_SIZE * 3) + (SPACE * 4);
	private final int LINE_SIZE = SHAPE_SIZE / 10;
	
	private IShapeDraw[] shapesDrawers;
	
	public CardPainter(IShapeDraw[] shapesDrawers)
	{
		this.shapesDrawers = shapesDrawers;
	}
	
	public void DrawCard(Object drawingBoard, int x, int y, int width, int height, Card card)
	{
		int shapeHeight = (height - (4 * SPACE)) / 3;
		int shapeWidth = width - (SPACE * 4);
		int posx = x + SPACE * 2;
		
		this.setColor(drawingBoard, card.getColor());

		this.setStroke(drawingBoard, LINE_SIZE);
		
		this.setColor(drawingBoard, Color.BLACK.getRGB());
		
		for (int i = 0; i < card.getNumber(); i++)
		{
			int posy = (int)(y + (SPACE * (2 - (0.5 * (card.getNumber() - 1)))) +
					   (shapeHeight + SPACE) * i + (1 - ((card.getNumber() - 1) * 0.5)) * shapeHeight);
			
			// Set thee paint
			this.setPaint(drawingBoard, card);
			shapesDrawers[card.getShape() - 1].DrawShape(drawingBoard, posx, posy, shapeWidth, shapeHeight, true);
			this.setColor(drawingBoard, card.getColor());
			shapesDrawers[card.getShape() - 1].DrawShape(drawingBoard, posx, posy, shapeWidth, shapeHeight, false);
			
		}
		
		this.setColor(drawingBoard, Color.BLACK.getRGB());
		this.drawBorder(drawingBoard, x, y, width, height);
	}
	
	protected abstract void setColor(Object drawingBoard, int rgba);
	protected abstract void setStroke(Object drawingBoard, int size);
	protected abstract void setPaint(Object drawingBoard, Card card);
	protected abstract void drawBorder(Object drawingBoard, int x, int y, int width, int height);
}
