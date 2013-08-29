package ActivationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;
import Common.Photo;

public class ActivationManager {

	private static final ActivationManager instance = new ActivationManager();

	//states
	private static final int REGULAR_MODE = 0;
	private static final int DEDICATED_MODE = 1;

	// TODO: maybe do this by number of photos for collage
	private static final int CANDIDATE_EVENTS_FOR_COLLAGE = 3;
	private static final int NEW_CANDIDATE_THRESHOLD_DELTA = 1200;
	private static final String TAG = "ActionManager.ActionManager";


	//instance fields
	private BlockingQueue<Photo> buffer = new LinkedBlockingQueue<Photo>();
	private BlockingQueue<DedicatedRequest> requestBuffer = new LinkedBlockingQueue<DedicatedRequest>();
	private List<Photo> processedPhotos = new ArrayList<Photo>();

	private int currentState = 0; // start in REGULAR_MODE;
	private int remainingEvents = CANDIDATE_EVENTS_FOR_COLLAGE;
	private int remainingHorizontal = 0;
	private int remainingVertical = 0;
	
	private Photo lastRecievedPhoto = null;


	private ActivationManager() {

	}

	public static ActivationManager getInstance() {
		return instance;
	}
	
	public List<Photo> getProcessedPhotos() {
		return processedPhotos;
	}

	private boolean isNewEventCandidate(Photo newPhoto) {
		
		//TODO : uncomment the if
		//if (newPhoto.getTakenDate().isAfter(lastRecievedPhoto.getTakenDate())) { // should always be true
			int delta = lastRecievedPhoto.timeDeltaInSecondsFrom(newPhoto);
			Log.d(TAG, "diff from last photo: " + delta);
			return (delta > NEW_CANDIDATE_THRESHOLD_DELTA) ? true : false;
		//}
	//	else { // should not happen, except on daylight savings time switch
		//	return false;
	//	}
		
	}

	private boolean isCollageNeeded() {
		return (((currentState == DEDICATED_MODE || currentState == REGULAR_MODE)) && (remainingEvents == 0) || // currentState == DEDICATED_MODE || currentState == REGULAR_MODE
				(currentState == DEDICATED_MODE && 
				((remainingHorizontal == 0) ||
						(remainingVertical == 0)))); 
	}
	
	private boolean isFirstEvent() {
		return (lastRecievedPhoto == null);
	}
	
	/**
	 * @param photo
	 * @return TRUE if next module should be awakened
	 */
	private boolean processPhoto(Photo photo) {
		if (isFirstEvent() || isNewEventCandidate(photo)) {  // new event candidate
			lastRecievedPhoto = photo;

			if (remainingEvents > 0) { 
				remainingEvents--;
			}
		}
//		else  { // add photo to last added event in container
//			event = CandidatePhotoContainer.getInstance().getLastAddedEvent();
//			if (!event.isPhotoInEvent(photo)) {
//				event.addPhoto(photo);
//			}
//			else {
//				// TODO: handle this situation that should not happen
//			}
//		}

		if (currentState == DEDICATED_MODE && photo.isHorizontal()) {
			remainingHorizontal--;
		}
		if ((currentState == DEDICATED_MODE && !photo.isHorizontal())) {
			remainingVertical--;
		}

		return isCollageNeeded();
	}


	/**
	 * Empties buffer and decides if to trigger clustering
	 * @return true iff collage is needed
	 */
	public boolean processPhotoBuffer() {

		boolean isCollageNeeded = false;
		// empty the buffer
		Photo photo = null;
		while (!buffer.isEmpty()) { 
			photo = buffer.remove();
			if (photo != null) {
				isCollageNeeded = processPhoto(photo);
				processedPhotos.add(photo); 
			}
		}

		// advance in collage process if necessary
		if (isCollageNeeded) {
			setToRegularMode(); // upon decision to create collage, switch to REGULAR_MODE
		}
		
		return isCollageNeeded;
	}

	public void addToBuffer(Photo p) {
		try {
			buffer.put(p);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addRequestToBuffer(DedicatedRequest request) {
		try {
			requestBuffer.put(request);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void consumeDedictedRequests() {
		while (!requestBuffer.isEmpty()) {
			DedicatedRequest request = requestBuffer.remove();
			// make sure dedicated request has information
			if (!request.isEmptyRequest()) {
				setToDedicatedMode(request);
			}
		}
	}

	private void setMode(int newState, DedicatedRequest request) {
		switch (newState) {
		case DEDICATED_MODE: {
			if (request != null) {
				this.remainingHorizontal = Math.max(this.remainingHorizontal, request.getHorizontalNeeded());
				this.remainingVertical = Math.max(this.remainingVertical, request.getVerticalNeeded());
				currentState = DEDICATED_MODE;
			}
			break;
		}
		case REGULAR_MODE: {
			remainingEvents = CANDIDATE_EVENTS_FOR_COLLAGE; 
			remainingHorizontal = 0;
			remainingVertical = 0;
			currentState = REGULAR_MODE;
		}
		}
	}

	private synchronized void setToRegularMode() {
		setMode(REGULAR_MODE, null);
	}

	private synchronized void setToDedicatedMode(DedicatedRequest request) {
		setMode(DEDICATED_MODE, request);
	}
}

