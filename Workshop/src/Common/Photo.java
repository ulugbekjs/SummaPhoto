package Common;

import java.io.File;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

/**
 * Represents a photo in the gallery that was taken by the user
 * @author yonatan
 *
 */
public class Photo implements Comparable<Photo> {

	private DateTime takenDate;
	private GPSPoint location;
	private boolean isHorizontal;
	private int height;
	private int width;
	private String path; // 
	private String fileName;
	private double ID; //unique per Photo

	public Photo(Date date, int width, int height, GPSPoint location, String path) {
		this.takenDate = new DateTime(date);
		this.location = location;
		this.height = height;
		this.width = width;
		this.isHorizontal = (width > height);
		this.path = path;
		this.fileName = new File(path).getName();
		this.ID = takenDate.getMillis();
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isHorizontal() {
		return isHorizontal;
	}
	
	public Double getID() {
		return ID;
	}
	
	public String getFilePath()
	{
		return this.path;
	}

	public GPSPoint getLocation() {
		return location;
	}

	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}

	public DateTime getTakenDate() {
		return this.takenDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result
				+ ((takenDate == null) ? 0 : takenDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Photo other = (Photo) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (takenDate == null) {
			if (other.takenDate != null)
				return false;
		} else if (!takenDate.equals(other.takenDate))
			return false;
		return true;
	}

	public double distanceFrom(Photo otherPhoto) {
		return this.getLocation().distanceFrom(otherPhoto.getLocation());
	}

	public int timeDeltaInSecondsFrom(Photo otherPhoto) {
		DateTime thisTime = new DateTime(takenDate);
		DateTime otherTime = new DateTime(otherPhoto.getTakenDate());

		return Math.abs(Seconds.secondsBetween(otherTime,thisTime).getSeconds());
		
//		Date otherTime = otherPhoto.getTakenDate();
//		return (int) Math.abs((otherTime.getTime() - this.takenDate.getTime()) / 1000);
	}

	@Override
	public int compareTo(Photo o) {
		return this.takenDate.compareTo(o.takenDate);
	}


}
