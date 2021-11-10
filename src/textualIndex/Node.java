/**
 * 
 */
package textualIndex;

import java.io.IOException;
import java.util.Hashtable;

/**
 * <h>Node</h>
 * represents the Node in hierarichical tree
 * 
 * @author feng zijin
 *
 */
public class Node {
	private String mWord;
	private int mID;
	private Vocabulary vocabulary;
	
	/**
	 * Constructor
	 * 
	 * @param mWord
	 */
	public Node(String mWord) {
		this.mWord = mWord;
		setVocabulary(this.mWord);
		setmID(this.vocabulary.getmID());
	}
	
	/**
	 * Constructor
	 * 
	 * @param mID
	 */
	public Node(int mID) {
		this.mID = mID;
		setVocabulary(this.mID);
		setmWord(this.vocabulary.getmWord());
	}
	
	/**
	 * @return the mWord
	 */
	public String getmWord() {
		return mWord;
	}

	/**
	 * @param mWord
	 *            the mWord to set
	 */
	public void setmWord(String mWord) {
		this.mWord = mWord;
	}
	
	/**
	 * @return the vocabulary
	 */
	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	/**
	 * @param vocabulary
	 *            the vocabulary to set
	 */
	public void setVocabulary(Vocabulary vocabulary) {
		this.vocabulary = vocabulary;
	}
	
	/**
	 * @param mWord
	 * 
	 */
	public void setVocabulary(String mWord) {
		Hashtable<Integer, Vocabulary> dictionary = Dictionary.dictionary;
		
		Vocabulary voc = null;
		
		int dictSize = dictionary.size();
		for (int i = 0; i < dictSize; i ++) {
			if (dictionary.get(i).getmWord().equals(mWord)) {
				voc = dictionary.get(i);
			}
		}
		
		if (!voc.getmWord().equals(mWord)) System.out.println("something wrong_Node_setVocabulary");
		
		this.vocabulary = voc;
	}
	
	/**
	 * @param mID
	 * 
	 */
	public void setVocabulary(int mID) {
		Hashtable<Integer, Vocabulary> dictionary = Dictionary.dictionary;
		
		Vocabulary voc = null;
		
		int dictSize = dictionary.size();
		for (int i = 0; i < dictSize; i ++) {
			if (dictionary.get(i).getmID() == mID) {
				voc = dictionary.get(i);
			}
		}
		
		if (voc.getmID() != mID) System.out.println("something wrong_Node_setVocabulary");
		
		this.vocabulary = voc;
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
	 * test
	 * 
	 */
	public static void main(String[] args) throws IOException {
		String fileInput = System.getProperty("user.dir") + "/keywordIndex.txt";
		Dictionary dic = new Dictionary();
		dic.loadData(fileInput);
		
		Node node = new Node(10);
		System.out.println(node.getmWord() + " id = " + node.getmID());
		
		node = new Node("women");
		System.out.println(node.getmWord() + " id = " + node.getmID());
	}
}
