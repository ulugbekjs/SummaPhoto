
package Generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import android.util.Log;

import Bing.Pushpin;


/**
 * 
 * @author omri
 *This class provides methods to locate the different pictures around the map in good positions
*/

public class LocatePicturesWithMap {


	private final String TAG = "LoctePicturesOnMap" ;
	
	private HashMap<PixelPoint,Slot> pixelPointToSlotDictionary;
	private HashMap<PixelPoint,Pushpin> pixelPointToPushPinDictionary;

	// Those sets includes the location of each pushPin in the collage 
	private Set<PixelPoint> horizontalPushPinPointsSet;
	private Set<PixelPoint> verticalPushPinPointsSet;
	// Those sets includes the location of each different slot's connection points on the frame around the map
	private Set<PixelPoint> horizontalSlotPointsSet;
	private Set<PixelPoint> verticalSlotPointsSet;


	// a list which will include the object which represents which picture shpuld be populated in each slot
	private List<SlotPushPinTuple> slotsToPushPinList;

	public LocatePicturesWithMap (HashMap<PixelPoint,Slot> pixelPointToSlotDictionary, 
			HashMap<PixelPoint,Pushpin> pixelPointToPushPinDictionary)
	{

		this.pixelPointToPushPinDictionary = pixelPointToPushPinDictionary;
		this.pixelPointToSlotDictionary = pixelPointToSlotDictionary;
		verticalPushPinPointsSet = new HashSet<PixelPoint>();
		verticalSlotPointsSet = new HashSet<PixelPoint>();
		horizontalPushPinPointsSet = new HashSet<PixelPoint>();
		horizontalSlotPointsSet = new HashSet<PixelPoint>();
		initiateSlotsAndPushPinsSets (this.pixelPointToSlotDictionary, this.pixelPointToPushPinDictionary);

	}

	/**
	 * @param pixelPointToSlotDictionary
	 * @param pixelPointToPushPinDictionary
	 * The method initiates relevant sets for the algorithm 
	 */
	private void initiateSlotsAndPushPinsSets (HashMap<PixelPoint,Slot> pixelPointToSlotDictionary,
			HashMap<PixelPoint,Pushpin> pixelPointToPushPinDictionary)
	{
		if ((pixelPointToSlotDictionary == null) || (pixelPointToPushPinDictionary == null))
		{
			Log.d(TAG, "one of the arguments for constructor is null");
			return;
		}
		for (Map.Entry<PixelPoint,Slot> entry :pixelPointToSlotDictionary.entrySet())
		{
			if (entry.getValue().isHorizontal())
			{
				horizontalSlotPointsSet.add(entry.getKey());
			}
			else 
			{
				verticalSlotPointsSet.add(entry.getKey());
			}
		}
		for (Map.Entry<PixelPoint,Pushpin> entry :pixelPointToPushPinDictionary.entrySet())
		{
			if (entry.getValue().getPhoto().isHorizontal())
			{
				horizontalPushPinPointsSet.add(entry.getKey());
			}
			else 
			{
				verticalPushPinPointsSet.add(entry.getKey());
			}
		}
	}


