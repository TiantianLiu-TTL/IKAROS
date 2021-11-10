/**
 * 
 */
package indoor_entitity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <h>IndoorSpace</h>
 * models the indoor space
 * 
 * @author feng zijin
 *
 */
public class IndoorSpace {
	// indoor entities
	/** all the doors of the indoor space */
	public static List<Door> iDoors = new ArrayList<Door>();
	
	/** all the partitions of the indoor space */
	public static List<Partition> iPartitions = new ArrayList<Partition>();
	
	/** all the floors of the indoor space */
	public static List<Floor> iFloors = new ArrayList<Floor>();
	
	/** all the shops of the indoor space */
	public static List<Shop> iShops = new ArrayList<Shop>();
	
	// data structure
	/** the in-memory D2D Matrix */
	public static HashMap<String, D2Ddistance> iD2D = new HashMap<String, D2Ddistance>();
	
	// counter
	/** the Number of Floor per mall */
	public static int iNumberFloorPerMall;
	
	/** the Number of Doors per floor */
	public static int iNumberDoorsPerFloor;
	
	/** the Number of Partitions per floor */
	public static int iNumberParsPerFloor;
}
