package ActivationManager;

import java.util.LinkedList;
import java.util.List;

public class CandidatePhotoContainer {

	private static final CandidatePhotoContainer instance = new CandidatePhotoContainer(); 
	
	private List<EventCandidate> events = new LinkedList<EventCandidate>();

	public int getEventCandidateNumber() {
		return events.size();
	}
	
	public boolean isEmpty(){
		return (events.size() == 0);
	}
	
	public EventCandidate getLastAddedEvent() {
		return events.get(events.size()-1);
	}
	
	private CandidatePhotoContainer() {}
	
	public static CandidatePhotoContainer getInstance() {
		return instance;
	}
	
	public void addEvent(EventCandidate e) {
		events.add(e);
	}
	
	public List<EventCandidate> getAllEventsInContainer() {
		return events;
	}
}
