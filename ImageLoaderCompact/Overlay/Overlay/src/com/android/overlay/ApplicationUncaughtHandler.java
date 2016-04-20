package com.android.overlay;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.android.overlay.manager.LogManager;
import com.android.overlay.utils.LogUtils;

/**
 * 
 * @author liu_chonghui
 * 
 */
public class ApplicationUncaughtHandler implements UncaughtExceptionHandler {

	private Context mContext = null;

	public ApplicationUncaughtHandler(Context context) {
		this.mContext = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (ex != null) {
			String message = null;
			if (ex.getMessage() != null) {
				message = ex.getMessage();
			} else {
				message = ex.toString();
			}
			if (LogManager.getInstance() != null) {
				LogManager lc = LogManager.getInstance();
				if (lc.showLogcat()) {
					System.out.println(message);
				}
			}
			StringBuilder sb = new StringBuilder();
			String temp = message;
			if (temp != null) {
				sb.append(temp);
			}
			sb.append("\r\n");
			sb.append(thread.getName());
			sb.append(" Trace: \r\n");
			StackTraceElement[] elements = ex.getStackTrace();
			if (elements != null) {
				for (StackTraceElement element : elements) {
					temp = element.toString();
					if (temp != null) {
						sb.append(temp);
					}
					sb.append("\r\n");
				}
			}

			// if the exception was thrown in a background thread inside
			// AsyncTask, then the actual exception can be found with getCause
			sb.append("Cause: ");
			Throwable theCause = ex.getCause();
			if (theCause != null) {
				temp = theCause.toString();
				if (temp != null) {
					sb.append(temp);
				}
			}
			sb.append("\r\nCause Stack:\r\n");
			theCause = ex.getCause();
			if (theCause != null) {
				elements = theCause.getStackTrace();
				if (elements != null) {
					for (StackTraceElement element : elements) {
						temp = element.toString();
						if (temp != null) {
							sb.append(temp);
						}
						sb.append("\r\n");
					}
				}
			}
			if (LogManager.getInstance() != null) {
				LogManager lc = LogManager.getInstance();
				if (lc.showLogcat()) {
					System.out.println(sb.toString());
				}
				if (lc.showDetail()) {
					LogUtils.logToDefaultFile(sb.toString());
				}
				if (lc.showReport()) {
					lc.startReport(mContext, sb.toString());
				} else {
					lc.startReport(mContext, null);
				}
			}
		}
	}

	public void uncaughtException(Exception e) {
		uncaughtException(Thread.currentThread(), e);
	}

}
