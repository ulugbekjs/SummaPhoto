package Common;

public class Point {

	private double longitude;
	private double latitude;

	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	
	public Point(double latitude, double longitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public double distanceFrom(Point p) {
		return Math.sqrt((this.longitude-p.getLongitude())*(this.longitude-p.getLongitude()) + 
				(this.latitude-p.getLatitude())*(this.latitude-p.getLatitude()));
	}
	
	@Override
	public String toString() {
		return (latitude + "," + longitude);
	}
}
