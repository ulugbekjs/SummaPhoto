package Generator;

/**
 * This class represents a line; SOULD NOT BE USED for line with undefined slope (line that is vertical to X-axis)
 * @author omri
 *
 */
public class Line {
	
	private double slope;
	
	private double constant;
	
	private PixelPoint fromPoint;
	private PixelPoint toPoint;
	
	private boolean isUndefinedSlope = false;
	
	public Line (PixelPoint fromPoint, PixelPoint toPoint)
	{
		this.fromPoint = fromPoint;
		this.toPoint = toPoint;
		slope = calculateSlope();
		constant = calculateConst();
	}
	
	public double getSlope() {
		return slope;
	}
	
	public double getConstant() {
		return constant;
	}
	
	public int getLineXDelta() {
		return Math.abs(toPoint.getX() - fromPoint.getX());
	}
	
	public int getLineYDelta() {
		return Math.abs(toPoint.getY() - fromPoint.getY());
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
	
	
	public PixelPoint getFromPoint() {
		return fromPoint;
	}
	
	public PixelPoint getToPoint() {
		return toPoint;
	}
	
	/** 
	 * @return the angle between Y-axis to the line from pointA to pointB. The value is [0...360]
	 * while 0 is the north, 90 is the east etc.
	 */
	
	public double getTetaFromYAxis ()
	{
				
		// in case that the line is vertical to the x-Axis the the slope is undefined but the angle is 0
		if (isUndefinedSlope)
		{
			return 0;
		}
		float angle = (float) Math.toDegrees(Math.atan2(toPoint.getX() - fromPoint.getX(), toPoint.getY() - fromPoint.getY()));

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
		if ((fromPoint == null) || (toPoint == null))
		{
			return Double.NaN;
		}
		if (fromPoint.getX() ==  toPoint.getX())
		{
			isUndefinedSlope = true;
			return Double.NaN;
		}
			
		if (fromPoint.getX() > toPoint.getX() ){
			deltaY = (double) (fromPoint.getY() - toPoint.getY());
			deltaX = (double) (fromPoint.getX() - toPoint.getX());
		}
		else {
			deltaY = (double) (toPoint.getY() - fromPoint.getY() );
			deltaX = (double) (toPoint.getX()- fromPoint.getX());
		}
		slope = deltaY / deltaX;
		return slope;
	}
	/**
	 * @return the constant of the line. If the slope is undefined retrun NaN
	 */
	private Double calculateConst()

	{
		if (isUndefinedSlope)
			return Double.NaN;
		return (fromPoint.getY() - slope*fromPoint.getX());
	}
	
	
	/**
	 * @param point
	 * @return whether the point is above this line. If the scope is undefined, returns true if the x coordinate of the point is
	 * bigger than the x coordinate of the line
	 */
	public Boolean isPointAboveLine (PixelPoint point)
	{
		if (point == null)
			return false;
		if (isUndefinedSlope)
		{
			return  (point.getX()> fromPoint.getX()); 
		}
		if ((point.getX() * slope + constant) < point.getY())
			return true;
		return false;
	}
	
	public Boolean isUndefiendSlope()
	{
		return this.isUndefinedSlope;
	}
	
	

	

}
