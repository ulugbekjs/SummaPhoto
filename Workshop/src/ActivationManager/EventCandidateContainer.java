package ActivationManager;

import java.util.LinkedList;
import java.util.List;

public class EventCandidateContainer {

	private static final EventCandidateContainer instance = new EventCandidateContainer(); 
	
	private List<EventCandidate> events = new LinkedList<EventCandidate>();

	private int eventCandidateCounter = 0;

	public int getEventCandidateCounter() {
		return eventCandidateCounter;
	}
	
	public boolean isEmpty(){
		return (eventCandidateCounter == 0);
	}
	
	public EventCandidate getLastAddedEvent() {
		return events.get(events.size()-1);
	}
	
	private EventCandidateContainer() {}
	
	public static EventCandidateContainer getInstance() {
		return instance;
	}
	
	public void addEvent(EventCandidate e) {
		events.add(e);
	}
	
	
}
