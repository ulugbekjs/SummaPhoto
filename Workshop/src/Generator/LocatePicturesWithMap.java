package Generator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.R.bool;
import android.R.integer;

import com.example.aworkshop.SettingsActivity;


/** This class provides methods to locate the different pictures around the map in good positions **/
public class LocatePicturesWithMap {

	// This set includes the location of each picture on the map 
	private Set<PixelPoint> picturesOnMapSet;
	// This set includes the location of each different points on the frame around the map
	private Set<PixelPoint> pointsOnFrameSet;


	// Subsets of pointsOnFrameSet and  picturesOnMapSet for recursive algorithm issues
	private Set<PixelPoint> firstSebSetofPointsOnFrameSet;
	private Set<PixelPoint> firstSubSetOfPicturesOnMapSet;

	private Set<PixelPoint> secondSubSetofPointsOnFrameSet;
	private Set<PixelPoint> secondSubSetOfPicturesOnMapSet;

	private List<PointsTuple> pointsTupleList;

	public LocatePicturesWithMap ( Set<PixelPoint> picturesOnMapSet, Set<PixelPoint> pointsOnFrameSet )
	{
		this.picturesOnMapSet =  picturesOnMapSet;
		this.pointsOnFrameSet = pointsOnFrameSet;
	}

	/*
	 * This function returns a list of PointsTuple: each tuple contains one point from the map and one point on the frame.
	 * After drawing a line between the points in each tuple there WONT be any intersections between the lines.
	 */
	public List<PointsTuple> matchPictureOnMapToPointOnFrame ()
	{
		pointsTupleList = new LinkedList<PointsTuple>();
		splitSetsEqualPointsTuple (picturesOnMapSet,pointsOnFrameSet);
		return pointsTupleList;
	}

