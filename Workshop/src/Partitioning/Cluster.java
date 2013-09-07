package Partitioning;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * This class represents a cluster that is culculated by the algorithm
 * @author omri
 *
 */
public class Cluster {

	public List<PhotoObjectForClustering> photosInCluster = null;
	
	public Cluster()
	{
		photosInCluster = new LinkedList<PhotoObjectForClustering>();
	}
	
	public void sortPhotosInClusterByData ()
	{
		Collections.sort(photosInCluster);
	}
}
