package Partitioning;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
