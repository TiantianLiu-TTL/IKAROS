/**
 * 
 */
package datagenerate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import utilities.Functions;

/**
 * @author feng zijin
 *
 */
public class ReadFile {
	public static void main(String[] args) throws IOException {
		String inputFile1 = System.getProperty("user.dir") + "/shopsWithKey.txt";
		String inputFile2 = System.getProperty("user.dir") + "/shops-done.txt";
		String outputFile = System.getProperty("user.dir") + "/shopsWithKey-new.txt";
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> previous = new ArrayList<String>();
		ArrayList<String> name = new ArrayList<String>();
		
		Path path = Paths.get(inputFile2);
		Scanner scanner = new Scanner(path);
		
		int lineNum = 0;
		//read line by line
		while(scanner.hasNextLine()){
		    //process each line
		    String line = scanner.nextLine();
		    String[] tempArr = line.split("\t");
		    lineNum ++;
		    String str = "";
		    if (tempArr.length == 7) {
		    		str = tempArr[6];
		    } else if (tempArr.length == 7) {
		    		str = tempArr[5];
		    }
		    
		    String[] strs = str.split(",");
		    String attach = "";
		    for (int i = 0; i < strs.length - 1; i ++) {
		    		attach = attach + strs[i] + "\t";
		    }
		    attach += strs[strs.length - 1];
		    
		    keys.add(attach);
		    
		}
		
		System.out.println(lineNum);
		
		path = Paths.get(inputFile1);
		scanner = new Scanner(path);
		
		int lineNum1 = 0;
		//read line by line
		while(scanner.hasNextLine()){
		    //process each line
		    String line = scanner.nextLine();
		    String[] tempArr = line.split("\t");
		    lineNum1 ++;
		    String str = "";
		    
		    name.add(tempArr[1]);
		    System.out.println(tempArr[1]);
		    for (int i = 0; i < 8; i ++) {
		    		str += tempArr[i] + "\t";
		    }
		    
		    previous.add(str);
		}
		
		System.out.println(lineNum1);
		
		if (keys.size() != previous.size()) System.out.println("something wrong");
		
		try {
			FileWriter fwShop = new FileWriter(outputFile);
			
			for (int i = 0; i < keys.size(); i ++) {
				fwShop.write(previous.get(i) + "\t" + name.get(i) + keys.get(i) + "\n");
			}
			
			fwShop.flush();
			fwShop.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
}
