package algorithm_ikrq;

import com.github.davidmoten.rtree.geometry.Geometries;
import datagenerate.AssignShop;
import indoor_entitity.*;
import rStarTree2D.UpperTree;
import textualIndex.ConnectivityTier;
import textualIndex.Dictionary;
import utilities.Constant;
import utilities.DataGenConstant;
import utilities.DoorType;
import utilities.FilePaths;
import utilities.Functions;

import java.io.IOException;
import java.util.*;

public class Algo_ikrq {
	public static AssignShop assignShop = new AssignShop();
	public static Dictionary dictionary = new Dictionary();
	public static UpperTree upperTree = new UpperTree();
	public static HashMap<String, Double> d2dDistMap = new HashMap<String, Double>(); // the distance between two doors
	public static HashMap<String, String> d2dRouteMap = new HashMap<String, String>(); // the route between two doors
	public static ArrayList<Double> KRankScorePIList = new ArrayList<>();
	public static HashMap<String, HashMap<String, Double>> dominate = new HashMap<>();
	public static HashMap<String, Double> kbounds = new HashMap<String, Double>();
	public static double threshold = 0.1;

	public Algo_ikrq() throws IOException {
		System.out.println("Algo_ikrq " + FilePaths.FilePathPre);
		assignShop.assignShop();
		assignShop.staticAssign();

		Runtime r = Runtime.getRuntime();
		r.gc();
		long start = r.totalMemory() - r.freeMemory();
		String fileInput = FilePaths.FilePathPre + "/identitykeywordIndex.txt";
		dictionary.loadIdenData(fileInput);
		long end = r.totalMemory() - r.freeMemory();
		long memory = end - start;
		System.out.println("memory = " + memory / 1024 / 1024);

		upperTree.loadData();

		int totalWordSize = 0;
		int max = 0;
		int min = 9999;
		for (int i = 0; i < IndoorSpace.iShops.size(); i++) {
			totalWordSize += IndoorSpace.iShops.get(i).getmDescription().split("\t").length;
			if (max < IndoorSpace.iShops.get(i).getmDescription().split("\t").length)
				max = IndoorSpace.iShops.get(i).getmDescription().split("\t").length;
			if (min > IndoorSpace.iShops.get(i).getmDescription().split("\t").length)
				min = IndoorSpace.iShops.get(i).getmDescription().split("\t").length;
		}
		System.out.println("totalWordSize = " + totalWordSize + " ave = "
				+ totalWordSize / IndoorSpace.iPartitions.size() + " max = " + max + " min = " + min);

		int lowerTreeHeight = upperTree.get(0).getLowerTree().height();
		int lowerTreeSize = upperTree.get(0).getLowerTree().size();
		int totalSize = 0;

		for (int i = 0; i < upperTree.size(); i++) {
			totalSize += upperTree.get(0).getLowerTree().size();
		}

		System.out.println("Tree upper part generated! size = " + upperTree.size() + " height = " + upperTree.height()
				+ "" + " each leave node link to lower part with size = " + lowerTreeSize + " height = "
				+ lowerTreeHeight + "" + ". Total tree size = " + totalSize + " height = "
				+ (upperTree.height() + lowerTreeHeight));

	}

