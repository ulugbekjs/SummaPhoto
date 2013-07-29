package Bing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import android.os.Environment;
import android.util.Log;
import ActivationManager.EventCandidate;
import ActivationManager.EventCandidateContainer;

import Common.BoundingBox;
import Common.Photo;
import Common.Point;

public class BingServices {
	private final static String TAG = "BingServices";

	public static StaticMap getStaticMap(List<Point> points) {

		StaticMap map = null;

		if (points.size()  > 0) { // Request iff there is one photo

			map = new StaticMap();
			
			//get jpeg
			map.setJpgPath(getStaticMapOrMetadataFile(false, points));
			
			// get metadata
			map.setMetadataPath(getStaticMapOrMetadataFile(true, points));

			File xmlFile = new File(map.getMetadataPath());
			fillStaticMapWithData(xmlFile, map);
			
		}
		else {
			Log.d(TAG, "getStaticMap: Zero locations in request");
		}

		return map;
	}
	public static List<Point> getImagesPointsArray() {
		List<Point> points = new ArrayList<Point>();
		//TODO: this should work with the ActualEventContainer
		for (EventCandidate event: EventCandidateContainer.getInstance().getAllEventsInContainer()) {
			for (Photo photo : event.getEventPhotos()) {
				points.add(photo.getLocation());
			}
		}
		return points;
	}


	private static String getStaticMapOrMetadataFile(boolean metadata, List<Point> points) {
		
		File file = null;
		try {

			URL                 url;
			URLConnection   urlConn;
			DataOutputStream    printout;
			DataInputStream     input;

			String urlString ="http://dev.virtualearth.net/REST/v1/Imagery/Map/AerialWithLabels?";
			//Make the actual connection
			if (metadata) {
				urlString += "mmd=1&o=xml";
			}
			else {
				urlString += "mmd=0";
			}

			urlString = urlString + "&mapSize=700,600&dcl=1&key=AjuPzlE1V8n1TJJK7T7elqCZlfi6wdLGvjyYUn2aUsNJ5ORSwnc-ygOwBvTa9Czt";

			url = new URL(urlString);
			urlConn = url.openConnection();
			urlConn.setDoInput (true);
			urlConn.setDoOutput (true); // POST Request
			urlConn.setUseCaches (false);
			urlConn.setRequestProperty("Content-Type", "text/plain");
			urlConn.setRequestProperty("charset",  "charset=utf-8");

			// adding pushpins coordinates to BING request
			StringBuilder builder = new StringBuilder();

			for (Point point : points)  {
				builder.append("pp=");
				builder.append(point.toString());
				builder.append(";14;\r\n");
			}

			String strContent = builder.toString();

			urlConn.setRequestProperty("Content-Length", Integer.valueOf(strContent.getBytes().length).toString()); 
			printout = new DataOutputStream (urlConn.getOutputStream ());
			printout.writeBytes (strContent);
			printout.flush ();

			// Get response
			input = new DataInputStream (urlConn.getInputStream());
			
			File externalStorageDir = Environment.getExternalStorageDirectory();
			File testsDir = new File(externalStorageDir, "Tests");
			file = new File(testsDir, "moshiko.");
			
			if (!metadata) {
				// TODO: make jpg data work with imageIO and not with file
				file = new File(file.getPath() + "jpg");
			}
			else {
				file = new File(file.getPath()  + "xml");
			}

			readFromStreamAndWriteToFile(input, testsDir, file);
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   

		return file.getPath();
	}

	private static void readFromStreamAndWriteToFile(DataInputStream input, File dir, File file) throws IOException {

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		file.delete();
		file.createNewFile();

		
		FileOutputStream fop = new FileOutputStream(file);

		byte[] buffer = new byte[1024];
		while (input.read(buffer) > -1) {
			fop.write(buffer);	
		}

		fop.flush();
		fop.close();
	}
	private static void fillStaticMapWithData(File xmlFile, StaticMap map) {

		SAXBuilder builder = new SAXBuilder();

		Document document = null;
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element rootNode = document.getRootElement();
		Namespace namespace = rootNode.getNamespace();

		try {

			Element metaNode = rootNode.getChild("ResourceSets",namespace);
			metaNode = metaNode.getChild("ResourceSet",namespace).getChild("Resources", namespace).getChild("StaticMapMetadata", namespace);

			// get bounding box
			Element node = metaNode.getChild("BoundingBox",namespace);

			double SouthLatitude = Double.valueOf(node. getChildText("SouthLatitude",namespace)); // "SouthLatitude"
			double WestLongitude = Double.valueOf(node. getChildText("WestLongitude",namespace)); // "WestLongitude"
			double NorthLatitude = Double.valueOf(node. getChildText("NorthLatitude",namespace)); // "NorthLatitude"
			double EastLongitude = Double.valueOf(node. getChildText("EastLongitude",namespace)); // "EastLongitude"

			BoundingBox box = new BoundingBox(new Point(NorthLatitude, WestLongitude), 
					new Point(EastLongitude, SouthLatitude));
			map.setBox(box);

			// get center point
			node = metaNode.getChild("MapCenter", namespace);
			double latitude = Double.valueOf(node.getChildText("Latitude", namespace));
			double longitude = Double.valueOf(node.getChildText("Longitude", namespace));

			Point center = new Point(latitude, longitude);
			map.setCenterPoint(center);

			// get pushpins data: location, anchor & offsets
			Element pushpinsNode =  metaNode.getChild("Pushpins", namespace);

			for (Element pushpinNode : pushpinsNode.getChildren()) {

				node = pushpinNode.getChild("Point", namespace);
				latitude = Double.valueOf(node.getChildText("Latitude", namespace));	
				longitude = Double.valueOf(node.getChildText("Longitude", namespace)); 

				node = pushpinNode.getChild("Anchor", namespace);
				int ax = Integer.valueOf(node.getChildText("X", namespace));	
				int ay = Integer.valueOf(node.getChildText("Y", namespace));	

				node = pushpinNode.getChild("TopLeftOffset", namespace);
				int tx = Integer.valueOf(node.getChildText("X", namespace));	
				int ty = Integer.valueOf(node.getChildText("Y", namespace));	

				node = pushpinNode.getChild("BottomRightOffset", namespace);
				int bx = Integer.valueOf(node.getChildText("X", namespace));	
				int by = Integer.valueOf(node.getChildText("Y", namespace));	

				map.addPushpin(new Pushpin(new Point(latitude,longitude),
						new int[] {ax, ay},
						new int[] {tx, ty},
						new int[] {bx, by}));
			}
		}
		catch (NullPointerException exception) {
			// TODO: handle null (nodes that are not found)
		}
	}
}

