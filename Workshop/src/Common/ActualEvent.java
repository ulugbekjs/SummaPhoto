package Common;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class ActualEvent {

	private UUID eventID;
	private Set<Photo> eventPhotos = new TreeSet<Photo>();
	private Date startTime = null;
	private Date endTime = null;
	private int horizontalPhotosCount = 0;
	private int verticalPhotosCount = 0;
	
	//geographic data fields
	private Point centerPoint;
	private BoundingBox box;

	public ActualEvent() {
		eventID = UUID.randomUUID();
	}

	public boolean isEmpty() {
		return (this.eventPhotos.size() == 0); 
	}

	public UUID getEventID() {
		return this.eventID;	
	}

	public int getPhotosCount() {
		return this.eventPhotos.size();
	}

	public Date getEventStartTime() {
		return this.startTime;
	}

	public Date getEventEndTime() {
		return this.endTime;
	}

	public int horizontalPhotosCount() {
		return this.horizontalPhotosCount;
	}

	public int verticalPhotosCount() {
		return verticalPhotosCount;
	}

	public boolean isPhotoInEvent(Photo photo) {
		return eventPhotos.contains(photo);
	}
	
	public void calculateEventBoundingBox() {
		//TODO bounding box calculation
	}
	
	public BoundingBox getBoundingBox() {
		return box;
		
	}
	
	public void calculateCenterPoint() {
		//TODO square point calculation
	}
	
	public Point getCenterPoint() {
		return centerPoint;
		
	}

	public void addPhotoToEvent(Photo photo) {
		if (photo.isHorizontal()) 
			horizontalPhotosCount++;
		else 
			verticalPhotosCount++;

		if (startTime == null) // update only on first added photo
			startTime = photo.getTakenDate();
		endTime = photo.getTakenDate();

		// double-sided linking
		eventPhotos.add(photo); 
		photo.attachToEvent(this);
	}
}
