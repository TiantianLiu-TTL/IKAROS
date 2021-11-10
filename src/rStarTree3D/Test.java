/**
 * 
 */
package rStarTree3D;

import java.lang.Integer;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import com.github.davidmoten.rtree3d.geometry.Geometry;
import com.github.davidmoten.rtree3d.geometry.Point;
import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.Leaf;
import com.github.davidmoten.rtree3d.Node;
import com.github.davidmoten.rtree3d.Entry;
import com.github.davidmoten.rtree3d.NonLeaf;
import com.github.davidmoten.rtree3d.RTree;

import algorithm_ikrq.StartPoint;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import rx.Observable;

/**
 * @author feng zijin
 *
 */
public class Test{
	
	
	private static void loadData() throws IOException {
		StartPoint start = new StartPoint();
	}
	
	private static <T extends Geometry> void print(Node<Integer, Box> node, int depth)
            throws FileNotFoundException {

        PrintStream out = new PrintStream("target/out" + depth + ".txt");
        print(node, out, depth, depth);
        out.close();
    }

    private static <T extends Geometry> void print(Node<Integer, Box> node, PrintStream out,
            int minDepth, int maxDepth) {
        print(node, out, 0, minDepth, maxDepth);
    }

    private static <T extends Geometry> void print(Node<Integer, Box> node, PrintStream out, int depth,
            int minDepth, int maxDepth) {
        if (depth > maxDepth) {
            return;
        }
        if (node instanceof NonLeaf) {
            NonLeaf<Integer, Box> n = (NonLeaf<Integer, Box>) node;
            Box b = node.geometry().mbb();
            if (depth >= minDepth)
                print(b, out);
            for (Node<Integer, Box> child : n.children()) {
                print(child, out, depth + 1, minDepth, maxDepth);
            }
        } else if (node instanceof Leaf && depth >= minDepth) {
            Leaf<Integer, Box> n = (Leaf<Integer, Box>) node;
            print(n.geometry().mbb(), out);
        }
    }

    private static void print(Box b, PrintStream out) {
        out.format("%s,%s,%s,%s,%s,%s\n", b.x1(), b.y1(), b.z1(), b.x2(), b.y2(), b.z2());
    }
    
	public static void main(String[] arg) throws IOException {
		final Box bounds = Box.create(0.0, 0.0, 0.0, 100.0, 100.0, 10.0);
		
		// create RTree
	    int maxChildren = 3;
	    RTree<Integer, Box> tree = RTree.star().minChildren((maxChildren) / 2)
	            .maxChildren(maxChildren).bounds(bounds).create();
	    
	    loadData();
	    
	    for (int i = 0; i < IndoorSpace.iPartitions.size(); i ++) {
	    		Partition partition = IndoorSpace.iPartitions.get(i);
	    		TreeNode node = new TreeNode(partition.getX1(), partition.getY1(), (partition.getmFloor()) * 1
	    				, partition.getX2(), partition.getY2(), (partition.getmFloor() + 1) * 1, partition,
	    				partition.getdistMatrix(), partition.getConnectivityTier());
	    		
	    		Box box = Box.create(partition.getX1(), partition.getY1(), (partition.getmFloor()) * 1
	    				, partition.getX2(), partition.getY2(), (partition.getmFloor() + 1) * 1);
	    		box.setmNode(node);
	    		
	    		tree = tree.add(partition.getmID(), box);
	    		
	    		if ((partition.getmFloor()) * 1 != ((partition.getmFloor() + 1) * 1) - 1) System.out.println("something wrong");
	    }
	    
	    System.out.println("tree size = " + tree.size());
	    
	    ArrayList<Box> boxes = new ArrayList<Box>();
	    
	    for (int i = 0; i < IndoorSpace.iPartitions.size(); i ++) {
	    		Partition partition = IndoorSpace.iPartitions.get(i);
	    		
	    		Observable<Entry<Integer, Box>> entries = tree.search(Point.create(partition.getcenterX(), partition.getcenterY()
	    				, partition.getmFloor() + 0.5));
		    int count = entries.count().toBlocking().single();
		    
		    entries.subscribe(
		    		e -> boxes.add(e.geometry())
		    	);
		    
		    if(count != 1) System.out.println("something wrong");
	    }
	    
	    System.out.println("total boxes size = " + boxes.size());
	    for(int i = 0; i < boxes.size();i ++) {
	    		System.out.println();
	    		
	    		Box box = boxes.get(i);
	    		System.out.println("Box " + box.cornerToString());
	    		
	    		TreeNode node = box.getmNode();
	    		System.out.println("Node " + node.cornerToString() + " id = " + node.getmID());
	    		
	    		Partition partition = node.getmPartition();
	    		System.out.println("Partition " + partition.cornerToString3D() + " id = " + partition.getmID());
	    }
	    
	    
	    for (int depth = 0; depth <= 10; depth++) {
            print(tree.root().get(), depth);
            //System.out.println("depth file written " + depth);
        }
	    
	    
	    String fileInput = System.getProperty("user.dir") + "/source.r";
	    Runtime.getRuntime().exec("/bin/sh R CMD BATCH " + fileInput); 
	    System.out.println("png");
	}
}
