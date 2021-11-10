package com.whynot.demo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import algorithm_ikrq.Algo_ikrq;
import indoor_entitity.Floor;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.Point;
import indoor_entitity.Shop;
import utilities.DataGenConstant;
import utilities.FilePaths;
import utilities.RoomType;

/**
 * Servlet implementation class DemoServlet
 */
@WebServlet("/DemoServlet")
public class DemoServlet extends HttpServlet {
	
	private static Algo_ikrq algo;
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Integer> Keywordmap_str2Int = new HashMap<String, Integer> ();
	private HashMap<Integer, String> Keywordmap_int2Str = new HashMap<Integer, String> ();
	
    public DemoServlet() {
        super();
    }

	/**
	 * will be run automatically once the server started
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		System.out.println(System.getProperty("user.dir"));
		System.out.println("init");
		
		try {
			algo = new Algo_ikrq();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String fileInput_i = FilePaths.FilePathPre + "/identitykeywordIndex.txt";
		
		Path path_i = Paths.get(fileInput_i);
		Scanner scanner_i = null;
		
		try {
			scanner_i = new Scanner(path_i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//read line by line
		while(scanner_i.hasNextLine()){
		    //process each line
		    String line = scanner_i.nextLine();
		    String[] tempArr = line.split("\t");
		    
		    if (tempArr.length != 2) System.out.println("Something wrong_loadKeywordIndex");
		    
		    String word = tempArr[0];
		    	int key = Integer.parseInt(tempArr[1]);
		    	
		    	Keywordmap_str2Int.put(word, key);
		    	Keywordmap_int2Str.put(key, word);
		}
		
		String fileInput_r = FilePaths.FilePathPre + "/regularkeywordIndex.txt";
		
		Path path_r = Paths.get(fileInput_r);
		Scanner scanner_r = null;
		
		try {
			scanner_r = new Scanner(path_r);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//read line by line
		while(scanner_r.hasNextLine()){
		    //process each line
		    String line = scanner_r.nextLine();
		    String[] tempArr = line.split("\t");
		    
		    if (tempArr.length != 2) System.out.println("Something wrong_loadKeywordIndex");
		    
		    String word = tempArr[0];
		    	int key = Integer.parseInt(tempArr[1]);
		    	
		    	Keywordmap_str2Int.put(word, key);
		    	Keywordmap_int2Str.put(key, word);
		}
		
		System.out.println("Keywordmap size = " + Keywordmap_str2Int.size() + " Keywordmap_int2Str size = " + Keywordmap_int2Str.size());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//	  System.out.println("doGet");
	  String qt = request.getParameter("q_type");
	  
	  if(qt==null) return;
	  
	  if (qt.equals("0")) {
		  
	  }
	  
	  if(qt.equals("1")) { //spatial keyword top-k query
		  
		  String q_id = request.getParameter("q_id");
		  int currentFloor = Integer.parseInt(request.getParameter("q_currentFloor"));
		  
		  int partitionID = -1;
		  if (currentFloor > 0) partitionID = Integer.parseInt(q_id.replace("v","")) + (currentFloor * DataGenConstant.curSizePar);
		  else if (currentFloor == 0) partitionID = Integer.parseInt(q_id.replace("v",""));
		  
//		  System.out.println("q_id: " + q_id + " partitionID: " + partitionID + " currentFloor: " + currentFloor);
		  
		  response.setContentType("text/plain");
	      response.setCharacterEncoding("UTF-8");
	      response.getWriter().write(getPartitionID(partitionID));
	      
	  } else if (qt.equals("2")) {
		  String q_sloc = request.getParameter("q_sloc");
		  String q_eloc = request.getParameter("q_eloc");
		  double q_distance = Double.parseDouble(request.getParameter("q_distance"));
		  ArrayList<Integer> q_keywords = new ArrayList<Integer>();
		   
		  String[] tmp = request.getParameter("q_keyowrd").split(",");
		   
		  for(int i = 0; i < tmp.length; i++) {
			  if(Keywordmap_str2Int.containsKey(tmp[i])) {
				  if (Keywordmap_str2Int.get(tmp[i]) == 0) continue;
				  q_keywords.add(Keywordmap_str2Int.get(tmp[i]));
			   }
		  }
		   
		  int q_k  = Integer.parseInt(request.getParameter("q_k"));
		   
		  System.out.println("sloc: " + q_sloc + " eloc: " + q_eloc + " distance: " + q_distance + " keywords: " + q_keywords + " k: " + q_k);
		   
		  response.setContentType("text/plain");
		  response.setCharacterEncoding("UTF-8");
		  response.getWriter().write(TOE(q_sloc, q_eloc, q_distance, q_keywords, q_k));
		  
	  } else if (qt.equals("3")) {
		  String q_sloc = request.getParameter("q_sloc");
		  String q_eloc = request.getParameter("q_eloc");
		  double q_distance = Double.parseDouble(request.getParameter("q_distance"));
		  ArrayList<Integer> q_keywords = new ArrayList<Integer>();
		   
		  String[] tmp = request.getParameter("q_keyowrd").split(",");
		   
		  for(int i = 0; i < tmp.length; i++) {
			  if (Keywordmap_str2Int.containsKey(tmp[i])) {
				  if (Keywordmap_str2Int.get(tmp[i]) == 0) continue;
				  q_keywords.add(Keywordmap_str2Int.get(tmp[i]));
			  }
		  }
		  
		  int q_k  = Integer.parseInt(request.getParameter("q_k"));
		   
		  System.out.println("sloc: " + q_sloc + " eloc: " + q_eloc + " distance: " + q_distance + " keywords: " + q_keywords + " k: " + q_k);
		   
		  response.setContentType("text/plain");
		  response.setCharacterEncoding("UTF-8");
		  response.getWriter().write(KOE(q_sloc, q_eloc, q_distance, q_keywords, q_k));
		  
	  } else if (qt.equals("4")) {
		  String q_floor = request.getParameter("q_floor");
		  
		  int floorID = Integer.parseInt(q_floor);
		  
		  response.setContentType("text/plain");
	      response.setCharacterEncoding("UTF-8");
	      response.getWriter().write(getShopNames(floorID));
	  } else if (qt.equals("5")) {
		  String q_x = request.getParameter("q_x");
		  String q_y = request.getParameter("q_y");
		  String q_floor = request.getParameter("q_floor");
		  
		  String loc = q_floor + "," + q_x + "," + q_y;
		  
		  response.setContentType("text/plain");
	      response.setCharacterEncoding("UTF-8");
	      response.getWriter().write(getShopName(loc));
	  }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		System.out.println("doPost");
		doGet(request, response);
	}
	
	protected double TSim(HashSet<Integer> s1, HashSet<Integer> s2){
		double ans = 0;
		for(int i : s1)
			if(s2.contains(i)) ans++;
		return ans/(s1.size()+s2.size()+ans);
	}
	
	protected double delta_doc(HashSet<Integer> idoc, HashSet<Integer> doc){
		double ans = 0.0;
		for(int i : idoc)
			if(doc.contains(i)) ans = ans + 1;
		
		return idoc.size()-ans+doc.size()-ans;
	}
	
	private String getPartitionID(int partitionID) {
		String result = "";
		
//		System.out.println(partitionID);
		Shop shop;
		
		if (IndoorSpace.iPartitions.get(partitionID).getshop() == null) return result;
		else shop = IndoorSpace.iPartitions.get(partitionID).getshop();
		
		
		String mDescription = shop.getmDescription();
		String[] temps = mDescription.split("\t");
		String[] keywords = new String[temps.length];
		
		for (int i = 0; i < temps.length; i ++) {
			int temp = Integer.parseInt(temps[i]);
			if (i != 0 && temp < 0) {
				System.out.println("something_wrong_DemoServlet_getPartitionID");
				return "something_wrong_DemoServlet_getPartitionID";
			}
			
			if (i == 0 && temp < 0) {
				keywords[i] = Keywordmap_int2Str.get(temp);
			}
			
			if (i != 0 && temp > 0) {
				keywords[i] = Keywordmap_int2Str.get(temp);;
			}
		}
		
		String keywordList = "";
		
		for (int i = 0; i < keywords.length - 1; i ++) {
			keywordList += keywords[i] + "\t";
		}
		keywordList += keywords[keywords.length - 1];
		
		result = shop.getoriginalmID() + "," + shop.getmName() + "," + shop.getmOpening_hours() + "," + shop.getmImg() + "," + 
				shop.getmUrl() + "," + shop.getmPhone() + "," + shop.getmWebsite() + "," + keywordList;
		
		return result;
	}
	
	private String getShopNames(int floorID) {
		String result = "";
		
		Floor floor = IndoorSpace.iFloors.get(floorID);
		ArrayList<Integer> partitions = new ArrayList<Integer>();
		partitions = floor.getmPartitions();
		
		for (int i = 0; i < partitions.size() - 1; i ++) {
			if (IndoorSpace.iPartitions.get(partitions.get(i)).getmType() == RoomType.STORE) {
				result += IndoorSpace.iPartitions.get(partitions.get(i)).getshop().getmName() + ";";
			} else {
				result += " " + ";";
			}
		}
		
		if (IndoorSpace.iPartitions.get(partitions.get(partitions.size() - 1)).getmType() == RoomType.STORE) {
			result += IndoorSpace.iPartitions.get(partitions.get(partitions.size() - 1)).getshop().getmName();
		} else {
			result += " ";
		}
		
		return result;
	}
	
	private String getShopName(String loc) {
		String result = "";
		
		Point point = algo.locPoint(loc);
        Partition partition = algo.locPartition(loc);
        if (partition.getmType() == RoomType.HALLWAY) {
        		result = "HALLWAY";
        } else if (partition.getmType() == RoomType.STAIRCASE) {
        		result = "STAIRCASE";
        } else if (partition.getmType() == RoomType.STORE) {
        		result = partition.getshop().getmName();
        }
		
		return result;
	}
	
	private String TOE(String q_sloc, String q_eloc, double q_distance, ArrayList<Integer> q_keywords, int q_k) throws IOException {
		// TODO Auto-generated method stub
//		System.out.println("In toe");
		String result = algo.ptpWordDist1(q_sloc, q_eloc, q_distance, q_keywords, q_k);
		System.out.println("toe: " + result);
		return result;
	}
	
	private String KOE(String q_sloc, String q_eloc, double q_distance, ArrayList<Integer> q_keywords, int q_k) throws IOException {
		// TODO Auto-generated method stub
//		System.out.println("In koe");
		String result =  algo.ptpWordDist2(q_sloc, q_eloc, q_distance, q_keywords, q_k);
		System.out.println("koe: " + result);
		return result;
	}
	
	protected HashSet<HashSet<Integer>> powerset(HashSet<Integer> keyword){
		HashSet<HashSet<Integer>> ans = new HashSet<HashSet<Integer>> ();
		
	    if (keyword.isEmpty()) {
	    	ans.add(new HashSet<Integer>());
	    	return ans;
	    }
		
		ArrayList<Integer> list = new ArrayList<Integer>(keyword);
		Integer head = list.get(0);
		HashSet<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
		for(HashSet<Integer> set : powerset(rest)) {
			HashSet<Integer> newset = new HashSet<Integer>();
			newset.add(head);
			newset.addAll(set);
			ans.add(newset);
			ans.add(set);
		}
		
		return ans;
	}
}
