/**
 * 
 */
package textualIndex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import datagenerate.AssignShop;
import indoor_entitity.Shop;
import utilities.DataGenConstant;
import utilities.Functions;

/**
 * <h>Dictionary</h>
 * represent dictionary that stores vocabularies 
 *
 * @author feng zijin
 *
 */
public class Dictionary {
	public static Hashtable<Integer, Vocabulary> dictionary;
	
	/**
	 * Constructor
	 * 
	 */
	public Dictionary() {
		dictionary = new Hashtable<Integer, Vocabulary>();
	}
	
	/**
	 * load the keywordIndex file
	 * @throws IOException 
	 * 
	 */
	public void loadData(String fileInput) throws IOException {
		Path path = Paths.get(fileInput);
		Scanner scanner = new Scanner(path);
		
		//read line by line
		while(scanner.hasNextLine()){
		    //process each line
		    String line = scanner.nextLine();
		    String[] tempArr = line.split("\t");
		    
		    if (tempArr.length != 2) System.out.println("Something wrong_loadKeywordIndex");
		    
		    String word = tempArr[0];
		    	int key = Integer.parseInt(tempArr[1]);
		    	
		    	Vocabulary vocabulary = new Vocabulary(key, word);
		    	
		    	this.addVocabulary(vocabulary);
		}
		
		System.out.println("dictionary generate finished! size = " + this.dictionary.size());
	}

	public void loadIdenData(String fileInput) throws IOException {
		Path path = Paths.get(fileInput);
		Scanner scanner = new Scanner(path);

		//read line by line
		while(scanner.hasNextLine()){
			//process each line
			String line = scanner.nextLine();
			String[] tempArr = line.split("\t");

			if (tempArr.length != 2) System.out.println("Something wrong_loadKeywordIndex");

			String word = tempArr[0];
			int key = Integer.parseInt(tempArr[1]);

			Vocabulary vocabulary = new Vocabulary(key, word, "I");

			this.addVocabulary(vocabulary);
		}

		System.out.println("dictionary generate finished! size = " + this.dictionary.size());
	}
	/**
	 * @return the size of the dictionary
	 * 
	 */
	public static int size() {
		return dictionary.size();
	}
	
	/**
	 * @param vocabulary the vocabulary to be added
	 * 
	 */
	public static void addVocabulary(Vocabulary vocabulary) {
		dictionary.put(vocabulary.getmID(), vocabulary);
	}
	
	/**
	 * @param mID the key of the vocabulary
	 * @return the vocabulary
	 * 
	 */
	public static Vocabulary getVocabulary(int mID) {
		return dictionary.get(mID);
	}
	
	/**
	 * @param mWord
	 * @return the vocabulary
	 * 
	 */
	public static Vocabulary getVocabulary(String mWord) {
		Vocabulary result = null;
		
		int dictSize = dictionary.size();
		for (int i = 0; i < dictSize; i ++) {
			if (dictionary.get(i).getmWord().equals(mWord)) {
				result = dictionary.get(i);
			}
		}
		
		return result;
	}
	
	/**
	 * the a list of partitions that contains the word
	 * @param wordID the word id
	 * @return the list of partition in
	 * 
	 */
	public static ArrayList<Integer> getPartition(int wordID) {
		String word = dictionary.get(wordID).getmWord();
		//System.out.println("wordID = " + wordID + " word = " + word);
		return getVocabulary(word).getmNodes();
	}

	public static ArrayList<Integer> getIdenPartition(int wordID) {
		String word = dictionary.get(wordID).getmWord();
		//System.out.println("wordID = " + wordID + " word = " + word);
		return getIdenVocabulary(word).getmNodes();
	}

	public static Vocabulary getIdenVocabulary(String mWord) {
		Vocabulary result = null;

		int dictSize = dictionary.size();
		for (int i = 1; i <= dictSize; i ++) {
			if (dictionary.get(-i).getmWord().equals(mWord)) {
				result = dictionary.get(-i);
			}
		}

		return result;
	}

	
	/**
	 * get the word id or a word
	 * @param word the word
	 * @return the word id
	 * 
	 */
	public static int getWordID(String word) {
		Vocabulary voc = getVocabulary(word);
		if (voc == null) return -1;
		else return voc.getmID();
	}
	
	/**
	 * convert the word list to word id list
	 * @param words the word list
	 * @return the word id list
	 * 
	 */
	public static ArrayList<Integer> toIndex(ArrayList<String> words) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		int wordSize = words.size();
		for (int i = 0; i < wordSize; i ++){
			result.add(getWordID(words.get(i)));
		}
		
		return result;
	}
	
	/**
	 * show the whole dictionary as a string
	 * @return string
	 * 
	 */
	public String toString() {
		String result = "";
		
		Set<Integer> keys = dictionary.keySet();
		Iterator<Integer> itr = keys.iterator();
		
	    while (itr.hasNext()) {
	    		int key = itr.next();
	    		int mID = dictionary.get(key).getmID();
	    		String mWord = dictionary.get(key).getmWord();
	    		int mImpact = dictionary.get(key).getmImpact();
	    		
	    		if (key != mID) System.out.println("something wrong_Dictioanry_toString");
	    		
	    		result = result + key + " " + mID + " " + mWord + " " + mImpact + "\n";
	    }
		
		return result;
	}
	
	/**
	 * test
	 * 
	 */
	public static void main(String[] args) throws IOException {
		String fileInput = System.getProperty("user.dir") + "/keywordIndex.txt";
		Dictionary dic = new Dictionary();
		dic.loadData(fileInput);
		
		Vocabulary voc = dic.getVocabulary(302);
		System.out.println(voc.getmID() + voc.getmImpact() + voc.getmWord());
	}
}
