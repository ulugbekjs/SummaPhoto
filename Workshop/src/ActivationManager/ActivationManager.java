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
	private static final int NEW_CANDIDATE_THRESHOLD_DELTA = 300;
	private static final String TAG = ActivationManager.class.getName();


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


	/**
	 * Guesses if photo is part of the last event recoginzed by the AM
	 * @param newPhoto
	 * @return true iff photo starts a new event
	 */
	private boolean isNewEventCandidate(Photo newPhoto) {

		int delta = lastRecievedPhoto.timeDeltaInSecondsFrom(newPhoto);
		Log.d(TAG, "diff of " + newPhoto.getFileName()  + " from last photo " + newPhoto.getFileName() + ": " + delta);
		return (delta > NEW_CANDIDATE_THRESHOLD_DELTA) ? true : false;
	}

	/**
	 * Based on mode, decided if clustering should be made
	 * @return true iff clustering algorithm should be invoked
	 */
	private boolean isClusteringNeeded() {
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

		return isClusteringNeeded();
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

	

	/**
	 * method to add a DedicatedRequest to the AM queue
	 * @param request
	 */
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

	/**
	 * Changes AM mode
	 * @param newState
	 * @param request
	 */
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
	
	public int getState(){
		return this.currentState;
	}

	public synchronized void setToRegularMode() {
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

