package ActivationManager;

import java.util.LinkedList;

@SuppressWarnings("serial")
public class LimitedLinkedList<E> extends LinkedList<E> {
	
	int limit;
	
	public LimitedLinkedList(int limit) {
		this.limit = limit;
	}
	@Override
	public boolean add(E e) {
		if (super.size() < limit)
			 return super.add(e);
		return false;
	};
}
