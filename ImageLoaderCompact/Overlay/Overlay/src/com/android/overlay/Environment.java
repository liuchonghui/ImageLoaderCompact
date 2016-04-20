package com.android.overlay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.overlay.manager.LogManager;

/**
 * Entry point boilerplate.
 * 
 * @author liu_chonghui
 */
public class Environment {

	protected Application mApplication;

	protected final ArrayList<Object> registeredManagers;

	protected Map<Class<? extends BaseManagerInterface>, Collection<? extends BaseManagerInterface>> managerInterfaces;

	protected Map<Class<? extends BaseUIListener>, Collection<? extends BaseUIListener>> uiListeners;

	protected final ExecutorService backgroundExecutor;

	protected final Handler handler;

	protected String overlayManagers;

	protected String overlayTables;

	protected boolean serviceStarted;

	protected boolean appInitialized;

	protected boolean firstNotified;

	protected boolean closing;

	protected boolean closed;

	protected Future<Void> loadFuture;

	protected final Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			for (OnTimerListener listener : getManagers(OnTimerListener.class)) {
				listener.onTimer();
			}
			if (!closing) {
				startTimer();
			}
		}
	};

	public Environment() {
		this(null, null);
	}

	public Environment(String overlayManagersRes, String overlayTablesRes) {
		this.overlayManagers = overlayManagersRes;
		this.overlayTables = overlayTablesRes;
		serviceStarted = false;
		appInitialized = false;
		firstNotified = false;
		closing = false;
		closed = false;
		uiListeners = new HashMap<Class<? extends BaseUIListener>, Collection<? extends BaseUIListener>>();
		managerInterfaces = new HashMap<Class<? extends BaseManagerInterface>, Collection<? extends BaseManagerInterface>>();
		registeredManagers = new ArrayList<Object>();
		handler = new Handler(Looper.getMainLooper());
		backgroundExecutor = Executors
				.newSingleThreadExecutor(new ThreadFactory() {
					@Override
					public Thread newThread(Runnable runnable) {
						Thread thread = new Thread(runnable,
								"Background executor service");
						thread.setPriority(Thread.MIN_PRIORITY);
						thread.setDaemon(true);
						return thread;
					}
				});
	}

	public void onCreate(Application application) {
		mApplication = application;
		onCreate();
	}

	public void onCreate() {
		Log.d("ENV", "onCreate");
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		TypedArray managerClasses = null;
		try {
			managerClasses = getResources().obtainTypedArray(
					getResId(overlayManagers));
		} catch (Exception e) {
			e.printStackTrace();
			managerClasses = null;
		}
		if (managerClasses != null) {
			for (int index = 0; index < managerClasses.length(); index++) {
				String className = managerClasses.getString(index);
				try {
					Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			managerClasses.recycle();
		}

		TypedArray tableClasses = null;
		try {
			tableClasses = getResources().obtainTypedArray(
					getResId(overlayTables));
		} catch (Exception e) {
			e.printStackTrace();
			tableClasses = null;
		}
		if (tableClasses != null) {
			for (int index = 0; index < tableClasses.length(); index++) {
				String className = tableClasses.getString(index);
				try {
					Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			tableClasses.recycle();
		}
	}

	public void registerManagerClasses(Class[] cls) {
		try {
			for (int i = 0; i < cls.length; i++) {
				Class.forName(cls[i].getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run(Application application) {
		application.startService(KeepAliveService.createIntent(application));
	}

	public boolean isInitialized() {
		return appInitialized;
	}

	protected void onStart() {
		Log.d("ENV", "onStart");
		startManagerSize = getManagers(OnStartListener.class).size();
		if (startManagerSize > 0) {
			for (OnStartListener listener : getManagers(OnStartListener.class)) {
				new Thread(new OnStartRunnable(listener)).start();
			}
		} else {
			onManagerStarted();
		}
	}

	class OnStartRunnable implements Runnable {
		OnStartListener listener;
		public OnStartRunnable(OnStartListener listener) {
			this.listener = listener;
		}

		@Override
		public void run() {
			try {
				if (this.listener != null) {
					this.listener.onStart();
				}
			} finally {
				notifyManagerStarted();
			}
		}
	}

	int startManagerSize;
	int runningManagerSize;

	protected synchronized void notifyManagerStarted() {
		runningManagerSize = runningManagerSize + 1;
		if (runningManagerSize >= startManagerSize) {
			onManagerStarted();
		}
	}

	protected void onLoad() {
		Log.d("ENV", "onLoad");
		for (OnLoadListener listener : getManagers(OnLoadListener.class)) {
			listener.onLoad();
		}
	}

	protected void onInitialized() {
		Log.d("ENV", "onInitialized");
		for (OnInitializedListener listener : getManagers(OnInitializedListener.class)) {
			listener.onInitialized();
		}
		appInitialized = true;

		if (KeepAliveService.getInstance() != null) {
			KeepAliveService.getInstance().changeForeground();
		}
		startTimer();
	}

	protected void onClose() {
		Log.d("ENV", "onServiceDestroy");
		for (Object manager : registeredManagers) {
			if (manager instanceof OnCloseListener) {
				((OnCloseListener) manager).onClose();
			}
		}
		closed = true;
	}

	protected void onUnload() {
		Log.d("ENV", "onUnload");
		for (Object manager : registeredManagers) {
			if (manager instanceof OnUnloadListener) {
				((OnUnloadListener) manager).onUnload();
			}
		}
	}

	public boolean doNotify() {
		if (firstNotified) {
			return false;
		}
		firstNotified = true;
		return true;
	}

	public void onServiceStarted() {
		Log.d("ENV", "onServiceStarted");
		if (serviceStarted) {
			return;
		}
		serviceStarted = true;
		onStart();
	}

	public void onManagerStarted() {
		Log.d("ENV", "onManagerStarted");
		loadFuture = backgroundExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					Log.d("ENV", "loadFuture onLoad");
					onLoad();
				} finally {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								loadFuture.get();
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							} catch (ExecutionException e) {
								throw new RuntimeException(e);
							}
							Log.d("ENV", "loadFuture onInitialized");
							onInitialized();
						}
					});
				}
				return null;
			}
		});
	}

	public void onServiceDestroy() {
		Log.d("ENV", "onServiceDestroy");
		if (closed) {
			return;
		}
		onClose();
		runInBackground(new Runnable() {
			@Override
			public void run() {
				onUnload();
			}
		});
	}

	public void requestToClose() {
		closing = true;
		try {
			getApplicationContext().stopService(
					KeepAliveService.createIntent(getApplicationContext()));
		} catch (Exception e) {
		}
	}

	public boolean isClosing() {
		return closing;
	}

	public static final int SDK_INT = Integer.valueOf(Build.VERSION.SDK);

	public boolean isContactsSupported() {
		return SDK_INT >= 5
				&& getApplicationContext().checkCallingOrSelfPermission(
						"android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED;
	}

	public void onLowMemory() {
		for (OnLowMemoryListener listener : getManagers(OnLowMemoryListener.class)) {
			listener.onLowMemory();
		}
	}

	public void onTrimMemory(int level) {
		for (OnTrimMemoryListener listener : getManagers(OnTrimMemoryListener.class)) {
			listener.onTrimMemory(level);
		}
	}

	public void onTerminate() {
		requestToClose();
	}

	protected void startTimer() {
		runOnUiThreadDelay(timerRunnable, OnTimerListener.DELAY);
	}

	public void addManager(Object manager) {
		registeredManagers.add(manager);
	}

	public void removeManager(Object manager) {
		registeredManagers.remove(manager);
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseManagerInterface> Collection<T> getManagers(
			Class<T> cls) {
		if (closed) {
			return Collections.emptyList();
		}

		Collection<T> collection = (Collection<T>) managerInterfaces.get(cls);
		if (collection == null) {
			collection = new ArrayList<T>();
			for (Object manager : registeredManagers) {
				if (cls.isInstance(manager)) {
					collection.add((T) manager);
				}
			}
			collection = Collections.unmodifiableCollection(collection);
			managerInterfaces.put(cls, collection);
		}
		return collection;
	}

	public void requestToClear() {
		runInBackground(new Runnable() {
			@Override
			public void run() {
				clear();
			}
		});
	}

	protected void clear() {
		for (Object manager : registeredManagers) {
			if (manager instanceof OnClearListener) {
				((OnClearListener) manager).onClear();
			}
		}
	}

	/**
	 * Do not modify
	 */
	public void requestToWipe() {
		runInBackground(new Runnable() {
			@Override
			public void run() {
				clear();
				for (Object manager : registeredManagers) {
					if (manager instanceof OnWipeListener) {
						((OnWipeListener) manager).onWipe();
					}
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected <T extends BaseUIListener> Collection<T> getOrCreateUIListeners(
			Class<T> cls) {
		Collection<T> collection = (Collection<T>) uiListeners.get(cls);
		if (collection == null) {
			collection = new ArrayList<T>();
			uiListeners.put(cls, collection);
		}
		return collection;
	}

	public <T extends BaseUIListener> Collection<T> getUIListeners(Class<T> cls) {
		if (closed) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableCollection(getOrCreateUIListeners(cls));
	}

	public <T extends BaseUIListener> void addUIListener(Class<T> cls,
			T listener) {
		getOrCreateUIListeners(cls).add(listener);
	}

	public <T extends BaseUIListener> void removeUIListener(Class<T> cls,
			T listener) {
		getOrCreateUIListeners(cls).remove(listener);
	}

	public void onError(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (OnErrorListener onErrorListener : getUIListeners(OnErrorListener.class)) {
					onErrorListener.onError(resourceId);
				}
			}
		});
	}

	public void onError(NetworkException networkException) {
		LogManager.exception(getApplicationContext(), networkException);
		onError(networkException.getResourceId());
	}

	public void runInBackground(final Runnable runnable) {
		backgroundExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
				}
			}
		});
	}

	public void runOnUiThread(final Runnable runnable) {
		handler.post(runnable);
	}

	public void runOnUiThreadDelay(final Runnable runnable, long delayMillis) {
		handler.postDelayed(runnable, delayMillis);
	}

	public int getResId(String res) {
		return getResId(mApplication, res);
	}

	public Application getApplication() {
		return mApplication;
	}

	public Context getApplicationContext() {
		return mApplication.getApplicationContext();
	}

	public final String getString(String resStr) {
		int id = getResId(resStr);
		if (id == 0) {
			return null;
		}
		return mApplication.getString(id);
	}

	public final String getString(int resId) {
		return mApplication.getString(resId);
	}

	public final String getString(int resId, Object... formatArgs) {
		return mApplication.getString(resId, formatArgs);
	}

	public Resources getResources() {
		return mApplication.getResources();
	}

	public final String getPackageName() {
		return mApplication.getPackageName();
	}

	public static int getResId(Context context, String res) {
		try {
			String[] tmp = res.split("[.]");
			if (tmp.length == 3 && tmp[0].equals("R")) {
				return getResId(context, tmp[1], tmp[2]);
			} else
				return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	static int getResId(Context context, String resType, String resName) {
		try {
			Field tmp = Class.forName(
					context.getPackageName() + ".R$" + resType).getField(
					resName);
			int resId = Integer.parseInt(tmp.get(tmp.getName()).toString());
			return resId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int[] getResIds(Context context, String res) {
		try {
			String[] tmp = res.split("[.]");
			if (tmp.length == 3 && tmp[0].equals("R")) {
				return getResIds(context, tmp[1], tmp[2]);
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static int[] getResIds(Context context, String resType, String resName) {
		try {
			Field tmp = Class.forName(
					context.getPackageName() + ".R$" + resType).getField(
					resName);
			int[] obj = (int[]) tmp.get(tmp.getName());
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
