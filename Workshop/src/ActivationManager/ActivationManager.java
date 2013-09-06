package ActivationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;
import Common.Photo;
import Common.PhotoContainer;

public class ActivationManager {

	private static final ActivationManager instance = new ActivationManager();

	//states
	private static final int REGULAR_MODE = 0;
	private static final int DEDICATED_MODE = 1;

	// TODO: maybe do this by number of photos for collage
	private static final int CANDIDATE_EVENTS_FOR_COLLAGE = 3;
	private static final int NEW_CANDIDATE_THRESHOLD_DELTA = 60;
	private static final String TAG = "ActionManager.ActionManager";


	//instance fields
	
	private BlockingQueue<DedicatedRequest> requestBuffer = new LinkedBlockingQueue<DedicatedRequest>();

	private int currentState = 0; // start in REGULAR_MODE;
	private int remainingEvents = CANDIDATE_EVENTS_FOR_COLLAGE;
	private int remainingHorizontal = 0;
	private int remainingVertical = 0;

	private Photo lastRecievedPhoto = null;
	private PhotoContainer photoContainer = null;


	private ActivationManager() {
		photoContainer = PhotoContainer.getInstance();
	}

	public static ActivationManager getInstance() {
		return instance;
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
		return (((currentState == REGULAR_MODE) && (remainingEvents == 0)) || 
				(currentState == DEDICATED_MODE && 
				((remainingHorizontal == 0) &&
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
		if ((currentState == REGULAR_MODE) && (isFirstEvent() || isNewEventCandidate(photo))) {  // new event candidate
			lastRecievedPhoto = photo;

			if (remainingEvents > 0) { 
				remainingEvents--;
			}
		}

		if (currentState == DEDICATED_MODE && photo.isHorizontal()) {
			if (remainingHorizontal > 0) 
				remainingHorizontal--;
		}
		if ((currentState == DEDICATED_MODE && !photo.isHorizontal())) {
			if (remainingVertical > 0)
				remainingVertical--;
		}
		
		photoContainer.moveToProcessedPhotos(photo);

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
		while (!photoContainer.isEmpty()) { 
			photo = photoContainer.getNextPhotoFromBuffer();
			if (photo != null) {
				isCollageNeeded = processPhoto(photo);
			}
		}

		// advance in collage process if necessary
		if (isCollageNeeded) {
			setToRegularMode(); // upon decision to create collage, switch to REGULAR_MODE
		}

		return isCollageNeeded;
	}

	

	public void addRequestToBuffer(DedicatedRequest request) {
		try {
			requestBuffer.put(request);
		} catch (InterruptedException e) {
			Log.e(TAG, "Error when adding Dedicated request to  request buffer");
		}
	}

	/**
	 * consume request sent in last run
	 * @return true if mode changed
	 */
	public boolean consumeDedictedRequests() {
		boolean changed = false;
		while (!requestBuffer.isEmpty()) {
			DedicatedRequest request = requestBuffer.remove();
			// make sure dedicated request has information
			if (!request.isEmptyRequest()) {
				 setToDedicatedMode(request);
				 changed = true;
			}
		}
		return changed;
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
			lastRecievedPhoto = null;
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
	
	@Override 
	public String toString() {
		return ("State: " + currentState + "\nremainingEvents: " + remainingEvents + "\nremainingHorizontal: "+ remainingHorizontal + "\nremainingVertical: " + remainingVertical); 
	}
}

