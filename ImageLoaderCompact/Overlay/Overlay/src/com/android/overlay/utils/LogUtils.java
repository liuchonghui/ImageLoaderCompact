/**
 * 
 */
package com.android.overlay.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.android.overlay.RunningEnvironment;
import com.android.overlay.manager.LogManager;

/**
 * @author liu_chonghui
 * 
 */
public class LogUtils {

	private static Logger logger;
	
	public static int d(Object obj, String msg) {
		return LogManager.d(obj, msg);
	}

	public static int e(Object obj, String msg) {
		return LogManager.e(obj, msg);
	}

	public static int i(Object obj, String msg) {
		return LogManager.i(obj, msg);
	}

	public static int w(Object obj, String msg) {
		return LogManager.w(obj, msg);
	}

	public static int v(Object obj, String msg) {
		return LogManager.v(obj, msg);
	}

	public static boolean logToFile(String filePath, String msg) {
		if (logger == null) {
			logger = Logger.getLogger(RunningEnvironment.getInstance()
					.getPackageName());
		}

		try {
			FileHandler fh = new FileHandler(filePath, 256 * 1024, 1, true);
			fh.setLevel(Level.INFO);
			fh.setFormatter(new SimpleFormatter());
			logger.setLevel(Level.INFO);
			logger.addHandler(fh);
			logger.info(msg);
			logger.removeHandler(fh);
			fh.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean logToDefaultFile(String msg) {
		LogManager lc = LogManager.getInstance();
		if (null == lc) {
			return false;
		}

		StringBuilder sb = new StringBuilder();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String logDir = lc.getLogPath();
			try {
				File defaultLogDir = new File(logDir);
				if (!defaultLogDir.exists()) {
					defaultLogDir.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			sb.append(logDir);
			sb.append(File.separator);

		} else {
			sb.append(Environment.getDataDirectory().getAbsolutePath());
			sb.append(File.separator);
		}
		sb.append(lc.getLogName());

		return logToFile(sb.toString(), msg);
	}

	public static void logToEmailAddress(Context context, String sendTo,
			String title, String text, String launchfail) {
		Uri uri = Uri.parse("mailto:" + sendTo);
		Intent msg = new Intent(Intent.ACTION_SENDTO, uri);
		msg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		msg.putExtra(Intent.EXTRA_EMAIL, sendTo);
		msg.putExtra(Intent.EXTRA_SUBJECT, title);
		msg.putExtra(Intent.EXTRA_TEXT, text);
		try {
			context.startActivity(msg);
		} catch (ActivityNotFoundException e) {
			if (launchfail != null && launchfail.length() > 0) {
				Toast.makeText(context, launchfail, Toast.LENGTH_LONG).show();
			}
		}
	}
}