	// TOE
	public String ptpWordDist1(String sloc, String eloc, double distCon, ArrayList<Integer> queW, int k)
			throws IOException {
		String result = "no such a route";
		dominate = new HashMap<>();
		kbounds = new HashMap<String, Double>();
		double kBound = 0;
		// get candidate keywords
		CandidateWords candWords = new CandidateWords(queW, threshold);
		ArrayList<ArrayList<double[]>> allCandWords = candWords.findAllCandWords();
		ArrayList<Double> addAllCanWord = new ArrayList<>();

		// System.out.println("candidate keywords ok");
		for (int i = 0; i < allCandWords.size(); i++) {
			// System.out.println("Candidate words of No." + i + "query word");
			for (int j = 0; j < allCandWords.get(i).size(); j++) {
				addAllCanWord.add(allCandWords.get(i).get(j)[0]);
				// System.out.println(allCandWords.get(i).get(j)[0] + " " +
				// allCandWords.get(i).get(j)[1]);
			}
		}
		// System.out.println(addAllCanWord.toString());

		double maxRelScore = queW.size() + 1;

		// starting
		Point sPoint = locPoint(sloc);
		Partition sPartition = locPartition(sloc);
		int vs = sPartition.getmID();
		int f_vs = sPartition.getmFloor();
		int sid = -2;

		Point ePoint = locPoint(eloc);
		Partition ePartition = locPartition(eloc);
		int ve = ePartition.getmID();
		int f_ve = ePartition.getmFloor();
		int eid = -1;

		// initialize the piority queues
		MinHeap<Stamp> Q = new MinHeap<>("rankingscore");
		MinHeap<Stamp> FQ = new MinHeap<>("rankingscore");
		MinHeap<Stamp> FQ_PI = new MinHeap<>("rankingscore");

		ArrayList<Integer> canDoor = new ArrayList<>();
		ArrayList<Integer> nonCanDoor = new ArrayList<>();

		// initialize stamp
		Stamp s0 = new Stamp(vs, new ArrayList<Integer>());
		s0.Rp.add(sid);
		s0.Wr.add(sPartition.getIkeyword());
		s0.P.add(vs);
		Score s0Score = new Score(allCandWords, s0.Wr, 0, distCon);
		s0Score.initPrevious();
		double relScore = s0Score.calRelScore();
		s0.setRelScore(relScore);
		s0.setWc(s0Score.getWc());
		s0.setScore(s0Score);

		for (int i = 0; i < s0.Wc.size(); i++) {
			if (s0.Wc.get(i) == 0) {
				s0.Pc.add(-1);
			} else {
				s0.Pc.add(vs);
			}
		}

		s0.setD(0);
		s0.setRankScore(0);

		Q.insert(s0);

		HashMap<String, Double> Dist = new HashMap<String, Double>(); // the distance between two doors
		HashMap<String, String> Route = new HashMap<String, String>(); // the route between two doors
		HashMap<String, Double> doorParDist = new HashMap<>();
		HashMap<String, String> doorParRoute = new HashMap<>();

		// System.out.println("start");
		int h = 0;

		while (Q.heapSize > 0) {
			Stamp si = Q.delete_min();
			// h++;

			// System.out.print("current partition:" + si.v + "\t");
			// System.out.print("current route:");
			// for (int i = 0; i < si.Rp.size(); i++) {
			// System.out.print(si.Rp.get(i)+" ");
			// }
			// System.out.println(" current route distance: " + si.D);

			int vi = si.v;
			int currentPoint = si.Rp.get(si.Rp.size() - 1);
			Door doori = null;

			if (currentPoint != -2 && currentPoint != -1) {
				doori = IndoorSpace.iDoors.get(currentPoint);
			}

			ArrayList<Integer> parts = new ArrayList<Integer>();
			ArrayList<Integer> doors = new ArrayList<Integer>();
			ConnectivityTier conn = IndoorSpace.iPartitions.get(vi).getConnectivityTier();

			parts = conn.getP2PLeave();
			doors = conn.getP2DLeave();

			// System.out.println("parts: " + parts + " doors: " + doors);

			// for each of these next neighbours
			int partSize = parts.size();
			int doorSize = doors.size();
			// System.out.println("number of enterable partiton: " + partSize);
			if (partSize != doorSize)
				System.out.println("something wrong_Helper_ptpWordDist " + "" + Functions.printIntegerList(parts)
						+ " \n " + Functions.printIntegerList(doors) + "\n" + " vi = " + vi);

			if (si.D > getdominate(currentPoint, si.Pc)) {
				// System.out.println("si.D > getdominate(currentPoint, si.Pc)");
				continue;
			}

			// for each leavable door and its corresponding partition of vi
			for (int i = 0; i < partSize; i++) {

				int vj = parts.get(i);
				int dj = doors.get(i);

				ArrayList<Integer> djs = new ArrayList<Integer>();
				djs.add(dj);

				// System.out.println("vj: v" + vj + " dj: d" + dj);

				int flag = 0;
				if (si.Rp.size() >= 2) {
					for (int j = 0; j < si.Rp.size() - 1; j++) {
						if (si.Rp.get(j) == dj) {
							flag = 1;
							break;
						}
					}
				}
				if (flag == 1)
					continue;

				Door doorj = IndoorSpace.iDoors.get(dj);
				if (nonCanDoor.contains(dj)) {
					continue;
				}

				if (!canDoor.contains(dj)) {
					double lowDist1 = lowerBound(sPoint, doorj);
					double lowDist2 = lowerBound(doorj, ePoint);
					double lowDist = lowDist1 + lowDist2;
					if (lowDist > distCon) {
						nonCanDoor.add(dj);
						continue;
					} else
						canDoor.add(dj);
				}

				if (currentPoint == dj && IndoorSpace.iDoors.get(currentPoint).getmType() != DoorType.EXIT) {
					if (currentPoint == dj
							&& !addAllCanWord.contains((double) IndoorSpace.iPartitions.get(vi).getIkeyword())) {
						continue;
					}
				}

				Stamp sj = null;
				double D = 0;

				Partition parVi = IndoorSpace.iPartitions.get(vi);

				if (currentPoint == -2) { // if the current point is starting point
					D = sPoint.eDist(doorj);

					if (parVi.getmFloor() != IndoorSpace.iPartitions.get(vj).getmFloor()
							&& IndoorSpace.iDoors.get(dj).getmType() == DoorType.EXIT) {
						int fid1 = parVi.getmFloor();
						int fid2 = IndoorSpace.iPartitions.get(vj).getmFloor();
						D += DataGenConstant.lenStairway * (Math.abs(fid1 - fid2));
						// System.out.println(fid1 + " " + fid2);
						if (fid1 < fid2)
							djs.add(dj + DataGenConstant.curSizeDoor);
						else if (fid1 > fid2)
							djs.add(dj - DataGenConstant.curSizeDoor);
					}

					// System.out.println("D: " + D + " djs: " + djs);

				} else {// if the previous partition is not starting point partition
					D = parVi.getdistMatrix().getDistance(currentPoint, dj);

					if (D == -1) {
						System.out.println("D == -1");
						int fid1 = IndoorSpace.iDoors.get(currentPoint).getmFloor();
						int fid2 = IndoorSpace.iDoors.get(dj).getmFloor();
						D = DataGenConstant.lenStairway * (Math.abs(fid1 - fid2));

						if (IndoorSpace.iDoors.get(currentPoint).eDist(IndoorSpace.iDoors.get(dj)) > Constant.small) {
							D += IndoorSpace.iDoors.get(currentPoint).eDist(IndoorSpace.iDoors.get(dj));
							djs.add(0, (doori.getmID() + DataGenConstant.curSizeDoor));
						}
					}

					if (parVi.getmFloor() != IndoorSpace.iPartitions.get(vj).getmFloor()
							&& IndoorSpace.iDoors.get(dj).getmType() == DoorType.EXIT) {
						int fid1 = parVi.getmFloor();
						int fid2 = IndoorSpace.iPartitions.get(vj).getmFloor();
						D += DataGenConstant.lenStairway * (Math.abs(fid1 - fid2));
						// System.out.println(fid1 + " " + fid2);
						if (fid1 < fid2)
							djs.add(dj + DataGenConstant.curSizeDoor);
						else if (fid1 > fid2)
							djs.add(dj - DataGenConstant.curSizeDoor);
					}

					// System.out.println("D: " + D + " djs: " + djs);
				}

				if (si.D + D <= distCon) {
					double lowBound = lowerBound(doorj, ePoint);

					if (si.D + D + lowBound > distCon) {
						continue;
					}

					Score score = new Score(allCandWords, queW, si.D + D + lowBound, distCon);
					double maxRankScore = score.calRankScore();

					if (maxRankScore < kBound) {
						continue;
					}

					sj = update1_A(vj, djs, D, si, allCandWords, distCon);

					if (vj == ve) {
						double Df = doorj.eDist(ePoint);
						Stamp sf = update1(vj, -1, Df, sj, allCandWords, distCon);

						if (sf.D <= distCon && !FQ_PI.exists(sf) && sf.rankScore >= kBound
								&& caldominate(-1, sf.Pc, sf.D, sf.Rp)) {
							// System.out.println("insert " + sf.Rp + " " + sf.rankScore);
							FQ_PI.insert(sf);
							kbounds.put(sf.Pc.toString(), sf.rankScore);
							kBound = getKBound(kbounds, k);

						}

					} else if (caldominate(djs.get(djs.size() - 1), sj.Pc, sj.D, sj.Rp)) {

						if (sj.relScore < maxRelScore) {

							if (!Q.exists(sj)) {
								// System.out.println("insert Q, " + sj.v + " " + sj.Rp + " " + sj.rankScore);
								Q.insert(sj);
							}
						} else {
							double D4 = Constant.large;
							double D5 = Constant.large;
							double minDist = Constant.large;
							ArrayList<Integer> enterEndDoors = new ArrayList<>();
							enterEndDoors = IndoorSpace.iPartitions.get(ve).getConnectivityTier().getP2DEnter();
							ArrayList<Integer> leaveLastDoors = new ArrayList<>();
							leaveLastDoors = IndoorSpace.iPartitions.get(vj).getConnectivityTier().getP2DLeave();

							calDistofDoorPar(dj, ve, Dist, Route, doorParDist, doorParRoute, distCon - sj.D, sloc,
									sj.Rp);

							for (int y = 0; y < enterEndDoors.size(); y++) {
								int enterEndDoorId = enterEndDoors.get(y);
								Door enterEndDoor = IndoorSpace.iDoors.get(enterEndDoorId);
								String routeEndTemp;
								String routeEnd = "";
								if (enterEndDoorId == dj) {
									D4 = 0;
									routeEndTemp = Integer.toString(enterEndDoorId);
								} else {
									D4 = Dist.get(dj + "-" + enterEndDoorId);
									routeEndTemp = Route.get(dj + "-" + enterEndDoorId);
								}
								String[] routeEndArr = routeEndTemp.split("\t");

								if (routeEndArr.length >= 2
										&& leaveLastDoors.contains(Integer.parseInt(routeEndArr[1]))) {
									routeEnd = Functions.arrayToString1D(routeEndArr, 1, routeEndArr.length);
								} else if (leaveLastDoors.contains(dj)) {
									routeEnd = routeEndTemp;
								} else {
									double minEndDist = Constant.large;
									for (int f = 0; f < leaveLastDoors.size(); f++) {
										int leaveLastDoorId = leaveLastDoors.get(f);
										Door leaveLastDoor = IndoorSpace.iDoors.get(leaveLastDoorId);
										double D31;
										D31 = doorj.eDist(leaveLastDoor);
										String str1 = d2dDist(leaveLastDoorId, enterEndDoorId, sj.Rp);
										String[] str1Arr = str1.split("\t");
										double D32 = Double.parseDouble(str1Arr[0]);
										if (D31 + D32 < minEndDist) {
											minEndDist = D31 + D32;
											routeEnd = Functions.arrayToString1D(str1Arr, 1, str1Arr.length);
										}
										D4 = minEndDist;

									}
								}

								D5 = enterEndDoor.eDist(ePoint);
								Stamp sf = update2(ve, routeEnd + "\t" + "-1", (D4 + D5), sj, allCandWords, distCon);

								// System.out.println("Rp:" + sf.Rp + " D: " + sf.D + " rankScore: " +
								// sf.rankScore + " kbound: " + kBound);

								if (sf.D <= distCon && !FQ_PI.exists(sf) && sf.rankScore >= kBound
										&& caldominate(-1, sf.Pc, sf.D, sf.Rp)) {
									// System.out.println("insert");
									FQ_PI.insert(sf);
									kbounds.put(sf.Pc.toString(), sf.rankScore);
									kBound = getKBound(kbounds, k);
								}

							}

						}
					}
				}
			}
		}

		System.out.println("FQ_PI,heapSize: " + FQ_PI.heapSize);
		if (FQ_PI.heapSize != 0) {
			// System.out.println("have route " + FQ_PI.heapSize);
			result = "";
			int resultNum = 0;
			while (FQ_PI.heapSize != 0 && resultNum < k) {
				Stamp sf = FQ_PI.delete_min();
				// System.out.println(resultNum + " " + sf.Rp + " " + sf.Pc + " " + sf.rankScore
				// + " " + sf.D);
				if (sf.D > getdominate(-1, sf.Pc))
					continue;
				result += Functions.array2String(sf.Rp) + "\t" + Functions.array2String(sf.Pc) + "\t"
						+ Functions.array2String(sf.Wc) + "\t" + sf.D + "\t" + sf.relScore + "\t" + sf.rankScore + "\t";

				String floorNum = f_vs + ",";
				for (int i = 1; i < sf.Rp.size() - 1; i++) {
					floorNum += IndoorSpace.iDoors.get(sf.Rp.get(i)).getmFloor() + ",";
				}
				floorNum += f_ve + ";";

				result += floorNum;

				resultNum++;
			}
		}

		return result;
	}

