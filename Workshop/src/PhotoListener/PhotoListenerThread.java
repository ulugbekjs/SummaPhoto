package PhotoListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

import ActivationManager.ActivationManager;
import Common.GPSPoint;
import Common.Photo;
import android.R.integer;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

/**
 * The thread that observes the camera's folder
 * @author yonatan
 *
 */
public class PhotoListenerThread extends FileObserver {

	private final String TAG = PhotoListenerThread.class.getName();
	String absolutePath;

	public PhotoListenerThread(String path) {
		super(path, FileObserver.CLOSE_WRITE);
		this.absolutePath = path;
	}

	//	@Override
	//	public void onEvent(int event, String path) {
	//		try{
	//			if (isExternalStorageReadable()) {
	//				if (path.endsWith(".jpg")) {
	//					Photo photo = null;
	//					GeoLocation location = null;
	//					while (photo == null) {
	//						String file = absolutePath + path;
	//						try {
	//							photo = createPhotoFromFile(file, location );
	//						}
	//						catch (ImageProcessingException e) { // this means photo was not saved fully
	//							Log.e(TAG, "Sleeping until photo completely saved");
	//							int counter = 0;
	//							try {
	//								counter++;
	//								if (counter < 5)
	//									Thread.sleep(1000, 0);
	//								else {
	//									// give up
	//									return;
	//								}
	//							} catch (InterruptedException e1) {
	//								Log.d(TAG, "onEvent: InterruptedException");
	//							}
	//						}
	//					}
	//					if (photo != null)
	//						ActivationManager.getInstance().addToBuffer(photo);
	//				}
	//			}
	//			else {
	//				Log.d(TAG,"External storage is not readable");
	//				// TODO: error to user
	//			}
	//		}
	//		catch (NullPointerException exception) {
	//			int x = 5;
	//			x++;
	//		}
	//	}

	//	@Override
	//	public void onEvent(int event, String path) {
	//
	//		if (isExternalStorageReadable()) {
	//			if (path.endsWith(".jpg")) {
	//				boolean isJellyBean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	//
	//				Photo photo = null;
	//				String file = absolutePath + path;
	//
	//				if ((event & FileObserver.CLOSE_WRITE) > 0) {
	//					if (isJellyBean) {
	//						cache.add(file);
	//						Log.e(TAG, "Photo was saved to cache");
	//					} else {
	//						try {
	//							photo = createPhotoFromFile(file);
	//							Log.e(TAG, "Photo was saved straight to file");
	//
	//						} catch (ImageProcessingException e) {
	//							// TODO Auto-generated catch block
	//							e.printStackTrace();
	//						}
	//
	//					}
	//				} else if ((event & FileObserver.MOVED_TO) > 0 && isJellyBean && cache.contains(path)) {
	//					try {
	//						photo = createPhotoFromFile(file );
	//						Log.e(TAG, "Photo was saved from tmp to file");
	//
	//					} catch (ImageProcessingException e) {
	//						// TODO Auto-generated catch block
	//						e.printStackTrace();
	//					}
	//					cache.remove(path);
	//				}
	//				if (photo != null)
	//					ActivationManager.getInstance().addToBuffer(photo);
	//			}
	//
	//		}
	//		else {
	//			Log.d(TAG,"External storage is not readable");
	//			// TODO: error to user
	//		}
	//	}

	@Override
	public void onEvent(int event, String path) {

		if (isExternalStorageReadable()) {
			if (path.endsWith(".jpg")) {

				Photo photo = null;
				String file = absolutePath + path;

				if ((event & FileObserver.CLOSE_WRITE) > 0) {
					try {
						photo = createPhotoFromFile(file);
						Log.d(TAG, "Photo taken: " + file + " was read from file");

					} catch (ImageProcessingException e) {
						Log.e(TAG, "Photo taken: " + "was NOT read from file properly");
					}
				}

				if (photo != null)
					ActivationManager.getInstance().addToBuffer(photo);
			}
		}
		else {
			Log.wtf(TAG,"External storage is not readable!");
		}
	}

	public static Photo createPhotoFromFile(String file) throws ImageProcessingException {

		Photo photo = null;

		File path = new File(file);

		// extract photo metadata
		Metadata metadata = null;
		try {
			metadata = ImageMetadataReader.readMetadata(path);
		} catch (IOException e) {
			throw new ImageProcessingException(e);
		}

		//get location
		GpsDirectory directory1 = metadata.getDirectory(GpsDirectory.class);

		GeoLocation location = directory1.getGeoLocation();
		if (location == null) { // photo has no location
			return null;
		}

		//get time
		ExifSubIFDDirectory directory2 = metadata.getDirectory(ExifSubIFDDirectory.class);

		Date date = directory2.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);


		//get dimensions
		JpegDirectory jpgDirectory = metadata.getDirectory(JpegDirectory.class);
		try {
			int width = jpgDirectory.getImageWidth();
			int	height = jpgDirectory.getImageHeight();

			photo = new Photo(
					date,
					width,
					height,
					new GPSPoint(location.getLatitude(),location.getLongitude()),
					path.getPath());
		} catch (MetadataException e) {
			// TODO ERROR reading EXIF details of photo
			e.printStackTrace();
		}

		return photo;
	}

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
