package com.example.nitro;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowMetrics;


public class MainActivity extends Activity implements SensorEventListener {
    SensorManager sensorManager;
    public static float[] sensorInput = new float[3];
    Sensor sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        WindowMetrics dm = getWindowManager().getCurrentWindowMetrics();
        Constants.SCREEN_WIDTH = dm.getBounds().width();
        Constants.SCREEN_HEIGHT = dm.getBounds().height();
        setContentView(new GamePanel(this, this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorInput[0] = event.values[0];
        sensorInput[1] = event.values[1];
        sensorInput[2] = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void toLevels(){
        Intent i = new Intent(this, LevelSelectionActivity.class);
        startActivity(i);
    }
}