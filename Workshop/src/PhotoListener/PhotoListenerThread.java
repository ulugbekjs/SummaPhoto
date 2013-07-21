package PhotoListener;

import java.io.File;
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

import Common.Photo;
import Common.Point;

public class PhotoListenerThread {

	public PhotoListenerThread() {
	}

	public Photo createPhotoFromFile(String path) {

		// extract photo metadata
		File jpegFile = new File ("c:\\users\\yonatan\\pictures\\try.jpg");
		Metadata metadata = null;
		try {
			metadata = ImageMetadataReader.readMetadata(jpegFile);
		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//get location
		GpsDirectory directory1 = metadata.getDirectory(GpsDirectory.class);

		GeoLocation location = directory1.getGeoLocation();

		//get time
		ExifSubIFDDirectory directory2 = metadata.getDirectory(ExifSubIFDDirectory.class);

		Date date = directory2.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

		Photo photo = null;

		//get dimensions
		JpegDirectory jpgDirectory = metadata.getDirectory(JpegDirectory.class);
		try {
			int width = jpgDirectory.getImageWidth();
			int	height = jpgDirectory.getImageHeight();
			photo = new Photo(
					date,
					width,
					height,
					new Point(location.getLongitude(),location.getLatitude()),
					true);
		} catch (MetadataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
}
