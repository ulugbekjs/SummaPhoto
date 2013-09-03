package Partitioning;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import android.R.integer;

import Common.*;


/**
 * This class provides an implementation for clustering the different photos that are retrieved from the activation manager 
 * @author omri
 */
public class DBScan {

	/*
	 * Parameters for deciding weather two pictures are "close" to each other as
	 * part of the DBScan algorithm
	 */
	private final double MaxSecondsInterval = 600;
	private final double MaxMetersInterval = 50;
	private final int minNumberOfPointsInCluster = 2;
	private final int minNumberOfPointsInClusterForNoisyPictures = 1;
	private final double MaxRatioOfNoise = 0.4;

	private Hashtable<Double, PhotoObjectForClustering> unvisitedPhotos = null;
	private Hashtable<Double, PhotoObjectForClustering> visitedPhotos = null;
	
	private List<Photo> photosData;
	
	public DBScan(List<Photo> photosData) {
		this.photosData = photosData;
	}
	
	private Boolean initialize ()
	{
		PhotoObjectForClustering tempObject;
		unvisitedPhotos = new Hashtable<Double, PhotoObjectForClustering>();
		if (photosData == null) {
			return false;
		}
		else {
			for (Photo p : photosData) {
				if (p != null) {
					tempObject = new PhotoObjectForClustering(p);
					unvisitedPhotos.put(p.getID(), tempObject);
				}
			}
		}

		visitedPhotos = new Hashtable<Double, PhotoObjectForClustering>();
		return true;
	}
	
	/**
	 * Takes photos and sorts them to ActualEvents according to the algorithm
	 * @return ActualEventBundle containing the events containing photos
	 */
	public ActualEventsBundle ComputeCluster ()
	{
		return	runDBScanAlgorithm (false); 
	}
	
	
	/**
	 * The actual clustering method
	 * @param isNoisyRun - indicates if the photos are considered noisy. In such case, reduce the number of minimum photos in cluster
	 * @return ActualEventBundle containing the events containing photos
	 */
	
	private ActualEventsBundle runDBScanAlgorithm(Boolean isNoisyRun) {
		if (!initialize())
			return null;
		Integer noiseCounter =  0;
		Integer minimumNumberOfPhotosInCluster = isNoisyRun? minNumberOfPointsInClusterForNoisyPictures: minNumberOfPointsInCluster;
		Integer numberOfPhotosToClusterInteger = unvisitedPhotos.size();
		List<Cluster> clustersList = new LinkedList<Cluster>();
		PhotoObjectForClustering arbitraryUnvisitedPhoto;
		
		// continue the clustering operation while there are photos that are still unvisited
		while (!unvisitedPhotos.isEmpty()) {
			arbitraryUnvisitedPhoto = getArbitraryPhotoFromHashTableClustering(unvisitedPhotos);
			moveToVisited(arbitraryUnvisitedPhoto);
			Queue<PhotoObjectForClustering> neighborsList = getNeighbors(arbitraryUnvisitedPhoto);
			if (neighborsList.size() < minimumNumberOfPhotosInCluster) {
				arbitraryUnvisitedPhoto.isNoise = true;
				noiseCounter ++;
			} else {
				Cluster cluster = new Cluster();
				clustersList.add(cluster);
				arbitraryUnvisitedPhoto.addPointToCluster(cluster);
				expandCluster(cluster, arbitraryUnvisitedPhoto, neighborsList, minimumNumberOfPhotosInCluster);
			}
		}
		//  In case that too many pictures were marked as noise, and it is the first run of the clustering algorithm
		// re-run algorithm with lower parameter for minimumPhotosInCluster
		if (((double)noiseCounter /(double)numberOfPhotosToClusterInteger > MaxRatioOfNoise) && (!isNoisyRun))
		{
			return runDBScanAlgorithm(true); 
		}
		else {
			return getActualEventsList(clustersList);
		}
		
	}
	
	
	/**
	 * @param clusterList
	 * @return ActualEventBundle which is constructed according to the clusters in list
	 */
	private ActualEventsBundle getActualEventsList(List<Cluster> clusterList) {
		List<ActualEvent> events = new LinkedList<ActualEvent>();
		for (Cluster cluster : clusterList) {
			events.add(new ActualEvent(cluster));
		}
		return new ActualEventsBundle(events);
	}

	/**
	 * 
	 * @param cluster
	 * @param photo
	 * @param neighbors
	 * This method expand the cluster by iterating the photo's neighbors that might be added to the cluster
	 */
	private void expandCluster(Cluster cluster, PhotoObjectForClustering photo,
			Queue<PhotoObjectForClustering> neighbors, Integer minimumNumberOfPhotosInCluster) {
		if (neighbors != null) {
			Queue<PhotoObjectForClustering> subNeighborsList;

			PhotoObjectForClustering neighbor;
			while (!neighbors.isEmpty()) {
				neighbor = neighbors.remove();
				if (!neighbor.isVisited) {
					moveToVisited(neighbor);
					subNeighborsList = getNeighbors(neighbor);
					if ((subNeighborsList != null)
							&& (subNeighborsList.size() >= minimumNumberOfPhotosInCluster)) {
						neighbors.addAll(subNeighborsList);
					}
				}
				if (neighbor.cluster == null) {
					neighbor.addPointToCluster(cluster);
				}
			}
		}
	}

	private void moveToVisited(PhotoObjectForClustering p) {
		p.isVisited = true;
		unvisitedPhotos.remove(p.getID());
		visitedPhotos.put(p.getID(), p);
	}

	private boolean isEpsilonDistanced(PhotoObjectForClustering p1,
			PhotoObjectForClustering p2) {
		if ((p1.distanceFrom(p2) < MaxMetersInterval)
				&& (p1.timeDeltaInSecondsFrom(p2) < MaxSecondsInterval))
			return true;
		return false;
	}

	private Queue<PhotoObjectForClustering> getNeighbors(PhotoObjectForClustering p) {
		Queue<PhotoObjectForClustering> photosEpsilonClose = new PriorityQueue<PhotoObjectForClustering>();
		for (PhotoObjectForClustering photoCandidate : unvisitedPhotos.values()) {
			if (isEpsilonDistanced(p, photoCandidate)) {
				photosEpsilonClose.add(photoCandidate);
			}

		}
		for (PhotoObjectForClustering photoCandidate : visitedPhotos.values()) {
			if (isEpsilonDistanced(p, photoCandidate)) {
				photosEpsilonClose.add(photoCandidate);
			}
		}
		return photosEpsilonClose;
	}

	private PhotoObjectForClustering getArbitraryPhotoFromHashTableClustering(
			Hashtable<Double, PhotoObjectForClustering> hashTable) {
		if ((hashTable == null) || (hashTable.isEmpty())) {
			return null;
		}
		double tempKey = hashTable.keys().nextElement();
		return hashTable.get(tempKey);
	}
}
