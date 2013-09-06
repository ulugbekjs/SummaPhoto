
package Generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.PrivateCredentialPermission;

import android.R.bool;
import android.R.integer;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

import Bing.Pushpin;



/** This class provides methods to locate the different pictures around the map in good positions **/
public class LocatePicturesWithMap {


	private final String TAG = "LoctePicturesOnMap" ;
	private HashMap<PixelPoint,Slot> pixelPointToSlotDictionary;
	private HashMap<PixelPoint,Pushpin> pixelPointToPushPinDictionary;

	// Those sets includes the location of each picture on the map 
	private Set<PixelPoint> horizontalPushPinPointsSet;
	private Set<PixelPoint> verticalPushPinPointsSet;
	// Those sets includes the location of each different points on the frame around the map
	private Set<PixelPoint> horizontalSlotPointsSet;
	private Set<PixelPoint> verticalSlotPointsSet;


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
		splitSetsEqualPointsTuple (horizontalPushPinPointsSet,horizontalSlotPointsSet);
		splitSetsEqualPointsTuple(verticalPushPinPointsSet, verticalSlotPointsSet);
		return slotsToPushPinList;
	}

	private Boolean splitSetsEqualPointsTuple (Set<PixelPoint> pushPinsSubSet, Set<PixelPoint> slotsSubSet)
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
		if (pushPinsSubSet.size() != slotsSubSet.size())
		{
			Log.d(TAG, "Number of slots and number of pictures is not equal");
			return false;
		}
		if (pushPinsSubSet.size() == 0)
			return true;
		if (pushPinsSubSet.size() == 1){
			PixelPoint lastSlot = null;
			PixelPoint lastPushPin = null;
			if (pushPinsSubSet.iterator().hasNext() && slotsSubSet.iterator().hasNext()) {
				lastPushPin = pushPinsSubSet.iterator().next(); 
				lastSlot = slotsSubSet.iterator().next();
				tempTupleToAdd = new SlotPushPinTuple (lastPushPin, pixelPointToPushPinDictionary.get(lastPushPin),
						lastSlot,pixelPointToSlotDictionary.get(lastSlot));
				slotsToPushPinList.add(tempTupleToAdd);
				return true;

			}
			else {
				return false;
			}
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
				return true;
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
						return true;
					}
				}
				
			}
			
		}
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
	

		Boolean isUndefinedSlope = pushPinCandidate.getX() == slotCandidate.getX();
		PointLineStatus pointLineStatus = null;
		// the equation of the line between those points is: Y = slope * x + constant
		if (!isUndefinedSlope)
		{
			slope = calculateSlope (pushPinCandidate,slotCandidate );
			double constant = pushPinCandidate.getY() - slope * pushPinCandidate.getX();
			if (constant != (slotCandidate.getY() - slope * slotCandidate.getX()))
			{
				Log.d(TAG, "error calculating slope and const");
			}
			for (PixelPoint pushPin :pushPinsSubSet)
			{
				if (pushPin == pushPinCandidate)
					continue;
				pointLineStatus = ComputePointLineStatus(pushPin, slope, constant,firstSubSetOfPushPinPoints, 
						localSetForPushPinsOnLine, secondSubSetOfPushPinPoints);
				if (pointLineStatus == PointLineStatus.Error) {
					return false;
				}
			}
			for (PixelPoint slot :slotsSubSet)
			{
				if (slot == slotCandidate)
					continue;
				pointLineStatus = ComputePointLineStatus(slot, slope, constant,firstSubSetofSlotsPoints, 
						localSetForSlotssOnLines, secondSubSetofSlotsPoints);
				if (pointLineStatus == PointLineStatus.Error) {
					return false;
				}
			}
		}
		else 
		{
			return isSplitingEqualUndefinedSlope (pushPinsSubSet, slotsSubSet, pushPinCandidate, slotCandidate, listOfSplitedPixelPointSets);
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
		/// undefined undefienc undefines
		Log.d(TAG, "calculating for undefined slope");
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
			Collection<PixelPoint> pointsOnLineCollection, Collection<PixelPoint> pointsUnderLineCollection)
	{
		if ((pointsAboveLineCollection == null) || (pointsOnLineCollection == null) || (pointsUnderLineCollection == null))
		{
			Log.d(TAG, "when trying to assign point to relvant set (onLine, underLine, AboveLine), one of the" +
					"arguments to method was null");
			return PointLineStatus.Error;
		}
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
	
	/**
	 * @param pushPinsSubSet - sub set of pushPins that had to be matched to slots
	 * @param slotsSubSet - sub set of slots that had to me matched to push pins
	 * @param pushPinCandidate - push pin candidate
	 * @param slotCandidate - slot candidate
	 * @return True if the pushPin candidate and slot candidate split the plane in such way that the line between them won't
	 * intersect other lines between pushPins and slots
	 */
	/**
	private Boolean isSplitingEqualCopy (Set<PixelPoint> pushPinsSubSet, Set<PixelPoint> slotsSubSet,
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
		
		List<PixelPoint> localSetForPushPinsOnLine = new LinkedList<PixelPoint>() ;
		List<PixelPoint> localSetForSlotssOnLines = new LinkedList<PixelPoint>() ;


		double slope;
		Integer numberOfPushPinsAboveLine = 0;
		Integer numberOfSlotsAboveLine = 0;
		Integer numberOfPushPinsUnderLine = 0;
		Integer numberOfSlotsUnderLine = 0;
		Integer numberOfPushPinsOnLine = 0;
		Integer numberOfSlotsOnLine = 0;

		Boolean isUndefinedSlope = pushPinCandidate.getX() == slotCandidate.getX();
		PointLineStatus pointLineStatus = null;
		// the equation of the line between those points is: Y = slope * x + constant
		if (!isUndefinedSlope)
		{
			slope = calculateSlope (pushPinCandidate,slotCandidate );
			double constant = pushPinCandidate.getY() - slope * pushPinCandidate.getX();
			for (PixelPoint pushPin :pushPinsSubSet)
			{
				if (pushPin == pushPinCandidate)
					continue;
				pointLineStatus = isPointAboveLine(pushPin, slope, constant);
				if ( pointLineStatus == PointLineStatus.pointAbove)
				{
					numberOfPushPinsAboveLine++;
					firstSubSetOfPushPinPoints.add(pushPin);
				}
				else {
					if (pointLineStatus == PointLineStatus.PointUnder)
					{
					secondSubSetOfPushPinPoints.add(pushPin);
					numberOfPushPinsUnderLine ++;
					}
					else {
						{
							numberOfPushPinsOnLine ++;
							localSetForPushPinsOnLine.add(pushPin);
						}
					}
				}
			}
			for (PixelPoint slot :slotsSubSet )
			{
				if (slot == slotCandidate)
					continue;
				pointLineStatus = isPointAboveLine(slot, slope, constant);
				if (pointLineStatus == PointLineStatus.pointAbove)
				{
					numberOfSlotsAboveLine++;
					firstSubSetofSlotsPoints.add(slot);
				}
				else {
					if (pointLineStatus == PointLineStatus.PointUnder)
					{
					secondSubSetofSlotsPoints.add(slot);
					numberOfSlotsUnderLine ++;
					}
					else {
						{
							numberOfSlotsOnLine ++;
							localSetForSlotssOnLines.add(slot);
						}
					}
				}
			}
		}
		else 
		{
			return isSplitingEqualUndefinedSlope (pushPinsSubSet, slotsSubSet, pushPinCandidate, slotCandidate, listOfSplitedPixelPointSets);
		}
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
			for (int i=numberOfPushPinsAboveLine; i < numberOfNeededItmesAboveLine; i ++)
			{
				if (localSetForPushPinsOnLine.size()  == 0)
				{
					Log.d(TAG, "trying to move items from list of pushPins on line while it is empty");
					return false;
				}
				firstSubSetOfPushPinPoints.add(localSetForPushPinsOnLine.remove(0));
			}
			secondSubSetOfPushPinPoints.addAll(localSetForPushPinsOnLine);
			
			for (int i=numberOfSlotsAboveLine; i < numberOfNeededItmesAboveLine; i ++)
			{
				if (localSetForSlotssOnLines.size()  == 0)
				{
					Log.d(TAG, "trying to move items from list of slots on line while it is empty");
					return false;
				}
				firstSubSetofSlotsPoints.add(localSetForSlotssOnLines.remove(0));
			}
			secondSubSetofSlotsPoints.addAll(localSetForSlotssOnLines);
			return true;
		}
		return false;
	}
	**/
	/** This methods checks whether the point is above the line represented by the slope and constant **/
	/**
	private PointLineStatus isPointAboveLine (PixelPoint point, double slope, double constant)
	{
		if (point.getX() * slope + constant < point.getY())
			return PointLineStatus.pointAbove;
		if  (point.getX() * slope + constant == point.getY())
			return PointLineStatus.PointOn;
		return PointLineStatus.PointUnder;
	}
	**/
	
}