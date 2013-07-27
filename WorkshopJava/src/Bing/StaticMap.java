package Bing;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import Common.BoundingBox;
import Common.Point;

public class StaticMap {

	private UUID requestUuid;
	private BoundingBox box;
	private Point centerPoint;
	public Point getCenterPoint() {
		return centerPoint;
	}
	public void setCenterPoint(Point centerPoint) {
		this.centerPoint = centerPoint;
	}

	private String jpgPath;
	private String metadataPath;
	private List<Pushpin> pins = new LinkedList<Pushpin>(); 
			
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
	}
	public UUID getRequestUuid() {
		return requestUuid;
	}
	public BoundingBox getBox() {
		return box;
	}
	
	public void addPushpin(Pushpin pin) {
		pins.add(pin);
	}

	public void setBox(BoundingBox box) {
		this.box = box;
	}
	
	public StaticMap() {
		this.requestUuid = UUID.randomUUID();
	}
	
	
}
