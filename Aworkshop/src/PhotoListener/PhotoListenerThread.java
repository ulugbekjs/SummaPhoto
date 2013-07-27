package PhotoListener;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.media.ExifInterface;
import android.os.Environment;


import Common.Photo;
import Common.Point;

public class PhotoListenerThread {

	public PhotoListenerThread() {
	}

	public Photo createPhotoFromFile(File path) {

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//get location
		float coordinate[] = null;
		boolean res = exif.getLatLong(coordinate);
		if (!res) {
			// TODO: write to log
		}

		//get time
		String timeString = exif.getAttribute(ExifInterface.TAG_DATETIME);


		//get dimensions
		int width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
		int	height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
		if (width == 0 || height == 0) {
			//TODO: something
		}
		
		Photo photo = null;

		photo = new Photo(
				new Date(timeString),
				width,
				height,
				new Point(coordinate[0], coordinate[1]),
				true,
				path.getPath());


		return photo;
	}

	/**
	 * use EXIFInterface from android to decide orientation
	 * @param width
	 * @param height
	 * @return
	 */
	// TODO: implement
	private static boolean isHorizontal(int width, int height) {
		return false;

	}
	
	// TODO: switch to private
	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
