package com.android.overlay;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.android.overlay.manager.NotificationManager;
import com.android.overlay.manager.SettingsManager;

/**
 * @author liu_chonghui
 * 
 */
public class KeepAliveService extends Service {

	private Method startForeground;
	private Method stopForeground;

	private static KeepAliveService instance;

	public static KeepAliveService getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		try {
			startForeground = getClass().getMethod("startForeground",
					new Class[] { int.class, Notification.class });
			stopForeground = getClass().getMethod("stopForeground",
					new Class[] { boolean.class });
		} catch (NoSuchMethodException e) {
			startForeground = stopForeground = null;
		}

		changeForeground();
	}

	public void changeForeground() {
		if (RunningEnvironment.getInstance().isInitialized()
				&& SettingsManager.eventsPersistent()) {
			startForegroundWrapper(NotificationManager.getInstance()
					.getPersistentNotification());
		} else {
			stopForegroundWrapper();
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		RunningEnvironment.getInstance().onServiceStarted();
	}

	// @Override
	// public int onStartCommand(Intent intent, int flags, int startId) {
	// super.onStartCommand(intent, flags, startId);
	// if (SettingsManager.eventsPersistent()) {
	// return Service.START_STICKY;
	// }
	// return Service.START_NOT_STICKY;
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForegroundWrapper();
		RunningEnvironment.getInstance().onServiceDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	void startForegroundWrapper(Notification notification) {
		if (startForeground != null) {
			Object[] startForegroundArgs = new Object[] {
					Integer.valueOf(NotificationManager.PERSISTENT_NOTIFICATION_ID),
					notification };
			try {
				startForeground.invoke(this, startForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
			} catch (IllegalAccessException e) {
				// Should not happen.
			}
		} else {
//			setForeground(true);
			try {
				android.app.NotificationManager nm = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(NotificationManager.PERSISTENT_NOTIFICATION_ID,
						notification);
			} catch (SecurityException e) {
			}
		}
	}

	void stopForegroundWrapper() {
		if (stopForeground != null) {
			try {
				stopForeground.invoke(this, new Object[] { Boolean.TRUE });
			} catch (InvocationTargetException e) {
				// Should not happen.
			} catch (IllegalAccessException e) {
				// Should not happen.
			}
		} else {
//			setForeground(false);
		}
	}

	public static Intent createIntent(Context context) {
		return new Intent(context, KeepAliveService.class);
	}

}
