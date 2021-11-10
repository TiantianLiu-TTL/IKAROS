/**
 * 
 */
package textualIndex;

import java.io.IOException;
import java.util.ArrayList;

import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import utilities.DataGenConstant;

/**
 * <h>Vocabulary</h>
 * represents vocabulary
 * 
 * @author feng zijin
 *
 */
public class Vocabulary {
	private int mID;
	private String mWord;
	private int mImpact;
	private ArrayList<Integer> mNodes = new ArrayList<Integer>();
	
	/**
	 * Constructor
	 * 
	 * @param mWord
	 */
	public Vocabulary(String mWord) {
		this.mID = -1;
		this.mWord = mWord;
		this.updatemImpact();
	}
	
	/**
	 * Constructor
	 * 
	 * @param mID
	 * @param mWord
	 */
	public Vocabulary(int mID, String mWord) {
		this.mID = mID;
		this.mWord = mWord;
		this.updatemImpact();
	}

	public Vocabulary(int mID, String mWord, String type) {
		this.mID = mID;
		this.mWord = mWord;
		this.updatemIdenImpact();

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
	 * @return the mImpact
	 */
	public int getmImpact() {
		return mImpact;
	}

	/**
	 * Update the mImpact of this word
	 */
	public void updatemImpact() {
		int tempImpact = 0;

		int partitionSize = IndoorSpace.iPartitions.size();
		for (int i = 0; i < partitionSize; i ++) {
			int[] mKeywords = new int[DataGenConstant.mKeyworSize];
			mKeywords = IndoorSpace.iPartitions.get(i).getmKeywords();
			
			int keywordSize = mKeywords.length;
			for (int j = 0; j < keywordSize; j ++) {
				int keywordIndex = -1;
				
				if (mKeywords[j] == 1) keywordIndex = j;
				
				if (keywordIndex == this.mID) {
					
					tempImpact++;
					
					this.addmNode(i);

					break;
				}
			}
		}


		this.mImpact = tempImpact;
		
		if(this.mImpact != mNodes.size()) System.out.println("somthing wrong_Vocabulary_updateImpact");
		
		String str = "";
		int nodeSize = mNodes.size();
		for (int i = 0; i < nodeSize; i ++) {
			str += IndoorSpace.iPartitions.get(mNodes.get(i)).getmID() + "\t";
		}
		
//		System.out.println(this.mID + " impact = " + this.mImpact + " mNodes size = " + mNodes.size() + " in partition " + str);
	}
	public void updatemIdenImpact() {
		int tempImpact = 0;

		int partitionSize = IndoorSpace.iPartitions.size();
		for (int i = 0; i < partitionSize; i++) {
			int iKeywords = IndoorSpace.iPartitions.get(i).getIkeyword();
			if (iKeywords == this.mID) {
				tempImpact++;
				this.addmNode(i);
			}
		}
		this.mImpact = tempImpact;

		if(this.mImpact != mNodes.size()) System.out.println("somthing wrong_Vocabulary_updateImpact");
		String str = "";
		int nodeSize = mNodes.size();
		for (int i = 0; i < nodeSize; i ++) {
			str += IndoorSpace.iPartitions.get(mNodes.get(i)).getmID() + "\t";
		}

		//System.out.println(this.mID + " impact = " + this.mImpact + " mNodes size = " + mNodes.size() + " in partition " + str);



	}
	/**
	 * @param mNode
	 *            the mNode to add
	 */
	public void addmNode(int mNode) {
		this.mNodes.add(mNode);
	}
	
	/**
	 * @return the mNodes
	 */
	public ArrayList<Integer> getmNodes() {
		return mNodes;
	}

	/**
	 * @param mNodes
	 *            the mNodes to set
	 */
	public void setmNodes(ArrayList<Integer> mNodes) {
		this.mNodes = mNodes;
	}
}