	// KOE
	public static String ptpWordDist2(String sloc, String eloc, double distCon, ArrayList<Integer> queW, int k)
			throws IOException {
		String result = "no such a route";
		int maxRelScore = queW.size() + 1;
		double kBound = 0;
		dominate = new HashMap<>();
		kbounds = new HashMap<String, Double>();

		// word
		CandidateWords candWords = new CandidateWords(queW, threshold);
		ArrayList<ArrayList<double[]>> allCandWords = candWords.findAllCandWords();

		// starting
		Point sPoint = locPoint(sloc);
		Partition sPartition = locPartition(sloc);
		int vs = sPartition.getmID();
		int f_vs = sPartition.getmFloor();
		int sid = -2;

		// ending
		Point ePoint = locPoint(eloc);
		Partition ePartition = locPartition(eloc);
		int ve = ePartition.getmID();
		int f_ve = ePartition.getmFloor();
		int eid = -1;

		ArrayList<Integer> candPartition = new ArrayList<>();
		ArrayList<Integer> nonCandPartition = new ArrayList<>();
		// partitions with words
		ArrayList<ArrayList<Integer>> P = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> P_PI = new ArrayList<Integer>();

		// System.out.print("allCandWords: ");
		for (int i = 0; i < allCandWords.size(); i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();

			for (int j = 0; j < allCandWords.get(i).size(); j++) {
				// System.out.print((int)allCandWords.get(i).get(j)[0] + " ");
				if (dictionary.getIdenPartition((int) allCandWords.get(i).get(j)[0]) == null) {
					// System.out.println("candidate word " + (int)allCandWords.get(i).get(j)[0] +
					// "have no partition");
				} else
					temp.addAll(Dictionary.getIdenPartition((int) allCandWords.get(i).get(j)[0]));
			}

			// remove start partition and end partiton;
			if (temp.contains(vs)) {
				int index = temp.indexOf(vs);
				temp.remove(index);
			}
			if (temp.contains(ve)) {
				int index = temp.indexOf(ve);
				temp.remove(index);
			}

			P.add(temp);
		}

		// if lowerbound between start partition and end partition is greater than
		// distCon, return result;
		double lowDist = calLowerBoundofPars(vs, ve);
		if (lowDist >= distCon)
			return result;

		// get all candidate partitions
		for (int i = 0; i < P.size(); i++) {
			P_PI.addAll(P.get(i));
		}

		// add end partition to P_PI;
		P_PI.add(ve);

		// initialize the piority queues
		MinHeap<Stamp> Q = new MinHeap<>("rankingscore");
		MinHeap<Stamp> FQ_PI = new MinHeap<>("rankingscore");

		// create a Stamp
		Stamp s0 = new Stamp(vs, new ArrayList<Integer>());
		s0.Rp.add(sid);
		s0.P.add(vs);
		s0.Wr.add(sPartition.getIkeyword());
		Score s0Score = new Score(allCandWords, s0.Wr, 0, distCon);
		s0Score.initPrevious();
		double relScore = s0Score.calRelScore();
		s0.setRelScore(relScore);
		s0.setWc(s0Score.getWc());
		s0.setScore(s0Score);
		for (int i = 0; i < s0.Wc.size(); i++) {
			if (s0.Wc.get(i) == 0) {
				s0.Pc.add(-1);
			} else {
				s0.Pc.add(vs);
			}
		}
		s0.setD(0);
		s0.setRankScore(0);

		Q.insert(s0);

		int h = 0;
		while (Q.heapSize > 0) {

			HashMap<String, Double> Dist = new HashMap<String, Double>(); // the distance between two doors
			HashMap<String, String> Route = new HashMap<String, String>(); // the route between two doors
			HashMap<String, Double> doorParDist = new HashMap<>();
			HashMap<String, String> doorParRoute = new HashMap<>();

			// h += 1;
			// dequeue a stamp
			Stamp si = Q.delete_min();

			// System.out.print("current partition:" + si.v + "\t");
			// System.out.print("current route:");
			for (int i = 0; i < si.Rp.size(); i++) {
				System.out.print(si.Rp.get(i) + "  ");
			}
			// System.out.println(" current route distance: " + si.D);

			// get the current partition of si
			int vi = si.v;

			ArrayList<Integer> Wci = si.Wc;
			ArrayList<Integer> index = new ArrayList<>();

			for (int i = 0; i < Wci.size(); i++) {
				if (Wci.get(i) == 0) {
					index.add(i);
				}
			}

			ArrayList<Integer> doors = new ArrayList<Integer>();
			doors = IndoorSpace.iPartitions.get(vi).getConnectivityTier().getP2DLeave();
			int currentPoint = si.Rp.get(si.Rp.size() - 1);

			// System.out.println("leaveable doors: " + doors);

			if (si.D > getdominate(currentPoint, si.Pc)) {
				continue;
			}

			Door doori = null;

			if (currentPoint != -2 && currentPoint != -1) {
				doori = IndoorSpace.iDoors.get(currentPoint);
			}

			ArrayList<Integer> candPar = new ArrayList<>();
			if (currentPoint == -2) {
				candPar.addAll(P_PI);
			} else {
				for (int i = 0; i < index.size(); i++) {
					candPar.addAll(P.get(index.get(i)));
				}
				candPar.add(ve);
			}

			// System.out.println("candidate partitions: " + candPar);
			// start expanding
			for (int i = 0; i < candPar.size(); i++) {

				int vj = candPar.get(i);
				// System.out.print("\tfor vi: v" + vi + " to vj: v" + vj + " ");
				double dist11 = -99999;

				if (nonCandPartition.contains(vj))
					continue;
				else if (candPartition.contains(vj)) {

					if (currentPoint == -2) {
						dist11 = lowerBound(sPoint, vj, ePoint);
					} else {
						dist11 = lowerBound(doori, vj, ePoint);
					}
					if (dist11 > distCon - si.D) {
						continue;
					}
				} else {
					double dist13;

					if (currentPoint == -2) {
						dist11 = lowerBound(sPoint, vj, ePoint);
						dist13 = dist11;
					} else {
						dist11 = lowerBound(doori, vj, ePoint);
						dist13 = lowerBound(sPoint, vj, ePoint);
					}

					if (dist13 > distCon) {
						nonCandPartition.add(vj);
						continue;
					} else {
						candPartition.add(vj);
					}

					if (dist11 > distCon - si.D) {
						continue;
					}
				}

				ArrayList<Integer> enter = new ArrayList<Integer>();
				enter = IndoorSpace.iPartitions.get(vj).getConnectivityTier().getP2DEnter();

				// System.out.println("\tvj: v" + vj + " enter from doors: " + enter);

				calDistofDoorPar(currentPoint, vj, Dist, Route, doorParDist, doorParRoute, distCon - si.D, sloc, si.Rp);

				// try {
				// TimeUnit.SECONDS.sleep(20);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				for (int l = 0; l < enter.size(); l++) {
					int enterDoorId = enter.get(l);
					Door enterDoor = IndoorSpace.iDoors.get(enterDoorId);
					double D2 = Constant.large;
					String routeTemp = "";
					String route = "";

					// System.out.println("\t\t from d" + currentPoint + " to d" + enterDoorId);

					if (currentPoint == enterDoorId) {
						D2 = 0;
						routeTemp = Integer.toString(enterDoorId);
					} else {
						D2 = Dist.get(currentPoint + "-" + enterDoorId);
						routeTemp = Route.get(currentPoint + "-" + enterDoorId);
					}

					// System.out.println("routeTemp: " + routeTemp + " D: " + D2);

					String[] routeArr = routeTemp.split("\t");

					if (currentPoint == -2) {
						route = Functions.arrayToString1D(routeArr, 1, routeArr.length);
					} else if (routeArr.length >= 2 && doors.contains(Integer.parseInt(routeArr[1]))) {
						route = Functions.arrayToString1D(routeArr, 1, routeArr.length);
					} else if (doors.contains(currentPoint)) {
						route = routeTemp;
					} else {
						double minDist = Constant.large;

						for (int f = 0; f < doors.size(); f++) {
							int leaveDoorId = doors.get(f);
							Door leaveDoor = IndoorSpace.iDoors.get(leaveDoorId);
							double D11;

							if (currentPoint == -2) {
								D11 = sPoint.eDist(leaveDoor);
							} else {
								D11 = doori.eDist(leaveDoor);
							}

							String str1 = d2dDist(leaveDoorId, enterDoorId, si.Rp);
							String[] str1Arr = str1.split("\t");
							double D12 = Double.parseDouble(str1Arr[0]);

							if (D11 + D12 < minDist) {
								minDist = D11 + D12;
								route = Functions.arrayToString1D(str1Arr, 1, str1Arr.length);
							}
							D2 = minDist;
						}
					}

					if ((si.D + D2) <= distCon) {
						double lowBound = lowerBound(enterDoor, ePoint);
						if (si.D + D2 + lowBound > distCon)
							continue;

						Score score = new Score(allCandWords, queW, si.D + D2 + lowBound, distCon);
						double maxRankScore = score.calRankScore();
						if (maxRankScore <= kBound)
							continue;
						Stamp sj = update2(vj, route, D2, si, allCandWords, distCon);

						// System.out.println("get r: " + sj.Rp);

						if (vj == ve) {
							// System.out.println("true");
							int lastDoorId = Integer.parseInt(routeArr[routeArr.length - 1]);
							Door lastDoor = IndoorSpace.iDoors.get(lastDoorId);
							double Df = lastDoor.eDist(ePoint);
							Stamp sf = update2(vj, "-1", Df, sj, allCandWords, distCon);

							if (sf.D <= distCon && !FQ_PI.exists(sf) && sf.rankScore >= kBound
									&& caldominate(-1, sf.Pc, sf.D, sf.Rp)) {
								// System.out.println("insert into fq_pi, sf.rankScore: " + sf.rankScore);
								FQ_PI.insert(sf);
								kbounds.put(sf.Pc.toString(), sf.rankScore);
								kBound = getKBound(kbounds, k);
							} else {
//								System.out.println("kill " + sf.D + " " + !FQ_PI.exists(sf) + " sf.rankScore: "
//										+ sf.rankScore + " kBound: " + kBound);
							}
						} else {
//							System.out.println("false");
							if (sj.relScore < maxRelScore) {
								if (!Q.exists(sj) && caldominate(enter.get(l), sj.Pc, sj.D, sj.Rp)) {
//									System.out.println("insert");
									Q.insert(sj);
								}
							} else {
//								System.out.println("not insert");
								double D4 = Constant.large;
								double D5 = Constant.large;
								ArrayList<Integer> enterEndDoors = new ArrayList<>();
								enterEndDoors = IndoorSpace.iPartitions.get(ve).getConnectivityTier().getP2DEnter();
								ArrayList<Integer> leaveLastDoors = new ArrayList<>();
								leaveLastDoors = IndoorSpace.iPartitions.get(vj).getConnectivityTier().getP2DLeave();

								// System.out.println("\t\t enterEndDoors: " + enterEndDoors + " leaveLastDoors:
								// " + leaveLastDoors + " ve: " + ve + " vj: " + vj);

								calDistofDoorPar(enterDoorId, ve, Dist, Route, doorParDist, doorParRoute,
										distCon - sj.D, sloc, sj.Rp);

								for (int y = 0; y < enterEndDoors.size(); y++) {
									int enterEndDoorId = enterEndDoors.get(y);
									Door enterEndDoor = IndoorSpace.iDoors.get(enterEndDoorId);
									String routeEndTemp;
									String routeEnd = "";

									if (enterEndDoorId == enterDoorId) {
										D4 = 0;
										routeEndTemp = Integer.toString(enterEndDoorId);
									} else {
										D4 = Dist.get(enterDoorId + "-" + enterEndDoorId);
										routeEndTemp = Route.get(enterDoorId + "-" + enterEndDoorId);
									}

									String[] routeEndArr = routeEndTemp.split("\t");

									// System.out.println("\t\t\t routeEndTemp: " + routeEndTemp);

									if (routeEndArr.length >= 2
											&& leaveLastDoors.contains(Integer.parseInt(routeEndArr[1]))) {
										routeEnd = Functions.arrayToString1D(routeEndArr, 1, routeEndArr.length);
									} else if (leaveLastDoors.contains(enterDoorId)) {
										routeEnd = routeEndTemp;
									} else {
										double minEndDist = Constant.large;

										for (int f = 0; f < leaveLastDoors.size(); f++) {
											int leaveLastDoorId = leaveLastDoors.get(f);
											Door leaveLastDoor = IndoorSpace.iDoors.get(leaveLastDoorId);
											double D31;
											D31 = enterDoor.eDist(leaveLastDoor);
											String str1 = d2dDist(leaveLastDoorId, enterEndDoorId, sj.Rp);
											String[] str1Arr = str1.split("\t");
											double D32 = Double.parseDouble(str1Arr[0]);

											if (D31 + D32 < minEndDist) {
												minEndDist = D31 + D32;
												routeEnd = Functions.arrayToString1D(str1Arr, 1, str1Arr.length);
											}

											D4 = minEndDist;
										}
									}

									D5 = enterEndDoor.eDist(ePoint);
									Stamp sf = update2(ve, routeEnd + "\t" + "-1", (D4 + D5), sj, allCandWords,
											distCon);

									// System.out.println("\t\t\t route: " + sf.Rp + " D: " + sf.D + " rankScore: "
									// + sf.rankScore + " kbound: " + kBound);

									if (sf.D <= distCon && !FQ_PI.exists(sf) && sf.rankScore >= kBound
											&& caldominate(-1, sf.Pc, sf.D, sf.Rp)) {
//										System.out.println("\t\t\t insert");
										FQ_PI.insert(sf);
										kbounds.put(sf.Pc.toString(), sf.rankScore);
										kBound = getKBound(kbounds, k);
									}
								}
							}
						}
					}
				}
			}
		}

//		System.out.println("FQ_PI,heapSize: " + FQ_PI.heapSize);

