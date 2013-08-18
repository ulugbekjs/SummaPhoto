import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;


public class tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File photoDir = new File("c:\\Users\\yonatan\\test");

		BufferedImage mainImg = new BufferedImage(3264,2448,
				BufferedImage.TYPE_INT_RGB);

		Slot[] slots = new Slot[8]; // slots[0] == Top Left Photo

		slots[0] = new Slot(new PixelPoint(0, 367), new PixelPoint(642, 1224));
		slots[1] = new Slot(new PixelPoint(0, 1224), new PixelPoint(642, 2080));
		slots[2] = new Slot(new PixelPoint(775, 1805), new PixelPoint(1632, 2448));
		slots[3] = new Slot(new PixelPoint(1632, 1805), new PixelPoint(2488, 2448));
		slots[4] = new Slot(new PixelPoint(2621, 1224), new PixelPoint(3264, 2080));
		slots[5] = new Slot(new PixelPoint(2621, 367), new PixelPoint(3264, 1224));
		slots[6] = new Slot(new PixelPoint(1632, 0), new PixelPoint(2488, 642));
		slots[7] = new Slot(new PixelPoint(775, 0), new PixelPoint(1632, 642));

		int i = 0;
		Graphics graphics = mainImg.createGraphics();

		for (File file : photoDir.listFiles()) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(file);

				BufferedImage resized = resizeImage(img, (int) slots[i].getWidth(), (int) slots[i].getHeight(), img.getType());
				//	Image resized = img.getScaledInstance((int) slots[i].getWidth(), (int) slots[i].getHeight(), Image.SCALE_FAST);

				PixelPoint topleftPixelPoint = slots[i].getTopLeft();
				PixelPoint bottomRightPixelPoint =  slots[i++].getBottomRight();

				graphics.drawImage(resized,
						topleftPixelPoint.getX(),
						topleftPixelPoint.getY(),
						bottomRightPixelPoint.getX(),
						bottomRightPixelPoint.getY(),
						0, 
						0,
						resized.getWidth(null),
						resized.getHeight(null),
						null);


				//	graphics.
			} catch (IOException e) {
				System.out.println("kaki");
			}
		}

		File mapDir = new File("c:\\Users\\yonatan\\map");

		for (File f : mapDir.listFiles()) {
			BufferedImage mapImg = null;
			try {
				mapImg = ImageIO.read(f);

				Template t= Template.getTemplate(1);
				BufferedImage resizedMap = resizeImage(mapImg, (int) t.getMapPixelWidth(), (int)t.getMapPixelHeight(), mapImg.getType());
				//	Image resized = img.getScaledInstance((int) slots[i].getWidth(), (int) slots[i].getHeight(), Image.SCALE_FAST);

				graphics.drawImage(resizedMap,
						t.getMapSlot().getTopLeft().getX(),
						t.getMapSlot().getTopLeft().getY(),
						t.getMapSlot().getBottomRight().getX(),
						t.getMapSlot().getBottomRight().getY(),
						0, 
						0,
						resizedMap.getWidth(null),
						resizedMap.getHeight(null),
						null);


				//	graphics.
			} catch (IOException e) {
				System.out.println("kaki");
			}

		}
		
		Graphics2D g2D= (Graphics2D) graphics;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setStroke(new BasicStroke(10));
		g2D.setColor(new Color(14390684));
		g2D.drawLine(642, 2080, 2448, 642);

		try {
			ImageIO.write(mainImg, 
					"jpg",
					new File("c:\\Users\\yonatan\\result.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight, int type){
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
		g.dispose();

		return resizedImage;
	}


}
