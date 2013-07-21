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
	
	public Point(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public double distanceFrom(Point p) {
		return Math.sqrt((this.longitude-p.getLongitude())*(this.longitude-p.getLongitude()) + 
				(this.latitude-p.getLatitude())*(this.latitude-p.getLatitude()));

	}
}
