package Generator;


public class BlockTemplate extends AbstractTemplate{
	
	public static final int BLOCKS_TEMPLATE_NUM = 3;
	
	private BlockTemplate(int blocks) {
		super(blocks);
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

//		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(1761, 1224)), 0);
//		template.addSlot(new Slot(new PixelPoint(0, 1225), new PixelPoint(1761, 2448)), 1);
//		template.addSlot(new Slot(new PixelPoint(1762, 0), new PixelPoint(2600, 1013)), 2);
//		template.addSlot(new Slot(new PixelPoint(2601, 0), new PixelPoint(3264, 1013)), 3);
//		template.addSlot(new Slot(new PixelPoint(1762, 1014), new PixelPoint(2431, 1520)), 4);
//		template.addSlot(new Slot(new PixelPoint(2432, 1014), new PixelPoint(2850, 1775)), 5);
//		template.addSlot(new Slot(new PixelPoint(2851, 1014), new PixelPoint(3264, 1775)), 6);
//		template.addSlot(new Slot(new PixelPoint(1762, 1521), new PixelPoint(2431, 2448)), 7);
//		template.addSlot(new Slot(new PixelPoint(2432, 1776), new PixelPoint(3264, 2448)), 8);
		
		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(792, 551)), 0);
		template.addSlot(new Slot(new PixelPoint(0, 552), new PixelPoint(792, 1102)), 1);
		template.addSlot(new Slot(new PixelPoint(793, 0), new PixelPoint(1170, 456)), 2);
		template.addSlot(new Slot(new PixelPoint(1171, 0), new PixelPoint(1469, 456)), 3);
		template.addSlot(new Slot(new PixelPoint(793, 456), new PixelPoint(1094, 684)), 4);
		template.addSlot(new Slot(new PixelPoint(1095, 456), new PixelPoint(1283, 799)), 5);
		template.addSlot(new Slot(new PixelPoint(1284, 456), new PixelPoint(1469, 799)), 6);
		template.addSlot(new Slot(new PixelPoint(793, 685), new PixelPoint(1094, 1102)), 7);
		template.addSlot(new Slot(new PixelPoint(1095, 800), new PixelPoint(1469, 1102)), 8);

		return template;
	}
	
	private static BlockTemplate getTemplate2() {
		BlockTemplate template = new BlockTemplate(10);

//		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(1038, 1644)), 0);
//		template.addSlot(new Slot(new PixelPoint(1039, 0), new PixelPoint(1952, 1644)), 1);
//		template.addSlot(new Slot(new PixelPoint(0, 1645), new PixelPoint(1158, 2448)), 2);
//		template.addSlot(new Slot(new PixelPoint(1159, 1645), new PixelPoint(1952, 2047)), 3);
//		template.addSlot(new Slot(new PixelPoint(1159, 2048), new PixelPoint(1952, 2448)), 4);
//		template.addSlot(new Slot(new PixelPoint(1953, 0), new PixelPoint(3264, 900)), 5);
//		template.addSlot(new Slot(new PixelPoint(1953, 901), new PixelPoint(2608, 2006)), 6);
//		template.addSlot(new Slot(new PixelPoint(2609, 901), new PixelPoint(3264, 2006)), 7);
//		template.addSlot(new Slot(new PixelPoint(1953, 2007), new PixelPoint(2684, 2448)), 8);
//		template.addSlot(new Slot(new PixelPoint(2685, 2007), new PixelPoint(3264, 2448)), 9);
		
		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(467, 740)), 0);
		template.addSlot(new Slot(new PixelPoint(468, 0), new PixelPoint(874, 740)), 1);
		template.addSlot(new Slot(new PixelPoint(0, 741), new PixelPoint(521, 1102)), 2);
		template.addSlot(new Slot(new PixelPoint(522, 741), new PixelPoint(874, 921)), 3);
		template.addSlot(new Slot(new PixelPoint(522, 922), new PixelPoint(874, 1102)), 4);
		template.addSlot(new Slot(new PixelPoint(875, 0), new PixelPoint(1469, 405)), 5);
		template.addSlot(new Slot(new PixelPoint(875, 406), new PixelPoint(1174, 903)), 6);
		template.addSlot(new Slot(new PixelPoint(1175, 406), new PixelPoint(1469, 903)), 7);
		template.addSlot(new Slot(new PixelPoint(875, 904), new PixelPoint(1208, 1102)), 8);
		template.addSlot(new Slot(new PixelPoint(1209, 904), new PixelPoint(1469, 1102)), 9);
		
		return template;
	}
	
	private static BlockTemplate getTemplate3() {
		BlockTemplate template = new BlockTemplate(6);

//		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(1815, 1225)), 0);
//		template.addSlot(new Slot(new PixelPoint(0, 1226), new PixelPoint(1815, 2448)), 1);
//		template.addSlot(new Slot(new PixelPoint(1816, 0), new PixelPoint(2653, 1681)), 2);
//		template.addSlot(new Slot(new PixelPoint(2654, 0), new PixelPoint(3264, 1681)), 3);
//		template.addSlot(new Slot(new PixelPoint(1816, 1682), new PixelPoint(2376, 2448)), 4);
//		template.addSlot(new Slot(new PixelPoint(2377, 1682), new PixelPoint(3264, 2448)), 5);
//		
		template.addSlot(new Slot(new PixelPoint(0, 0), new PixelPoint(817, 506)), 0);
		template.addSlot(new Slot(new PixelPoint(0, 507), new PixelPoint(817, 1102)), 1);
		template.addSlot(new Slot(new PixelPoint(818, 0), new PixelPoint(1194, 756)), 2);
		template.addSlot(new Slot(new PixelPoint(1195, 0), new PixelPoint(1469, 756)), 3);
		template.addSlot(new Slot(new PixelPoint(818, 757), new PixelPoint(1069, 1102)), 4);
		template.addSlot(new Slot(new PixelPoint(1070, 757), new PixelPoint(1469, 1102)), 5);
		
		return template;
	}
}
