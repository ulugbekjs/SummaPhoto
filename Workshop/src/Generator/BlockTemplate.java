package Generator;

public class BlockTemplate extends AbstractTemplate{
	
	private BlockTemplate(int blocks) {
		super(blocks);
		TEMPLATES_NUM = 3;
	}

	public static BlockTemplate getTemplate(int num) {
		BlockTemplate template = null;
		switch (num) {
		case 1: {
			template =  getTemplate1();
			break;
		}
		case 2: {
			template =  getTemplate2();
			break;
		}
		case 3: {
			template =  getTemplate3();
			break;
		}
		default:
			break;
		}

		return template;
	}

	private static BlockTemplate getTemplate1() {
		BlockTemplate template = new BlockTemplate(9);

		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(1761, 1357)), 0);
		template.addSlot(new Slot(new PixelPoint(0, 1358), new PixelPoint(1761, 2448)), 1);
		template.addSlot(new Slot(new PixelPoint(1762, 0), new PixelPoint(2715, 1013)), 2);
		template.addSlot(new Slot(new PixelPoint(2716, 0), new PixelPoint(3264, 1013)), 3);
		template.addSlot(new Slot(new PixelPoint(1762, 1014), new PixelPoint(2431, 1725)), 4);
		template.addSlot(new Slot(new PixelPoint(2432, 1014), new PixelPoint(2850, 1775)), 5);
		template.addSlot(new Slot(new PixelPoint(2851, 1014), new PixelPoint(3264, 1775)), 6);
		template.addSlot(new Slot(new PixelPoint(1762, 1726), new PixelPoint(2431, 2448)), 7);
		template.addSlot(new Slot(new PixelPoint(2432, 1776), new PixelPoint(3264, 2448)), 8);

		return template;
	}
	
	private static BlockTemplate getTemplate2() {
		BlockTemplate template = new BlockTemplate(10);

		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(1038, 1644)), 0);
		template.addSlot(new Slot(new PixelPoint(1039, 0), new PixelPoint(1952, 1644)), 1);
		template.addSlot(new Slot(new PixelPoint(0, 1645), new PixelPoint(1158, 2448)), 2);
		template.addSlot(new Slot(new PixelPoint(1159, 1645), new PixelPoint(1952, 2047)), 3);
		template.addSlot(new Slot(new PixelPoint(1159, 2048), new PixelPoint(1952, 2448)), 4);
		template.addSlot(new Slot(new PixelPoint(1953, 0), new PixelPoint(3264, 900)), 5);
		template.addSlot(new Slot(new PixelPoint(1953, 901), new PixelPoint(2684, 2006)), 6);
		template.addSlot(new Slot(new PixelPoint(2685, 901), new PixelPoint(3264, 2006)), 7);
		template.addSlot(new Slot(new PixelPoint(1953, 2007), new PixelPoint(2773, 2448)), 8);
		template.addSlot(new Slot(new PixelPoint(2774, 2007), new PixelPoint(3264, 2448)), 9);
		
		return template;
	}
	
	private static BlockTemplate getTemplate3() {
		BlockTemplate template = new BlockTemplate(6);

		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(1815, 1225)), 0);
		template.addSlot(new Slot(new PixelPoint(0, 1226), new PixelPoint(1815, 2448)), 1);
		template.addSlot(new Slot(new PixelPoint(1816, 0), new PixelPoint(2653, 1681)), 2);
		template.addSlot(new Slot(new PixelPoint(2654, 0), new PixelPoint(3264, 1681)), 3);
		template.addSlot(new Slot(new PixelPoint(1816, 1682), new PixelPoint(2376, 2448)), 4);
		template.addSlot(new Slot(new PixelPoint(2377, 1682), new PixelPoint(3264, 2448)), 5);
		
		return template;
	}
}
