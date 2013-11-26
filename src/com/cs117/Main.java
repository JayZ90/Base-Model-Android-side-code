package com.cs117;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.cs117.R;

public class Main extends Activity implements SensorEventListener {
	
	private float mLastX, mLastY, mLastZ;
	private final int timeout = 500;
	private boolean mInitialized;
	private String moveDir;
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float pHorNOISE = (float) 6.0;
    private final float pVerNOISE = (float) 15.0;
    private final float nHorNOISE = (float) -6.0;
    private final float nVerNOISE = (float) -10.0;
    private final String up = "Drone is moving UP";
    private final String down = "Drone is moving DOWN";
    private final String left = "Drone is moving LEFT";
    private final String right = "Drone is moving RIGHT";
    private final String forward = "Drone is moving FORWARD";
    private final String backward = "Drone is moving BACKWARD";
    private final String none = "Drone is waiting for command";
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		TextView tvMove= (TextView)findViewById(R.id.move);
		ImageView iv = (ImageView)findViewById(R.id.image);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
			mInitialized = true;
		} else {
			float deltaX = (x - mLastX); 
			float deltaY = (y - mLastY);
			float deltaZ = (z - mLastZ);
			
			if ((deltaX < pHorNOISE) && (deltaX > nHorNOISE)) deltaX = (float)0.0;
			if ((deltaY < pHorNOISE) && (deltaY > nHorNOISE)) deltaY = (float)0.0;
			if ((deltaZ < pVerNOISE) && (deltaZ > nVerNOISE)) deltaZ = (float)0.0;
			
			moveDir = none;
			
			
			if (x > pHorNOISE) {
				// send RIGHT
				moveDir = right;
				System.out.println("got right");
			}
			if (x < nHorNOISE) {
				// send LEFT
				moveDir = left;
				System.out.println("got left");
			}
			if (y > pHorNOISE) {
				// send FORWARD
				moveDir = forward;
				System.out.println("got forward");
			}
			if (y < nHorNOISE) {
				// send BACKWARD
				moveDir = backward;
				System.out.println("got backward");
			}
			if ((z - 9.8) > pVerNOISE) {
				// send UP
				moveDir = up;
				System.out.println("got up");
			}
			if ((z - 9.8) < nVerNOISE) {
				// send DOWN
				moveDir = down;
				System.out.println("got down");
			}
			
			
			
			
			/* Method 2: 
			
			if (deltaX >= 0) {
				if ((deltaZ > 0) && (deltaX > deltaZ)){
					// SEND "RIGHT"
					moveDir = right;
					System.out.println("got right");
				}
				else if ((deltaZ > 0) && (deltaX < deltaZ)){
					// SEND "UP"
					moveDir = up;
					System.out.println("got up");
				}
				else if ((deltaZ < 0) && (Math.abs(deltaZ) > deltaX)){
					// SEND "DOWN"
					moveDir = down;
					System.out.println("got down");
				}
			}			
			else if (deltaX < 0) {
				if ((deltaZ < 0) && (deltaX > deltaZ)){
					// SEND "DOWN"
					moveDir = down;
					System.out.println("got down!");
				}
				else if ((deltaZ < 0) && (deltaX < deltaZ)){
					// SEND "LEFT"
					moveDir = left;
					System.out.println("got right!");
				}
				else if ((deltaZ > 0) && (deltaZ > Math.abs(deltaX))){
					// SEND "UP"
					moveDir = up;
					System.out.println("got up!");
				}
			}
			*/
			
			mLastX = x;
			mLastY = y;
			mLastZ = z;
	
			tvX.setText(Float.toString(x));
			tvY.setText(Float.toString(y));
			tvZ.setText(Float.toString(z));
			tvMove.setText(moveDir);
			iv.setVisibility(View.VISIBLE);
			if (Math.abs(deltaX) > Math.abs(deltaZ)) {
				iv.setImageResource(R.drawable.horizontal);  // X direction DOMINATES
			} else if (Math.abs(deltaY) > Math.abs(deltaX)) {
				iv.setImageResource(R.drawable.vertical);  // Y direction DOMINATES
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
		}
	}
}