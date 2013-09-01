package Bing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.jdom2.JDOMException;

import com.example.aworkshop.SettingsActivity;

import android.accounts.NetworkErrorException;
import android.os.Environment;
import android.util.Log;
import Common.Photo;
import Common.GPSPoint;

public class BingServices {

	private final static String TAG = BingServices.class.getName();

	/**
	 * Queries BING for JPG & Metadata for ActualEvent points
	 * @param points list of all Points in ActualEvents
	 * @param width width in pixels
	 * @param height height in pixels
	 * @return StaticMap, or NULL if map could not be created
	 */
	public static StaticMap getStaticMap(List<Photo> photos, int width, int height) {

		StaticMap map = null;

		if (photos.size()  > 0) { // Request only iff there is at least one photo

			map = new StaticMap(photos, width, height);

			List<GPSPoint> points = getImagesPointsList(photos);
			try {
				map.setJpgPath(getJPG(points, width, height), width, height);
			} catch (NetworkErrorException e) {
				Log.e(TAG, "Network error when getting map jpg from Bing");
			} catch (IOException e) {
				Log.e(TAG, "Error when writing / reading recieved jpg");
			}
			try {
				map.setMetadataPath(getJPGMetadata(points, width, height));
			} catch (NetworkErrorException e) {
				Log.e(TAG, "Network error when getting map metadata from Bing");
			} catch (JDOMException e) {
				Log.e(TAG, "Error when parsing Bing xml");
			} catch (IOException e) {
				Log.e(TAG, "Error while writing / reading recieved xml");
			}

			if (map.getJpgPath() == null || map.getMetadataPath() == null) { // verify paths
				return null;  // free map for GC
			}

		}
		else {
			Log.d(TAG, "getStaticMap: Zero locations in request");
		}

		return map;
	}

	private static String getJPG(List<GPSPoint> points, int width, int height) throws NetworkErrorException, IOException {
		return createHTTPRequest(false, points, width, height);
	}

	private static String getJPGMetadata(List<GPSPoint> points, int width, int height) throws NetworkErrorException, IOException {
		return createHTTPRequest(true, points, width, height);
	}

	/**
	 * creates a List of Points to be sent to BING from all current ActualEvents in ActualEventContainer
	 * @return List of all GPSPoints in photo list
	 */
	public static List<GPSPoint> getImagesPointsList(List<Photo> photos) {
		List<GPSPoint> points = new ArrayList<GPSPoint>();

		for (Photo photo : photos) {
			points.add(photo.getLocation());
		}

		return points;
	}

	/**
	 * Queries Bing for JPG or Metadata
	 * @param metadata TRUE if method should query for Metadata, FALSE if method should query for JPG
	 * @param points 
	 * @return Path of newly saved .JPG/XML or NULL
	 * @throws NetworkErrorException 
	 * @throws IOException 
	 * @throws  
	 */
	private static String createHTTPRequest(boolean metadata, List<GPSPoint> points, int width, int height) throws NetworkErrorException, IOException {

		String file = null;


		String urlString ="http://dev.virtualearth.net/REST/v1/Imagery/Map/AerialWithLabels?";
		//Make the actual connection
		if (metadata) {
			urlString += "mmd=1&o=xml";
		}
		else {
			urlString += "mmd=0";
		}

		// concatenate with BING key
		urlString = urlString + "&mapSize=" + width +"," + height + "&dcl=1&key=AjuPzlE1V8n1TJJK7T7elqCZlfi6wdLGvjyYUn2aUsNJ5ORSwnc-ygOwBvTa9Czt";

		// Construct POST Request

		HttpPost postReq = new HttpPost(urlString);

		// adding pushpins coordinates to BING request
		StringBuilder builder = new StringBuilder();
		for (GPSPoint point : points)  {
			builder.append("pp=");
			builder.append(point.toString());
			builder.append(";14;\r\n");
		}

		StringEntity entity = null;
		try {
			entity = new StringEntity(builder.toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			Log.e(TAG, "Error building request to Bing:\n" + builder.toString());
		}
		postReq.setEntity(entity);

		// Set BING Requested headers
		postReq.setHeader("Content-Type", "text/plain");
		postReq.setHeader("charset",  "charset=utf-8");

		HttpResponse response = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			response = httpclient.execute(postReq);
		} catch (ClientProtocolException e) {
			throw new NetworkErrorException(e);
		} catch (IOException e) { // connection to Bing
			throw new NetworkErrorException(e);
		}

		StatusLine statusLine = response.getStatusLine();
		if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			out.close();

			// normal return
			file = createOutputFile(metadata, out);

		} else {
			//Closes the connection.
			response.getEntity().getContent().close();
			throw new IOException(statusLine.getReasonPhrase());
		}

		return file;
	}

	private static String createOutputFile(boolean metadata, ByteArrayOutputStream out) throws IOException {

		File file = new File(SettingsActivity.APP_TEMP_DIR, "map_temp.");

		// Construct right file according to requested content
		if (!metadata) {
			file = new File(file.getPath() + "jpg");
		}
		else {
			file = new File(file.getPath()  + "xml");
		}

		if (file.exists()) {
			file.delete();
		}
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			Log.d("Test", "sdcard mounted and writable");
			file.createNewFile();
		}
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Log.d("Test", "sdcard mounted readonly");
		}
		else {
			Log.d("Test", "sdcard state: " + state);
		}

		// actual write to file
		OutputStream outputStream = new FileOutputStream (file); 
		out.writeTo(outputStream);
		out.flush();
		out.close();
		out = null;

		return file.getPath();
	}
}