	/**
	 * @return list of slotsPushPinTuple which indicated the data which pushPin related to every slot in the template
	 */
	public List<SlotPushPinTuple> matchPicturesOnMapToPointOnFrame ()
	{
		slotsToPushPinList = new LinkedList<SlotPushPinTuple>();
		splitSetsEqualPointsTuple (horizontalPushPinPointsSet,horizontalSlotPointsSet, false);
		splitSetsEqualPointsTuple(verticalPushPinPointsSet, verticalSlotPointsSet, true);
		return slotsToPushPinList;
	}

	
	/**
	 * @param pushPinsSubSet - set of pushPine that should be connected to slots
	 * @param slotsSubSet - set of slots that should be connected to pushPins 
	 * @param reduceIntersections - if need to reduce the number of intersections
	 * @return - True if there is a line that connects one pushPin and one slot, such as the number of pushPins at one side
	 * of the line equals  the number of slot's connection points . If so, the method continues to split recursively the subSets, 
	 * and adds the relevant slot-PushPin tuple to the list
	 */
	private Boolean splitSetsEqualPointsTuple (Set<PixelPoint> pushPinsSubSet, 
			Set<PixelPoint> slotsSubSet, boolean reduceIntersections)
	{
		// Subsets of pointsOnFrameSet and  picturesOnMapSet for recursive algorithm issues
		Set<PixelPoint> firstSubSetofSlotsPoints =new HashSet<PixelPoint>();
		Set<PixelPoint> firstSubSetOfPushPinPoints = new HashSet<PixelPoint>();
		Set<PixelPoint> secondSubSetofSlotsPoints =new HashSet<PixelPoint>();
		Set<PixelPoint> secondSubSetOfPushPinPoints =new HashSet<PixelPoint>();

		// list of subset for recursive calls for the function if needed
		List<Set<PixelPoint>> listOfSplitedPixelPointSets = new LinkedList<Set<PixelPoint>>();
		listOfSplitedPixelPointSets.add(firstSubSetofSlotsPoints);
		listOfSplitedPixelPointSets.add(firstSubSetOfPushPinPoints);
		listOfSplitedPixelPointSets.add(secondSubSetofSlotsPoints);
		listOfSplitedPixelPointSets.add(secondSubSetOfPushPinPoints);

		SlotPushPinTuple tempTupleToAdd;

		// dictionary that will contain pushPin-slot tuple that split the plane as needed, but the line that connects them
		// intersects other lines between slots and pushPins
		TreeMap<Integer, SlotPushPinTuple> candidtesTuplesHashMap = new TreeMap<Integer, LocatePicturesWithMap.SlotPushPinTuple>();
		
		PixelPoint closestSlot;
		if (pushPinsSubSet.size() != slotsSubSet.size())
		{
			// may happen not because of error when there are extra photos 
			Log.d(TAG, "Number of slots and number of pictures is not equal");
		}
		if (slotsSubSet.size() == 0)
			return true;

		int interscetionsNumber;
		for (PixelPoint pushPinPoint : pushPinsSubSet) {
			// First try to split the sets with closest point in pointsOnFrameSubSetSet to the chosen point
			closestSlot = findClosestPointInSet(pushPinPoint,slotsSubSet);
			if (areSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint, closestSlot, listOfSplitedPixelPointSets))
			{
				tempTupleToAdd = new SlotPushPinTuple (pushPinPoint, pixelPointToPushPinDictionary.get(pushPinPoint),
						closestSlot,pixelPointToSlotDictionary.get(closestSlot));
				interscetionsNumber =  calculateIntersections (pushPinPoint,closestSlot);
				if ((reduceIntersections) && (interscetionsNumber > 0))
					candidtesTuplesHashMap.put(interscetionsNumber, tempTupleToAdd);
				else {
					slotsToPushPinList.add(tempTupleToAdd);
					if (slotsToPushPinList.size() == pixelPointToSlotDictionary.keySet().size())
						return true;
					splitSetsEqualPointsTuple (firstSubSetOfPushPinPoints, firstSubSetofSlotsPoints, reduceIntersections);
					splitSetsEqualPointsTuple (secondSubSetOfPushPinPoints, secondSubSetofSlotsPoints, reduceIntersections);
					return true;
				}
				
			}
			for (PixelPoint slotPoint : slotsSubSet)
			{
				if (areSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint , slotPoint, listOfSplitedPixelPointSets))
				{
					tempTupleToAdd = new SlotPushPinTuple (pushPinPoint, pixelPointToPushPinDictionary.get(pushPinPoint),
							slotPoint,pixelPointToSlotDictionary.get(slotPoint));
					interscetionsNumber =  calculateIntersections (pushPinPoint,slotPoint);
					// in case that the line create intersections with other, don't add it immediately and try find better tuple
					if ((reduceIntersections) && (interscetionsNumber > 0))
						candidtesTuplesHashMap.put(interscetionsNumber, tempTupleToAdd);
					else {
						// no intersection - continue recursively on each side of the line
						slotsToPushPinList.add(tempTupleToAdd);
						if (slotsToPushPinList.size() == pixelPointToSlotDictionary.keySet().size())
							return true;
						splitSetsEqualPointsTuple (firstSubSetOfPushPinPoints, firstSubSetofSlotsPoints, reduceIntersections);
						splitSetsEqualPointsTuple (secondSubSetOfPushPinPoints, secondSubSetofSlotsPoints, reduceIntersections);
						return true;
					}						
				}
			}



		}
		// if no tuple was found - add the tuple that creates minimum intersections
		Log.d(TAG, "adding line with minimum itersections");
		
		if (candidtesTuplesHashMap.keySet().isEmpty())
		{
			//debuging log
			Log.d(TAG, "didn't find point-slot valid dividers");
			for (PixelPoint pushPinPoint : pushPinsSubSet)
			{
				Log.d(TAG, pushPinPoint.getX() + "    " + pushPinPoint.getY());
			}
			for (PixelPoint slotPoint : slotsSubSet)
			{
				Log.d(TAG, slotPoint.getX() + "    " + slotPoint.getY());
			}
			return false;
		}
		// get candidate with minimum number of intersections
		Entry<Integer,SlotPushPinTuple> entryToAdd = candidtesTuplesHashMap.firstEntry();
		if (areSplitingEqual(pushPinsSubSet, slotsSubSet,entryToAdd.getValue().getPointOnMapPixelPoint() , entryToAdd.getValue().getPointOnFrame(),
				listOfSplitedPixelPointSets))
		{
			slotsToPushPinList.add(entryToAdd.getValue());
			if (slotsToPushPinList.size() == pixelPointToSlotDictionary.keySet().size())
				return true;
			splitSetsEqualPointsTuple (firstSubSetOfPushPinPoints, firstSubSetofSlotsPoints, reduceIntersections);
			splitSetsEqualPointsTuple (secondSubSetOfPushPinPoints, secondSubSetofSlotsPoints, reduceIntersections);
			return true;
		}
		else
		{
			Log.d(TAG, "Error while trying to add candidate");
			return false;
		}
	}
	

	/**
	 * @param pushPinPoint
	 * @param slotPoint
	 * @return the number of intersection between the line that the pushPin-Slot connection will create to the lines that are
	 * created by other pushPin-Slot that already contained in the list
	 */

	private int calculateIntersections (PixelPoint pushPinPoint, PixelPoint slotPoint)
	{
		if ((pushPinPoint == null) || (slotPoint == null) || (slotsToPushPinList == null))
				return 0;
		int x1 = pushPinPoint.getX();
		int y1 = pushPinPoint.getY();
		int x2 = slotPoint.getX();
		int y2 = slotPoint.getY();
		int x3; 
		int y3;
		int x4;
		int y4;
		int numberOfInetsections = 0;
		for (SlotPushPinTuple tuple : slotsToPushPinList) {
			if (!tuple.getSlot().hasConnectingLinePoint())
			{
				continue;
			}
			x3 = tuple.getPushpin().getAnchor().getX();
			y3 = tuple.getPushpin().getAnchor().getY();
			x4 = tuple.getSlot().getConnectingLinePoint().getX();
			y4 =  tuple.getSlot().getConnectingLinePoint().getY();
			if (linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4))
				numberOfInetsections++;
		}
		return numberOfInetsections;
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
		for (PixelPoint pointInSet :setOfPoints)
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
	 * @param listOfSplitedPixelPointSets - lists of sets, that will contain the pushPins and slots at each side of the line
	 * that connects the pushPin candidate and slot candidate
	 * @return True if the pushPin candidate and slot candidate split the plane in such way that the number of
	 * push pins at on side of the line equals the number of slots connection points
	 */
	private Boolean areSplitingEqual (Set<PixelPoint> pushPinsSubSet, Set<PixelPoint> slotsSubSet,
			PixelPoint pushPinCandidate, PixelPoint slotCandidate, List<Set<PixelPoint>> listOfSplitedPixelPointSets)
	{

		if ((listOfSplitedPixelPointSets == null) || (listOfSplitedPixelPointSets.size() < 4))
			return false;
		Set<PixelPoint> firstSubSetofSlotsPoints = listOfSplitedPixelPointSets.get(0);
		Set<PixelPoint> firstSubSetOfPushPinPoints = listOfSplitedPixelPointSets.get(1);
		Set<PixelPoint> secondSubSetofSlotsPoints = listOfSplitedPixelPointSets.get(2);
		Set<PixelPoint> secondSubSetOfPushPinPoints = listOfSplitedPixelPointSets.get(3);
		firstSubSetofSlotsPoints.clear();
		firstSubSetOfPushPinPoints.clear();
		secondSubSetofSlotsPoints.clear();
		secondSubSetOfPushPinPoints.clear();
		
		List<PixelPoint> localSetForPushPinsOnLine = new LinkedList<PixelPoint>() ;
		List<PixelPoint> localSetForSlotssOnLines = new LinkedList<PixelPoint>() ;


		double slope;
		double constant;

		Boolean isUndefinedSlope = pushPinCandidate.getX() == slotCandidate.getX();
		PointLineStatus pointLineStatus = null;
		// the equation of the line between those points is: Y = slope * x + constant
		
		// in case that the line is vertical to X axis, then slope is undefined, and constant is the X coordinate
		if (isUndefinedSlope)
		{
			slope = Double.NaN;
			constant = pushPinCandidate.getX();
		}

		else 
		{
			slope = calculateSlope (pushPinCandidate,slotCandidate );
			constant = pushPinCandidate.getY() - slope * pushPinCandidate.getX();
			if (constant != (slotCandidate.getY() - slope * slotCandidate.getX()))
			{
				Log.d(TAG, "error calculating slope and const");
			}
		}	

		// go over all pushPins and all slots, and decide for each of them where it is located in comparison to the line
		for (PixelPoint pushPin :pushPinsSubSet)
		{
			if (pushPin == pushPinCandidate)
				continue;
			pointLineStatus = ComputePointLineStatus(pushPin, slope, constant,firstSubSetOfPushPinPoints, 
					localSetForPushPinsOnLine, secondSubSetOfPushPinPoints,isUndefinedSlope );
			if (pointLineStatus == PointLineStatus.Error) {
				return false;
			}
		}
		for (PixelPoint slot :slotsSubSet)
		{
			if (slot == slotCandidate)
				continue;
			pointLineStatus = ComputePointLineStatus(slot, slope, constant,firstSubSetofSlotsPoints, 
					localSetForSlotssOnLines, secondSubSetofSlotsPoints, isUndefinedSlope);
			if (pointLineStatus == PointLineStatus.Error) {
				return false;
			}
		}

		Integer numberOfPushPinsAboveLine =  firstSubSetOfPushPinPoints.size();
		Integer numberOfSlotsAboveLine = firstSubSetofSlotsPoints.size();
		Integer numberOfPushPinsUnderLine = secondSubSetOfPushPinPoints.size();
		Integer numberOfSlotsUnderLine = secondSubSetofSlotsPoints.size();
		Integer numberOfPushPinsOnLine = localSetForPushPinsOnLine.size();
		Integer numberOfSlotsOnLine = localSetForSlotssOnLines.size();
		
		
	
		// conclude if there is side of the line, in which the number of pushPins equals the number of slots. if so - move to other
		// side all pushPin/slots that are ON the line and return true;
	
		if (numberOfPushPinsAboveLine == numberOfSlotsAboveLine)
		{
			secondSubSetOfPushPinPoints.addAll(localSetForPushPinsOnLine);
			secondSubSetofSlotsPoints.addAll(localSetForSlotssOnLines);
			return true;
		}
		if (numberOfPushPinsUnderLine == numberOfSlotsUnderLine)
		{
			firstSubSetOfPushPinPoints.addAll(localSetForPushPinsOnLine);
			firstSubSetofSlotsPoints.addAll(localSetForSlotssOnLines);
			return true;
		}
		if (((Math.abs(numberOfPushPinsAboveLine - numberOfSlotsAboveLine) <= numberOfPushPinsOnLine) &&
				(numberOfPushPinsAboveLine < numberOfSlotsAboveLine)) || 
			 ((Math.abs(numberOfPushPinsAboveLine - numberOfSlotsAboveLine) <= numberOfSlotsOnLine)) && 
			 	(numberOfPushPinsAboveLine > numberOfSlotsAboveLine))
		{
			Integer numberOfNeededItmesAboveLine = Math.max(numberOfPushPinsAboveLine, numberOfSlotsAboveLine);
			if (!moveFromOnLineListToAboveAndUnderSets(firstSubSetOfPushPinPoints, secondSubSetOfPushPinPoints, 
					localSetForPushPinsOnLine, numberOfPushPinsAboveLine, numberOfNeededItmesAboveLine))
				return false;
			if (!moveFromOnLineListToAboveAndUnderSets(firstSubSetofSlotsPoints, secondSubSetofSlotsPoints, 
					localSetForSlotssOnLines, numberOfSlotsAboveLine, numberOfNeededItmesAboveLine))
				return false;
			return true;
		}
		return false;
	}
	
	/**
	 * @param aboveSet - set of PixelPoints that are above line
	 * @param underSet - set of PixelPoints that are under line
	 * @param onLineList - list of PixelPoints that are int line
	 * @param currentNumberOfItemsAboveLine
	 * @param numberOfNeededItmesAboveLine
	 * @return - True if succeed to move the (numberOfNeededItemsAboveLine - currentNumberOfItemsAboveLine) items from the list t
	 * to the aboveSet. other items in the list will be moved to the under set
	 */
	private Boolean moveFromOnLineListToAboveAndUnderSets (Set <PixelPoint> aboveSet, Set <PixelPoint> underSet,
			List<PixelPoint> onLineList, int currentNumberOfItemsAboveLine, int numberOfNeededItmesAboveLine  )
	{
		for (int i=currentNumberOfItemsAboveLine; i < numberOfNeededItmesAboveLine; i ++)
		{
			
			if (!addOneItemToSetFromList(onLineList, aboveSet))
			{
				Log.d(TAG, "trying to move items from list of pushPins on line while it is empty");
				return false;
			}
		}
		underSet.addAll(onLineList);
		return true;
	}
	
	/**
	 * @param list - list to get an item
	 * @param set - set wehere to add the item
	 * @return - True uppon successfull adding of first item in list to the ser
	 */
	private Boolean addOneItemToSetFromList (List<PixelPoint> list, Set<PixelPoint> set)
	{
		if ((list == null) || (list.size() == 0 ) || (set == null))
			return false;
		set.add(list.remove(0));
		return true;
	}
	
	
	 /**
     * Tells whether the two line segments cross.
     * 
     * @param x1
     *            the x coordinate of the starting point of the first segment.
     * @param y1
     *            the y coordinate of the starting point of the first segment.
     * @param x2
     *            the x coordinate of the end point of the first segment.
     * @param y2
     *            the y coordinate of the end point of the first segment.
     * @param x3
     *            the x coordinate of the starting point of the second segment.
     * @param y3
     *            the y coordinate of the starting point of the second segment.
     * @param x4
     *            the x coordinate of the end point of the second segment.
     * @param y4
     *            the y coordinate of the end point of the second segment.
     * @return true, if the two line segments cross.
     */
    public static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3,
            double y3, double x4, double y4) {
        /*
         * A = (x2-x1, y2-y1) B = (x3-x1, y3-y1) C = (x4-x1, y4-y1) D = (x4-x3,
         * y4-y3) = C-B E = (x1-x3, y1-y3) = -B F = (x2-x3, y2-y3) = A-B Result
         * is ((AxB) (AxC) <=0) and ((DxE) (DxF) <= 0) DxE = (C-B)x(-B) =
         * BxB-CxB = BxC DxF = (C-B)x(A-B) = CxA-CxB-BxA+BxB = AxB+BxC-AxC
         */

        x2 -= x1; // A
        y2 -= y1;
        x3 -= x1; // B
        y3 -= y1;
        x4 -= x1; // C
        y4 -= y1;

        double AvB = x2 * y3 - x3 * y2;
        double AvC = x2 * y4 - x4 * y2;

        // Online
        if (AvB == 0.0 && AvC == 0.0) {
            if (x2 != 0.0) {
                return (x4 * x3 <= 0.0)
                        || ((x3 * x2 >= 0.0) && (x2 > 0.0 ? x3 <= x2 || x4 <= x2 : x3 >= x2
                                || x4 >= x2));
            }
            if (y2 != 0.0) {
                return (y4 * y3 <= 0.0)
                        || ((y3 * y2 >= 0.0) && (y2 > 0.0 ? y3 <= y2 || y4 <= y2 : y3 >= y2
                                || y4 >= y2));
            }
            return false;
        }

        double BvC = x3 * y4 - x4 * y3;

        return (AvB * AvC <= 0.0) && (BvC * (AvB + BvC - AvC) <= 0.0);
    }
	
	
	
	
	/** 
	 * @param point - point in the plane to be checked
	 * @param slope - slope of the line
	 * @param constant - constant of the line
	 * @param pointAboveLineColection
	 * @param pointOnLineCollection
	 * @param pointsUnderLineCollection
	 * @return This methods checks whether the point is above \ on\ under the line represented by the slope and constant,and adds
	 * it to the relevant set
	 */
	private PointLineStatus ComputePointLineStatus (PixelPoint point, double slope, double constant,  Collection<PixelPoint> pointsAboveLineCollection,
			Collection<PixelPoint> pointsOnLineCollection, Collection<PixelPoint> pointsUnderLineCollection, Boolean undefinedSlope)
	{
		if ((pointsAboveLineCollection == null) || (pointsOnLineCollection == null) || (pointsUnderLineCollection == null))
		{
			Log.d(TAG, "when trying to assign point to relvant set (onLine, underLine, AboveLine), one of the" +
					"arguments to method was null");
			return PointLineStatus.Error;
		}
		if (!undefinedSlope)
		{
			int yValueOnLineInteger = Math.round(Math.round(point.getX() * slope + constant));
			if (yValueOnLineInteger < point.getY())
			{
				pointsAboveLineCollection.add(point);
				return PointLineStatus.pointAbove;
			}
			if  (yValueOnLineInteger== point.getY())
			{
				pointsOnLineCollection.add(point);
				return PointLineStatus.PointOn;
			}
		}
		else 
		{
			if (constant < point.getX())
			{
				pointsAboveLineCollection.add(point);
				return PointLineStatus.pointAbove;
			}
			if  (constant== point.getX())
			{
				pointsOnLineCollection.add(point);
				return PointLineStatus.PointOn;
			}	
		}
		pointsUnderLineCollection.add(point);	
		return PointLineStatus.PointUnder;
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
	
	/** This ENUM describes the relations between line and point on a plane **/ 
	private enum PointLineStatus
	{
		pointAbove,
		PointOn,
		PointUnder,
		Error
	}

	/**
	 * This class represents a slot-pushPin tuple, which indicates that in specific collage a pushPin is connected to a slot
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