import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.io.FileInputStream;
import java.util.Comparator;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * 
 * @author Brandon Fain
 *
 */

/*
 * NOTES FOR FELIX AND
 * BRADY************************************************************
 * 
 * 11/23/22
 * Went ahead and started implementing the initialize. Right now it reads the
 * stream and saves an
 * adjacency matrix of the points hashcode with the value being a hashset of the
 * neighbors hashcodes.
 * 
 * The problem with this is we need a method that returns the original point
 * object or we need to change
 * the hashset to store points instead of points.hashCode. Take a look and let
 * me know what you think.
 * -bh
 * 
 * 11/26/22
 * Not sure why initialize isn't working, but went ahead and started
 * implementing the rest of the methods
 * assuming we will be able to fix it in its current implementation (point based
 * adjacency matrix).
 * Take a look when you get the chance at it and the jUnit tests. Thanks
 * -bh
 * 
 * 11/26/22
 * Fixed broke shit. TODO: Fix scanner so it can read if theres a name or not in the edges and verticies.
 * -bh
 * 
 * 11/28/22
 * Fixed above todo, found new problem when uploading to gradescope. TODO: fix isConnected. DFS is not working.
 * 
 * 11/28/22
 * Fixed above todo, 35/35 on gradescope finally!
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * 
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */

    // Instance variables:
    private HashMap<Point, HashSet<Point>> AdjMatrix = new HashMap<>(); // Creates adjacency matrix

    public void initialize(FileInputStream file) throws Exception {
        // TODO: Implement initialize

        /*
         * The format of the FileInputStream is shown below.
         * num_vertices num_edges
         * node0_name node0_latitude node0_longitude
         * node1_name node1_latitude node1_longitude
         * ...
         * index_u_edge0 index_v_edge0 optional_edge0_name
         * index_v_edge1 index_v_edge1 optional_edge1_name
         * ...
         */

        Scanner sc = new Scanner(file);
        int numVerticies = sc.nextInt();
        Point[] pointArray = new Point[numVerticies]; // Used to keep track of position of each point.
        int numEdges = sc.nextInt();
        // Stores the number of verticies and edges in the graph, then loops through all
        // the verticies
        for (int vertex = 0; vertex < numVerticies; vertex++) {
            String name = sc.next(); // not sure if we need the name, but made it just in case.
            Point p = new Point(sc.nextDouble(), sc.nextDouble());
            AdjMatrix.put(p, new HashSet<Point>());
            // Adds the points hashcode to the adjacency matrix and creates a hashset for
            // its edges.
            pointArray[vertex] = p; // Stores order we added vertex so we can look them up to add edges.
        }
        // Need to implement a method for verifying if the edges have names or not. If
        // they have names
        // we need to skip over them.
        for (int edge = 0; edge < numEdges; edge++) {
            try {
                int firstVert = sc.nextInt();
                int secVert = sc.nextInt();
                // Adds points to both vertex neighbors.
                AdjMatrix.get(pointArray[firstVert]).add(pointArray[secVert]);
                AdjMatrix.get(pointArray[secVert]).add(pointArray[firstVert]);
            } catch (Exception e) {
                // TODO: handle exception
                sc.next();
                int firstVert = sc.nextInt();
                int secVert = sc.nextInt();
                // Adds points to both vertex neighbors.
                AdjMatrix.get(pointArray[firstVert]).add(pointArray[secVert]);
                AdjMatrix.get(pointArray[secVert]).add(pointArray[firstVert]);
            } 
            //sc.skip("[^0-9]"); //Skip anything that isn't a number. Doesn't work. I think it's either seeing
                                        //an empty string after skipping the name or the names have numbers which it
                                        //is seeing. Need to figure out how to skip strings if they appear. 
        }
        //System.out.println(AdjMatrix);
        sc.close();
    }

    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * 
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        // TODO: Implement nearestPoint
        // Create a new double to track the distance and replace it as we find the
        // minimum distance.
        double minDist = Double.POSITIVE_INFINITY;
        Point ret = p; // Initializing condition. We can add if ret = p at the end return "no
                       // neighbors"
        for (Point v : AdjMatrix.keySet()) {
            if (v.distance(p) < minDist) {
                minDist = v.distance(p);
                ret = v;

            }
        }
        return ret;
    }

    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points,
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * 
     * @param start Beginning point. May or may not be in the graph.
     * @param end   Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        // Method works currently
        double totalDistance = 0;
        for (int k = 0; k < route.size() - 1; k++) {
            totalDistance += route.get(k).distance(route.get(k + 1));
        }
        return totalDistance;
    }

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * 
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        // TODO: Implement connected
        HashSet<Point> visited = new HashSet<>();
        Stack<Point> pointStack = new Stack<>();
        pointStack.push(p1);
        Point current = p1;
        while (!visited.contains(p2) && !pointStack.isEmpty()){
            current = pointStack.pop();
            visited.add(current);
            for (Point p : AdjMatrix.get(current)){
                if (!visited.contains(p)){
                    pointStack.push(p);
                }
                
            }

        }
        return visited.contains(p2);
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * 
     * @param start Beginning point.
     * @param end   Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route,
     *                                            either because start is not
     *                                            connected to end or because start
     *                                            equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        // This works. Didn't document it my fault. -bh
        Map<Point, Double> distance = new HashMap<>();
        Map<Point, Point> previous = new HashMap<>();
        Comparator<Point> comp = (a, b) -> (int) (distance.get(a) - distance.get(b));
        PriorityQueue<Point> toExplore = new PriorityQueue<>(comp);
        Point current = start;
        List<Point> ret = new ArrayList<>();
        distance.put(current, 0.0);
        toExplore.add(current);
        while (!toExplore.isEmpty()) {
            current = toExplore.remove();
            for (Point neighbor : AdjMatrix.get(current)) {
                double weight = neighbor.distance(current);
                if (!distance.containsKey(neighbor) ||
                        distance.get(neighbor) > distance.get(current) + weight) {
                    distance.put(neighbor, distance.get(current) + weight);
                    previous.put(neighbor, current);
                    toExplore.add(neighbor);
                }
            }
        }
        Point check = end;
        while (!check.equals(start)) {
            if (previous.containsKey(check)) {
                ret.add(check);
                check = previous.get(check);
            } else {
                throw new InvalidAlgorithmParameterException();
            }
        }
        ret.add(start);
        Collections.reverse(ret);

        return ret;
    }

}
