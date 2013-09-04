package Generator;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTemplate {
	
	public static final int MAP_TYPE = 1;
	public static final int BLOCK_TYPE = 2;

	//protected instance fields
	protected Slot[] slots = null; // slots[0] == Top Left Photo
	protected List<Integer> verticalSlots;
	protected List<Integer> horizontalSlots;

	public List<Integer> getVerticalSlots() {
		return verticalSlots;
	}

	public List<Integer> getHorizontalSlots() {
		return horizontalSlots;
	}

	public AbstractTemplate(int slotsInTemplate) {
		slots = new Slot[slotsInTemplate];
		verticalSlots = new ArrayList<Integer>();
		horizontalSlots = new ArrayList<Integer>();
	}

	public int getCurrentNumberOfSlots() {
		return getNumberOfSlots() - horizontalSlots.size() - verticalSlots.size();
	}

	public int getNumberOfSlots() {
		return slots.length;
	}

	public Slot getSlot(int number) {
		return slots[number];
	}

	public boolean isTemplateFull() {
		return (slots.length == getCurrentNumberOfSlots());
	}

	public int getRemainingVerticalSlots() {
		return verticalSlots.size();
	}
	
	public int getRemainingHorizontalSlots() {
		return horizontalSlots.size();
	}
	
	protected void addSlot(Slot slot, int index) {
		this.slots[index] = slot;
		
		if (slot.isHorizontal()) {
			this.horizontalSlots.add(index);
		}
		else {
			this.verticalSlots.add(index);
		}
	}	
}

