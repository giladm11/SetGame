package GUI.Components.Drawing;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

public class LinerPaint extends TexturePaint
{

	public LinerPaint(Color[] Colors, int[] sizes, boolean vertical) {
		super(getBufferedImage(Colors, sizes, vertical),
			  new Rectangle(0,0,(vertical ? 1 : getSum(sizes)) ,(vertical ? getSum(sizes) : 1)));
	}
	
	public LinerPaint(Color[] Colors, int[] sizes) {
		this(Colors, sizes, true);
	}
	private static BufferedImage getBufferedImage(Color[] colors, int[] sizes, boolean vertical)
	{
		int[] picSizes = new int[]{1,1};
		int sumSizes = getSum(sizes);

		int nIsVerical = (vertical ? 1 : 0);
		picSizes[nIsVerical] = sumSizes;
				
		BufferedImage image = new BufferedImage(picSizes[0], picSizes[1], BufferedImage.TYPE_INT_ARGB);
		
		int imageIndex = 0;
		
		for (int colorIndex = 0; colorIndex < colors.length; colorIndex++) {
			for (int i = 0; i < sizes[colorIndex]; i++)
			{
				image.setRGB(((nIsVerical + 1) % 2) * imageIndex,
							 nIsVerical * imageIndex,
							 colors[colorIndex].getRGB());
				imageIndex++;
			}
		}
		return image;
	}
	
	private static int getSum(int[] arr)
	{
		int sum = 0;
		
		// Get the sizeSum
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		
		return sum;
	}
}
