package Common;

import android.location.Location;

public class GPSPoint  {

	private double longitude;
	private double latitude;

	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	
	public GPSPoint(double latitude, double longitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * method to return real distance between two GPS Points
	 * @param p
	 * @return
	 */
	public float distanceFrom(GPSPoint p) {
		float[] results = new float[3]; 
		// Using Android's Location.distanceBetween to ensure that calculation of distance is more accurate 
		Location.distanceBetween(this.latitude, this.longitude, p.getLatitude(), p.getLongitude(), results);
		return results[0];
	}
	
	@Override
	public String toString() {
		return (latitude + "," + longitude);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (!(other instanceof GPSPoint))
			return false;
		GPSPoint otherGPSPoint = (GPSPoint) other;
		return ((otherGPSPoint.getLatitude() == latitude) && (otherGPSPoint.getLongitude() == longitude));
	}
	
}
