package PhotoListener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import Common.Photo;
import Common.Point;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.FileObserver;

public class PhotoListener extends FileObserver {

	String absolutePath;

	public PhotoListener(String path) {
		super(path, FileObserver.CREATE);
		this.absolutePath = path;
	}

	@Override
	public void onEvent(int event, String path) {
		if (isExternalStorageReadable() && path.endsWith(".jpg")) {
			String file = absolutePath + System.getProperty("file.separator") + path;
			Photo photo = createPhotoFromFile(file);
			
		}
		else {
			// TODO: write to log
		}
	}
	

	public static Photo createPhotoFromFile(String file) {

		File path = new File(file);

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//get location
		double latitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE, 0);
		double longitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LONGITUDE, 0);
		Point location = new Point(latitude, longitude);

		//get time
		SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
		String timeString = exif.getAttribute(ExifInterface.TAG_DATETIME);
		Date date = null;
		try {
			date = format.parse(timeString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//get dimensions
		int width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
		int	height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
		if (width == 0 || height == 0) {
			//TODO: something
		}

		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

		Photo photo = new Photo(
				date,		
				width,
				height,
				location,
				isHorizontal(orientation, width,height),
				path.getPath());


		return photo;
	}

	/**
	 * use EXIFInterface from android to decide orientation
	 * @param width
	 * @param height
	 * @return
	 */
	private static boolean isHorizontal(int orientation, int width, int height) {
		if (orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL) {
			return true;
		}
		else {
			if (orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL)
				return false;
			else { // if EXIF data of orientation not available, decide manually
				if (width >= height)
					return true;
			}
		}
		return false;
	}
	
	// TODO: switch to private
		/* Checks if external storage is available to at least read */
		private static boolean isExternalStorageReadable() {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state) ||
					Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				return true;
			}
			return false;
		}

}
