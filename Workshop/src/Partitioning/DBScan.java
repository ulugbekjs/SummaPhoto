package Partitioning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import android.R.bool;
import android.R.integer;
import android.R.string;
import android.webkit.WebChromeClient.CustomViewCallback;
import Common.*;

public class DBScan {

	/*
	 * Parameters for deciding weather two pictures are "close" to each other as
	 * part of the DBScan algorithm
	 */
	private final double MaxSecondsInterval = 600;
	private final double MaxMetersInterval = 50;
	private final int minNumberOfPointsInCluster = 2;

	private Hashtable<Double, PhotoObjectForClustering> unvisitedPhotos = null;
	private Hashtable<Double, PhotoObjectForClustering> visitedPhotos = null;

	public DBScan(List<Photo> photosData) {
		PhotoObjectForClustering tempObject;
		unvisitedPhotos = new Hashtable<Double, PhotoObjectForClustering>();
		if (photosData != null) {
			for (Photo p : photosData) {
				if (p != null) {
					tempObject = new PhotoObjectForClustering(p);
					unvisitedPhotos.put(p.getID(), tempObject);
				}
			}
		}
		visitedPhotos = new Hashtable<Double, PhotoObjectForClustering>();
	}

	/**
	 * Takes photos and sorts them to ActualEvents
	 * @return ActualEventBundle containing the events containing photos
	 */
	public List<Cluster> runAlgorithmClusters() {
		List<Cluster> clustersList = new LinkedList<Cluster>();
		PhotoObjectForClustering arbitraryUnvisitedPhoto;
		while (!unvisitedPhotos.isEmpty()) {
			arbitraryUnvisitedPhoto = getArbitraryPhotoFromHashTableClustering(unvisitedPhotos);
			moveToVisited(arbitraryUnvisitedPhoto);
			Queue<PhotoObjectForClustering> neighborsList = regionQueryList(arbitraryUnvisitedPhoto);
			if (neighborsList.size() < minNumberOfPointsInCluster) {
				arbitraryUnvisitedPhoto.isNoise = true;
			} else {
				Cluster cluster = new Cluster();
				clustersList.add(cluster);
				arbitraryUnvisitedPhoto.addPointToCluster(cluster);
				expandCluster(cluster, arbitraryUnvisitedPhoto, neighborsList);
			}
		}
		return getActualEventsList(clustersList);
	}
	
	private ActualEventsBundle getActualEventsList(List<Cluster> clusterList) {
		List<ActualEvent> events = new LinkedList<ActualEvent>();
		for (Cluster cluster : clusterList) {
			events.add(new ActualEvent(cluster));
		}
		return new ActualEventsBundle(events);
	}

	private void expandCluster(Cluster c, PhotoObjectForClustering p,
			Queue<PhotoObjectForClustering> neighbors) {
		if (neighbors != null) {
			Queue<PhotoObjectForClustering> subNeighborsList;

			PhotoObjectForClustering neighbor;
			while (!neighbors.isEmpty()) {
				neighbor = neighbors.remove();
				if (!neighbor.isVisited) {
					moveToVisited(neighbor);
					subNeighborsList = regionQueryList(neighbor);
					if ((subNeighborsList != null)
							&& (subNeighborsList.size() >= minNumberOfPointsInCluster)) {
						neighbors.addAll(subNeighborsList);
					}
				}
				if (neighbor.cluster == null) {
					neighbor.addPointToCluster(c);
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

	private Queue<PhotoObjectForClustering> regionQueryList(
			PhotoObjectForClustering p) {
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
