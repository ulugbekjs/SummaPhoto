package Common;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;



public class ActualEventContainerSingelton  {

	private static final ActualEventContainerSingelton instance = new ActualEventContainerSingelton();
	private Set<ActualEvent> events = new TreeSet<ActualEvent>();

	private ActualEventContainerSingelton() {
	}

	private void addEvent(ActualEvent event) {
		if ((event != null) && (events.contains(event)))
			events.add(event);
	}

	public static ActualEventContainerSingelton getInstance() {
		return instance;
	}
}
