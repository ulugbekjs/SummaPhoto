package Generator;

import java.util.HashSet;
import java.util.Set;
import Bing.StaticMap;

public class MapTemplate extends AbstractTemplate{

	public final static int MAP_TEMPLATES_NUM = 2; // number of templates existing for this template type


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

	public int getMapPixelWidth() {
		return mapSlot.getSlotWidth();
	}

	public int getMapPixelHeight() {
		return mapSlot.getSlotHeight();
	}

	public boolean setMap(StaticMap newMap) {
		if (newMap.getPixelWidth() == this.getMapPixelWidth() && // only add map if it meets the planned map dimension
				newMap.getPixelHeight() == this.getMapPixelHeight()) {
			map = newMap;
			try {
				
				mapSlot.assignToPhoto(newMap.getPhotoObject());
			}
			catch (Exception ex)
			{
				/** TODO: add exceptionsHandling **/ 
				return false;
			}
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


	// template of size 1469*1102. slot 0 and 3 are vertical
	private static MapTemplate getTemplate1() {
		
		MapTemplate template = new MapTemplate(10);

		template.addSlot(new Slot(new PixelPoint(0, 289), new PixelPoint(349, 811),new PixelPoint  (349, 523)) , 0);
		template.addSlot(new Slot(new PixelPoint(350, 812), new PixelPoint (734, 1102), new PixelPoint  (542, 812)), 1);
		template.addSlot(new Slot(new PixelPoint(735, 812), new PixelPoint (1119, 1102), new PixelPoint  (927, 812)), 2);
		template.addSlot(new Slot(new PixelPoint(1120, 290), new PixelPoint(1469, 811), new PixelPoint  (1120, 523)), 3);
		template.addSlot(new Slot(new PixelPoint(735, 0), new PixelPoint(1119, 289), new PixelPoint (927, 289)), 4);
		template.addSlot(new Slot(new PixelPoint(350, 0), new PixelPoint(734, 289), new PixelPoint (542, 289)), 5);
		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint (349, 288), new PixelPoint (349, 289)), 6);
		template.addSlot(new Slot(new PixelPoint(0, 812), new PixelPoint(349, 1102),  new PixelPoint (349, 812)), 7);
		template.addSlot(new Slot(new PixelPoint(1120, 812), new PixelPoint(1469, 1102), new PixelPoint (1120, 812)), 8);
		template.addSlot(new Slot(new PixelPoint(1120, 0), new PixelPoint(1469, 289), new PixelPoint (1120, 289)), 9);
		
		template.mapSlot = new Slot (new PixelPoint(350, 290), new PixelPoint(1119, 811));
		
		return template;
	}
	
	
	 // template of size 1469*1102. slot 0 and 3 are vertical 
	private static MapTemplate getTemplate2() {
		
		MapTemplate template = new MapTemplate(8);

		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(349, 551),new PixelPoint  (349, 400)) , 0);
		template.addSlot(new Slot(new PixelPoint(0, 552), new PixelPoint(349, 1102),new PixelPoint  (349, 682)) , 1);
		template.addSlot(new Slot(new PixelPoint(1120, 0), new PixelPoint(1469, 551),new PixelPoint  (1120, 400)) , 2);
		template.addSlot(new Slot(new PixelPoint(1120, 552), new PixelPoint(1469, 1102),new PixelPoint  (1120, 682)) , 3);
		template.addSlot(new Slot(new PixelPoint(350, 812), new PixelPoint (734, 1102), new PixelPoint  (542, 812)), 4);
		template.addSlot(new Slot(new PixelPoint(735, 812), new PixelPoint (1119, 1102), new PixelPoint  (927, 812)), 5);
		template.addSlot(new Slot(new PixelPoint(735, 0), new PixelPoint(1119, 289), new PixelPoint (927, 289)), 6);
		
		template.addSlot(new Slot(new PixelPoint(350, 0), new PixelPoint(734, 289), new PixelPoint (542, 289)), 7);
		template.mapSlot = new Slot (new PixelPoint(350, 290), new PixelPoint(1119, 811));
		
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
