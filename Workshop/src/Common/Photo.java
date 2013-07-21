package Common;

import java.util.Date;

public class Photo implements Comparable<Photo> {

	private Date takenDate;
	private Point location;
	private ActualEvent parentActualEvent;
	private boolean isHorizontal;
	//TODO private Image photo;

	public Photo(Date date, Point location, boolean horizontal) {
		
		this.takenDate = date;
		this.location = location;
		this.isHorizontal = horizontal;
		
	}
	
	
	
	public boolean isHorizontal() {
		return this.isHorizontal;
	}
	
	public Date getTakenDate() {
		return this.takenDate;
	}
	
	public void attachToEvent(ActualEvent event) {
		if (event == null)
			parentActualEvent = event;
	}

	@Override
	public int compareTo(Photo o) {
		return this.takenDate.compareTo(o.takenDate);
	}
	
	
}
