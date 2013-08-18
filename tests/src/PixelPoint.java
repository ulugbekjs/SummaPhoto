

public class PixelPoint{
	private int x;
	private int y;

	public int getY() {
		return this.y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public PixelPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public double distanceFrom(PixelPoint p) {
		return Math.sqrt(Math.pow(this.x-p.getX(),2) + Math.pow(this.y-p.getY(),2));
	}

}
