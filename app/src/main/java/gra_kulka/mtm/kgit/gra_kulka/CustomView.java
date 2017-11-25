package gra_kulka.mtm.kgit.gra_kulka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class CustomView extends View {

    public int xmax, ymax;
    private Bitmap ballBmp;
    private Bitmap holeBitmap;
    Bitmap fenceBmp;
    final int ballWidth = 100;
    final int ballHeight = 100;
    ObstaclesManager obstacleManager;
    TextView tv;
    boolean isFinished = false;
    int points = 0;

    public void setPointsView(TextView tv) {
        this.tv = tv;
    }

    int hole_x;
    int hole_y;
    int hole_w = 250;
    int hole_h = 250;

    int border_width = 20;

    // zmiana polożenia
    float dir_x = 0;
    float dir_y = 0;

    float fx = 0f;
    float fy = 0f;

    // akutalna pozycja piłki
    int x_pos;
    int y_pos;

    //akceleracja
    private float xAcceleration = 0f;
    private float yAcceleration = 0f;


    public void setDir(float x, float y) {
        if (!isFinished) {
            dir_x += x;
            dir_y += y;

            dir_x = (float) (Math.round(dir_x * 100.0) / 100.0);
            dir_y = (float) (Math.round(dir_y * 100.0) / 100.0);
        }
    }


    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.grass);
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

        ballBmp = Bitmap.createScaledBitmap(ball, ballWidth, ballHeight, true);

        Bitmap hole = BitmapFactory.decodeResource(getResources(), R.drawable.hole);
        holeBitmap = Bitmap.createScaledBitmap(hole, hole_w, hole_h, true);

        fenceBmp = BitmapFactory.decodeResource(getResources(), R.drawable.fence);
    }

    public void doAnimate() {

        checkBallInHole();
        int tmp_x_pos = getBallX((int) ((float) x_pos + dir_x));
        int tmp_y_pos = getBallY((int) ((float) y_pos + dir_y));
        Obstacle o;
        if ((o = checkCollision(tmp_x_pos, tmp_y_pos)) == null) {
            x_pos = tmp_x_pos;
            y_pos = tmp_y_pos;
        }
        invalidate();
    }

    private int getBallX(int px) {
        if (px < (xmax - border_width - ballHeight) & px > border_width) {
            return px;
        } else if (px > xmax - border_width - ballHeight) {
            return xmax - border_width - ballHeight;
        } else if (px < border_width) {
            return border_width;
        } else {
            System.out.println("ERRRROR !!!!!!");
            return 0;
        }
    }

    private int getBallY(int py) {
        if (py < (ymax - border_width - ballWidth) && py > border_width) {
            return py;
        } else if (py < border_width) {
            return border_width;
        } else if (py > ymax - border_width - ballHeight) {
            return ymax - border_width - ballWidth;
        } else {
            System.out.println("ERRRROR !!!!!!");
            return 0;
        }

    }


    private void checkBallInHole() {
        if ((Math.abs((x_pos + 50) - (hole_x + 125)) < 50) && (Math.abs((y_pos + 50) - (hole_y + 125)) < 50)) {
            System.out.println("WON !!!!!!");
            int holeposs[] = obstacleManager.getFreePositions(hole_w, hole_h);
            hole_x = holeposs[0];
            hole_y = holeposs[1];
            points += 10;
            ((MainActivity) getContext()).setPoints(String.valueOf(points));
        }
    }

    private Obstacle checkCollision(int x, int y) {
        if (obstacleManager != null) {
            int r = ballWidth / 2;
            int xs = x_pos + ballWidth / 2;
            int ys = y_pos + ballHeight / 2;

            Obstacle o;
            for (int i = 0; i < 360; i += 5) {
                int ltX = (int) ((r * Math.cos(Math.toRadians(i))) + xs);
                int ltY = (int) ((r * Math.sin(Math.toRadians(i))) + ys);
                // System.out.println("i:" + i + " , lts: " + ltX + "," + ltY);
                if ((o = obstacleManager.isCollision(ltX, ltY)) != null) {
                    //System.out.println("sin 30 = " + Math.sin(30));
                    System.out.println("kolizja na kącie: " + i);

                    // colission on right side
                    if (((i >= 0 && i <= 45) || i > 315)) {
                        System.out.println("right collision");
                        dir_x *= -0.8;
                        x_pos = o.x - ballWidth - 1;
                    }
                    // collision on left side
                    else if ((i >= 135 && i <= 225)) {
                        System.out.println("left collision");
                        dir_x *= -0.8;
                        x_pos = o.x + o.width + 1;
                    }
                    //collison on top
                    else if (i >= 225 && i <= 315) {
                        System.out.println("top collision");
                        dir_y *= -0.8;
                        y_pos = o.y + o.height + 1;
                    }
                    //collision on bottom
                    else if (i > 45 && i < 135) {
                        System.out.println("bottom collision");
                        dir_y *= -0.8;
                        y_pos = o.y - ballHeight - 1;
                    }

                    //System.out.println("dirx: " + dir_x + " , diry: " + dir_y + ", positions: " + x_pos + ", " + y_pos);
                    return o;
                }
            }
        }

        return null;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        xmax = w;
        ymax = h;
        x_pos = w / 2;
        y_pos = h / 2;
        startGame();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isFinished) {
            final Bitmap bitmap = ballBmp;
            final Bitmap hbitmap = holeBitmap;
            //canvas.drawBitmap(mWood, 0, 0, null);

            //draw hole
            canvas.drawBitmap(hbitmap, hole_x, hole_y, null);

            for (Obstacle o : obstacleManager.getObstacles()) {
                Bitmap fb = Bitmap.createScaledBitmap(fenceBmp, o.width, o.height, true);
                canvas.drawBitmap(fb, o.x, o.y, null);
            }
            canvas.drawBitmap(bitmap, x_pos, y_pos, null);
        }
    }


    public void gameOver() {
        isFinished = true;
        points = 0;
    }


    public void startGame() {
        obstacleManager = new ObstaclesManager(xmax, ymax, 6);
        Obstacle top = new Obstacle(0, 0, xmax, border_width);
        Obstacle bottom = new Obstacle(0, ymax - border_width, xmax, border_width);
        Obstacle left = new Obstacle(0, 0, border_width, ymax);
        Obstacle right = new Obstacle(xmax - border_width, 0, border_width, ymax);
        obstacleManager.addObstacle(top);
        obstacleManager.addObstacle(bottom);
        obstacleManager.addObstacle(left);
        obstacleManager.addObstacle(right);

        dir_x = 0;
        dir_y = 0;
        int holeposs[] = obstacleManager.getFreePositions(hole_w, hole_h);
        hole_x = holeposs[0];
        hole_y = holeposs[1];

        int ballposs[] = obstacleManager.getFreePositions(ballWidth, ballHeight);
        x_pos = ballposs[0];
        y_pos = ballposs[1];

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isFinished) {
            isFinished = false;
            startGame();
            ((MainActivity) getContext()).startGame();
            ((MainActivity) getContext()).setPoints(String.valueOf(points));
        }


        return true;
    }
}
