import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.InvalidAlgorithmParameterException;
import java.util.Scanner;
import java.util.*;

/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
public class GraphDemo {
    public static void main(String[] args) throws FileNotFoundException {
        GraphProcessor gp = new GraphProcessor();
        try {
            gp.initialize(new FileInputStream(new File("data/usa.graph")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        System.out.print("Pick a city- ");
        Scanner userIn = new Scanner(System.in);
        String city = userIn.next();
        System.out.print("Pick another city- ");
        String city2 = userIn.next();
        userIn.close();

        Scanner cities = new Scanner(new BufferedReader(new FileReader("data/uscities.csv")));
        cities.useDelimiter(",");
        String curr = cities.next() + "," +  cities.next();
        //System.out.println(curr);
        while (!curr.equals(city)) {
        cities.nextLine();
            curr = cities.next() + "," + cities.next();
        }

        double plat = cities.nextDouble();
        //System.out.println(plat);
        String plongTemp = cities.nextLine();
        //System.out.println(plongTemp.substring(1));
        double plong = Double.parseDouble(plongTemp.substring(1));
        Point p = new Point(plat, plong);
        cities.close();

        Scanner cities2 = new Scanner(new BufferedReader(new FileReader("data/uscities.csv")));
        cities2.useDelimiter(",");
        curr = cities2.next() + "," +  cities2.next();
        while (!curr.equals(city2)) {
        cities2.nextLine();
            curr = cities2.next() + "," + cities2.next();
        }

        double plat2 = cities2.nextDouble();
        //System.out.println(plat);
        String plongTemp2 = cities2.nextLine();
        //System.out.println(plongTemp.substring(1));
        double plong2 = Double.parseDouble(plongTemp2.substring(1));
        Point p2 = new Point(plat2, plong2);
        cities.close();

        long startTime = System.nanoTime();

        Point realP = gp.nearestPoint(p);
        Point realp2 = gp.nearestPoint(p2);


            try {
                List<Point> elRoute = gp.route(realP, realp2);
                Double routeLength = gp.routeDistance(elRoute);
                long elapsedNanos = System.nanoTime() - startTime;
                System.out.println("Nearest point for " + city + ": " + realP);
                System.out.println("Nearest point for " + city2 + ": " + realp2);
                System.out.println("Total length of route: " + String.format("%.2f",routeLength) + " miles");
                Double ms = Double.valueOf(elapsedNanos/1000000);
                System.out.println("Total time to get nearest points, route, and distance: " + ms + "ms");
                Visualize vm = new Visualize("data/usa.vis", "images/usa.png");
                vm.drawRoute(elRoute);
            } catch (InvalidAlgorithmParameterException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
    }
}