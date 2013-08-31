package Common;

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

import android.R.bool;

public class Utils {
	public static Photo createPhotoFromFile(String filePath) throws ImageProcessingException {

		Photo photo = null;

		File path = new File(filePath);

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


}
