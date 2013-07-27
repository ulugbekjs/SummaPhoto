package Bing;

import Common.Point;

public class Pushpin {
	private Point point;
	private int[] anchor;
	private int[] topLeftOffset;
	private int[] bottomRightOffset;
	
	public Point getPoint() {
		return point;
	}

	public int[] getAnchor() {
		return anchor;
	}

	public int[] getTopLeftOffset() {
		return topLeftOffset;
	}

	public int[] getBottomRightOffset() {
		return bottomRightOffset;
	}

	public Pushpin(Point point, int[] anchor, int[] topLeftOffset, int[] bottomRightOffset) {
		this.point = point;
		this.anchor = anchor;
		this.topLeftOffset = topLeftOffset;
		this.bottomRightOffset = bottomRightOffset;
	}
}