	private void splitSetsEqualPointsTuple (Set<PixelPoint> picturesOnMapSubSet, Set<PixelPoint> pointsOnFrameSubSetSet)
	{
		PixelPoint closestPoint;
		if (picturesOnMapSubSet.size() == 0)
			return;
		if (picturesOnMapSubSet.size() == 1){
			pointsTupleList.add(new PointsTuple(picturesOnMapSubSet.iterator().next(),pointsOnFrameSubSetSet.iterator().next()));
			return;
		}
		
		
		for (PixelPoint pointInA : picturesOnMapSubSet) {
			// First try to split the sets with closest point in pointsOnFrameSubSetSet to the chosen point
			closestPoint = findClosestPointInSet(pointInA,pointsOnFrameSubSetSet);
			if (isSplitingEqual(picturesOnMapSubSet, pointsOnFrameSubSetSet,pointInA, closestPoint))
			{
				pointsTupleList.add(new PointsTuple(pointInA, closestPoint));
				splitSetsEqualPointsTuple (firstSebSetofPointsOnFrameSet,firstSubSetOfPicturesOnMapSet);
				splitSetsEqualPointsTuple (secondSubSetofPointsOnFrameSet, secondSubSetOfPicturesOnMapSet);
				return;
			}
			else {
				for (PixelPoint pointInB : pointsOnFrameSubSetSet)
				{
					if (isSplitingEqual(picturesOnMapSubSet, pointsOnFrameSubSetSet,pointInA , pointInB))
					{
						pointsTupleList.add(new PointsTuple(pointInA, pointInB));
						splitSetsEqualPointsTuple (firstSebSetofPointsOnFrameSet,firstSubSetOfPicturesOnMapSet);
						splitSetsEqualPointsTuple (secondSubSetofPointsOnFrameSet, secondSubSetOfPicturesOnMapSet);
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
	 * @param points in sub set of picturesOnMapSet
	 * @param point in sub set of pointsOnFrameSet
	 * @param selected point in  sub set of picturesOnMapSet
	 * @param selected point in  sub set of pointsOnFrameSet
	 * @return True if the line between pointInA and pointInB divides the 2D plate in such way that
	 * the number of point from  picturesOnMapSet that are "above" the line equals to number of points in pointsOnFrameSet 
	 * that are above line. If the method returns true, it updates firstSubSetofPointsOnFramSet, firstSubSetOfPicturesOnMapSet 
	 * secondSubSetofPointsOnFramSet, secondSubSetOfPicturesOnMapSet  with relevant point from group A and group B.
	 */
	private Boolean isSplitingEqual (Set<PixelPoint> picturesOnMapSubSet, Set<PixelPoint> pointsOnFrameSubSetSet,
			PixelPoint pointInA, PixelPoint pointInB)
	{
		double slope;
		Integer numberOfPointsInGroupAAboveLine = 0;
		Integer numberOfPointsInGroupBAboveLine = 0;
		
		//initiating sub-sets which will be used for recursion
		firstSubSetOfPicturesOnMapSet = new HashSet<PixelPoint>();
		secondSubSetOfPicturesOnMapSet = new HashSet<PixelPoint>();
		firstSebSetofPointsOnFrameSet = new HashSet<PixelPoint>();
		secondSubSetofPointsOnFrameSet = new HashSet<PixelPoint>();
		
		Boolean isUndefinedSlope = pointInA.getX() == pointInB.getX();
		
		// the equation of the line between those points is: Y = slope * x + constant
		if (!isUndefinedSlope)
		{
			Double deltaY;
			Double deltaX;
			if (pointInA.getX() > pointInB.getX() ){
			 deltaY = (double) (pointInA.getY() - pointInB.getY());
			 deltaX = (double) (pointInA.getX() - pointInB.getX());
			}
			else {
				 deltaY = (double) (pointInB.getY() - pointInA.getY() );
				 deltaX = (double) (pointInB.getX()- pointInA.getX());
			}
			slope = deltaY / deltaX;
			double constant = pointInA.getY() - slope * pointInA.getX();
			for (PixelPoint tempPointInA :picturesOnMapSubSet )
			{
				if (tempPointInA == pointInA)
					continue;
				if (isPointAboveLine(tempPointInA, slope, constant))
				{
					numberOfPointsInGroupAAboveLine++;
					firstSubSetOfPicturesOnMapSet.add(tempPointInA);
				}
				else {
					secondSubSetOfPicturesOnMapSet.add(tempPointInA);
				}
			}
			for (PixelPoint tempPointInB :pointsOnFrameSubSetSet )
			{
				if (tempPointInB == pointInB)
					continue;
				if (isPointAboveLine(tempPointInB, slope, constant))
				{
					numberOfPointsInGroupBAboveLine++;
					firstSebSetofPointsOnFrameSet.add(tempPointInB);
				}
				else {
					secondSubSetofPointsOnFrameSet.add(tempPointInB);
				}
			}
		}
		else 
		{
			double verticalLineX = pointInA.getX();
			for (PixelPoint pointinA :picturesOnMapSubSet )
			{
				if (pointInA.getX() > verticalLineX)
				{
					numberOfPointsInGroupAAboveLine++;
					firstSubSetOfPicturesOnMapSet.add(pointinA);
				}
				else {
					secondSubSetOfPicturesOnMapSet.add(pointinA);
				}
			}
			for (PixelPoint pointinB :pointsOnFrameSubSetSet )
			{
				if (pointinB.getX() > verticalLineX)
				{
					numberOfPointsInGroupAAboveLine++;
					firstSubSetOfPicturesOnMapSet.add(pointinB);
				}
				else {
					secondSubSetOfPicturesOnMapSet.add(pointinB);
				}
			}
		}
		return (numberOfPointsInGroupAAboveLine == numberOfPointsInGroupBAboveLine);
	}


	/** This methods checks whether the point is above the line represented by the slope and constant **/ 
	private Boolean isPointAboveLine (PixelPoint point, double slope, double constant)
	{
		if (point.getX() * slope + constant < point.getY())
			return true;
		return false;
	}
	
	



	public class PointsTuple
	{
		private PixelPoint pointOnMap;
		private PixelPoint pointOnFrame;

		public PointsTuple (PixelPoint pointOnMap, PixelPoint pointOnFrame)
		{
			this.pointOnMap = pointOnMap;
			this.pointOnFrame = pointOnFrame;
		}

		public PixelPoint getPointOnMapPixelPoint ()
		{
			return pointOnMap;
		}

		public PixelPoint getPointOnFrame ()
		{
			return pointOnFrame;
		}
	}



}
