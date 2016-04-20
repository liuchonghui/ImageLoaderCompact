package com.android.overlay.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import com.android.overlay.OnCloseListener;
import com.android.overlay.OnInitializedListener;
import com.android.overlay.OnScreenChangeListener;
import com.android.overlay.OnScreenOrientationListener;
import com.android.overlay.RunningEnvironment;

/**
 * Manage screen on / off.
 * 
 * @author liu_chonghui
 * 
 */
public class ScreenManager implements OnInitializedListener, OnCloseListener {

	protected ScreenStatusWatcher screenWatcher;
	protected static ScreenManager instance;
	protected PowerManager powerManager;
	protected boolean isScreenOn;

	protected SensorEventWatcher sensorWatcher;
	protected SensorManager sensorManager;
	protected Sensor sensor;
	protected OnScreenOrientationListener mListener; // single

	static {
		instance = new ScreenManager();
		RunningEnvironment.getInstance().addManager(instance);
	}

	public static ScreenManager getInstance() {
		return instance;
	}

	protected ScreenManager() {
		screenWatcher = new ScreenStatusWatcher() {
			@Override
			protected void onScreenOn() {
				super.onScreenOn();
				isScreenOn = true;
				onScreenChangedTo(isScreenOn);
			}

			@Override
			protected void onScreenOff() {
				super.onScreenOff();
				isScreenOn = false;
				onScreenChangedTo(isScreenOn);
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		RunningEnvironment.getInstance().getApplicationContext()
				.registerReceiver(screenWatcher, filter);

		powerManager = (PowerManager) RunningEnvironment.getInstance()
				.getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		isScreenOn = false;
		if (powerManager != null) {
			isScreenOn = powerManager.isScreenOn();
		}

		sensorWatcher = new SensorEventWatcher() {
			@Override
			protected void onPortrait() {
				super.onPortrait();
				onOrientationChanged(true);
			}

			@Override
			protected void onLandScape() {
				super.onLandScape();
				onOrientationChanged(false);
			}
		};
		sensorManager = (SensorManager) RunningEnvironment.getInstance()
				.getApplicationContext().getSystemService("sensor");
		if (sensorManager != null) {
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
	}

	@Override
	public void onInitialized() {
	}

	public boolean isScreenOn() {
		return isScreenOn;
	}

	@Override
	public void onClose() {
		if (screenWatcher != null) {
			try {
				RunningEnvironment.getInstance().getApplicationContext()
						.unregisterReceiver(screenWatcher);
			} catch (Exception e) {
			}
		}
	}

	protected void onScreenChangedTo(boolean isScreenOn) {
		if (isScreenOn) {
			RunningEnvironment.getInstance().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for (OnScreenChangeListener listener : RunningEnvironment
							.getInstance().getUIListeners(
									OnScreenChangeListener.class)) {
						listener.onScreenOn();
					}
				}
			});

		} else {
			RunningEnvironment.getInstance().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for (OnScreenChangeListener listener : RunningEnvironment
							.getInstance().getUIListeners(
									OnScreenChangeListener.class)) {
						listener.onScreenOff();
					}
				}
			});
		}
	}

	/**
	 * WARNNING# Need Filter: Intent.ACTION_SCREEN_ON/Intent.ACTION_SCREEN_OFF
	 */
	public static class ScreenStatusWatcher extends BroadcastReceiver {

		protected void onScreenOn() {
			Log.d("SCREEN", "ON");
		}

		protected void onScreenOff() {
			Log.d("SCREEN", "OFF");
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				onScreenOn();
			} else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				onScreenOff();
			}
		}
	}

	public void register(OnScreenOrientationListener listener) {
		if (sensorManager != null && listener != null) {
			mListener = listener;
			sensorManager.registerListener(sensorWatcher, sensor, 3);
		}
	}

	public void unRegister(OnScreenOrientationListener listener) {
		if (sensorManager != null && listener != null) {
			mListener = null;
			sensorManager.unregisterListener(sensorWatcher, sensor);
		}
	}

	protected void onOrientationChanged(boolean isPortrait) {
		if (mListener != null) {
			mListener.onScreenOrientation(isPortrait);
		}
	}

	public static class SensorEventWatcher implements SensorEventListener {

		protected void onPortrait() {
			Log.d("SCREEN", "Portrait");
		}

		protected void onLandScape() {
			Log.d("SCREEN", "LandScape");
		}

		protected boolean isGravity;

		public static boolean getSystemGravity(Context context) {
			int i;
			try {
				i = android.provider.Settings.System.getInt(
						context.getContentResolver(), "accelerometer_rotation");
			} catch (android.provider.Settings.SettingNotFoundException settingnotfoundexception) {
				settingnotfoundexception.printStackTrace();
				return false;
			}
			return i != 0;
		}

		public static boolean isLandScape() {
			return RunningEnvironment.getInstance().getApplicationContext()
					.getResources().getConfiguration().orientation == 2;
		}

		public void onAccuracyChanged(Sensor sensor1, int i) {
		}

		public void onSensorChanged(SensorEvent sensorevent) {
			isGravity = getSystemGravity(RunningEnvironment.getInstance()
					.getApplicationContext());
			if (!isGravity) {
				Log.d("SCREEN", "not Gravity");
				if (!isLandScape()) {
					Log.d("SCREEN",
							"setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)");
				}

			} else {
				float f = sensorevent.values[0];
				float f1 = sensorevent.values[1];
				float f2 = sensorevent.values[2];
				if (Math.abs(f) <= 3F && f1 >= 8.5F && Math.abs(f2) <= 5F) {
					onPortrait();
				}
				if (Math.abs(f) >= 8F && Math.abs(f1) <= 3.5F
						&& (double) Math.abs(f2) <= 6D) {
					onLandScape();
				}
			}
		}
	}
}
