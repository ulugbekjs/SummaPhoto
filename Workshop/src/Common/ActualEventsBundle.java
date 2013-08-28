package Common;

import java.util.List;

public class ActualEventsBundle {

	private List<ActualEvent> events;
	int horizontal = 0;
	int vertical = 0;
	
	public ActualEventsBundle(List<ActualEvent> events) {
		this.events = events;
		calculatePhotosByOrientation();
	}
	
	private void calculatePhotosByOrientation() {
		for (ActualEvent event : events) {
			for (Photo photo : event.getEventPhotos()) {
				if (photo.isHorizontal())
					horizontal++;
				else {
					vertical++;
				}
			}
		}
	}
	
	public int horizontalCount() {
		return this.horizontal;
	}
	
	public int verticalCount() {
		return this.vertical;
	}
	
	public List<ActualEvent> getActualEvents() {
		return this.events;
	}
	
	
}
