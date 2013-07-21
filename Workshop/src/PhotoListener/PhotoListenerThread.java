package PhotoListener;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

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
		
		//get time
		ExifSubIFDDirectory directory2 = metadata.getDirectory(ExifSubIFDDirectory.class);

		Date date = directory2.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

		//get location
		GpsDirectory directory1 = metadata.getDirectory(GpsDirectory.class);

		GeoLocation location = directory1.getGeoLocation();

	
		
		Photo photo = new Photo(date, new Point(location.getLongitude(), location.getLatitude()), true);
		return photo;
	}
}
