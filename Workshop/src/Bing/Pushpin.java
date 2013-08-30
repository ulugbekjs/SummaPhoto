package Bing;

import Common.GPSPoint;
import Generator.PixelPoint;

/**
 * Represents a pushpin returned from Bing
 * @author yonatan
 *
 */
public class Pushpin {
	private GPSPoint point;
	private PixelPoint anchor;
	private int[] topLeftOffset;
	private int[] bottomRightOffset;
	
	public GPSPoint getPoint() {
		return point;
	}

	public PixelPoint getAnchor() {
		return anchor;
	}

	public int[] getTopLeftOffset() {
		return topLeftOffset;
	}

	public int[] getBottomRightOffset() {
		return bottomRightOffset;
	}

	public Pushpin(GPSPoint point, PixelPoint anchor, int[] topLeftOffset, int[] bottomRightOffset) {
		this.point = point;
		this.anchor = anchor;
		this.topLeftOffset = topLeftOffset;
		this.bottomRightOffset = bottomRightOffset;
	}
}
