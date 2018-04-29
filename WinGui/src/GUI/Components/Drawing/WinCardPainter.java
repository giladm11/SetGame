package GUI.Components.Drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import GUI.Drawing.CardPainter;
import GUI.Drawing.IShapeDraw;
import Game.Card;

public class WinCardPainter extends CardPainter {
	
	private static final int BORDER_ROUND = 5;
	private final int INNER_LINE_SIZE = 2;
	private final int[][] PAINT_SIZES = new int[][]
			{
				new int[]{1},
				new int[]{INNER_LINE_SIZE * 2, INNER_LINE_SIZE},
				new int[]{1}
			};
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static final IShapeDraw[] drawFunctions =
			new IShapeDraw[]
					{
		new IShapeDraw() {
			@Override
			public void DrawShape(Object drawingBoard, int x, int y, int width,
					int height, boolean fill) {
				Graphics g = (Graphics)drawingBoard;
				if (fill)
					g.fillPolygon(new int[]{(x + (width / 2)), x, x + width},
							  new int[]{y, y + height, y + height},
							  3);
				else
					g.drawPolygon(new int[]{(x + (width / 2)), x, x + width},
							  new int[]{y, y + height, y + height},
							  3);
			}
		},
		new IShapeDraw() {
			@Override
			public void DrawShape(Object drawingBoard, int x, int y, int width,
					int height, boolean fill) {
				Graphics g = (Graphics)drawingBoard;
				if (fill)
					g.fillOval(x, y, width, height);
				else
					g.drawOval(x, y, width, height);
			}
		},
		new IShapeDraw() {
			@Override
			public void DrawShape(Object drawingBoard, int x, int y, int width,
					int height, boolean fill) {
				Graphics g = (Graphics)drawingBoard;
				if (fill)
					g.fillRoundRect(x, y, width, height, width / 4, height / 4);
				else
					g.drawRoundRect(x, y, width, height, width / 4, height / 4);
			}}
};

	private Paint GetPaint(Card c)
	{
		Color[][] colors = new Color[][]
				{
					new Color[]{TRANSPARENT},
					new Color[]{TRANSPARENT, new Color(c.getColor())},
					new Color[]{new Color(c.getColor())}
				};
		return new LinerPaint(colors[c.getFill().getValue() - 1],
		                      PAINT_SIZES[c.getFill().getValue() - 1]);	
	}
	
	public WinCardPainter() {
		super(drawFunctions);
	}

	@Override
	protected void setColor(Object drawingBoard, int rgba) {
		Graphics g = (Graphics)drawingBoard;
		g.setColor(new Color(rgba, true));
	}

	@Override
	protected void setStroke(Object drawingBoard, int size) {
		Graphics2D g = (Graphics2D)drawingBoard;
		g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	}

	@Override
	protected void setPaint(Object drawingBoard, Card card) {
		Graphics2D g = (Graphics2D)drawingBoard;
		g.setPaint(this.GetPaint(card));
	}

	@Override
	protected void drawBorder(Object drawingBoard, int x, int y, int width,
			int height) {
		Graphics2D g = (Graphics2D)drawingBoard;
		g.drawRoundRect(x, y, width, height, BORDER_ROUND, BORDER_ROUND);
	}
	
	
}