		if (FQ_PI.heapSize != 0) {
			result = "";
			int resultNum = 0;

			while (FQ_PI.heapSize != 0 && resultNum < k) {
				Stamp sf = FQ_PI.delete_min();
//				System.out.println(resultNum + " " + sf.Rp + " " + sf.Pc + " " + sf.rankScore + " " + sf.D);
				if (sf.D > getdominate(-1, sf.Pc))
					continue;
				result += Functions.array2String(sf.Rp) + "\t" + Functions.array2String(sf.Pc) + "\t"
						+ Functions.array2String(sf.Wc) + "\t" + sf.D + "\t" + sf.relScore + "\t" + sf.rankScore + "\t";

				String floorNum = f_vs + ",";
				for (int i = 1; i < sf.Rp.size() - 1; i++) {
					floorNum += IndoorSpace.iDoors.get(sf.Rp.get(i)).getmFloor() + ",";
				}
				floorNum += f_ve + ";";

				result += floorNum;

				resultNum++;
			}
		}

		return result;
	}

	
	/**
	 * calculate the indoor distance between two doors
	 *
	 * @param ds
	 *            door id ds
	 * @param de
	 *            door id de
	 * @return distance
	 */
	public static String d2dDist(int ds, int de) {
		// System.out.println("ds = " + IndoorSpace.iDoors.get(ds).toString() + " de = "
		// + IndoorSpace.iDoors.get(de).toString());
		String result = "";

		if (ds == de)
			return 0 + "\t";

		int size = IndoorSpace.iDoors.size();
		BinaryHeap<Double> H = new BinaryHeap<Double>(size);
		double[] dist = new double[size]; // stores the current shortest path distance
		// from source ds to a door de
		PrevPair[] prev = new PrevPair[size]; // stores the corresponding previous partition
		// and door pair (v,di) through which the algorithm
		// visits the current door de.
		boolean[] visited = new boolean[size]; // mark door as visited

		for (int i = 0; i < size; i++) {
			int doorID = IndoorSpace.iDoors.get(i).getmID();
			if (doorID != i)
				System.out.println("something wrong_Helper_d2dDist");
			if (doorID != ds)
				dist[i] = Constant.large;
			else
				dist[i] = 0;

			// enheap
			H.insert(dist[i], doorID);

			PrevPair pair = null;
			prev[doorID] = pair;
		}

		while (H.heapSize > 0) {
			String[] str = H.delete_min().split(",");
			int di = Integer.parseInt(str[1]);
			double dist_di = Double.parseDouble(str[0]);

			// System.out.println("dequeue <" + di + ", " + dist_di + ">");

			if (di == de) {
				// System.out.println("d2dDist_ di = " + di + " de = " + de);
				result += getPath(prev, ds, de);
				return result = dist_di + "\t" + result;
			}

			visited[di] = true;
			// System.out.println("d" + di + " is newly visited");

			Door door = IndoorSpace.iDoors.get(di);
			ArrayList<Integer> parts = new ArrayList<Integer>(); // list of leavable partitions
			parts = door.getD2PLeave();

			int partSize = parts.size();

			for (int i = 0; i < partSize; i++) {
				ArrayList<Integer> doorTemp = new ArrayList<Integer>();
				int v = parts.get(i); // partition id
				Partition partition = IndoorSpace.iPartitions.get(v);
				doorTemp = partition.getConnectivityTier().getP2DLeave();

				// remove the visited doors
				ArrayList<Integer> doors = new ArrayList<Integer>(); // list of unvisited leavable doors
				int doorTempSize = doorTemp.size();
				for (int j = 0; j < doorTempSize; j++) {
					int index = doorTemp.get(j);
					// System.out.println("index = " + index + " " + !visited[index]);
					if (!visited[index]) {
						doors.add(index);
					}
				}

				int doorSize = doors.size();
				// System.out.println("doorSize = " + doorSize + ": " +
				// Functions.printIntegerList(doors));

				for (int j = 0; j < doorSize; j++) {
					int dj = doors.get(j);
					if (visited[dj])
						System.out.println("something wrong_Helper_d2dDist2");
					// System.out.println("for d" + di + " and d" + dj);

					double fd2d = partition.getdistMatrix().getDistance(di, dj);
					;
					if (fd2d == -1) {
						int fid1 = IndoorSpace.iPartitions.get(IndoorSpace.iDoors.get(di).getmPartitions().get(0))
								.getmFloor();
						int fid2 = IndoorSpace.iPartitions.get(IndoorSpace.iDoors.get(dj).getmPartitions().get(0))
								.getmFloor();
						fd2d = DataGenConstant.lenStairway * (Math.abs(fid1 - fid2));
						// System.out.println("fid1 = " + fid1 + " fid2 = " + fid2 + " fd2d = " + fd2d);
					}

					if ((dist[di] + fd2d) < dist[dj]) {
						double oldDj = dist[dj];
						dist[dj] = dist[di] + fd2d;
						H.updateNode(oldDj, dj, dist[dj], dj);
						prev[dj] = new PrevPair(v, di);
						prev[dj].toString();
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param prev
	 * @return a string path
	 */
	public static String getPath(PrevPair[] prev, int ds, int de) {
		String result = de + "";

		// System.out.println("ds = " + ds + " de = " + de + " " + prev[de].par + " " +
		// prev[de].door);
		int currp = prev[de].par;
		int currd = prev[de].door;

		while (currd != ds) {
			result = currd + "\t" + result;
			// System.out.println("current: " + currp + ", " + currd + " next: " +
			// prev[currd].toString());
			currp = prev[currd].par;
			currd = prev[currd].door;

		}

		result = currd + "\t" + result;

		return result;
	}

	/**
	 * calculate the distance between two floors
	 *
	 * @param f1
	 *            floor id
	 * @param f2
	 *            floor id
	 * @return distance
	 */
	public static double f2fDist(int f1, int f2) {
		return Math.abs(f1 - f2) * DataGenConstant.lenStairway;
	}

	/**
	 * split the string by comma
	 * 
	 * @param str
	 *            a string of keywords seperated by comma
	 * @return int list that store these id of these keywords
	 */
	public static ArrayList<Integer> splitComma(String str) {
		ArrayList<Integer> result = new ArrayList<Integer>();

		String[] tempArr = str.split(",");

		int tempArrSize = tempArr.length;
		for (int i = 0; i < tempArrSize; i++) {
			int temp = Dictionary.getWordID(tempArr[i].toLowerCase());
			if (temp == -1)
				continue;
			else
				result.add(temp);
		}

		return result;
	}

	/**
	 * convert a string array to int array
	 *
	 * @param str
	 *            a string array
	 * @return a int array
	 */
	public static int[] strArrToIntArr(String[] str) {
		int[] result = new int[str.length];

		int strSize = str.length;
		for (int i = 0; i < strSize; i++) {
			result[i] = Integer.parseInt(str[i]);
		}

		return result;
	}

	/**
	 * locate a partition according to location string
	 *
	 * @param str
	 *            a string
	 * @return partition
	 */
	public static Partition locPartition(String str) {
		int[] sLoc = strArrToIntArr(str.split(","));
		// System.out.println(sLoc[0] + " " + sLoc[1] + " " + sLoc[2]);
		return upperTree.get(sLoc[0]).getLowerTree().search(Geometries.point(sLoc[1], sLoc[2])).get(0).getpartition();
	}

	/**
	 * locate a point according to location string
	 *
	 * @param str
	 *            a string
	 * @return point
	 */
	public static indoor_entitity.Point locPoint(String str) {
		int[] sLoc = strArrToIntArr(str.split(","));
		return new indoor_entitity.Point(sLoc[1], sLoc[2], sLoc[0]);
	}

	/**
	 * locate a list of partitions according to keyowrd string
	 *
	 * @param str
	 *            a string
	 * @return a list of partitions
	 */
	public static ArrayList<Partition> keywordPartition(String str) {
		ArrayList<Integer> words = new ArrayList<Integer>();
		ArrayList<Partition> partitions = new ArrayList<Partition>();

		words = splitComma(str);

		if (words.size() <= 0) {
			System.out.println("no partition contains any of you keywords");
			return null;
		} else {
			int wordsSize = words.size();
			for (int i = 0; i < wordsSize; i++) {
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp = Dictionary.getPartition(words.get(i));

				int tempSizw = temp.size();
				for (int j = 0; j < tempSizw; j++) {
					partitions.add(IndoorSpace.iPartitions.get(j));
				}

//				System.out.println(
//						"you input " + words.get(i) + " it has keywords " + "" + Functions.printIntegerList(temp));
			}
		}

		return partitions;
	}


	/**
	 * @param vj
	 *            partition
	 * @param dj
	 *            new door
	 * @param D
	 *            new partial distance
	 * @param si
	 *            stamp
	 * @param allCandWords
	 *            all candidate keywords
	 * @param distCon
	 *            distance constrain
	 * @return
	 */
	private Stamp update1(int vj, int dj, double D, Stamp si, ArrayList<ArrayList<double[]>> allCandWords,
			double distCon) {
		Stamp s = new Stamp(si);

		s.v = vj;

		// update the Rp
		s.Rp.add(dj);

		// update distance
		s.D += D;

		int new_Wr = -99999;

		if (dj != -1) {
			new_Wr = IndoorSpace.iPartitions.get(vj).getIkeyword();
			s.Wr.add(new_Wr);
			s.P.add(vj);
		}

		Score sScore = new Score(si.score, new_Wr, s.D);
		double relScore = sScore.calRelScore();
		s.relScore = relScore;
		s.setScore(sScore);

		s.Wc = sScore.getWc();

		s.Pc = new ArrayList<Integer>();

		for (int i = 0; i < s.Wc.size(); i++) {
			if (s.Wc.get(i) == 0) {
				s.Pc.add(-1);
			} else {
				int index = s.Wr.indexOf(s.Wc.get(i));
				s.Pc.add(s.P.get(index));
			}
		}

		s.rankScore = sScore.calRankScore();

		return s;
	}

	private Stamp update1_A(int vj, ArrayList<Integer> djs, double D, Stamp si,
			ArrayList<ArrayList<double[]>> allCandWords, double distCon) {
		Stamp s = new Stamp(si);

		s.v = vj;

		// update the Rp
		s.Rp.addAll(djs);

		// update distance
		s.D += D;

		int new_Wr = -99999;

		if (!(djs.size() == 1 && djs.get(0) == -1)) {
			s.Wr.add(IndoorSpace.iPartitions.get(vj).getIkeyword());
			s.P.add(vj);

			new_Wr = IndoorSpace.iPartitions.get(vj).getIkeyword();
		}

		Score sScore = new Score(si.score, new_Wr, s.D);
		double relScore = sScore.calRelScore();
		s.relScore = relScore;
		s.setScore(sScore);

		s.Wc = sScore.getWc();

		s.Pc = new ArrayList<Integer>();

		for (int i = 0; i < s.Wc.size(); i++) {
			if (s.Wc.get(i) == 0) {
				s.Pc.add(-1);
			} else {
				int index = s.Wr.indexOf(s.Wc.get(i));
				s.Pc.add(s.P.get(index));
			}
		}

		s.rankScore = sScore.calRankScore();

		return s;
	}

	/**
	 * @param vj
	 *            partition
	 * @param route
	 *            new partial route
	 * @param D
	 *            new partial distance
	 * @param si
	 *            old stamp
	 * @param allCandWords
	 *            all candidate keywords list
	 * @param distCon
	 *            distance constrain
	 * @return
	 */
	private static Stamp update2(int vj, String route, double D, Stamp si, ArrayList<ArrayList<double[]>> allCandWords,
			double distCon) {
		Stamp s = new Stamp(si);

		// update the previous partition
		s.setV(vj);

		// update the Rp
		ArrayList<Integer> newRp = new ArrayList<>();

		if (!route.equals("")) {
			String[] doors = route.split("\t");
			int doorSize = doors.length;

			for (int i = 0; i < doorSize; i++) {
				if (doors[i].equals(""))
					continue;
				s.Rp.add(Integer.parseInt(doors[i]));
				newRp.add(Integer.parseInt(doors[i]));
			}
		}

		// update distance
		s.D += D;

		// find route partition
		ArrayList<Integer> parts = new ArrayList<>(); // all partition in route Rp, except the first partiton;

		for (int i = 0; i < newRp.size() - 1; i++) {
			if (newRp.get(i + 1) == -1)
				continue;
			List<Integer> part1 = IndoorSpace.iDoors.get(newRp.get(i)).getmPartitions();
			List<Integer> part2 = IndoorSpace.iDoors.get(newRp.get(i + 1)).getmPartitions();
			boolean found = false;

			for (int a = 0; a < part1.size(); a++) {
				for (int b = 0; b < part2.size(); b++) {
					if ((int) part1.get(a) == (int) part2.get(b)) {
						if (!parts.contains(part1.get(a)))
							parts.add(part1.get(a));
						found = true;
						break;
					}
				}
			}
		}

		ArrayList<Integer> new_Wr = new ArrayList<Integer>();

		// update route keywords
		for (int i = 0; i < parts.size(); i++) {
			int word = IndoorSpace.iPartitions.get(parts.get(i)).getIkeyword();
			s.Wr.add(word);
			new_Wr.add(word);
			s.P.add(parts.get(i));
		}

		if (!route.equals("-1")) {
			int word = IndoorSpace.iPartitions.get(vj).getIkeyword();
			s.Wr.add(word);
			new_Wr.add(word);
			s.P.add(vj);
		}

		Score sScore = new Score(si.score, new_Wr, s.D);
		double relScore = sScore.calRelScore();
		s.relScore = relScore;
		s.setScore(sScore);

		s.Wc = sScore.getWc();
		s.Pc = new ArrayList<>();

		for (int i = 0; i < s.Wc.size(); i++) {
			if (s.Wc.get(i) == 0) {
				s.Pc.add(-1);
			} else {
				int index = s.Wr.indexOf(s.Wc.get(i));
				s.Pc.add(s.P.get(index));
			}
		}

		s.rankScore = sScore.calRankScore();

		return s;
	}

	public static double calLowerBoundofPars(int v1ID, int v2ID) {
		double minDist = Constant.large;
		ArrayList<Integer> leave = new ArrayList<Integer>();
		ArrayList<Integer> enter = new ArrayList<Integer>();
		leave = IndoorSpace.iPartitions.get(v1ID).getConnectivityTier().getP2DLeave();
		enter = IndoorSpace.iPartitions.get(v2ID).getConnectivityTier().getP2DEnter();
		for (int a = 0; a < leave.size(); a++) {
			int d1 = leave.get(a);
			for (int b = 0; b < enter.size(); b++) {
				int d2 = enter.get(b);
				double result = lowerBound(IndoorSpace.iDoors.get(d1), IndoorSpace.iDoors.get(d2));
				if (result < minDist)
					minDist = result;
			}
		}
		return minDist;
	}

	public static void calDistofDoors(int v1ID, int v2ID, HashMap<String, Double> Dist, HashMap<String, String> Route,
			HashMap<String, Double> parDist, HashMap<String, String> parRoute, double distCon) {
		double minParDist = Constant.large;
		String minParRoute = "";
		Double minDist = Constant.large;
		ArrayList<Integer> leave = new ArrayList<Integer>();
		ArrayList<Integer> enter = new ArrayList<Integer>();
		leave = IndoorSpace.iPartitions.get(v1ID).getConnectivityTier().getP2DLeave();
		enter = IndoorSpace.iPartitions.get(v2ID).getConnectivityTier().getP2DEnter();
		for (int a = 0; a < leave.size(); a++) {
			int d1 = leave.get(a);
			for (int b = 0; b < enter.size(); b++) {
				int d2 = enter.get(b);
				if (lowerBound(IndoorSpace.iDoors.get(d1), IndoorSpace.iDoors.get(d2)) > distCon) {
					Dist.put(d1 + "-" + d2, Constant.large);
					Route.put(d1 + "-" + d2, "");
					continue;
				}
				String str = d2dDist(d1, d2);
				String[] strs = str.split("\t");
				// update minDist
				minDist = Double.parseDouble(strs[0]);
				if (minDist <= distCon) {
					String minRoute = Functions.arrayToString1D(strs, 1, strs.length);
					// System.out.println("min distance between d" + d1 + " and d" + d2 + " = " +
					// minDist + " route = "
					// + "" + minRoute + " reverse route = " + Functions.reverse(minRoute));
					Dist.put(d1 + "-" + d2, minDist);
					Route.put(d1 + "-" + d2, minRoute);
					if (minDist < minParDist) {
						minParDist = minDist;
						minParRoute = minRoute;

					}
				} else {
					Dist.put(d1 + "-" + d2, Constant.large);
					Route.put(d1 + "-" + d2, "");
				}
			}
		}
		parDist.put(v1ID + "-" + v2ID, minParDist);
		parRoute.put(v1ID + "-" + v2ID, minParRoute);

	}

	public static void calDistofDoorPar(int diID, int v2ID, HashMap<String, Double> Dist, HashMap<String, String> Route,
			HashMap<String, Double> parDist, HashMap<String, String> parRoute, double distCon, String sloc,
			ArrayList<Integer> Rp) {
		double minParDist = Constant.large;
		String minParRoute = "";
		Double minDist = Constant.large;
		ArrayList<Integer> enter = new ArrayList<Integer>();
		ArrayList<String> results = new ArrayList<>();
		enter = IndoorSpace.iPartitions.get(v2ID).getConnectivityTier().getP2DEnter();

		int d1 = diID;

//		System.out.println("d1: d" + d1);

		// if (d1 == -2) {
		//
		// Point sPoint = locPoint(sloc);
		// Partition sPartition = locPartition(sloc);
		// int vs = sPartition.getmID();
		// ArrayList<Integer> doors = new ArrayList<Integer>();
		// doors = sPartition.getConnectivityTier().getP2DLeave();
		//
		// for (int i = 0; i < doors.size(); i ++) {
		//
		// double toDoor = sPoint.eDist(IndoorSpace.iDoors.get(doors.get(i)));
		// System.out.println("toDoor: " + toDoor);
		//
		// for (int j = 0; j < enter.size(); j ++) {
		// System.out.print("between d" + doors.get(i) + " and d" + enter.get(j) +
		// "\t");
		// String str = d2dDist(doors.get(i), enter.get(j), Rp);
		// String[] strs = str.split("\t");
		// double d = Double.parseDouble(strs[0]) + toDoor;
		// String r = Functions.arrayToString1D(strs, 1, strs.length);
		// results.add(d + "\t" + "-2" + "\t" + r);
		// System.out.println("D: " + d + " r: " + r + " new r: " + "-2" + "\t" + r);
		// }
		// }
		//
		// } else {
		// for (int i = 0; i < enter.size(); i ++) {
		// System.out.println("between d" + d1 + " and d" + enter.get(i));
		// String str = d2dDist(d1, enter.get(i), Rp);
		// results.add(str);
		// }
		// }

		results = d2dsDist(d1, enter, sloc, Rp);

		int resultsSize = results.size();
		for (int i = 0; i < resultsSize; i++) {
			String str = results.get(i);
//			System.out.println(str);
			String[] strs = str.split("\t");
			minDist = Double.parseDouble(strs[0]);
			int d2 = Integer.parseInt(strs[strs.length - 1]);

			if (minDist <= distCon) {
				String minRoute = Functions.arrayToString1D(strs, 1, strs.length);

				if (Dist.containsKey(d1 + "-" + d2)) {
					double temp = Dist.get(d1 + "-" + d2);
					if (temp > minDist) {
						Dist.put(d1 + "-" + d2, minDist);
						Route.put(d1 + "-" + d2, minRoute);

						if (minDist < minParDist) {
							minParDist = minDist;
							minParRoute = minRoute;
						}
					}
				} else {
					Dist.put(d1 + "-" + d2, minDist);
					Route.put(d1 + "-" + d2, minRoute);

					if (minDist < minParDist) {
						minParDist = minDist;
						minParRoute = minRoute;
					}
				}
			} else {
				Dist.put(d1 + "-" + d2, Constant.large);
				Route.put(d1 + "-" + d2, "");
			}
		}

//		System.out.println("put r: " + minParRoute + " d: " + minParDist);

		parDist.put(diID + "-" + v2ID, minParDist);
		parRoute.put(diID + "-" + v2ID, minParRoute);
	}

	public static double calDistofPars(int v1ID, int v2ID, HashMap<String, Double> parDist,
			HashMap<String, String> parRoute, double distCon) {
		double minParDist = Constant.large;
		String minParRoute = "";
		ArrayList<Integer> leave = new ArrayList<Integer>();
		ArrayList<Integer> enter = new ArrayList<Integer>();
		leave = IndoorSpace.iPartitions.get(v1ID).getConnectivityTier().getP2DLeave();
		enter = IndoorSpace.iPartitions.get(v2ID).getConnectivityTier().getP2DEnter();
		for (int a = 0; a < leave.size(); a++) {
			int d1 = leave.get(a);
			for (int b = 0; b < enter.size(); b++) {
				int d2 = enter.get(b);
				if (lowerBound(IndoorSpace.iDoors.get(d1), IndoorSpace.iDoors.get(d2)) > distCon) {
					continue;
				}
				String str = d2dDist(d1, d2);
				String[] strs = str.split("\t");
				// update minDist
				double minDist = Double.parseDouble(strs[0]);
				if (minDist <= distCon) {
					String minRoute = Functions.arrayToString1D(strs, 1, strs.length);
					// System.out.println("min distance between d" + d1 + " and d" + d2 + " = " +
					// minDist + " route = "
					// + "" + minRoute + " reverse route = " + Functions.reverse(minRoute));
					if (minDist < minParDist) {
						minParDist = minDist;
						minParRoute = minRoute;

					}
				}
			}
		}
//		System.out.println(v1ID + "  " + v2ID + "  " + minParDist);
		parDist.put(v1ID + "-" + v2ID, minParDist);
		parRoute.put(v1ID + "-" + v2ID, minParRoute);
		return minParDist;
	}

	/**
	 * calculate the indoor distance between two point
	 *
	 * @param sloc
	 *            point p1
	 * @param eloc
	 *            point p2
	 * @return distance
	 */
	public static String ptpDist(String sloc, String eloc) {
		String result = "";

		Point sPoint = locPoint(sloc);
		Partition sPartition = locPartition(sloc);

		Point ePoint = locPoint(eloc);
		Partition ePartition = locPartition(eloc);

		if (sloc.equals(eloc)) {
			return sPoint.eDist(ePoint) + "\t";
		}

		if (sPartition.getmID() == ePartition.getmID()) {
			return sPoint.eDist(ePoint) + "\t";
		}

		ArrayList<Integer> sdoors = new ArrayList<Integer>();
		sdoors = sPartition.getConnectivityTier().getP2DLeave();
		ArrayList<Integer> edoors = new ArrayList<Integer>();
		edoors = ePartition.getConnectivityTier().getP2DEnter();

		double dist = Constant.large;
		int final_ds = -1;
		int final_de = -1;
		String final_middle = "";

		int sdoorSize = sdoors.size();
		for (int i = 0; i < sdoorSize; i++) {
			int ds = sdoors.get(i);
			Door d_s = IndoorSpace.iDoors.get(ds);
			double dist1 = distv(sPoint, d_s);

			int edoorSize = edoors.size();
			for (int j = 0; j < edoorSize; j++) {
				int de = edoors.get(j);
				Door d_e = IndoorSpace.iDoors.get(de);
				double dist2 = distv(ePoint, d_e);

				if (ds == de) {
					final_ds = ds;
					final_de = de;
					final_middle = "";
					dist = 0;
					continue;
				}

				String[] str = d2dDist(ds, de).split("\t");
				double temp = dist1 + Double.parseDouble(str[0]) + dist2;
				if (dist > temp) {
					final_ds = ds;
					final_de = de;
					final_middle = Functions.arrayToString1D(str, 1, str.length - 1);
					dist = temp;
				}
			}
		}

		result = dist + "\t" + final_middle + "\t" + final_de;

		return result;
	}

	/**
	 * calculate dist v
	 *
	 * @param p
	 *            point
	 * @param d
	 *            door
	 * @return distance
	 */
	public static double distv(Point p, Door d) {
		return p.eDist(d);
	}

	public static double getKRankScore(MinHeap<Stamp> FQ_PI, int k) {
		double kBound = 0;
		ArrayList<Stamp> S = new ArrayList<>();
		if (FQ_PI.heapSize < k)
			return 0;
		for (int i = 0; i < FQ_PI.heapSize; i++) {
			Stamp si = FQ_PI.delete_min();
			if (si.D <= getdominate(-1, si.Pc)) {
				S.add(si);
			}
		}
		if (S.size() >= k) {
			kBound = S.get(k - 1).rankScore;
			for (int i = 0; i < k; i++) {
				FQ_PI.insert(S.get(i));
			}
		} else {
			for (int i = 0; i < S.size(); i++) {
				FQ_PI.insert(S.get(i));
			}
		}
		return kBound;
	}

	public static double getKBound(HashMap<String, Double> kbounds, int k) {
		double kbound = 0;
		if (kbounds.size() < k)
			return kbound;
		else {
			kbounds = sortByValue(kbounds);
			Object[] values;
			values = kbounds.values().toArray();
			kbound = (double) values[k - 1];
			// System.out.println("==k " + "kbounds: " + kbounds + " k: " + k + " kbound: "
			// + kbound + "\t");
		}
		return kbound;
	}

	public static LinkedHashMap<String, Double> sortHashMapByValues(HashMap<String, Double> passedMap) {
		List<String> mapKeys = new ArrayList<>(passedMap.keySet());
		List<Double> mapValues = new ArrayList<>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

		Iterator<Double> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			double val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				String key = keyIt.next();
				double comp1 = passedMap.get(key);
				double comp2 = val;

				if (comp1 == comp2) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
		return sortedMap;
	}

	public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public static boolean caldominate(int di, ArrayList<Integer> Pc, double dist, ArrayList<Integer> Rp) {
		String d = String.valueOf(di);
		String P = Pc.toString();

		// System.out.print("di: " + di + " pc: " + P + " dist:" + dist + "\t");

		if (dominate == null || dominate.get(d) == null) {
			HashMap<String, Double> pcMap = new HashMap<>();
			pcMap.put(P, dist);
			dominate.put(d, pcMap);
			// System.out.println("caldominate: " + true + " dominate == null ||
			// dominate.get(d) == null");
			return true;

		} else if (dominate.get(d).get(P) == null) {
			dominate.get(d).put(P, dist);
			// System.out.println("caldominate: " + true + " dominate.get(d).get(P) ==
			// null");
			return true;

		} else if (dominate.get(d).get(P) > dist) {
			dominate.get(d).put(P, dist);
			// System.out.println("caldominate: " + true + " dominate.get(d).get(P) >
			// dist");
			return true;

		} else if (Rp.get(Rp.size() - 2) == di && dominate.get(d).get(P) == dist) {
			dominate.get(d).put(P, dist);
			// System.out.println("caldominate: " + true + " Rp.get(Rp.size() - 2) == di &&
			// dominate.get(d).get(P) == dist");
			return true;
		}

		// System.out.println("caldominate: " + false);

		return false;
	}

	public static double getdominate(int di, ArrayList<Integer> Pc) {
		double result = Constant.large;
		String d = String.valueOf(di);
		String P = Pc.toString();
		if (dominate == null || dominate.get(d) == null || dominate.get(d).get(P) == null) {
			return result;
		} else {
			result = dominate.get(d).get(P);
		}

		return result;
	}

	public static boolean haveDupRoute(ArrayList<Integer> Rp) {
		ArrayList<Integer> tempRp = new ArrayList<>();
		tempRp.addAll(Rp);
		int size = tempRp.size();
		for (int i = size - 1; i > 0; i--) {
			int temp = tempRp.get(i);
			tempRp.remove(i);
			if (tempRp.contains(temp) && tempRp.indexOf(temp) != tempRp.size() - 1) {
				return true;
			}
		}
		return false;

	}

	public static boolean haveDupRoute(String[] routeArr, ArrayList<Integer> Rp) {
		ArrayList<Integer> tempRoute = new ArrayList<>();
		ArrayList<Integer> tempRp = new ArrayList<>();
		tempRp.addAll(Rp);
		if (routeArr[0] == "")
			return false;
		for (int i = 0; i < routeArr.length; i++) {
			tempRoute.add(Integer.parseInt(routeArr[i]));
		}

		for (int i = 0; i < tempRp.size() - 1; i++) {
			for (int j = 0; j < tempRoute.size(); j++) {
				if ((int) tempRp.get(i) == (int) tempRoute.get(j)) {
					return true;
				}
			}
		}
		return false;

	}

	/**
	 * calculate the indoor distance between two doors
	 *
	 * @param ds
	 *            door id ds
	 * @param de
	 *            door id de
	 * @return distance
	 */
	public static String d2dDist(int ds, int de, ArrayList<Integer> Rp) {
		// System.out.println("!!ds = " + IndoorSpace.iDoors.get(ds).toString() + " de =
		// " + IndoorSpace.iDoors.get(de).toString());
		String result = "";

		if (ds == de)
			return 0 + "\t";
		if (Rp.contains(de))
			return Constant.large + "\t" + de;

		int size = IndoorSpace.iDoors.size();
		BinaryHeap<Double> H = new BinaryHeap<Double>(size);
		double[] dist = new double[size]; // stores the current shortest path distance
		// from source ds to a door de
		PrevPair[] prev = new PrevPair[size]; // stores the corresponding previous partition
		// and door pair (v,di) through which the algorithm
		// visits the current door de.
		boolean[] visited = new boolean[size]; // mark door as visited

		for (int i = 0; i < size; i++) {
			int doorID = IndoorSpace.iDoors.get(i).getmID();
			if (doorID != i)
				System.out.println("something wrong_Helper_d2dDist");
			if (doorID != ds)
				dist[i] = Constant.large;
			else
				dist[i] = 0;

			// enheap
			H.insert(dist[i], doorID);

			PrevPair pair = null;
			prev[doorID] = pair;
		}

		while (H.heapSize > 0) {
			String[] str = H.delete_min().split(",");
			int di = Integer.parseInt(str[1]);
			double dist_di = Double.parseDouble(str[0]);
			if (dist_di == Constant.large) {
				break;
			}

			// System.out.println("dequeue <" + di + ", " + dist_di + ">");

			if (di == de) {
				result += getPath(prev, ds, de);
				return result = dist_di + "\t" + result;
			}

			visited[di] = true;

			Door door = IndoorSpace.iDoors.get(di);
			ArrayList<Integer> parts = new ArrayList<Integer>(); // list of leavable partitions
			parts = door.getD2PLeave();

			int partSize = parts.size();

			for (int i = 0; i < partSize; i++) {
				ArrayList<Integer> doorTemp = new ArrayList<Integer>();
				int v = parts.get(i); // partition id
				Partition partition = IndoorSpace.iPartitions.get(v);
				doorTemp = partition.getConnectivityTier().getP2DLeave();

				// remove the visited doors
				ArrayList<Integer> doors = new ArrayList<Integer>(); // list of unvisited leavable doors
				int doorTempSize = doorTemp.size();
				for (int j = 0; j < doorTempSize; j++) {
					int index = doorTemp.get(j);
					if (!visited[index] && !Rp.contains(index)) {
						doors.add(index);
					}
				}

				int doorSize = doors.size();
				// System.out.println("doorSize = " + doorSize + ": " +
				// Functions.printIntegerList(doors));

				for (int j = 0; j < doorSize; j++) {
					int dj = doors.get(j);
					if (visited[dj])
						System.out.println("something wrong_Helper_d2dDist2");
					// System.out.println("for d" + di + " and d" + dj);

					double fd2d = partition.getdistMatrix().getDistance(di, dj);
					;
					boolean toAnotherFloor = false;
					int di_j = -1;

					if (fd2d == -1) {
						int fid1 = IndoorSpace.iDoors.get(di).getmFloor();
						int fid2 = IndoorSpace.iDoors.get(dj).getmFloor();
						fd2d = DataGenConstant.lenStairway * (Math.abs(fid1 - fid2));

						if (IndoorSpace.iDoors.get(di).eDist(IndoorSpace.iDoors.get(dj)) > Constant.small) {
							fd2d += IndoorSpace.iDoors.get(di).eDist(IndoorSpace.iDoors.get(dj));
							toAnotherFloor = true;
							if (fid1 > fid2)
								di_j = di - (DataGenConstant.curSizeDoor * (Math.abs(fid1 - fid2)));
							else
								di_j = di + (DataGenConstant.curSizeDoor * (Math.abs(fid1 - fid2)));
						}
					}

					if ((dist[di] + fd2d) < dist[dj]) {
						double oldDj = dist[dj];
						dist[dj] = dist[di] + fd2d;
						H.updateNode(oldDj, dj, dist[dj], dj);
						if (toAnotherFloor && di_j != -1) {
							prev[di_j] = new PrevPair(v, di);
							prev[di_j].toString();
							prev[dj] = new PrevPair(v, di_j);
							prev[dj].toString();
							// System.out.println(di + "-" + di_j + "-" + dj);
						} else {
							prev[dj] = new PrevPair(v, di);
							prev[dj].toString();
							// System.out.println(di + "-" + dj);
						}
					} else {
						// System.out.println();
					}
				}
			}
		}
		if (dist[de] == Constant.large) {
			return Constant.large + "\t";
		}

//		System.out.println(result);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		return result;
	}

	/**
	 * calculate the indoor distance between two doors
	 *
	 * @param ds ds id
	 * @param des de id list
	 * @param s
	 * @param Rp
	 * @return distance
	 */
	public static ArrayList<String> d2dsDist(int ds, ArrayList<Integer> des, String s, ArrayList<Integer> Rp) {
		// System.out.println("ds: " + ds + " des: " + des + " Rp: " + Rp);
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<Integer> des_PI = new ArrayList<Integer>();
		ArrayList<Integer> Rp_PI = new ArrayList<>();

		Rp_PI.addAll(Rp);
		des_PI = des;

		int desSize = des_PI.size();
		for (int i = 0; i < desSize; i++) {
			if (ds == des_PI.get(i)) {
				results.add(0 + "\t");
				des_PI.remove(i);
				i--;
				desSize--;
			} else if (Rp.contains(des_PI.get(i))) {
				results.add(Constant.large + "\t" + des_PI.get(i));
				des_PI.remove(i);
				i--;
				desSize--;
			}
		}

		// System.out.println("results: " + results);

		int size = IndoorSpace.iDoors.size() + 1;
		BinaryHeap<Double> H = new BinaryHeap<Double>(size);
		double[] dist = new double[size]; // stores the current shortest path distance
		// from source ds to a door de
		PrevPair[] prev = new PrevPair[size]; // stores the corresponding previous partition
		// and door pair (v,di) through which the algorithm
		// visits the current door de.
		boolean[] visited = new boolean[size]; // mark door as visited

		for (int i = 0; i < size - 1; i++) {
			int doorID = IndoorSpace.iDoors.get(i).getmID();
			if (doorID != i)
				System.out.println("something wrong_Helper_d2dDist");
			if (doorID != ds)
				dist[i] = Constant.large;
			else
				dist[i] = 0;

			// enheap
			H.insert(dist[i], doorID);

			PrevPair pair = null;
			prev[doorID] = pair;
		}

		if (ds == -2) {
			dist[size - 1] = 0;
			H.insert(dist[size - 1], size - 1);
			PrevPair pair = null;
			prev[size - 1] = pair;
		} else {
			dist[size - 1] = Constant.large;
			H.insert(dist[size - 1], size - 1);
			PrevPair pair = null;
			prev[size - 1] = pair;
		}

		int h = 0;
		while (H.heapSize > 0) {
			h++;
			String[] str = H.delete_min().split(",");
			int di = Integer.parseInt(str[1]);
			double dist_di = Double.parseDouble(str[0]);
			if (dist[di] == Constant.large) {
				break;
			}

			// System.out.println("dequeue <" + di + ", " + dist_di + ">");

			if (des_PI.size() == 0)
				break;
			int pos = des_PI.indexOf(di); // find the index of de which equal to di
			if (pos != -1) {
				int de = des_PI.get(pos);

				String r;

				if (ds == -2) {
					r = getPath(prev, size - 1, de, true);
				} else {
					r = getPath(prev, ds, de);
				}

				results.add(dist_di + "\t" + r);

				des_PI.remove(pos);
			}

			visited[di] = true;

			if (di == size - 1) {
				// System.out.print("d" + di + "\t");
				Point sPoint = locPoint(s);
				Partition sPartition = locPartition(s);
				int vs = sPartition.getmID();
				ArrayList<Integer> doors = new ArrayList<Integer>();
				doors = sPartition.getConnectivityTier().getP2DLeave();

				// System.out.println("doors: " + doors);

				int doorSize = doors.size();

				for (int j = 0; j < doorSize; j++) {

					int dj = doors.get(j);

					// System.out.print("\t to d" + dj + "\t");

					if (Rp_PI.contains(dj))
						continue;
					Door doorj = IndoorSpace.iDoors.get(dj);
					if (visited[dj])
						System.out.println("something wrong_Helper_d2dDist2");
					double fd2d = sPoint.eDist(doorj);

					double oldDj = dist[dj];
					dist[dj] = dist[di] + fd2d;

					// System.out.println("dist: " + dist[dj]);

					H.updateNode(oldDj, dj, dist[dj], dj);
					prev[dj] = new PrevPair(vs, di);
					prev[dj].toString();
				}
			} else {

				Door door = IndoorSpace.iDoors.get(di);
				ArrayList<Integer> parts = new ArrayList<Integer>(); // list of leavable partitions
				parts = door.getD2PLeave();

				// System.out.println("d" + di + " parts: " + parts);

				int partSize = parts.size();

				for (int i = 0; i < partSize; i++) {
					ArrayList<Integer> doorTemp = new ArrayList<Integer>();
					int v = parts.get(i); // partition id
					Partition partition = IndoorSpace.iPartitions.get(v);
					doorTemp = partition.getConnectivityTier().getP2DLeave();

					// System.out.print("\tfor v" + v + " doorTemp: " + doorTemp + " doors: ");

					// remove the visited doors
					ArrayList<Integer> doors = new ArrayList<Integer>(); // list of unvisited leavable doors
					int doorTempSize = doorTemp.size();
					for (int j = 0; j < doorTempSize; j++) {
						int index = doorTemp.get(j);
						if (Rp_PI.contains(index))
							continue;
						if (!visited[index]) {
							doors.add(index);
						}
					}

					// System.out.println(doors);

					int doorSize = doors.size();

					for (int j = 0; j < doorSize; j++) {
						int dj = doors.get(j);
						// System.out.print("\t\tto d" + dj);
						if (visited[dj])
							System.out.println("something wrong_Helper_d2dDist2");

						double fd2d = partition.getdistMatrix().getDistance(di, dj);
						boolean toAnotherFloor = false;
						int di_j = -1;

						if (fd2d == -1) {
							int fid1 = IndoorSpace.iDoors.get(di).getmFloor();
							int fid2 = IndoorSpace.iDoors.get(dj).getmFloor();
							fd2d = DataGenConstant.lenStairway * (Math.abs(fid1 - fid2));

							if (IndoorSpace.iDoors.get(di).eDist(IndoorSpace.iDoors.get(dj)) > Constant.small) {
								fd2d += IndoorSpace.iDoors.get(di).eDist(IndoorSpace.iDoors.get(dj));
								toAnotherFloor = true;
								if (fid1 > fid2)
									di_j = di - (DataGenConstant.curSizeDoor * (Math.abs(fid1 - fid2)));
								else
									di_j = di + (DataGenConstant.curSizeDoor * (Math.abs(fid1 - fid2)));
							}

							// System.out.print("\t\tfid1: " + fid1 + " fid2: " + fid2 + " fd2d: " + fd2d +
							// " di_j: " + di_j + "\t");
						}

						if ((dist[di] + fd2d) < dist[dj]) {
							double oldDj = dist[dj];
							dist[dj] = dist[di] + fd2d;
							H.updateNode(oldDj, dj, dist[dj], dj);
							if (toAnotherFloor && di_j != -1) {
								prev[di_j] = new PrevPair(v, di);
								prev[di_j].toString();
								prev[dj] = new PrevPair(v, di_j);
								prev[dj].toString();
								// System.out.println(di + "-" + di_j + "-" + dj);
							} else {
								prev[dj] = new PrevPair(v, di);
								prev[dj].toString();
								// System.out.println(di + "-" + dj);
							}
						} else {
							// System.out.println();
						}
					}
				}
			}
		}

		if (des_PI.size() != 0) {
			for (int i = 0; i < des_PI.size(); i++) {
				results.add(Constant.large + "\t" + des_PI.get(i));
				des_PI.remove(i);
				i--;
			}
		}

		return results;
	}

	/**
	 * @param prev
	 * @return a string path
	 */
	public static String getPath(PrevPair[] prev, int ds, int de, boolean isDs) {
		String result = de + "";

		// System.out.println("ds = " + ds + " de = " + de + " " + prev[de].par + " " +
		// prev[de].door);
		int currp = prev[de].par;
		int currd = prev[de].door;

		while (currd != ds) {
			result = currd + "\t" + result;
			// System.out.println("current: " + currp + ", " + currd + " next: " +
			// prev[currd].toString());
			currp = prev[currd].par;
			currd = prev[currd].door;

		}
		if (isDs) {
			result = -2 + "\t" + result;
		} else {
			result = currd + "\t" + result;
		}

		return result;
	}

	/**
	 * calculate the lower bound distance between a point and a partition
	 *
	 * @param o1
	 *            indoor entity point
	 * @param parId
	 *            a partition id
	 * @return distance
	 */
	public static double lowerBound(Point o1, int parId) {
		double minDist = Constant.large;
		Partition partition = IndoorSpace.iPartitions.get(parId);

		// check if the point is inside the partition
		if (inside(o1, parId))
			return 0;

		ArrayList<Integer> enter = new ArrayList<Integer>();
		enter = partition.getConnectivityTier().getP2DEnter();

		for (int i = 0; i < enter.size(); i++) {
			int d = enter.get(i);
			double result = lowerBound(o1, IndoorSpace.iDoors.get(d));
			if (result < minDist)
				minDist = result;
		}

		return minDist;
	}

	/**
	 * calculate the lower bound distance between a partition and a point
	 *
	 * @param parId
	 *            a partition id
	 * @param o1
	 *            indoor entity point
	 * @return distance
	 */
	public static double lowerBound(int parId, Point o1) {
		double minDist = Constant.large;

		// check if the point is inside the partition
		if (inside(o1, parId))
			return 0;

		ArrayList<Integer> leave = new ArrayList<Integer>();
		leave = IndoorSpace.iPartitions.get(parId).getConnectivityTier().getP2DLeave();

		for (int i = 0; i < leave.size(); i++) {
			int d = leave.get(i);
			double result = lowerBound(IndoorSpace.iDoors.get(d), o1);
			if (result < minDist)
				minDist = result;
		}

		return minDist;
	}

	public static double lowerBound(Point o1, int parId, Point o2) {
		double minDist = Constant.large;

		// check if the point is inside the partition
		if (inside(o1, parId) && inside(o2, parId))
			return 0;
		if (inside(o1, parId) || inside(o2, parId)) {
			return lowerBound(o1, o2);
		}

		ArrayList<Integer> leave = new ArrayList<Integer>();
		ArrayList<Integer> enter = new ArrayList<>();
		leave = IndoorSpace.iPartitions.get(parId).getConnectivityTier().getP2DLeave();
		enter = IndoorSpace.iPartitions.get(parId).getConnectivityTier().getP2DEnter();

		// System.out.println("leave: " + leave + " enter: " + enter);

		for (int i = 0; i < enter.size(); i++) {
			int dEnter = enter.get(i);
			double dist1 = lowerBound(o1, IndoorSpace.iDoors.get(dEnter));

			for (int j = 0; j < leave.size(); j++) {
				int dLeave = leave.get(j);
				double dist2 = IndoorSpace.iDoors.get(dEnter).eDist(IndoorSpace.iDoors.get(dLeave));
				double dist3 = lowerBound(IndoorSpace.iDoors.get(dLeave), o2);
				double result = dist1 + dist2 + dist3;
				if (result < minDist)
					minDist = result;
			}
		}

		return minDist;
	}

	/**
	 * calculate the lower bound distance between two points
	 *
	 * @param o1
	 *            indoor entity point
	 * @param o2
	 *            indoor entity point
	 * @return distance
	 */
	public static double lowerBound(Point o1, Point o2) {

		double result = -1.0;

		// if these two points are on the same floor
		if (o1.getmFloor() == o2.getmFloor()) {
			result = o1.eDist(o2);

		} else { // if these two point are on different points
			int fID1 = o1.getmFloor();
			int fID2 = o2.getmFloor();
			Floor f1 = IndoorSpace.iFloors.get(fID1);
			Floor f2 = IndoorSpace.iFloors.get(fID2);

			double onFLoorDist = Constant.large;
			List<Integer> doors1 = new ArrayList<Integer>();
			List<Integer> doors2 = new ArrayList<Integer>();
			doors1 = f1.getmDoors();
			doors2 = f2.getmDoors();

			// System.out.print("f" + fID1 + " f" + fID2 + " doors1: " + doors1 + " doors2:
			// " + doors2);
			// each floor has 4 exits, two floors has 16 combination in total
			int size1 = doors1.size();
			int size2 = doors2.size();
			for (int i = 0; i < size1; i++) {
				double o1_d1 = IndoorSpace.iDoors.get(doors1.get(i)).eDist(o1);

				for (int j = 0; j < size2; j++) {
					double o2_d2 = IndoorSpace.iDoors.get(doors2.get(j)).eDist(o2);

					double sum = o1_d1 + o2_d2;
					if (sum < onFLoorDist) {
						onFLoorDist = sum;
					}
				}
			}

			result = f2fDist(fID1, fID2) + onFLoorDist;
		}

		// System.out.print("lowerbound o1: " + o1 + " o2: " + o2 + ": ");
		// System.out.println(result);
		return result;
	}

	/**
	 * check whether a point is inside a partition
	 * 
	 * @param o1
	 * @param parId
	 * @return
	 */
	private static boolean inside(Point o1, int parId) {
		double x = o1.getX();
		double y = o1.getY();

		Partition partition = IndoorSpace.iPartitions.get(parId);

		if (o1.getmFloor() != partition.getmFloor())
			return false;

		if (x >= partition.getX1() && x <= partition.getX2() && y >= partition.getY1() && y <= partition.getY2()) {
			return true;
		} else
			return false;
	}
}
