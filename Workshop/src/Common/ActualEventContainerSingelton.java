package Common;

import java.util.Set;
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
	
	public Set<ActualEvent> getEvents() {
		return this.events;
	}

	public static ActualEventContainerSingelton getInstance() {
		return instance;
	}
}
