package com.inja.boggle;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nejasix on 11/27/15.
 */
public class ShakeDetector implements SensorEventListener {

    private void attachSensor()
    {
        mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        List<Sensor> listOfSensorsOnDevice = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < listOfSensorsOnDevice.size(); i++) {
            if (listOfSensorsOnDevice.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {
                init = false;
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            }
        }
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    interface Shakecallback {
        void onShake();
    }

    public void register(Shakecallback shakecallback)
    {
        callBacks.add(shakecallback);
    }

    public void unregister(Shakecallback shakecallback)
    {
        callBacks.remove(shakecallback);
    }

    private boolean init;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private float x, y, z;
    private static final float ERROR =  4f;
    private List<Shakecallback> callBacks;
    private Context mContext;

    public ShakeDetector(Context context)
    {
        mContext = context;
        callBacks = new LinkedList<>();
        attachSensor();
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        //Get x,y and z values
        float x,y,z;
        x = e.values[0];
        y = e.values[1];
        z = e.values[2];


        if (!init) {
            this.x = x;
            this.y = y;
            this.z = z;
            init = true;
        } else {

            float diffX = Math.abs(this.x - x);
          //  Log.d("TAG", "sens dif = " + diffX);
            float diffY = Math.abs(this.y - y);
          //  Log.d("TAG", "sens dif = " + diffY);
            float diffZ = Math.abs(this.z - z);
           // Log.d("TAG", "sens dif = " + diffZ);

            //Handling ACCELEROMETER Noise
            if (diffX < ERROR) {

                diffX =  0f;
            }
            if (diffY < ERROR) {
                diffY = 0f;
            }
            if (diffZ < ERROR) {

                diffZ = 0f;
            }


            this.x = x;
            this.y = y;
            this.z = z;


            //Horizontal Shake Detected!
            if (diffX > diffY && diffX > diffZ) {
                for ( Shakecallback callBack : callBacks){
                    callBack.onShake();
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
