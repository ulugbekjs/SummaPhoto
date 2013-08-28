package Common;

import java.security.PublicKey;

import org.joda.time.DateTime;

import Partitioning.Cluster;

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
	

	public ActualEvent() {
	}


	/** This constructor receives a cluster as parameter and initiates a actual event with the photos of the cluster **/
	public ActualEvent (Cluster cluster)
	{
		cluster.sortPhotosInClusterByData();	
		for (Photo photo: cluster.photosInCluster)
			addPhoto(photo);
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
	
	
	@Override
	public void addPhoto(Photo photo) {
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
