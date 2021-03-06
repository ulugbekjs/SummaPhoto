import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import Common.Point;
import Common.Photo;
import Partitioning.Cluster;
import Partitioning.DBScan;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;


public class ExtractorOmri {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			

			Photo p1  = null;
			List<Photo> photosToCluster = new LinkedList<Photo>();
		
			File directory = new File("C:/Users/omri/Desktop/temp/Pics");
			for (File file : directory.listFiles()) {


				// extract photo metadata
				Metadata metadata = null;
				try {
					metadata = ImageMetadataReader.readMetadata(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ImageProcessingException e) {
					continue;
				}

				//get location
				GpsDirectory directory1 = metadata.getDirectory(GpsDirectory.class);

				GeoLocation location = directory1.getGeoLocation();
				if (location == null) { // photo has not location
					continue;
				}
				//get time
				ExifSubIFDDirectory directory2 = metadata.getDirectory(ExifSubIFDDirectory.class);

				Date date = directory2.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

				Photo photo = null;
				Point point = null;

				//get dimensions
				JpegDirectory jpgDirectory = metadata.getDirectory(JpegDirectory.class);
				try {
					int width = jpgDirectory.getImageWidth();
					int	height = jpgDirectory.getImageHeight();
					point = new Point(location.getLatitude(),location.getLongitude());

					photo = new Photo(
							date,
							width,
							height,
							point,
							null);
					photosToCluster.add(photo);							
				}
				catch (Exception e)
				{
				}

			}
			
			DBScan algorithimObjectDbScan = new DBScan(photosToCluster);
			List<Cluster> returnValueClusters = algorithimObjectDbScan.runAlgorithmClusters();

			System.out.println("Done");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;

		}
	}
}
