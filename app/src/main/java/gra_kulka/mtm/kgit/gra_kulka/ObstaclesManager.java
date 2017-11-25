package gra_kulka.mtm.kgit.gra_kulka;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by KGIT on 23.11.2017.
 */

public class ObstaclesManager {
    private List<Obstacle> obstacles = new ArrayList<>();

    int maxOSize = 300;
    int minOSize = 100;
    int width = 0;
    int height = 0;

    public ObstaclesManager(int width, int height, int count) {
        this.width = width;
        this.height = height;
        generateRandomObstacles(count);
    }

    public void generateRandomObstacles(int count) {
        obstacles.clear();
        int added = 0;
        do {
            Random r = new Random();
            int x = r.nextInt(width) + 1;
            int y = r.nextInt(height) + 1;

            int w = r.nextInt((maxOSize - minOSize) + 1) + minOSize;
            int h = r.nextInt((maxOSize - minOSize) + 1) + minOSize;
            Obstacle o = new Obstacle(x, y, w, h);
            obstacles.add(o);
            added++;
        } while (added != count);
    }


    public int[] getFreePositions(int w, int h) {
        int xy[] = new int[2];
        boolean found = false;
        do {
            Random r = new Random();
            int x = r.nextInt(width - w) + 1;
            int y = r.nextInt(height - h) + 1;
            //corner points
            int p1X = x;
            int p1Y = y;

            int p2X = x + w;
            int p2Y = y;

            int p3X = x;
            int p3Y = y + h;

            int p4X = x + w;
            int p4Y = y + h;

            // Rectangle rr = new Rectangle();
            if (isCollision(p1X, p1Y) == null && isCollision(p2X, p2Y) == null && isCollision(p3X, p3Y) == null && isCollision(p4X, p4Y) == null
                    && isCollision(x + w / 2, y + h / 2) == null) {
                xy[0] = x;
                xy[1] = y;
                found = true;
            }
        } while (found != true);

        return xy;
    }

    public Obstacle isCollision(int x, int y) {
        for (Obstacle o : obstacles) {
            if (o.isInContact(x, y)) {
                return o;
            }
        }
        return null;
    }

    public void addObstacle(Obstacle o) {
        obstacles.add(o);
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}
