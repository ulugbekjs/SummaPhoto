
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
	 * @return - True if there is a line that connects one pushPin and one slot, such as the number of pushPins above line equals
	 * the number of slot's connection points above line. If so, the method continues to split recursively the subSets, and adds
	 * the relevant slot-PushPin tuple to the list
	 */
	private Boolean splitSetsEqualPointsTuple (Set<PixelPoint> pushPinsSubSet, 
			Set<PixelPoint> slotsSubSet, boolean reduceIntersections)
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

		TreeMap<Integer, SlotPushPinTuple> candidtesTuplesHashMap = new TreeMap<Integer, LocatePicturesWithMap.SlotPushPinTuple>();
		
		PixelPoint closestSlot;
		if (pushPinsSubSet.size() != slotsSubSet.size())
		{
			Log.d(TAG, "Number of slots and number of pictures is not equal");
		}
		if (slotsSubSet.size() == 0)
			return true;

		int interscetionsNumber;
		for (PixelPoint pushPinPoint : pushPinsSubSet) {
			// First try to split the sets with closest point in pointsOnFrameSubSetSet to the chosen point
			closestSlot = findClosestPointInSet(pushPinPoint,slotsSubSet);
			if (isSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint, closestSlot, listOfSplitedPixelPointSets))
			{
				tempTupleToAdd = new SlotPushPinTuple (pushPinPoint, pixelPointToPushPinDictionary.get(pushPinPoint),
						closestSlot,pixelPointToSlotDictionary.get(closestSlot));
				
				interscetionsNumber =  createsCross (pushPinPoint,closestSlot);
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
				if (isSplitingEqual(pushPinsSubSet, slotsSubSet,pushPinPoint , slotPoint, listOfSplitedPixelPointSets))
				{
					tempTupleToAdd = new SlotPushPinTuple (pushPinPoint, pixelPointToPushPinDictionary.get(pushPinPoint),
							slotPoint,pixelPointToSlotDictionary.get(slotPoint));
					interscetionsNumber =  createsCross (pushPinPoint,slotPoint);
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
			}



		}
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
		if (isSplitingEqual(pushPinsSubSet, slotsSubSet,entryToAdd.getValue().getPointOnMapPixelPoint() , entryToAdd.getValue().getPointOnFrame(),
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

	private int createsCross (PixelPoint pushPinPoint, PixelPoint slotPoint)
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
			if (Common.Utils.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4))
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
	 * @param point - point in the plane to be checked
	 * @param slope - slope of the line
	 * @param constant - constant of the line
	 * @param pointAboveLineColection
	 * @param pointOnLineCollection
	 * @param pointsUnderLineCollection
	 * @return This methods checks whether the point is above \ on\ under the line represented by the slope and constant,and adds
	 * it to the relevant sets
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
	
	/** This enum describes the relations between line and point on a plane **/ 
	private enum PointLineStatus
	{
		pointAbove,
		PointOn,
		PointUnder,
		Error
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