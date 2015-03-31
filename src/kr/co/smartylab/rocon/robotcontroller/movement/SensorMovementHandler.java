package kr.co.smartylab.rocon.robotcontroller.movement;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.smartylab.rocon.robotcontroller.MainActivity;
import kr.co.smartylab.rocon.robotcontroller.comm.Communication;
import android.R.bool;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class to generate Turtlebot control messages
 *  
 * @author Moon Kwon Kim <mkdmkk@gmail.com>
 * @since 2015.03.27
 */
public class SensorMovementHandler implements SensorEventListener {
	private final long SENSING_PERIOD = 500;
	private final float GRAVITY = 9.8f;
	private final float X_MIN_FORWARD = -1.0f;
	private final float X_MIN_BACKWARD = 3.0f;
	private final float Y_MIN_LEFT = -2.0f;
	private final float Y_MIN_RIGHT = 2.0f;
	private final float Z_MIN_STOP = 9.35f;
	
	private Context context;
	
	private SensorManager sensorManager;
	private Sensor gravitySensor;
	private Timer sensingTimer;
	
	private float gravityX;
	private float gravityY;
	private float gravityZ;

	private Communication comm;
	private boolean isZeroSent = false;
	
	public SensorMovementHandler(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			gravityX = event.values[0];
			gravityY = event.values[1];
			gravityZ = event.values[2];
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void startMonitoring() {
		startSensing();
		sensingTimer = new Timer();
		sensingTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				float linear = calcLinear();
				float angular = calcAngular(linear);
				
				// Update UI
				String strLinear = String.format("%.2f", linear);
				String strAngular = String.format("%.2f", angular);
				((MainActivity)context).updateTextView("linear", strLinear);
				((MainActivity)context).updateTextView("angular", strAngular);
				if(comm != null) {
					if(linear == 0.0f && angular == 0.0f) {
						if(!isZeroSent) {
							comm.sendControlCommand(strLinear, strAngular);
							isZeroSent = true;
						}
					} else {
						comm.sendControlCommand(strLinear, strAngular);
						isZeroSent = false;
					}
				}
			}
		}, 0, SENSING_PERIOD);
	}

	public void stopMonitoring() {
		sensingTimer.cancel();
		this.stopSensing();
	}
	
	public void setCommunication(Communication comm) {
		this.comm = comm;
		comm.connect();
	}

	private float calcLinear() {
		float linear = 0f;
		if(gravityX > X_MIN_BACKWARD) {
			linear = -((gravityX-X_MIN_BACKWARD) / (GRAVITY-X_MIN_BACKWARD));
		} else if(gravityX < X_MIN_FORWARD) {
			linear = -((gravityX-X_MIN_FORWARD) / (GRAVITY+X_MIN_FORWARD));
		}
		if(linear < -1.0f) {
			return -1.0f;
		} else if(linear > 1.0f) {
			return 1.0f;
		}
		return linear;
	}

	private float calcAngular(float linear) {
		float angular = 0f;
		if(Y_MIN_RIGHT < gravityY || Y_MIN_LEFT > gravityY) {
			angular = gravityY / GRAVITY;
		}
		if(angular < -1.0f) {
			angular = -1.0f;
		} else if(angular > 1.0f) {
			angular = 1.0f;
		}
		return (linear < 0)?angular:-angular;
	}
	
	/**
	 * Register sensors
	 */
	private void startSensing() {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
	}

	/**
	 * Unregister sensors
	 */
	private void stopSensing() {
		sensorManager.unregisterListener(this, gravitySensor);
	}
}
