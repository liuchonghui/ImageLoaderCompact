package com.android.overlay;

import android.app.Application;

/**
 * Base entry point. {@link #onCreate()} should be called first.
 * 
 * @author liu_chonghui
 */
public class RunningEnvironment extends Environment {

	protected static RunningEnvironment instance;

	public static RunningEnvironment getInstance() {
		if (instance == null) {
			throw new IllegalStateException();
		}
		return instance;
	}
	
	public RunningEnvironment() {
		super();
		instance = this;
	}

	public RunningEnvironment(String managersRes, String tablesRes) {
		super(managersRes, tablesRes);
		instance = this;
	}

	public void onCreate(Application application) {
		super.onCreate(application);
	}

	public void registerManagerClasses(Class[] cls) {
		super.registerManagerClasses(cls);
	}

	public void run(Application application) {
		super.run(application);
	}

	protected void onUnload() {
		super.onUnload();
	}
}
