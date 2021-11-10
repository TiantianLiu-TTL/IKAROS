/**
 * 
 */
package rStarTree3D;

import java.io.IOException;

import com.github.davidmoten.rtree3d.geometry.Box;

import indoor_entitity.Partition;
import textualIndex.ConnectivityTier;
import textualIndex.DistMatrix;

/**
 * @author feng zijin
 *
 */
public class TreeNode {
	private double x1, y1, x2, y2, z1, z2;		// the corner of the node
	
	private int mID;		// the mID of the node
	private Partition mPartition;		// the partition stored in the node
	private DistMatrix mDistMatrix;		// the distance matrix of the partition	
	private ConnectivityTier mConnectivityTier;		// the connectivity tier of the partition
	
	
	/**
	 * constructor
	 */
	public TreeNode(double x1, double y1, double z1, double x2, double y2, double  z2,
			Partition mPartition, DistMatrix mDistMatrix, ConnectivityTier mConnectivityTier) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.mID = mPartition.getmID();
		this.mPartition = mPartition;
		this.mDistMatrix = mDistMatrix;
		this.mConnectivityTier = mConnectivityTier;
	}
	
	public double x1() {
        return x1;
    }

    public double y1() {
        return y1;
    }

    public double x2() {
        return x2;
    }

    public double y2() {
        return y2;
    }

    public double z1() {
        return z1;
    }

    public double z2() {
        return z2;
    }
	
	/**
	 * @return the mID
	 */
	public int getmID() {
		return mID;
	}

	/**
	 * @param mID
	 *            the mID to set
	 */
	public void setmID(int mID) {
		this.mID = mID;
	}
	
	/**
	 * @return the partition
	 */
	public Partition getmPartition() {
		return mPartition;
	}

	/**
	 * @param mPartition
	 *            the mPartition to set
	 */
	public void setmPartition(Partition mPartition) {
		this.mPartition = mPartition;
	}
	
	/**
	 * @return the mDistMatrix
	 */
	public DistMatrix getmDistMatrix() {
		return mDistMatrix;
	}

	/**
	 * @param mDistMatrix
	 *            the mDistMatrix to set
	 */
	public void setmDistMatrix(DistMatrix mDistMatrix) {
		this.mDistMatrix = mDistMatrix;
	}
	
	/**
	 * @return the mConnectivityTier
	 */
	public ConnectivityTier getmConnectivityTier() {
		return mConnectivityTier;
	}

	/**
	 * @param mConnectivityTier
	 *            the mConnectivityTier to set
	 */
	public void setmConnectivityTier(ConnectivityTier mConnectivityTier) {
		this.mConnectivityTier = mConnectivityTier;
	}
	
	public String cornerToString() {
		return "("  + x1 + ", " + y1 + ", " + z1 + "), (" + x2 + ", " + y2 + ", " + z2 + ")";
	}	
	
	public static void main(String[] arg) throws IOException {
		
	}
}
