/**
 * 
 */
package datagenerate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * <h>SplitShopDesc</h>
 * split the shop into saperate files
 * 
 * @author feng zijin
 *
 */
public class SplitShopDesc {
	private static String inputFile = System.getProperty("user.dir") + "/shops.txt";
	private static String outputFile = System.getProperty("user.dir") + "/data/shopDescription";
	
	public SplitShopDesc() {}
	
	public static void genShopKeyword()  throws IOException {
		Path path = Paths.get(inputFile);
		Scanner scanner = new Scanner(path);
		int fileNum = 1;
		//read line by line
		while(scanner.hasNextLine()){
		    //process each line
		    String line = scanner.nextLine();
		    String[] tempArr = line.split("\t");
		    
		    if (tempArr.length < 7) {
		    		System.out.println(line);
		    		System.out.println(tempArr.length);
		    } else {
		    		String nameNDescription = tempArr[0] + "\t" + tempArr[6];
		    		// System.out.println(nameNDescription);
		    		FileWriter fwDesc = new FileWriter(outputFile + "/shop" + (fileNum++) + ".txt");
		    		
		    		fwDesc.write(nameNDescription);
		    		
		    		fwDesc.flush();
		    		fwDesc.close();
		    }
		    
		}
		scanner.close();
		
		System.out.println("shops keyword generated !");
	}
	
	public static void main(String[] args) throws IOException {
	}
}
