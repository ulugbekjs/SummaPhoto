

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;


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
				tempObject = new PhotoObjectForClustering(p);
				unvisitedPhotos.put(p.getID(), tempObject);
			}
		}
		visitedPhotos = new Hashtable<Double, PhotoObjectForClustering>();
	}

	public List<Cluster> runAlgorithmClusters() {
		List<Cluster> clustersList = new LinkedList<Cluster>();
		PhotoObjectForClustering arbitraryUnvisitedPhoto;
		while (!unvisitedPhotos.isEmpty()) {
			arbitraryUnvisitedPhoto = getArbitraryPhotoFromHashTableClustering(unvisitedPhotos);
			moveToVisited(arbitraryUnvisitedPhoto);
			List<PhotoObjectForClustering> neighborsList = regionQueryList(arbitraryUnvisitedPhoto);
			if (neighborsList.size() < minNumberOfPointsInCluster) {
				arbitraryUnvisitedPhoto.isNoise = true;
			} 
			else {
				Cluster cluster = new Cluster();
				clustersList.add(cluster);
				arbitraryUnvisitedPhoto.addPointToCluster(cluster);
				expandCluster(cluster, arbitraryUnvisitedPhoto, neighborsList);
			}
		}
		return clustersList;
	}

	private void expandCluster(Cluster c, PhotoObjectForClustering p,
			List<PhotoObjectForClustering> neighbors) {
		if (neighbors != null) {
			List<PhotoObjectForClustering> subNeighborsList;
			for (PhotoObjectForClustering neighbor : neighbors) {
				if (!neighbor.isVisited) {
					moveToVisited(neighbor);
					subNeighborsList = regionQueryList(neighbor);
					if ((subNeighborsList != null)
							&& (subNeighborsList.size() >= minNumberOfPointsInCluster)) {
						neighbors.addAll(subNeighborsList);
					}
				}
				if (neighbor.cluster == null) {
					p.addPointToCluster(c);
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

	private List<PhotoObjectForClustering> regionQueryList(
			PhotoObjectForClustering p) {
		List<PhotoObjectForClustering> photosEpsilonClose = new LinkedList<PhotoObjectForClustering>();
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

	private PhotoObjectForClustering getArbitraryPhotoFromHashTableClustering (Hashtable<Double, PhotoObjectForClustering> hashTable)
	{
		if ((hashTable == null)|| (hashTable.isEmpty()))
		{
			return null;
		}
		double tempKey = hashTable.keys().nextElement();
		return hashTable.get(tempKey);
	}
}
