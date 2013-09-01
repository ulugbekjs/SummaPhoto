package Generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import Bing.Pushpin;



/** This class provides methods to locate the different pictures around the map in good positions **/
public class LocatePicturesWithMap {


	private HashMap<PixelPoint,Slot> pixelPointToSlotDictionary;
	private HashMap<PixelPoint,Pushpin> pixelPointToPushPinDictionary;

	// This set includes the location of each picture on the map 
	private Set<PixelPoint> pushPinPointsSet;
	// This set includes the location of each different points on the frame around the map
	private Set<PixelPoint> slotPointsSet;


	private List<SlotPushPinTuple> slotsToPushPinList;

	public LocatePicturesWithMap (HashMap<PixelPoint,Slot> pixelPointToSlotDictionary,
			HashMap<PixelPoint,Pushpin> pixelPointToPushPinDictionary)
	{
		this.pixelPointToPushPinDictionary = pixelPointToPushPinDictionary;
		this.pixelPointToSlotDictionary = pixelPointToSlotDictionary;
		this.pushPinPointsSet =  pixelPointToPushPinDictionary.keySet();
		this.slotPointsSet = pixelPointToSlotDictionary.keySet();
	}

	/**
	 * @return list of slotsPushPinTuple which indicated contains the data which pushPin related to every slot in the template
	 */
	public List<SlotPushPinTuple> matchPictureOnMapToPointOnFrame ()
	{
		slotsToPushPinList = new LinkedList<SlotPushPinTuple>();
		splitSetsEqualPointsTuple (pushPinPointsSet,slotPointsSet);
		return slotsToPushPinList;
	}

