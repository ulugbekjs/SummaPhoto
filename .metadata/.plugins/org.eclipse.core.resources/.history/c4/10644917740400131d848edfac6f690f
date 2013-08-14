package Generator;


public class SlotBoundingBox{
	
	private PixelPoint topLeft;
	private PixelPoint bottomRight;
	
	public SlotBoundingBox(PixelPoint topLeft, PixelPoint bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	public PixelPoint getTopLeft() {
		return this.topLeft;
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
