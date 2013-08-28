package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import Partitioning.Cluster;

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
	

	private ActualEvent() {
		eventID = UUID.randomUUID();
	}

	/**
	 * This constructor receives a cluster as parameter and initiates a actual event with the photos of the cluster 
	 * @param cluster
	 */
	public ActualEvent (Cluster cluster)
	{
		this();
		cluster.sortPhotosInClusterByData();	
		for (Photo photo: cluster.photosInCluster)
			addPhoto(photo);
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
	
	
	/**
	 * adds a photo to the event
	 * @param photo
	 */
	private void addPhoto(Photo photo) {
		if (photo.isHorizontal()) 
			horizontalPhotosCount++;
		else 
			verticalPhotosCount++;
		
		DateTime photoTakenTime = photo.getTakenDate();
		
		// updating starting time and ending time of actual event according to photo data (if needed) 
		if ((startTime == null) || (photoTakenTime.compareTo(startTime) < 0))
			startTime = photoTakenTime;
		if ((endTime == null) || (photoTakenTime.compareTo(endTime) > 0))
			endTime = photoTakenTime;

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
