package Common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Abstract class for the two types of Events in the system
 * @author yonatan
 *
 */
public abstract class AbstractEvent {
	
	private UUID eventID;
	protected List<Photo> verticalPhotos = new ArrayList<Photo>();
	protected List<Photo> horizontalPhotos = new ArrayList<Photo>();
	
	public AbstractEvent() {
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
	
	public abstract void addPhoto(Photo photo);
	
}
