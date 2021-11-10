/**
 * 
 */
package datagenerate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import indoor_entitity.IndoorSpace;
import indoor_entitity.Shop;
import ucar.unidata.geoloc.ogc.CoordRefSysToGML;
import utilities.DataGenConstant;
import utilities.FilePaths;
import utilities.Functions;

/**
 * <h>KeywordIndexer</h>
 * index the keyword, the first step of dealing with shops from shopsWithKey.txt.
 * 
 * @author feng zijin
 *
 */
public class KeywordIndexer {
	private static String inputFile = FilePaths.FilePathPre + "/shopsWithKey-new.txt";
	private static String outputFile_Shop = FilePaths.FilePathPre + "/shopsWithIndex.txt";
	private static String outputFile_Index = FilePaths.FilePathPre + "/keywordIndex.txt";
	private static String outputFile_identityWordIndex = FilePaths.FilePathPre + "/identityKeywordIndex.txt";
	private static String outputFile_regularWordIndex = FilePaths.FilePathPre + "/regularKeywordIndex.txt";
	private static String outputFile_wordRelationship_identity = FilePaths.FilePathPre + "/wordRelationship_identity.txt";
	private static String outputFile_wordRelationship_regular = FilePaths.FilePathPre + "/wordRelationship_regular.txt";
	private static String outputFile_shopsWithIRkeywordIndex = FilePaths.FilePathPre + "/shopsWithIRKeywordIndex.txt";

	private static ArrayList<String> keywordArr = new ArrayList<String>();  // store all keywords
	private static ArrayList<String> iKeywordArr = new ArrayList<String>();  // store identity keywords
	private static ArrayList<String> rKeywordArr = new ArrayList<String>();  // store regular keywords

	private static ArrayList<String> frontParts = new ArrayList<String>();  // store the shop information other than keyword
	private static ArrayList<String> tailParts = new ArrayList<String>();  // store the shop keyword
	private static int maxKeywordNumber = 0;
	private static String [][] shopInfo = new String [iKeywordArr.size()][maxKeywordNumber + 8];
	private static String [][] irKeyword = new String[iKeywordArr.size()][maxKeywordNumber + 1];



	public KeywordIndexer() {}
	
	public void index() throws IOException {
		readKeyword();
		
//		storeKeywordIndex();
//		storeWordRelationshipIdentity();
//		storeWordRelationshipRegular();
		
//		String result = indexKeyword();
//		
//		try {
//			FileWriter fwShop = new FileWriter(outputFile_Shop);
//			
//			fwShop.write(result);
//			
//			fwShop.flush();
//			fwShop.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//
//		storeShopsWithIRkeywordIndex();

		DataGenConstant.mKeyworSize = rKeywordArr.size();
		
		System.out.println("Keywords are indexed! total keyword size = " + DataGenConstant.mKeyworSize);
	}
	
	private void readKeyword() throws IOException {
		Path path = Paths.get(inputFile);
		Scanner scanner = new Scanner(path);
		
		//read line by line
		while(scanner.hasNextLine()){
		    //process each line
		    String line = scanner.nextLine();
		    String[] tempArr = line.split("\t");
		    
		    String frontPart = Functions.arrayToString1D(tempArr, 0, 7); // element 0 to 7 (exclude)
		    String tailPart = Functions.arrayToString1D(tempArr, 8, tempArr.length);  //element 8 to the last one
		    frontParts.add(frontPart);
		    tailParts.add(tailPart);
		    
		    int tempArrSize = tempArr.length;
		    if (tempArrSize > maxKeywordNumber) {
		    	maxKeywordNumber = tempArrSize;
			}
		    for (int i = 8; i < tempArrSize; i++) {
		    		if (!keywordArr.contains(tempArr[i].toLowerCase()) && tempArr[i] != null && !tempArr.equals("") && !tempArr[i].equals(" ")) {
		    			keywordArr.add(tempArr[i].toLowerCase());
		    		}

		    }
		    //generate identity word
			if (!iKeywordArr.contains(tempArr[1].toLowerCase())){

				iKeywordArr.add(tempArr[1].toLowerCase());
			}
		}

		//generate regular word
		for (int i=0; i<keywordArr.size(); i++){
			int flag = 1;
			for (int j=0; j<iKeywordArr.size(); j++){
				if (keywordArr.get(i).toLowerCase().equals(iKeywordArr.get(j))){
					flag = 0;
					break;
				}

			}
			if (flag == 1){
				rKeywordArr.add(keywordArr.get(i).toLowerCase());
			}
		}
	}
	
