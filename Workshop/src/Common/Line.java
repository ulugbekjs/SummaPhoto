package Common;


import Generator.PixelPoint;
/**
 * This class reprsents a line; SOULD NOT BE USED for line with undefined slope (line that is vertical to X-axis)
 * @author omri
 *
 */
public class Line {
	
	private double slope;
	
	private double constant;
	
	private PixelPoint pointA;
	private PixelPoint pointB;
	
	public Line (PixelPoint pointA, PixelPoint pointB)
	{
		this.pointA = pointA;
		this.pointB = pointB;
		slope = calculateSlope();
		constant = calculateConst();
	}
	
	public double getSlope() {
		return slope;
	}
	
	public double getConstant() {
		return constant;
	}
	
	/**
	 * @param x
	 * @return the value of y of the line
	 */
	
	public double getY (double x)
	{
		if (getSlope() == Double.NaN)
			return Double.NaN;
		return (x * getSlope() + constant);
	}
	
	
	public PixelPoint getPointA() {
		return pointA;
	}
	
	public PixelPoint getPointB() {
		return pointB;
	}
	
	/** 
	 * @return the angle between Y-axis to the line from pointA to pointB. The value is [0...360]
	 * while 0 is the north, 90 is the east etc.
	 */
	
	public double getTetaFromYAxis ()
	{
		/**
		if ((getConstant() == Double.NaN) || (getSlope() == Double.NaN))
			return Double.NaN;
		double theta = Math.atan2(pointB.getY() - pointA.getY(), pointB.getX() - pointA.getX());
		theta += Math.PI/2.0;
		double angle = Math.toDegrees(theta);
		if (angle < 0) {
			angle += 360;
		}
		return angle;
		**/
		float angle = (float) Math.toDegrees(Math.atan2(pointB.getX() - pointA.getX(), pointB.getY() - pointA.getY()));

	    if(angle < 0){
	        angle += 360;
	    }

	    return angle;
	}

	/**
	 * @return calculated slop of the line. If topLeft/buttomRight is null, or if the line is vertical to the X-axis
	 * NaN is returned
	 */
	private Double calculateSlope ()
	{
		Double deltaY;
		Double deltaX;
		Double slope;
		if ((pointA == null) || (pointB == null))
		{
			return Double.NaN;
		}
		if (pointA.getX() ==  pointB.getX())
		{
			return Double.NaN;
		}
			
		if (pointA.getX() > pointB.getX() ){
			deltaY = (double) (pointA.getY() - pointB.getY());
			deltaX = (double) (pointA.getX() - pointB.getX());
		}
		else {
			deltaY = (double) (pointB.getY() - pointA.getY() );
			deltaX = (double) (pointB.getX()- pointA.getX());
		}
		slope = deltaY / deltaX;
		return slope;
	}
	/**
	 * @return the constant of the line. If the slope is undefined retrun NaN
	 */
	private Double calculateConst()

	{
		if (Double.isNaN(slope))
			return Double.NaN;
		return (pointA.getY() - constant*pointA.getX());
	}
	
	
	/**
	 * @param point
	 * @return whether the point is above this line
	 */
	public Boolean isPointAboveLine (PixelPoint point)
	{
		if (point.getX() * slope + constant < point.getY())
			return true;
		return false;
	}

	

}
