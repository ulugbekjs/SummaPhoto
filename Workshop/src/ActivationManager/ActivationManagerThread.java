package ActivationManager;

import java.util.Queue;
import Common.Photo;

public class ActivationManagerThread {

	private static final ActivationManagerThread instance = null;

	//states
	private static final int REGULAR_MODE = 0;
	private static final int DEDICATED_MODE = 1;
	private static final int BUFFER_SIZE = 100;
	private static final int NEW_CANDIDATE_THRESHOLD_SCORE = 5;

	//instance fields
	private Queue<Photo> buffer = new LimitedLinkedList<Photo>(BUFFER_SIZE);
	private int currentState = 0; // start in REGULAR_MODE;

	private ActivationManagerThread() {
	}

	public static ActivationManagerThread getInstance() {
		return instance;
	}

	private double calculateCandidateScore(Photo newPhoto) {
		Photo lastPhoto = EventCandidateContainer.getInstance().getLastAddedEvent().getLastAddedPhoto();
		return (lastPhoto.distanceFrom(newPhoto) + lastPhoto.timeDeltaInSecondsFrom(newPhoto));
	}

	private boolean isNewEventCandidate(Photo newPhoto) {
		return (calculateCandidateScore(newPhoto) > NEW_CANDIDATE_THRESHOLD_SCORE) ? true : false;
	}

	/**
	 * 
	 * @param photo
	 * @return TRUE iff new EventCandidate was created
	 */
	public boolean processIncomingPhoto(Photo photo) {
		EventCandidate event = null;

		if (EventCandidateContainer.getInstance().isEmpty() || isNewEventCandidate(photo)) {  // new event
			event = new EventCandidate(photo);
			EventCandidateContainer.getInstance().addEvent(event);
			return true;
		}
		else  { // add photo to last added event in container
			event = EventCandidateContainer.getInstance().getLastAddedEvent();
			if (!event.isPhotoInEvent(photo)) {
				event.addPhoto(photo);
			}
			else {
				// TODO: handle this situation that should not happen
			}
		}
		return false;
	}

}

