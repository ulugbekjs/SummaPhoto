package Common;

import org.joda.time.DateTime;

/**
 * Represents a solid event after clustering of photos into events 
 * @author yonatan
 *
 */
public class ActualEvent extends AbstractEvent {
	
	private DateTime startTime = null;
	private DateTime endTime = null;
	
	private int horizontalPhotosCount = 0;
	private int verticalPhotosCount = 0;
	
	//geographic data fields
	private GPSPoint centerPoint;
	private GeoBoundingBox box;

	public ActualEvent() {
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

	@Override
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