	private String indexKeyword() {
		String overallResult = "";
		
		int frontPartSize = frontParts.size();
		for (int i = 0; i < frontPartSize; i++) {
			String result = frontParts.get(i) + "\t";
			
//			System.out.println(tailParts.get(i));
			String[] tempArr = tailParts.get(i).split("\t");
//			System.out.println(Functions.arrayToString1D(tempArr, 0, tempArr.length));
			
			// for each of keywords except the last one 
			int tempArrSize = tempArr.length - 1;
			for (int j = 0; j < tempArrSize; j++) {
				
				int keywordArrSize = keywordArr.size();
				for (int k = 0; k < keywordArrSize; k++) {
					if (tempArr[j].toLowerCase().equals(keywordArr.get(k))) {
//						System.out.println(tempArr[j] + " = " + keywordArr.get(k) + " at " + k);
						result += k + "\t";
					}
				}
			}
			
			// for the last keyword
			int keywordArrSize = keywordArr.size();
			for (int k = 0; k < keywordArrSize; k++) {
				if (tempArr[tempArr.length - 1].toLowerCase().equals(keywordArr.get(k))) {
//					System.out.println(tempArr[tempArr.length - 1] + " = " + keywordArr.get(k) + " at " + k);
					result += k + "\t";
				}
			}
			
			overallResult += result + "\n";
		}
		
		return overallResult;
	}

	private void storeShopsWithIRkeywordIndex() {
		try {

			Path path1 = Paths.get(inputFile);
			Scanner scanner1 = new Scanner(path1);

			shopInfo = new String [iKeywordArr.size()][maxKeywordNumber + 8];

			int k = 0;
			String result = "";
			//read line by line
			while(scanner1.hasNextLine()){
				//process each line
				String line = scanner1.nextLine();
				shopInfo[k] = line.split("\t");
				for (int i = 0; i < 7; i++) {
					result += shopInfo[k][i] + "\t";
				}
				for (int j = 0; j < irKeyword[k].length; j++) {
					result += irKeyword[k][j] + "\t";
				}
				result += "\n";
				k++;

			}

			FileWriter fwIndex = new FileWriter(outputFile_shopsWithIRkeywordIndex);

			// wordRelationship_identity
			fwIndex.write(result);

			fwIndex.flush();
			fwIndex.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	private void storeKeywordIndex() {
		try {
			FileWriter fwIndex = new FileWriter(outputFile_Index);
			
			// keyword+index
			int keywordArrSize = keywordArr.size();
			for (int i = 0; i < keywordArrSize; i++) {
				fwIndex.write(keywordArr.get(i) + "\t" + i + "\n");
			}
			
			fwIndex.flush();
			fwIndex.close();

			FileWriter fwIndex1 = new FileWriter(outputFile_identityWordIndex);

			// identitykeyword+index
			int iKeywordArrSize = iKeywordArr.size();
			for (int i = 0; i < iKeywordArrSize; i++) {
				fwIndex1.write(iKeywordArr.get(i) + "\t" + (i-iKeywordArrSize) + "\n");
			}

			fwIndex1.flush();
			fwIndex1.close();

			FileWriter fwIndex2 = new FileWriter(outputFile_regularWordIndex);

			// regularkeyword+index
			int rKeywordArrSize = rKeywordArr.size();
			for (int i = 0; i < rKeywordArrSize; i++) {
				fwIndex2.write(rKeywordArr.get(i) + "\t" + i + "\n");
			}

			fwIndex2.flush();
			fwIndex2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	private void storeWordRelationshipIdentity() {
		try {
			Path path = Paths.get(inputFile);
			Scanner scanner = new Scanner(path);


			int k = 0;
			int iKeywordArrSize = iKeywordArr.size();
			String result = "";


			//read line by line
			while(scanner.hasNextLine()){
				//process each line
				String line = scanner.nextLine();
				String[] tempArr = line.split("\t");
				result += (k - iKeywordArrSize) + "\t";
				k++;
				for (int i = 8; i < tempArr.length; i++) {
					for (int j = 1; j < rKeywordArr.size(); j++){
						if (tempArr[i].toLowerCase().equals(rKeywordArr.get(j).toLowerCase())) {
							result += j + "\t";
							break;
						}

					}
				}
				result += "\n";

			}

			FileWriter fwIndex = new FileWriter(outputFile_wordRelationship_identity);

			// wordRelationship_identity
			fwIndex.write(result);

			fwIndex.flush();
			fwIndex.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}


	private void storeWordRelationshipRegular() {
		try {
			Path path = Paths.get(outputFile_wordRelationship_identity);
			Scanner scanner = new Scanner(path);
			int k = 0;
			//System.out.println(maxKeywordNumber);
			irKeyword = new String[iKeywordArr.size()][maxKeywordNumber + 1];
			while (scanner.hasNextLine()){
				String line = scanner.nextLine();
				irKeyword[k] = line.split("\t");
				k++;

			}
			String result = "";

			for (int i = 1; i < rKeywordArr.size(); i++){

				result += i + "\t";

				for (int p = 0; p < iKeywordArr.size(); p++)
				{
					for (int j = 1; j < irKeyword[p].length; j++){
						//System.out.println(temp[p][j]+"    " + i);
						if (irKeyword[p][j].equals(String.valueOf(i))){
							result += irKeyword[p][0] + "\t";
							break;
						}
					}
				}
				result += "\n";


			}

			FileWriter fwIndex = new FileWriter(outputFile_wordRelationship_regular);

			// wordRelationship_regular
			fwIndex.write(result);

			fwIndex.flush();
			fwIndex.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	public static void main(String[] args) throws IOException {
		KeywordIndexer keywordIndexer = new KeywordIndexer();
		keywordIndexer.index();
	}
}
