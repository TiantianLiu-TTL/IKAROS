package algorithm_ikrq;

import datagenerate.AssignShop;
import indoor_entitity.Door;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import rStarTree2D.UpperTree;
import textualIndex.Dictionary;
import utilities.Constant;
import utilities.DataGenConstant;
import utilities.Functions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PreCompute {
    public static AssignShop assignShop = new AssignShop();
    public static Dictionary dictionary = new Dictionary();
    public static UpperTree upperTree = new UpperTree();
    private static String outputFile_d2dDist = System.getProperty("user.dir") + "/d2dDist.txt";
    private static String outputFile_d2dRoute = System.getProperty("user.dir") + "/d2dRoute.txt";

    public PreCompute() throws IOException {
        assignShop.assignShop();
        String fileInput = System.getProperty("user.dir") + "/identitykeywordIndex.txt";
        dictionary.loadIdenData(fileInput);
        upperTree.loadData();

        int lowerTreeHeight = upperTree.get(0).getLowerTree().height();
        int lowerTreeSize = upperTree.get(0).getLowerTree().size();
        int totalSize = 0;

        for (int i = 0; i < upperTree.size(); i++) {
            totalSize += upperTree.get(0).getLowerTree().size();
        }

        System.out.println("Tree upper part generated! size = " + upperTree.size() + " height = " + upperTree.height() + ""
                + " each leave node link to lower part with size = " + lowerTreeSize + " height = " + lowerTreeHeight + ""
                + ". Total tree size = " + totalSize + " height = " + (upperTree.height() + lowerTreeHeight));

        FileWriter fw1 = new FileWriter(outputFile_d2dDist, true);
        FileWriter fw2 = new FileWriter(outputFile_d2dRoute, true);
        try {

        String d2dDist = "";        // the distance between two doors
        String d2dRoute = "";        // the route between two doors



        for (int i = 2158; i < 2159; i++) {
            for (int j = 0; j < IndoorSpace.iDoors.size(); j++) {
                if (i == j) continue;
                int d1 = IndoorSpace.iDoors.get(i).getmID();
                int d2 = IndoorSpace.iDoors.get(j).getmID();
                System.out.println(d1 + " " + d2);
                String[] str = d2dDist(d1, d2).split("\t");
                String route = Functions.arrayToString1D(str, 1, str.length);

                d2dDist = d1 + "-" + d2 + "\t" + str[0] + "\n";
                fw1.write(d2dDist);
                d2dRoute = d1 + "-" + d2 + "\t" + route + "\n";
                fw2.write(d2dRoute);
            }
        }


        fw1.flush();
        fw1.close();

        fw2.flush();
        fw2.close();}
        catch (NullPointerException e) {
            System.out.println(e);
            fw1.flush();
            fw1.close();

            fw2.flush();
            fw2.close();

        }


    }


    /**
     * calculate the indoor distance between two doors
     *
     * @param ds door id ds
     * @param de door id de
     * @return distance
     */
    public static String d2dDist(int ds, int de) {
//		System.out.println("ds = " + IndoorSpace.iDoors.get(ds).toString() + " de = " + IndoorSpace.iDoors.get(de).toString());
        String result = "";

        if (ds == de) return 0 + "\t";

        int size = IndoorSpace.iDoors.size();
        BinaryHeap<Double> H = new BinaryHeap<Double>(size);
        double[] dist = new double[size];		//stores the current shortest path distance
        // from source ds to a door de
        PrevPair[] prev = new PrevPair[size];		//stores the corresponding previous partition
        // and door pair (v,di) through which the algorithm
        // visits the current door de.
        boolean[] visited = new boolean[size];		// mark door as visited

        for (int i = 0; i < size; i ++) {
            int doorID = IndoorSpace.iDoors.get(i).getmID();
            if(doorID != i) System.out.println("something wrong_Helper_d2dDist");
            if (doorID != ds) dist[i] = Constant.large;
            else dist[i] = 0;

            // enheap
            H.insert(dist[i], doorID);

            PrevPair pair = null;
            prev[doorID] = pair;
        }

        while(H.heapSize > 0) {
            String[] str = H.delete_min().split(",");
            int di = Integer.parseInt(str[1]);
            double dist_di = Double.parseDouble(str[0]);

//			System.out.println("dequeue <" + di + ", " + dist_di + ">");

            if (di == de) {
//				System.out.println("d2dDist_ di = " + di + " de = " + de);
                result += getPath(prev, ds, de);
                return result = dist_di + "\t" + result;
            }

            visited[di] = true;
//			System.out.println("d" + di + " is newly visited");

            Door door = IndoorSpace.iDoors.get(di);
            ArrayList<Integer> parts = new ArrayList<Integer>();		// list of leavable partitions
            parts = door.getD2PLeave();

            int partSize = parts.size();

            for (int i = 0; i < partSize; i ++) {
                ArrayList<Integer> doorTemp = new ArrayList<Integer>();
                int v = parts.get(i);		// partition id
                Partition partition = IndoorSpace.iPartitions.get(v);
                doorTemp = partition.getConnectivityTier().getP2DLeave();

                // remove the visited doors
                ArrayList<Integer> doors = new ArrayList<Integer>();		// list of unvisited leavable doors
                int doorTempSize = doorTemp.size();
                for (int j = 0; j < doorTempSize; j ++) {
                    int index = doorTemp.get(j);
//					System.out.println("index = " + index + " " + !visited[index]);
                    if (!visited[index]) {
                        doors.add(index);
                    }
                }

                int doorSize = doors.size();
//				System.out.println("doorSize = " + doorSize + ": " + Functions.printIntegerList(doors));

                for (int j = 0; j < doorSize; j ++) {
                    int dj = doors.get(j);
                    if (visited[dj]) System.out.println("something wrong_Helper_d2dDist2");
//					System.out.println("for d" + di + " and d" + dj);

                    double fd2d = partition.getdistMatrix().getDistance(di, dj);;
                    if (fd2d == -1) {
                        int fid1 = IndoorSpace.iPartitions.get(IndoorSpace.iDoors.get(di).getmPartitions().get(0)).getmFloor();
                        int fid2 = IndoorSpace.iPartitions.get(IndoorSpace.iDoors.get(dj).getmPartitions().get(0)).getmFloor();
                        fd2d = DataGenConstant.lenStairway * (Math.abs(fid1 - fid2));
//						System.out.println("fid1 = " + fid1 + " fid2 = " + fid2 + " fd2d = " + fd2d);
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

//		System.out.println("ds = " + ds + " de = " + de + " " + prev[de].par + " " + prev[de].door);
         int currp = prev[de].par;
        int currd = prev[de].door;

        while(currd != ds) {
            result = currd + "\t" + result;
//			System.out.println("current: " + currp + ", " + currd + " next: " + prev[currd].toString());
            currp = prev[currd].par;
            currd = prev[currd].door;

        }

        result = currd + "\t" + result;

        return result;
    }

    public static void main (String arg[]) throws IOException{
        PreCompute pc = new PreCompute();
    }
}

