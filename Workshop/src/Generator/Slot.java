package Generator;

import Common.Photo;

public class Slot{
	
	private PixelPoint topLeft;
	private PixelPoint bottomRight;
	private PixelPoint topRight;
	private PixelPoint bottomLeft;
	
	private int orientation;	
	private Photo photo = null;
	
	public Slot(PixelPoint topLeft, PixelPoint bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		this.topRight = new PixelPoint(bottomRight.getX(), topLeft.getY());
		this.bottomLeft = new PixelPoint(topLeft.getX(), bottomRight.getY());
	}
	
	public  boolean isAssignedToPhoto() {
		return (photo != null);
	}

	public PixelPoint getTopLeft() {
		return this.topLeft;
	}
	
	public PixelPoint getTopRight() {
		return this.topRight;
	}
	
	public PixelPoint getBottomLeft() {
		return this.bottomLeft;
	}

	public PixelPoint getBottomRight() {
		return this.bottomRight;
	}

	public double getWidth() {
		return Math.abs(bottomRight.distanceFrom(new PixelPoint(topLeft.getX(), bottomRight.getY())));
	}

	public double getHeight() {
		return Math.abs(bottomRight.distanceFrom(new PixelPoint(bottomRight.getX(), topLeft.getY())));
	}

}