package com.android.overlay.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.android.overlay.connection.ApnInfo;

import org.apache.http.HttpHost;

import java.util.Locale;

public final class NetUtils {

	public static HttpHost getCurrHttpProxy(Context context) {
		HttpHost proxy = null;
		try {
			ConnectivityManager connectManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = connectManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobile = connectManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifi != null && mobile != null && !wifi.isAvailable()
					&& mobile.isAvailable()) {
				ApnInfo apn = null;
				NetworkInfo active = connectManager.getActiveNetworkInfo();
				if (active != null) {
					apn = getCurrentApnInfo(context, active.getExtraInfo());
				}
				if (apn == null) {
					apn = getDefaultApnInfo(context);
				}
				if (apn != null && apn.hasProxy()) {
					proxy = new HttpHost(apn.proxy, Integer.valueOf(apn.port),
							HttpHost.DEFAULT_SCHEME_NAME);
				}
			}
		} catch (Exception e) {
			proxy = null;
		}

		return proxy;
	}

	public static boolean checkNetworkState(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean checkMobileState(Context context) {
		try {
			ConnectivityManager mgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = mgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobile != null
					&& (mobile.isAvailable() && mobile.isConnected())) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean checkWifiState(Context context) {
		try {
			ConnectivityManager connectManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = connectManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifi.isAvailable() && wifi.isConnected()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isNetworkAvailable(Context context) {
		if (isWifiAvailable(context) || isMobileAvailable(context)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isWifiAvailable(Context context) {
		try {
			ConnectivityManager connectManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo wifi = connectManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifi != null && wifi.isAvailable()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isMobileAvailable(Context context) {
		try {
			ConnectivityManager connectManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo mobile = connectManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobile != null && mobile.isAvailable()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	private static final Uri DEFAULT_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");
	private static final Uri CURRENT_APN_URI = Uri
			.parse("content://telephony/carriers/current");

	private static ApnInfo getDefaultApnInfo(Context context) {
		ApnInfo info = null;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(DEFAULT_APN_URI, null,
					null, null, null);
			if (cursor != null && cursor.moveToFirst()) {

				info = new ApnInfo();
				info.id = cursor.getString(cursor.getColumnIndex("_id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				info.apn = cursor.getString(cursor.getColumnIndex("apn"))
						.toLowerCase(Locale.getDefault());
				info.type = cursor.getString(cursor.getColumnIndex("type"));
				info.proxy = cursor.getString(cursor.getColumnIndex("proxy"));
				info.port = cursor.getString(cursor.getColumnIndex("port"));
				info.mcc = cursor.getString(cursor.getColumnIndex("mcc"));
				info.mnc = cursor.getString(cursor.getColumnIndex("mnc"));
				info.numeric = cursor.getString(cursor
						.getColumnIndex("numeric"));

				info.current = cursor.getString(cursor
						.getColumnIndex("current"));

				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			info = null;
			if (cursor != null) {
				cursor.close();
			}
		}

		return info;
	}

	private static ApnInfo getCurrentApnInfo(Context context, String apn) {
		ApnInfo info = null;
		Cursor cursor = null;

		if (apn == null) {
			return null;
		}

		try {
			cursor = context.getContentResolver().query(CURRENT_APN_URI, null,
					null, null, null);
			if (cursor != null && cursor.moveToFirst()) {

				String apnName = null;
				do {
					apnName = cursor.getString(cursor.getColumnIndex("apn"));
					if (apnName != null && apn.equalsIgnoreCase(apnName)) {
						info = new ApnInfo();
						info.id = cursor
								.getString(cursor.getColumnIndex("_id"));
						info.name = cursor.getString(cursor
								.getColumnIndex("name"));
						info.apn = cursor.getString(
								cursor.getColumnIndex("apn")).toLowerCase(
								Locale.getDefault());
						info.type = cursor.getString(cursor
								.getColumnIndex("type"));
						info.proxy = cursor.getString(cursor
								.getColumnIndex("proxy"));
						info.port = cursor.getString(cursor
								.getColumnIndex("port"));
						info.mcc = cursor.getString(cursor
								.getColumnIndex("mcc"));
						info.mnc = cursor.getString(cursor
								.getColumnIndex("mnc"));
						info.numeric = cursor.getString(cursor
								.getColumnIndex("numeric"));

						info.current = cursor.getString(cursor
								.getColumnIndex("current"));

						break;
					}
				} while (cursor.moveToNext());

				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			info = null;
			if (cursor != null) {
				cursor.close();
			}
		}

		return info;
	}
}
