package Common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import Generator.PixelPoint;
import Generator.LocatePicturesWithMap.SlotPushPinTuple;

public class TestsClass {
	
	public List<SlotPushPinTuple> testLocatePictuersOnMapList ()
	{
		Set<PixelPoint> firstSet = new HashSet<PixelPoint>();
		firstSet.add(new PixelPoint(30, 60));
		firstSet.add(new PixelPoint(15, 50));
		firstSet.add(new PixelPoint(10, 10));
		firstSet.add(new PixelPoint(30, 45));
	
		Set<PixelPoint> secondSet = new HashSet<PixelPoint>();
		secondSet.add(new PixelPoint(15,30));
		secondSet.add(new PixelPoint(30, 40));
		secondSet.add(new PixelPoint(30,45));
		secondSet.add(new PixelPoint(30,55));
		//LocatePicturesWithMap locatePicturesWithMap = new LocatePicturesWithMap(firstSet, secondSet);
		//List<LocatePicturesWithMap.PointsTuple> result = locatePicturesWithMap.matchPictureOnMapToPointOnFrame();
		return null;
	
	}
	public List<SlotPushPinTuple> testLocatePictuersOnMapList2 ()
	{
		Set<PixelPoint> firstSet = new HashSet<PixelPoint>();
		firstSet.add(new PixelPoint(1, 1));
		firstSet.add(new PixelPoint(4, 3));
		firstSet.add(new PixelPoint(6, 11));
		firstSet.add(new PixelPoint(9, 2));
	
	
		Set<PixelPoint> secondSet = new HashSet<PixelPoint>();
		secondSet.add(new PixelPoint(2,6));
		secondSet.add(new PixelPoint(7, 9));
		secondSet.add(new PixelPoint(13, 5));
		secondSet.add(new PixelPoint(11, 1));
	
		//LocatePicturesWithMap locatePicturesWithMap = new LocatePicturesWithMap(firstSet, secondSet);
		//List<LocatePicturesWithMap.PointsTuple> result = locatePicturesWithMap.matchPictureOnMapToPointOnFrame();
		return null;
	
	}


}
