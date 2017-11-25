package gra_kulka.mtm.kgit.gra_kulka;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    boolean isFinished = false;
    int points = 0;
    Date startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        TextView tv = findViewById(R.id.points);
        CustomView cv = findViewById(R.id.customView);
        cv.setPointsView(tv);

        TextView got = findViewById(R.id.gameOverText);
        got.setVisibility(View.INVISIBLE);

        startTime = Calendar.getInstance().getTime();

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(30);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinished) {
                                    CustomView cv = findViewById(R.id.customView);
                                    cv.doAnimate();
                                    long diffInMs = Calendar.getInstance().getTime().getTime() - startTime.getTime();
                                    long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                                    TextView tt = findViewById(R.id.timer);
                                    tt.setText(String.valueOf(30 - diffInSec));

                                    if (diffInSec >= 30) {
                                        gameOver();
                                    }
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }


    public void gameOver() {
        isFinished = true;
        System.out.println("GAME OVER!!");
        CustomView cv = findViewById(R.id.customView);
        cv.gameOver();

        TextView got = findViewById(R.id.gameOverText);
        TextView pts = findViewById(R.id.points);
        got.setVisibility(View.VISIBLE);
        got.setText("GAME OVER! YOUR POINTS: " + pts.getText());
    }

    public void setPoints(String p) {
        TextView tv = findViewById(R.id.points);
        startTime.setTime(startTime.getTime() + 2000);
        tv.setText(p);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            if (Math.abs(x) < 0.1) x = 0;
            if (Math.abs(y) < 0.1) y = 0;
            CustomView cv = findViewById(R.id.customView);
            cv.setDir(-x, y);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startGame() {
        isFinished = false;


        TextView got = findViewById(R.id.gameOverText);
        got.setVisibility(View.INVISIBLE);

        points = 0;
        startTime = Calendar.getInstance().getTime();

    }
}
