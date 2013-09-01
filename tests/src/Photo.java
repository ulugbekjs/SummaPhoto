

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;


public class Photo {

///	private DateTime takenDate;
//	private Date takenDate;
//	private GPSPoint location;
	private boolean isHorizontal;
	private int height;
	private int width;
	private String path; // 
//	private ActualEvent parentActualEvent;
	private double ID; //unique per Photo

	public Photo(Date date, int width, int height, String path) {
//		this.takenDate = new DateTime(date);
//		this.location = location;
		this.height = height;
		this.width = width;
		this.isHorizontal = (width > height);
		this.path = path;
		//this.ID = takenDate.getMillis();
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

	

	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}



	


}
