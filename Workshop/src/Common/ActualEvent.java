package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

/**
 * Represents a solid event after clustering of photos into events 
 * @author yonatan
 *
 */
public class ActualEvent{
	
	private UUID eventID;
	protected List<Photo> verticalPhotos = new ArrayList<Photo>();
	protected List<Photo> horizontalPhotos = new ArrayList<Photo>();
	
	private DateTime startTime = null;
	private DateTime endTime = null;
	
	private int horizontalPhotosCount = 0;
	private int verticalPhotosCount = 0;
	
	//geographic data fields
	private GPSPoint centerPoint;
	private GeoBoundingBox box;

	public ActualEvent() {
		eventID = UUID.randomUUID();
	}
	
	public boolean isEmpty() {
		return (getEventSize() == 0); 
	}

	public UUID getEventID() {
		return this.eventID;	
	}

	public int getEventSize() {
		return this.horizontalPhotos.size() + this.verticalPhotos.size();
	}

	public boolean isPhotoInEvent(Photo photo) {
		return (verticalPhotos.contains(photo) || horizontalPhotos.contains(photo));
	}
	
	public List<Photo> verticalPhotos() {
		return verticalPhotos;
	}	
	
	public List<Photo> horizontalPhotos() {
		return horizontalPhotos;
	}
	
	public List<Photo> getEventPhotos() {
		List<Photo> retList = new ArrayList<Photo>();
		retList.addAll(horizontalPhotos);
		retList.addAll(verticalPhotos);
		
		return retList;
	}

	public DateTime getEventStartTime() {
		return this.startTime;
	}

	public DateTime getEventEndTime() {
		return this.endTime;
	}

	public int horizontalPhotosCount() {
		return this.horizontalPhotosCount;
	}

	public int verticalPhotosCount() {
		return verticalPhotosCount;
	}
	
	public void calculateEventBoundingBox() {
		//TODO bounding box calculation
	}
	
	public GeoBoundingBox getBoundingBox() {
		return box;
		
	}
	
	public void calculateCenterPoint() {
		//TODO square point calculation
	}
	
	public GPSPoint getCenterPoint() {
		return centerPoint;
		
	}

	public void addPhoto(Photo photo) {
		if (photo.isHorizontal()) 
			horizontalPhotosCount++;
		else 
			verticalPhotosCount++;

		if (startTime == null) // update only on first added photo
			startTime = photo.getTakenDate();
		endTime = photo.getTakenDate();

		// double-sided linking
		if (photo.isHorizontal()) {
			horizontalPhotos.add(photo);
		}
		else {
			verticalPhotos.add(photo);
		}
		photo.attachToEvent(this);
	}
}
