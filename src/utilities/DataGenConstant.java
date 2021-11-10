/**
 * 
 */
package utilities;

/**
 * <h>DataGenConstant</h>
 * Constant Values in Data Generating
 * @author feng zijin
 *
 */
public class DataGenConstant {
	
	// PARAMETERS FOR INDOOR SPACES
	/** dimensions of the floor */
	public static double floorRangeX = 1368;
	public static double floorRangeY = 1368;

	/** numbers of the floor */
	public static int nFloor = 20;

	/** length of stairway between two floors */
	public static double lenStairway = 20.0;
	
	// ID COUNTERS FOR INDOOR ENTITIES
	/** the ID counter of Partitions */
	public static int mID_Par = 0;

	/** the ID counter of Doors */
	public static int mID_Door = 0;
	
	/** the ID counter of Floors */
	public static int mID_Floor = 0;
	
	/** the ID counter of Shops */
	public static int mID_Shop = 0;
	
	// KEYWORDS	
	public static int mKeyworSize = 0;
	
	// TREES	
	/** maximum children of upper tree */
	public static int maxChildUpper = 6;
	
	/** maximum children of lower tree */
	public static int maxChildLower = 150;
	
	public static int curSizePar = -1;
	
	public static int curSizeDoor = -1;
}
