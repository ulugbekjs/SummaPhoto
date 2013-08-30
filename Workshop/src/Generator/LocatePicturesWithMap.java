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


	// Subsets of pointsOnFrameSet and  picturesOnMapSet for recursive algorithm issues
	private Set<PixelPoint> firstSubSetofSlotsPoints;
	private Set<PixelPoint> firstSubSetOfPushPinPoints;
	private Set<PixelPoint> secondSubSetofSlotsPoints;
	private Set<PixelPoint> secondSubSetOfPushPinPoints;

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
		PixelPoint closestSlot;
		if (pushPinsSubSet.size() == 0)
			return;
		if (pushPinsSubSet.size() == 1){
			PixelPoint lastPointofMapSet = pushPinsSubSet.iterator().next();
			PixelPoint lastPointOnFramePixelPoint = slotsSubSet.iterator().next();
			slotsToPushPinList.add(new SlotPushPinTuple (lastPointofMapSet, pixelPointToPushPinDictionary.get(lastPointofMapSet),
					lastPointOnFramePixelPoint,pixelPointToSlotDictionary.get(lastPointOnFramePixelPoint)));
			return;
		}


		for (PixelPoint pushPinPoint : pushPinsSubSet) {
			// First try to split the sets with closest point in pointsOnFrameSubSetSet to the chosen point
			closestSlot = findClosestPointInSet(pushPinPoint,slotsSubSet);
			if (isSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint, closestSlot))
			{
				slotsToPushPinList.add(new SlotPushPinTuple(pushPinPoint, pixelPointToPushPinDictionary.get(pushPinPoint), 
						closestSlot,pixelPointToSlotDictionary.get(closestSlot) ));
				splitSetsEqualPointsTuple (firstSubSetofSlotsPoints,firstSubSetOfPushPinPoints);
				splitSetsEqualPointsTuple (secondSubSetofSlotsPoints, secondSubSetOfPushPinPoints);
				return;
			}
			else {
				for (PixelPoint slotPoint : slotsSubSet)
				{
					if (isSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint , slotPoint))
					{
						slotsToPushPinList.add(new SlotPushPinTuple(pushPinPoint,pixelPointToPushPinDictionary.get(pushPinPoint), 
								slotPoint,pixelPointToSlotDictionary.get(slotPoint)));
						splitSetsEqualPointsTuple (firstSubSetofSlotsPoints,firstSubSetOfPushPinPoints);
						splitSetsEqualPointsTuple (secondSubSetofSlotsPoints, secondSubSetOfPushPinPoints);
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
			PixelPoint pushPinCandidate, PixelPoint slotCandidate)
	{
		double slope;
		Integer numberOfPushPinsAboveLine = 0;
		Integer numberOfSlotsAboveLine = 0;

		//initiating sub-sets which will be used for recursion
		firstSubSetOfPushPinPoints = new HashSet<PixelPoint>();
		secondSubSetOfPushPinPoints = new HashSet<PixelPoint>();
		firstSubSetofSlotsPoints = new HashSet<PixelPoint>();
		secondSubSetofSlotsPoints = new HashSet<PixelPoint>();

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
			return isSplitingEqualUndefinedSlope (pushPinsSubSet, slotsSubSet, pushPinCandidate, slotCandidate);
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
			PixelPoint pushPinCandidate, PixelPoint slotCandidate)
	{
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
