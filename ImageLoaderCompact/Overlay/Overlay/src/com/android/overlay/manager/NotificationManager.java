package com.android.overlay.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.overlay.OnCloseListener;
import com.android.overlay.OnInitializedListener;
import com.android.overlay.OnLoadListener;
import com.android.overlay.RunningEnvironment;

/**
 * Manage notifications about message, authentication and subscription.
 */
public class NotificationManager implements OnInitializedListener,
		OnCloseListener, OnLoadListener, Runnable {

	public static final int PERSISTENT_NOTIFICATION_ID = 1;
	protected static final int CHAT_NOTIFICATION_ID = 2;
	protected static final int BASE_NOTIFICATION_PROVIDER_ID = 0xdead101;

	protected static final long VIBRATION_DURATION = 500;
	protected static final int MAX_NOTIFICATION_TEXT = 80;
//	protected final long startTime;
	protected final android.app.NotificationManager notificationManager;
	protected final Notification persistentNotification;
	// protected final PendingIntent clearNotifications;
//	protected final Handler handler;

//	protected final Runnable startVibro;

//	protected final Runnable stopVibro;

	/**
	 * List of providers for notifications.
	 */
	// protected final List<NotificationProvider<? extends NotificationItem>>
	// providers;

	private static NotificationManager instance;

	static {
		instance = new NotificationManager();
		RunningEnvironment.getInstance().addManager(instance);
	}

	public static NotificationManager getInstance() {
		return instance;
	}

	protected NotificationManager() {
		notificationManager = (android.app.NotificationManager) RunningEnvironment
				.getInstance().getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		persistentNotification = new Notification();
//		handler = new Handler(Looper.getMainLooper());
		// providers = new ArrayList<NotificationProvider<? extends
		// NotificationItem>>();
		// startTime = System.currentTimeMillis();
		// stopVibro = new Runnable() {
		// @Override
		// public void run() {
		// handler.removeCallbacks(startVibro);
		// handler.removeCallbacks(stopVibro);
		// ((Vibrator) NotificationManager.this.application
		// .getApplicationContext().getSystemService(
		// Context.VIBRATOR_SERVICE)).cancel();
		// }
		// };
		// startVibro = new Runnable() {
		// @Override
		// public void run() {
		// handler.removeCallbacks(startVibro);
		// handler.removeCallbacks(stopVibro);
		// ((Vibrator) NotificationManager.this.application
		// .getApplicationContext().getSystemService(
		// Context.VIBRATOR_SERVICE)).cancel();
		// ((Vibrator) NotificationManager.this.application
		// .getApplicationContext().getSystemService(
		// Context.VIBRATOR_SERVICE))
		// .vibrate(VIBRATION_DURATION);
		// handler.postDelayed(stopVibro, VIBRATION_DURATION);
		// }
		// };
	}

	public Intent createClearNotificationsIntent(Context context) {
		Intent intent = null;
		try {
			intent = new Intent(context, Class.forName(RunningEnvironment
					.getInstance().getString("R.string.notification_clear")));
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
					| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return intent;
	}

	@Override
	public void onLoad() {
	}

	@Override
	public void onInitialized() {
		runPersistentNotification();
	}

	/**
	 * Register new provider for notifications.
	 * 
	 * @param provider
	 */
//	public void registerNotificationProvider(
//			NotificationProvider<? extends NotificationItem> provider) {
//		providers.add(provider);
//	}

	/**
	 * Update notifications for specified provider.
	 * 
	 * @param <T>
	 * @param provider
	 * @param notify
	 *            Ticker to be shown. Can be <code>null</code>.
	 */
//	public <T extends NotificationItem> void updateNotifications(
//			NotificationProvider<T> provider, T notify) {
//		Log.d("NOTIFY", "updateNotifications:");
//		int id = providers.indexOf(provider);
//		if (id == -1) {
//			throw new IllegalStateException(
//					"registerNotificationProvider() must be called from onLoaded() method.");
//		} else {
//			id += BASE_NOTIFICATION_PROVIDER_ID;
//		}
//		Log.d("NOTIFY", "id:" + id);
//		Iterator<? extends NotificationItem> iterator = provider
//				.getNotifications().iterator();
//		if (!iterator.hasNext()) {
//			Log.d("NOTIFY", "NotificationManager.cancel:" + id);
//			notificationManager.cancel(id);
//		} else {
//			NotificationItem top;
//			String ticker;
//			if (notify == null) {
//				top = iterator.next();
//				ticker = null;
//			} else {
//				top = notify;
//				ticker = top.getTitle();
//			}
//			Intent intent = top.getIntent();
//			if (intent == null) {
//				Log.d("NOTIFY",
//						"NotificationItem.getIntent() == null try get from NotificationProvider!");
//				try {
//					intent = provider.getIntent(top);
//				} catch (Exception e) {
//					intent = null;
//				}
//			}
//			if (intent == null) {
//				Log.d("NOTIFY",
//						"NotificationProvider.getIntent() == null exit!");
//				return;
//			}
//			Log.d("NOTIFY", "ticker:" + ticker);
//			Notification notification = new Notification(provider.getIcon(),
//					ticker, System.currentTimeMillis());
//			if (!provider.canClearNotifications()) {
//				notification.flags |= Notification.FLAG_NO_CLEAR;
//			}
//			notification.setLatestEventInfo(
//					application.getApplicationContext(), top.getTitle(), top
//							.getText(), PendingIntent.getActivity(
//							application.getApplicationContext(), 0, intent,
//							PendingIntent.FLAG_UPDATE_CURRENT));
//			if (ticker != null) {
//				setNotificationDefaults(notification,
//						SettingsManager.eventsVibro(), provider.getSound(),
//						provider.getStreamType());
//			}
//			// notification.deleteIntent = clearNotifications;
//			Log.d("NOTIFY", "notify:" + top.getTitle() + ", " + top.getText());
//			notify(id, notification);
//		}
//	}

	/**
	 * Sound, vibration and lightning flags.
	 * 
	 * @param notification
	 * @param streamType
	 */
//	private void setNotificationDefaults(Notification notification,
//			boolean vibro, Uri sound, int streamType) {
//		notification.audioStreamType = streamType;
//		notification.defaults = 0;
//		notification.sound = sound;
//		if (vibro) {
//			if (SettingsManager.eventsIgnoreSystemVibro()) {
//				handler.post(startVibro);
//			} else {
//				notification.defaults |= Notification.DEFAULT_VIBRATE;
//			}
//		}
//		if (SettingsManager.eventsLightning()) {
//			notification.defaults |= Notification.DEFAULT_LIGHTS;
//			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//		}
//	}

	private void notify(int id, Notification notification) {
		try {
			notificationManager.notify(id, notification);
		} catch (SecurityException e) {
		}
	}

	// public void onClearNotifications() {
	// for (NotificationProvider<? extends NotificationItem> provider :
	// providers) {
	// if (provider.canClearNotifications()) {
	// provider.clearNotifications();
	// }
	// }
	// for (OnClearNotificationsListener listener : RunningEnvironment
	// .getInstance().getManagers(OnClearNotificationsListener.class)) {
	// listener.clearNotifications();
	// }
	// }

	@Override
	public void run() {
//		handler.removeCallbacks(this);
	}

	public Notification getPersistentNotification() {
		return persistentNotification;
	}

	@Override
	public void onClose() {
		if (notificationManager != null) {
			notificationManager.cancelAll();
		}
	}

	public Intent createReconnectionActivityIntent(Context context) {
		Intent intent = null;
		try {
			intent = new Intent(context, Class.forName(RunningEnvironment
					.getInstance().getString("R.string.page_reconnection")));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return intent;
	}

	protected void runPersistentNotification() {
		String contentText = "+++";
		Intent persistentIntent = createReconnectionActivityIntent(RunningEnvironment
				.getInstance().getApplicationContext());

		persistentNotification.icon = RunningEnvironment.getInstance()
				.getResId("R.drawable.ic_stat_normal");
		persistentNotification.setLatestEventInfo(RunningEnvironment
				.getInstance().getApplicationContext(), RunningEnvironment
				.getInstance().getString("R.string.application_name"),
				contentText, PendingIntent.getActivity(RunningEnvironment
						.getInstance().getApplicationContext(), 0,
						persistentIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		persistentNotification.flags = Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;
		persistentNotification.defaults = 0;
		persistentNotification.sound = null;
		persistentNotification.tickerText = null;

		if (SettingsManager.eventsPersistent()) {
			notify(PERSISTENT_NOTIFICATION_ID, persistentNotification);
		} else {
			notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
		}
	}

}
