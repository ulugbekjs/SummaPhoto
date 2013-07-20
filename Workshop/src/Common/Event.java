package Common;

import java.util.LinkedList;
import java.util.List;

public class Event {

	private List<Photo> eventPhotos;
	private int horizontalPhotosCount;
	private int verticalPhotosCount;
	
	public Event() {
		eventPhotos = new LinkedList<Photo>();
		horizontalPhotosCount = 0;
		verticalPhotosCount = 0;
	}
	
	public boolean isEmpty() {
		return (eventPhotos.size() == 0); 
	}
	
	public void addPhotoToEvent(Photo photo) {
		if (photo.isHorizontal()) 
			horizontalPhotosCount++;
		else 
			verticalPhotosCount++;
		
		// two sided referencing
		eventPhotos.add(photo); 
		photo.attachToEvent(this);
	}
}
