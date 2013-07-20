package Common;

public class PhotoContainerSingleton {

	    private static final PhotoContainerSingleton instance = new PhotoContainerSingleton();
	    private 
	 
	    private PhotoContainerSingleton() {}
	 
	    public static PhotoContainerSingleton getInstance() {
	        return instance;
	    }
	    
	    
}
