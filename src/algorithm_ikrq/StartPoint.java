/**
 * 
 */
package algorithm_ikrq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import datagenerate.AssignShop;
import indoor_entitity.Partition;
import indoor_entitity.Point;
import rStarTree2D.UpperTree;
import textualIndex.Dictionary;

/**
 * @author feng zijin
 *
 */
public class StartPoint {
	public static AssignShop assignShop;
	public static Dictionary dictionary;
	public static UpperTree upperTree;
	
	private static Scanner scanner = new Scanner(System.in);
	
	public static Partition sPartition;
	public static Point sPoint;
	public static Partition ePartition;
	public static Point ePoint;
	public static ArrayList<Partition> candPartition = new ArrayList<Partition>();
	
	public static String[] keywords;
	public static int distance;
	
	
	public StartPoint() throws IOException {
		// generate indoor space and assign shop
		assignShop = new AssignShop();
		assignShop.assignShop();
		
		System.out.println("=======================================================");
		
		// generate dictionary
		dictionary = new Dictionary();
		
		String fileInput = System.getProperty("user.dir") + "/keywordIndex.txt";
		dictionary.loadData(fileInput);
		
		System.out.println("=======================================================");
		
		// generate tree
		upperTree = new UpperTree();
		upperTree.loadData();
		
//		int lowerTreeHeight = upperTree.get(0).getLowerTree().height();
//		int lowerTreeSize = upperTree.get(0).getLowerTree().size();		
//		int totalSize = 0;
//		
//		for (int i = 0; i < upperTree.size(); i ++) {
//			totalSize += upperTree.get(0).getLowerTree().size();
//		}
//		
//		System.out.println("Tree upper part generated! size = " + upperTree.size() + " height = " + upperTree.height() + ""
//				+ " each leave node link to lower part with size = " + lowerTreeSize + " height = " + lowerTreeHeight + ""
//				+ ". Total tree size = " + totalSize + " height = " + (upperTree.height() + lowerTreeHeight));
		
		System.out.println("=======================================================");
		
		
	}
	
	public static void main(String[] args) throws IOException {
//		StartPoint start = new StartPoint();
//
//		System.out.println("Please input starting location (floor,x,y):");
//		String startingLoc = scanner.nextLine();
//		sPoint = Helper_1.locPoint(startingLoc);
//		sPartition = Helper_1.locPartition(startingLoc);
//
//		System.out.println("your starting partition is " + sPartition.getmID() + " loc = "
//				+ "" + sPartition.cornerToString2D() + " on floor " + sPartition.getmFloor());
//
//		System.out.println("Please input ending location (floor,x,y):");
//		String endingLoc = scanner.nextLine();
//		ePoint = Helper_1.locPoint(endingLoc);
//		ePartition = Helper_1.locPartition(endingLoc);
//
//		System.out.println("your ending partition is " + ePartition.getmID() + " loc = "
//				+ "" + ePartition.cornerToString2D() + " on floor " + ePartition.getmFloor());
//
//		System.out.println("Please input a set of keywords (key1,key2,...,keyN):");
//		String keyword = scanner.nextLine();
//		keywords = keyword.split(",");
//		candPartition = Helper_1.keywordPartition(keyword);
//
//		System.out.println("Please input a distance constraint:");
//		distance = scanner.nextInt();
//		System.out.println("you input distance = " + distance);
//
//		// add start, end partition to candidate partition list
//		candPartition.add(sPartition);
//		candPartition.add(ePartition);
//
//		System.out.println("lower bound distance = " + Helper_1.lowerBound(sPoint, ePoint));
	}
}
