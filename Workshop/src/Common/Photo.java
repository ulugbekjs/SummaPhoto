package Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import android.R.string;

/**
 * Represents a photo in the gallery that was taken by the user
 * @author yonatan
 *
 */
public class Photo implements Comparable<Photo> {

	private DateTime takenDate;
//	private Date takenDate;
	private GPSPoint location;
	private boolean isHorizontal;
	private int height;
	private int width;
	private String path; // 
	private ActualEvent parentActualEvent;
	private double ID; //unique per Photo

	public Photo(Date date, int width, int height, GPSPoint location, String path) {
		this.takenDate = new DateTime(date);
		this.location = location;
		this.height = height;
		this.width = width;
		this.isHorizontal = (width > height);
		this.path = path;
		this.ID = takenDate.getMillis();
	}
	
	public File getPhotoFile() throws FileNotFoundException {
		File file = null;
		file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		return file;
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

	public void attachToEvent(ActualEvent event) {
		if (event == null)
			parentActualEvent = event;
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
