package Generator;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.R.integer;

import Bing.StaticMap;

public class MapTemplate extends AbstractTemplate{


	private Slot mapSlot;

	private StaticMap map = null; // map in center of collage
	/**
	 * Template can only be created by static methods
	 * @param slotsInTemplate
	 * @param width map pixel width 
	 * @param height map pixel height
	 */
	private MapTemplate(int slotsInTemplate) {
		super(slotsInTemplate);
	}

	public double getMapPixelWidth() {
		return mapSlot.getSlotWidth();
	}

	public double getMapPixelHeight() {
		return mapSlot.getSlotHeight();
	}

	public boolean setMap(StaticMap newMap) {
		if (newMap.getPixelWidth() == this.getMapPixelWidth() && // only add map if it meets the planned map dimension
				newMap.getPixelHeight() == this.getMapPixelHeight()) {
			map = newMap;
			return true;
		}
		else {
			return false;
		}
	}
	
	public Slot getMapSlot() {
		return this.mapSlot;
	}
	
	
	/**
	 * @return The connection points of all slots in the tamplate
	 */
	public Set<PixelPoint> getLinesConnectionPoints()
	{
		if (slots == null)
			return null;
		Set<PixelPoint> connectionPoints = new HashSet<PixelPoint>();
		for (Integer i = 0; i< slots.length; i++)
		{
			if (slots[i].hasConnectingLinePoint())
			{
				connectionPoints.add(slots[i].getConnectingLinePoint());
			}
		}
		return connectionPoints;
	}
	
	public static MapTemplate getTemplate(int num) {
		MapTemplate template = null;
		switch (num) {
		case 1:
		{
			template =  getTemplate1();
			break;
		}
		case 2:
		{
			template =  getTemplate2();
			break;
		}
		default:
			break;
		}
		
		return template;
	}

	/**
	 * Constructing hard-coded template1
	 * @return
	 */
	private static MapTemplate getTemplate1() {
		MapTemplate template = new MapTemplate(8);

		// building slots - Counter Clockwise, starting with topLeft
		template.addSlot(new Slot(
				new PixelPoint(0, 367), 
				new PixelPoint(642, 1224)),
				0);
		template.addSlot(new Slot(
				new PixelPoint(0, 1224), 
				new PixelPoint(642, 2080)), 
				1);
		template.addSlot(new Slot(
				new PixelPoint(775, 1805), 
				new PixelPoint(1632, 2448)), 
				2);
		template.addSlot(new Slot(
				new PixelPoint(1632, 1805),
				new PixelPoint(2488, 2448)), 
				3);
		template.addSlot(new Slot(
				new PixelPoint(2621, 1224),
				new PixelPoint(3264, 2080)),
				4);
		template.addSlot(new Slot(
				new PixelPoint(2621, 367), 
				new PixelPoint(3264, 1224)), 
				5);
		template.addSlot(new Slot(new PixelPoint(1632, 0),
				new PixelPoint(2488, 642)), 
				6);
		template.addSlot(new Slot(
				new PixelPoint(775, 0),
				new PixelPoint(1632, 642)),
				7);

		template.mapSlot = new Slot(new PixelPoint(642, 642), new PixelPoint(2621, 1805));

		return template;
	}
	
	
	/**
	 * Constructing hard-coded template2
	 * @return
	 */
	private static MapTemplate getTemplate2() {
		
		MapTemplate template = new MapTemplate(12);

		template.addSlot(new Slot(new PixelPoint(0, 642), new PixelPoint(642, 1224),new PixelPoint  (642, 933)) , 0);
		template.addSlot(new Slot(new PixelPoint(0, 1224), new PixelPoint(642, 1805), new PixelPoint  (642, 1514)), 1);
		template.addSlot(new Slot(new PixelPoint(775, 1805), new PixelPoint (1632, 2448), new PixelPoint  (1204, 1805)), 2);
		template.addSlot(new Slot(new PixelPoint(1632, 1805), new PixelPoint (2488, 2448), new PixelPoint  (2060, 1805)), 3);
		template.addSlot(new Slot(new PixelPoint(2621, 1224), new PixelPoint (3264, 1805), new PixelPoint  (2621, 1514)), 4);
		template.addSlot(new Slot(new PixelPoint(2621, 642), new PixelPoint(3264, 1224), new PixelPoint  (2621, 933)), 5);
		template.addSlot(new Slot(new PixelPoint(1632, 0), new PixelPoint(2488, 642), new PixelPoint (2060, 642)), 6);
		template.addSlot(new Slot(new PixelPoint(775, 0), new PixelPoint(1632, 642), new PixelPoint (1204, 642)), 7);
		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint (775, 642), new PixelPoint (709, 642)), 8);
		template.addSlot(new Slot(new PixelPoint(0, 1805), new PixelPoint(775, 2448),  new PixelPoint (709, 1805)), 9);
		template.addSlot(new Slot(new PixelPoint(2488, 1805), new PixelPoint(3264, 2448), new PixelPoint (2554, 1805)), 10);
		template.addSlot(new Slot(new PixelPoint(2488, 0), new PixelPoint(3264, 642), new PixelPoint (2554, 642)), 11);
		
		template.mapSlot = new Slot (new PixelPoint(642, 642), new PixelPoint(2621, 1805));
		
		return template;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("[");
		for (int i = 0; i<slots.length; i++) {
			buffer.append(i + "=" + slots[i].getSlotWidth() + "," + slots[i].getSlotHeight() + ";" );
		}
		
		buffer.append("map=" + mapSlot.getSlotWidth() + "," + mapSlot.getSlotHeight());
		buffer.append("]");
		
		return buffer.toString();
	}

}
