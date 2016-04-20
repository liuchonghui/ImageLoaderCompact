package com.android.overlay.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.util.Log;

import com.android.overlay.OnTimerListener;
import com.android.overlay.RunningEnvironment;
import com.android.overlay.connection.ConnectionType;
import com.android.overlay.connection.NetworkState;
import com.android.overlay.connection.OnConnectedListener;

/**
 * Support single thread connection task and run when connected tasks.
 * 
 * @author liu_chonghui
 * 
 */
public class ConnectionManager implements OnConnectedListener, OnTimerListener {

	protected static ConnectionManager instance;
	protected ExecutorService singleThreadExecutor;
	protected ExecutorService operatorExecutor;
	protected List<Runnable> runnables;

	protected int localTimer = 0;
	protected int LOCALPERIOD = 10 * 60;

	static {
		instance = new ConnectionManager();
	}

	public static ConnectionManager getInstance() {
		return instance;
	}

	private ConnectionManager() {
		runnables = new ArrayList<Runnable>();
		singleThreadExecutor = Executors
				.newSingleThreadExecutor(new ThreadFactory() {

					public Thread newThread(Runnable runnable) {
						Thread thread = new Thread(runnable, "CM-sT Processor");
						thread.setDaemon(true);
						return thread;
					}
				});
		operatorExecutor = Executors
				.newSingleThreadExecutor(new ThreadFactory() {

					public Thread newThread(Runnable runnable) {
						Thread thread = new Thread(runnable, "CM-oP Processor");
						thread.setDaemon(true);
						return thread;
					}
				});
	}

	public void doInBackground(Runnable task) {
		singleThreadExecutor.submit(task);
	}

	@Override
	public void onConnected(ConnectionType type) {
		Log.d("HTTP", "execute(onConnected)");
		if (RunningEnvironment.getInstance().isInitialized()) {
			execute();
		}
	}

	public void executeWhenConnected(Runnable runnable) {
		Log.d("HTTP", "executeWhenConnected addone");
		runnables.add(runnable);
		if (NetworkState.available == NetworkManager.getInstance().getState()) {
			Log.d("HTTP", "execute(addRunnable)");
			execute();
		}
	}

	@Override
	public void onTimer() {
		localTimer++;
		if (localTimer > LOCALPERIOD) {
			localTimer = 0;
			Log.d("HTTP", "execute(onTimer)");
			execute();
		}
	}

	protected synchronized void execute() {
		for (int i = 0; i < runnables.size();) {
			operatorExecutor.submit(runnables.get(i));
			runnables.remove(i);
		}
	}

}
