package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

public class PhotoContainer {

	private static final String TAG = PhotoContainer.class.getName();
	private static final PhotoContainer instance = new PhotoContainer();

	private BlockingQueue<Photo> processedPhotos = new LinkedBlockingQueue<Photo>();  
	private BlockingQueue<Photo> buffer = new LinkedBlockingQueue<Photo>();

	private PhotoContainer() {
	}  

	public static PhotoContainer getInstance() {
		return instance;
	}

	public synchronized BlockingQueue<Photo> getProcessedPhotos() {
		return instance.processedPhotos;
	}

	public synchronized  Photo getNextPhotoFromBuffer() {
		return buffer.remove();
	}

	public synchronized void addToBuffer(Photo p) {
		if (!buffer.contains(p)) {
			try {
				buffer.put(p);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error adding new photo to container");
			}
		}
	}

	public synchronized void moveToProcessedPhotos(Photo photo) {
		if (!processedPhotos.contains(photo)) {
			processedPhotos.add(photo);
		}
	}

	/**
	 * delete photo from container
	 * @param deleted
	 * @return true iff the file was found and deleted
	 */
	public synchronized boolean onDelete(String deleted) {
		boolean wasDeleted = false;
		// scan queues
		for (Photo photo : processedPhotos) {
			if (photo.getFilePath().equals(deleted)) {
				processedPhotos.remove(photo);
				wasDeleted |= true;
			}
		}

		for (Photo photo : buffer) {
			if (photo.getFilePath().equals(deleted)) {
				buffer.remove(photo);
				wasDeleted |=true;
			}
		}
		return wasDeleted;
	}

	public synchronized boolean isEmpty() {
		return buffer.isEmpty();
	}

	public synchronized void clearProcessPhotos() {
		instance.processedPhotos.clear();
	}

}
