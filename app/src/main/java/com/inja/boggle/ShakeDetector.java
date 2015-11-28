package com.inja.boggle;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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

    interface ShakeCallBack {
        void onShake();
    }

    public void register(ShakeCallBack shakeCallBack)
    {
        callBacks.add(shakeCallBack);
    }

    public void unregister(ShakeCallBack shakeCallBack)
    {
        callBacks.remove(shakeCallBack);
    }

    private boolean init;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private float x1, x2, x3;
    private static final float ERROR =  5f;
    private List<ShakeCallBack> callBacks;
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
            x1 = x;
            x2 = y;
            x3 = z;
            init = true;
        } else {

            float diffX = Math.abs(x1 - x);
            float diffY = Math.abs(x2 - y);
            float diffZ = Math.abs(x3 - z);

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


            x1 = x;
            x2 = y;
            x3 = z;


            //Horizontal Shake Detected!
            if (diffX > diffY && diffX > diffZ) {
                for ( ShakeCallBack callBack : callBacks){
                    callBack.onShake();
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
