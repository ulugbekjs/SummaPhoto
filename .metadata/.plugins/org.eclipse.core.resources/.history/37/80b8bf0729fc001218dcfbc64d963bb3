package Common;

import java.util.LinkedList;
import java.util.Queue;

public class PhotoContainerSingleton {

	    private static final PhotoContainerSingleton instance = new PhotoContainerSingleton();
	    private Queue<Photo> photoQueue = new LinkedList<Photo>();
	 
	    private PhotoContainerSingleton() {
	    }
	    
	    public void addPhoto(Photo photo) {
	    	photoQueue.add(photo);
	    }
	 
	    public static PhotoContainerSingleton getInstance() {
	        return instance;
	    }
	    
	    
}
