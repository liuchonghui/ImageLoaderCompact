package com.android.overlay.manager;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Process;
import android.util.Log;

import com.android.overlay.RunningEnvironment;
import com.android.overlay.utils.IntentUtil;

/**
 * @author liu_chonghui
 * 
 */
public class LogManager {

	protected static boolean debugLevel0;
	// private static final boolean debugLevel1;
	// private static final boolean debugLevel2;
	private static Method getApplicationInfo;

	// static {
	// initCompatibility();
	// debugLevel0 = SettingsManager.debugLog();

	// debugLevel1 = (getApplicationInfo(RunningEnvironment.getInstance()
	// .getApplicationContext()).flags & ApplicationInfo.FLAG_DEBUGGABLE) !=
	// 0;

	// debugLevel2 = debugLevel0 && debugLevel1;
	// };

	protected static LogManager instance;

	static {
		instance = new LogManager(RunningEnvironment.getInstance()
				.getApplicationContext());
		RunningEnvironment.getInstance().addManager(instance);
	}

	public static LogManager getInstance() {
		return instance;
	}

	private LogManager(Context context) {
		debugLevel0 = SettingsManager.debugLog();
	}
	
	public void manualDebugEnable() {
		debugLevel0 = true;
	}

	public boolean showLogcat() {
		return true;
	}

	public boolean showDetail() {
		return debugLevel0;
	}

	public boolean showReport() {
		return debugLevel0;
	}

	public boolean startReport(Context context, String content) {

		if (content != null && content.length() > 0 && context != null) {
			Intent intent = null;
			PackageInfo info = null;
			try {
				info = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			String versionName = info == null ? "Unknown" : info.versionName
					+ "(" + info.versionCode + ")";
			ComponentName component = new ComponentName(
					context.getPackageName(),
					RunningEnvironment.getInstance().getString("R.string.page_debug"));
			intent = IntentUtil.createActivityInitValue(context, component);
			if (intent != null) {
				intent.putExtra("id", versionName);
				intent.putExtra("content", content);
				context.startActivity(intent);
			}
		}

		if (context == null) {
			return false;
		}

		if (context instanceof Activity) {
			((Activity) context).finish();
		}

		Process.killProcess(Process.myPid());
		System.exit(10);

		return true;
	}

	protected static void initCompatibility() {
		try {
			getApplicationInfo = Context.class.getMethod("getApplicationInfo",
					new Class[] {});
		} catch (NoSuchMethodException e) {
		}
	}

	public static ApplicationInfo getApplicationInfo(Context context) {
		ApplicationInfo applicationInfo;
		if (getApplicationInfo != null) {
			try {
				applicationInfo = (ApplicationInfo) getApplicationInfo
						.invoke(context);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				if (cause instanceof RuntimeException) {
					throw (RuntimeException) cause;
				} else if (cause instanceof Error) {
					throw (Error) cause;
				} else {
					throw new RuntimeException(e);
				}
			} catch (IllegalAccessException ie) {
				throw new RuntimeException(ie);
			}
		} else {
			try {
				applicationInfo = context.getPackageManager()
						.getApplicationInfo(context.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				Log.e("LogManager",
						"Can not find app package in system. Debug will be disabled.");
				applicationInfo = new ApplicationInfo();
				applicationInfo.flags = 0;
			}
		}
		return applicationInfo;
	}

	static public int dString(String tag, String msg) {
		if (isDebugable()) {
			return Log.d(tag, msg);
		} else {
			return 0;
		}
	}

	static public int eString(String tag, String msg) {
		if (isDebugable()) {
			return Log.e(tag, msg);
		} else {
			return 0;
		}
	}

	static public int iString(String tag, String msg) {
		if (isDebugable()) {
			return Log.i(tag, msg);
		} else {
			return 0;
		}
	}

	static public int wString(String tag, String msg) {
		if (isDebugable()) {
			return Log.w(tag, msg);
		} else {
			return 0;
		}
	}

	static public int vString(String tag, String msg) {
		if (isDebugable()) {
			return Log.v(tag, msg);
		} else {
			return 0;
		}
	}

	static public int d(Object obj, String msg) {
		return dString(obj.toString(), msg);
	}

	static public int e(Object obj, String msg) {
		return eString(obj.toString(), msg);
	}

	static public int i(Object obj, String msg) {
		return iString(obj.toString(), msg);
	}

	static public int w(Object obj, String msg) {
		return wString(obj.toString(), msg);
	}

	static public int v(Object obj, String msg) {
		return vString(obj.toString(), msg);
	}

	public static void exception(Object obj, Exception exception) {
		if (!isDebugable()) {
			return;
		}
		forceException(obj, exception);
	}

	public static void forceException(Object obj, Exception exception) {
		System.err.println(obj.toString());
		System.err.println(getStackTrace(exception));
	}

	private static String getStackTrace(Exception exception) {
		final StringWriter result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);
		return result.toString();
	}

	public static boolean isDebugable() {
		return debugLevel0;
	}

	public String getLogPath() {
		Context ctx = RunningEnvironment.getInstance().getApplicationContext();
		if (ctx == null) {
			return "unsupport";
		}
		String dowload = android.os.Environment.DIRECTORY_DOWNLOADS;
		File downloadDir = ctx.getExternalFilesDir(dowload);
		if (downloadDir == null) {
			return "unsupport";
		}
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}

		return downloadDir.getAbsolutePath();
	}

	public String getLogName() {
		return new String("overlay_g%g.log");
	}
}
