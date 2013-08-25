package Bing;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import Common.GeoBoundingBox;
import Common.GPSPoint;

/**
 * Includes Bing Map and Metadata returned from Bing Services
 * @author yonatan
 *
 */
public class StaticMap {

	private UUID requestUuid;
	private GeoBoundingBox box;
	private GPSPoint centerPoint;
	private int pixelWidth;
	private int pixelHeight;
	private String jpgPath;
	private String metadataPath;
	
	private List<Pushpin> pins = new LinkedList<Pushpin>(); 
	
	public StaticMap(int width, int height) {
		this.requestUuid = UUID.randomUUID();
		pixelWidth = width;
		pixelHeight = height;
	}
			
	public GPSPoint getCenterPoint() {
		return centerPoint;
	}

	public String getJpgPath() {
		return jpgPath;
	}
	public void setJpgPath(String jpgPath) {
		this.jpgPath = jpgPath;
	}
	public String getMetadataPath() {
		return metadataPath;
	}
	public void setMetadataPath(String metadataPath) {
		this.metadataPath = metadataPath;
		fillMapWithMetaData();
	}
	public UUID getRequestUuid() {
		return requestUuid;
	}
	public GeoBoundingBox getBox() {
		return box;
	}
	
	public int getPixelWidth() {
		return this.pixelWidth;
	}
	
	public int getPixelHeight() {
		return this.pixelHeight;
	}
	
	private void fillMapWithMetaData() {

		SAXBuilder builder = new SAXBuilder();

		Document document = null;
		try {
			File xmlFile = new File(this.metadataPath);
			if (!xmlFile.exists()) { // check that xml file still exists
				throw new IOException();
			}
			document = (Document) builder.build (xmlFile);
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

			GeoBoundingBox box = new GeoBoundingBox(new GPSPoint(NorthLatitude, WestLongitude), 
					new GPSPoint(EastLongitude, SouthLatitude));
			this.box = box;

			// get center point
			node = metaNode.getChild("MapCenter", namespace);
			double latitude = Double.valueOf(node.getChildText("Latitude", namespace));
			double longitude = Double.valueOf(node.getChildText("Longitude", namespace));

			GPSPoint center = new GPSPoint(latitude, longitude);
			this.centerPoint = center;

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

				pins.add(new Pushpin(new GPSPoint(latitude,longitude),
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
