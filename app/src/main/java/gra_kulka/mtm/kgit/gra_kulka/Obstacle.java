package gra_kulka.mtm.kgit.gra_kulka;

/**
 * Created by KGIT on 23.11.2017.
 */

public class Obstacle {
    int x;
    int y;
    int width;
    int height;

    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public boolean isInContact(int posX, int posY) {

        if ((posX >= x && posX <= (x + width)) && (posY >= y && posY <= (y + height))) {
            return true;
        }


        return false;
    }
}
