package ActivationManager;

import Common.AbstractEvent;
import Common.Photo;

public class EventCandidate extends AbstractEvent {

	private Photo lastAddedPhoto = null;
	
	public Photo getLastAddedPhoto() {
		return lastAddedPhoto;
	}

	public EventCandidate(Photo photo) {
		super();
		addPhoto(photo);
	}
	
	@Override
	public void addPhoto(Photo photo) {
		eventPhotos.add(photo);
		lastAddedPhoto = photo;
	}
	
}
