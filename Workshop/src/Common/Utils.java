package Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.summaphoto.R;
import com.summaphoto.SettingsActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Utils {

	/** Checks if external storage is available to at least read 
	 * */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 *  checks if external storage is available to read 
	 *  */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return (Environment.MEDIA_MOUNTED.equals(state));
	}

	/**
	 * Creates notification for user about a newly created collage
	 * @param photo
	 * @throws FileNotFoundException 
	 */
	public static void notifyUserCollageCreated(Photo photo) throws FileNotFoundException {

		File ROOT = new File(Environment.getExternalStorageDirectory(), "Pictures");

		return;
/**
		String  PHOTO_DIR = ROOT + File.separator + "SummaPhoto" + File.separator + photo.getFileName();
		File photoFile = new File(PHOTO_DIR);

		if (!photoFile.exists()) {
			throw new FileNotFoundException();
		}
		// scan into gallery
		Uri uri =  addImageToGallery(photo);

		Context context = SettingsActivity.CONTEXT;
		Intent it = new Intent();
		it.setDataAndType(uri, "image/jpeg");
		it.setAction(Intent.ACTION_VIEW);

		Intent[] arrIntents = {it};
		PendingIntent pendingIntent = PendingIntent.getActivities(context, 1, arrIntents, PendingIntent.FLAG_ONE_SHOT);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.icon)
		.setContentTitle("New collage created!")
		.setContentText("Click to see the newly created collage in the gallery")
		.setAutoCancel(true)
		.setContentIntent(pendingIntent)
		.setSound(alarmSound);


		NotificationManager mNotifyMgr = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(1, mBuilder.build());
		**/
	}

	public static void notifyUserWithError(String title, String text) {
		Context context = SettingsActivity.CONTEXT;

		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.icon)
		.setContentTitle(title)
		.setContentText(text)
		.setAutoCancel(true)
		.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0)) // creates empty notfication
		.setOnlyAlertOnce(true)
		.setSound(alarmSound);


		NotificationManager mNotifyMgr = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(1, mBuilder.build());

	}

	private static Uri addImageToGallery(Photo photo) {
		ContentValues image = new ContentValues();

		image.put(Images.Media.TITLE, photo.getFileName());
		image.put(Images.Media.DISPLAY_NAME, photo.getFileName());
		image.put(Images.Media.DESCRIPTION, "Summaphoto smart mode auto generated");
		image.put(Images.Media.DATE_ADDED, photo.getTakenDate().toString());
		image.put(Images.Media.DATE_TAKEN, photo.getTakenDate().toString());
		image.put(Images.Media.DATE_MODIFIED, photo.getTakenDate().toString());
		image.put(Images.Media.MIME_TYPE, "image/jpeg");
		image.put(Images.Media.ORIENTATION, 0);

		File photoFile = new File(photo.getFilePath());
		File parent = photoFile.getParentFile();
		String path = parent.toString().toLowerCase();
		String name = parent.getName().toLowerCase();
		image.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
		image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
		image.put(Images.Media.SIZE, photo.getFilePath().length());

		image.put(Images.Media.DATA, photoFile.getAbsolutePath());

		return SettingsActivity.CONTEXT.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
	}

	// TODO: remove, this is for Omri
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
		GeoLocation location = null;
		try {
			GpsDirectory directory1 = metadata.getDirectory(GpsDirectory.class);
			 location = directory1.getGeoLocation();
			if (location == null) { // photo has no location, dont create photo
				throw new NullPointerException();
			}
		}
		catch (NullPointerException exception) {
			Log.e("UTILS", "photo has no loction and EXIF metada");
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
			return null;
		}

		return photo;
	}
	
	 /**
     * Tells whether the two line segments cross.
     * 
     * @param x1
     *            the x coordinate of the starting point of the first segment.
     * @param y1
     *            the y coordinate of the starting point of the first segment.
     * @param x2
     *            the x coordinate of the end point of the first segment.
     * @param y2
     *            the y coordinate of the end point of the first segment.
     * @param x3
     *            the x coordinate of the starting point of the second segment.
     * @param y3
     *            the y coordinate of the starting point of the second segment.
     * @param x4
     *            the x coordinate of the end point of the second segment.
     * @param y4
     *            the y coordinate of the end point of the second segment.
     * @return true, if the two line segments cross.
     */
    public static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3,
            double y3, double x4, double y4) {
        /*
         * A = (x2-x1, y2-y1) B = (x3-x1, y3-y1) C = (x4-x1, y4-y1) D = (x4-x3,
         * y4-y3) = C-B E = (x1-x3, y1-y3) = -B F = (x2-x3, y2-y3) = A-B Result
         * is ((AxB) (AxC) <=0) and ((DxE) (DxF) <= 0) DxE = (C-B)x(-B) =
         * BxB-CxB = BxC DxF = (C-B)x(A-B) = CxA-CxB-BxA+BxB = AxB+BxC-AxC
         */

        x2 -= x1; // A
        y2 -= y1;
        x3 -= x1; // B
        y3 -= y1;
        x4 -= x1; // C
        y4 -= y1;

        double AvB = x2 * y3 - x3 * y2;
        double AvC = x2 * y4 - x4 * y2;

        // Online
        if (AvB == 0.0 && AvC == 0.0) {
            if (x2 != 0.0) {
                return (x4 * x3 <= 0.0)
                        || ((x3 * x2 >= 0.0) && (x2 > 0.0 ? x3 <= x2 || x4 <= x2 : x3 >= x2
                                || x4 >= x2));
            }
            if (y2 != 0.0) {
                return (y4 * y3 <= 0.0)
                        || ((y3 * y2 >= 0.0) && (y2 > 0.0 ? y3 <= y2 || y4 <= y2 : y3 >= y2
                                || y4 >= y2));
            }
            return false;
        }

        double BvC = x3 * y4 - x4 * y3;

        return (AvB * AvC <= 0.0) && (BvC * (AvB + BvC - AvC) <= 0.0);
    }
	
	

}