	private void splitSetsEqualPointsTuple (Set<PixelPoint> pushPinsSubSet, Set<PixelPoint> slotsSubSet)
	{
		// Subsets of pointsOnFrameSet and  picturesOnMapSet for recursive algorithm issues
		Set<PixelPoint> firstSubSetofSlotsPoints =new HashSet<PixelPoint>();
		Set<PixelPoint> firstSubSetOfPushPinPoints = new HashSet<PixelPoint>();
		Set<PixelPoint> secondSubSetofSlotsPoints =new HashSet<PixelPoint>();
		Set<PixelPoint> secondSubSetOfPushPinPoints =new HashSet<PixelPoint>();

		List<Set<PixelPoint>> listOfSplitedPixelPointSets = new LinkedList<Set<PixelPoint>>();
		listOfSplitedPixelPointSets.add(firstSubSetofSlotsPoints);
		listOfSplitedPixelPointSets.add(firstSubSetOfPushPinPoints);
		listOfSplitedPixelPointSets.add(secondSubSetofSlotsPoints);
		listOfSplitedPixelPointSets.add(secondSubSetOfPushPinPoints);

		SlotPushPinTuple tempTupleToAdd;

		PixelPoint closestSlot;
//		if (pushPinsSubSet.size() == 0)
//			return;
//		if (pushPinsSubSet.size() == 1){
//			PixelPoint lastSlot = null;
//			PixelPoint lastPushPin = null;
//			if (pushPinsSubSet.iterator().hasNext() && slotsSubSet.iterator().hasNext()) {
//				lastPushPin = pushPinsSubSet.iterator().next(); 
//				lastSlot = slotsSubSet.iterator().next();
//				tempTupleToAdd = new SlotPushPinTuple (lastPushPin, pixelPointToPushPinDictionary.get(lastPushPin),
//						lastSlot,pixelPointToSlotDictionary.get(lastSlot));
//				slotsToPushPinList.add(tempTupleToAdd);
//			}
//		return;

			
			if (pushPinsSubSet.size() == 0)
				return;
			if (pushPinsSubSet.size() == 1){
				PixelPoint lastPushPin = pushPinsSubSet.iterator().next();
				PixelPoint lastSlot = slotsSubSet.iterator().next();
				tempTupleToAdd = new SlotPushPinTuple (lastPushPin, pixelPointToPushPinDictionary.get(lastPushPin),
						lastSlot,pixelPointToSlotDictionary.get(lastSlot));
				slotsToPushPinList.add(tempTupleToAdd);
				return;
			}


		for (PixelPoint pushPinPoint : pushPinsSubSet) {
			// First try to split the sets with closest point in pointsOnFrameSubSetSet to the chosen point
			closestSlot = findClosestPointInSet(pushPinPoint,slotsSubSet);
			if (isSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint, closestSlot, listOfSplitedPixelPointSets))
			{
				tempTupleToAdd = new SlotPushPinTuple (pushPinPoint, pixelPointToPushPinDictionary.get(pushPinPoint),
						closestSlot,pixelPointToSlotDictionary.get(closestSlot));
				slotsToPushPinList.add(tempTupleToAdd);
				splitSetsEqualPointsTuple (firstSubSetOfPushPinPoints, firstSubSetofSlotsPoints);
				splitSetsEqualPointsTuple (secondSubSetOfPushPinPoints, secondSubSetofSlotsPoints);
				return;
			}
			else {
				for (PixelPoint slotPoint : slotsSubSet)
				{
					if (isSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint , slotPoint, listOfSplitedPixelPointSets))
					{
						tempTupleToAdd = new SlotPushPinTuple (pushPinPoint, pixelPointToPushPinDictionary.get(pushPinPoint),
								slotPoint,pixelPointToSlotDictionary.get(slotPoint));
						slotsToPushPinList.add(tempTupleToAdd);
						splitSetsEqualPointsTuple (firstSubSetOfPushPinPoints, firstSubSetofSlotsPoints);
						splitSetsEqualPointsTuple (secondSubSetOfPushPinPoints, secondSubSetofSlotsPoints);
						return;
					}
				}
			}
		}

	}



	/**
	 * @param point
	 * @param setOfPoints
	 * @return The poxelPoint in setOfPoints which is the closest to point
	 */
	private PixelPoint findClosestPointInSet (PixelPoint point,Set<PixelPoint> setOfPoints)
	{
		PixelPoint closestPoint = null;
		double minDistance = Double.MAX_VALUE;
		for (PixelPoint pointInSet :setOfPoints )
		{
			if (point.distanceFrom(pointInSet) <minDistance )
			{
				minDistance = point.distanceFrom(pointInSet);
				closestPoint = pointInSet; 
			}
		}
		return closestPoint;
	}


	/**
	 * @param pushPinsSubSet - sub set of pushPins that had to be matched to slots
	 * @param slotsSubSet - sub set of slots that had to me matched to push pins
	 * @param pushPinCandidate - push pin candidate
	 * @param slotCandidate - slot candidate
	 * @return True if the pushPin candidate and slot candidate split the plane in such way that the line between them won't
	 * intersect other lines between pushPins and slots
	 */
	private Boolean isSplitingEqual (Set<PixelPoint> pushPinsSubSet, Set<PixelPoint> slotsSubSet,
			PixelPoint pushPinCandidate, PixelPoint slotCandidate, List<Set<PixelPoint>> listOfSplitedPixelPointSets)
	{

		Set<PixelPoint> firstSubSetofSlotsPoints = listOfSplitedPixelPointSets.get(0);
		Set<PixelPoint> firstSubSetOfPushPinPoints = listOfSplitedPixelPointSets.get(1);
		Set<PixelPoint> secondSubSetofSlotsPoints = listOfSplitedPixelPointSets.get(2);
		Set<PixelPoint> secondSubSetOfPushPinPoints = listOfSplitedPixelPointSets.get(3);
		firstSubSetofSlotsPoints.clear();
		firstSubSetOfPushPinPoints.clear();
		secondSubSetofSlotsPoints.clear();
		secondSubSetOfPushPinPoints.clear();


		double slope;
		Integer numberOfPushPinsAboveLine = 0;
		Integer numberOfSlotsAboveLine = 0;

		Boolean isUndefinedSlope = pushPinCandidate.getX() == slotCandidate.getX();

		// the equation of the line between those points is: Y = slope * x + constant
		if (!isUndefinedSlope)
		{
			slope = calculateSlope (pushPinCandidate,slotCandidate );
			double constant = pushPinCandidate.getY() - slope * pushPinCandidate.getX();
			for (PixelPoint pushPin :pushPinsSubSet)
			{
				if (pushPin == pushPinCandidate)
					continue;
				if (isPointAboveLine(pushPin, slope, constant))
				{
					numberOfPushPinsAboveLine++;
					firstSubSetOfPushPinPoints.add(pushPin);
				}
				else {
					secondSubSetOfPushPinPoints.add(pushPin);
				}
			}
			for (PixelPoint slot :slotsSubSet )
			{
				if (slot == slotCandidate)
					continue;
				if (isPointAboveLine(slot, slope, constant))
				{
					numberOfSlotsAboveLine++;
					firstSubSetofSlotsPoints.add(slot);
				}
				else {
					secondSubSetofSlotsPoints.add(slot);
				}
			}
		}
		else 
		{
			return isSplitingEqualUndefinedSlope (pushPinsSubSet, slotsSubSet, pushPinCandidate, slotCandidate, listOfSplitedPixelPointSets);
		}
		return (numberOfPushPinsAboveLine == numberOfSlotsAboveLine);
	}
	/**
	 * @param pushPinsSubSet - sub set of pushPins that had to be matched to slots
	 * @param slotsSubSet - sub set of slots that had to me matched to push pins
	 * @param pushPinCandidate - push pin candidate
	 * @param slotCandidate - slot candidate
	 * @return True if the pushPin candidate and slot candidate split the plane in such way that the line between them won't
	 * intersect other lines between pushPins and slots
	 */
	private Boolean isSplitingEqualUndefinedSlope (Set<PixelPoint> pushPinsSubSet, Set<PixelPoint> slotsSubSet,
			PixelPoint pushPinCandidate, PixelPoint slotCandidate, List<Set<PixelPoint>> listOfSplitedPixelPointSets)
	{

		Set<PixelPoint> firstSubSetofSlotsPoints = listOfSplitedPixelPointSets.get(0);
		Set<PixelPoint> firstSubSetOfPushPinPoints = listOfSplitedPixelPointSets.get(1);
		Set<PixelPoint> secondSubSetofSlotsPoints = listOfSplitedPixelPointSets.get(2);
		Set<PixelPoint> secondSubSetOfPushPinPoints = listOfSplitedPixelPointSets.get(3);
		Integer numberOfPushPinsAboveLine = 0;
		Integer numberOfSlotsAboveLine = 0;
		double verticalLineX = pushPinCandidate.getX();
		for (PixelPoint pushPin :pushPinsSubSet )
		{
			if (pushPinCandidate.getX() > verticalLineX)
			{
				numberOfPushPinsAboveLine++;
				firstSubSetOfPushPinPoints.add(pushPin);
			}
			else {
				secondSubSetOfPushPinPoints.add(pushPin);
			}
		}
		for (PixelPoint slot :slotsSubSet )
		{
			if (slot.getX() > verticalLineX)
			{
				numberOfSlotsAboveLine++;
				firstSubSetofSlotsPoints.add(slot);
			}
			else {
				secondSubSetofSlotsPoints.add(slot);
			}
		}
		return (numberOfSlotsAboveLine == numberOfPushPinsAboveLine);
	}


	/** This methods checks whether the point is above the line represented by the slope and constant **/ 
	private Boolean isPointAboveLine (PixelPoint point, double slope, double constant)
	{
		if (point.getX() * slope + constant < point.getY())
			return true;
		return false;
	}

	/**
	 * 
	 * @param pointA - first point
	 * @param PointB - second point
	 * @return the slope of the line which connect those two points
	 */
	private double calculateSlope (PixelPoint pointA, PixelPoint PointB)
	{
		Double deltaY;
		Double deltaX;
		Double slope;
		if (pointA.getX() > PointB.getX() ){
			deltaY = (double) (pointA.getY() - PointB.getY());
			deltaX = (double) (pointA.getX() - PointB.getX());
		}
		else {
			deltaY = (double) (PointB.getY() - pointA.getY() );
			deltaX = (double) (PointB.getX()- pointA.getX());
		}
		slope = deltaY / deltaX;
		return slope;
	}

	/**
	 * This class represents a slot-pushPin tupple, which indicates that in specific collage a pushPin is connected to a slot
	 * @author omri
	 *
	 */
	public class SlotPushPinTuple
	{
		private PixelPoint pushPinPoint;
		private PixelPoint slotConnectionPoint;
		private Slot slot;
		private Pushpin pushPin;


		public SlotPushPinTuple (PixelPoint pushPinPoint, Pushpin pushpin, PixelPoint slotConnectionPoint,  Slot slot)
		{
			this.pushPinPoint = pushPinPoint;
			this.slotConnectionPoint = slotConnectionPoint;
			this.pushPin = pushpin;
			this.slot = slot;	
		}

		public PixelPoint getPointOnMapPixelPoint ()
		{
			return pushPinPoint;
		}

		public PixelPoint getPointOnFrame ()
		{
			return slotConnectionPoint;
		}

		public Slot getSlot ()
		{
			return this.slot;
		}


		public Pushpin getPushpin()
		{
			return this.pushPin;
		}

	}



}
