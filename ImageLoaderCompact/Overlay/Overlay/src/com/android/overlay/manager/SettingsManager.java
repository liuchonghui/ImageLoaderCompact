package com.android.overlay.manager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.overlay.RunningEnvironment;

public class SettingsManager {
	private final static SettingsManager instance;

	static {
		instance = new SettingsManager();
	}

	public static SettingsManager getInstance() {
		return instance;
	}

	private SettingsManager() {
	}

	public static boolean debugLog() {
		return getBoolean(
				RunningEnvironment.getInstance().getResId(
						"R.string.debug_log_key"), RunningEnvironment
						.getInstance().getResId("R.bool.debug_log_default"));
	}

	public static boolean eventsPersistent() {
		return getBoolean(
				RunningEnvironment.getInstance().getResId(
						"R.string.events_persistent_key"),
				RunningEnvironment.getInstance().getResId(
						"R.bool.events_persistent_default"));
	}

	public static Uri eventsSound() {
		return getSound(
				RunningEnvironment.getInstance().getResId(
						"R.string.events_sound_key"),
				Settings.System.DEFAULT_NOTIFICATION_URI, RunningEnvironment
						.getInstance()
						.getResId("R.string.events_sound_default"));
	}

	public static boolean eventsVibro() {
		return getBoolean(
				RunningEnvironment.getInstance().getResId(
						"R.string.events_vibro_key"), RunningEnvironment
						.getInstance().getResId("R.bool.events_vibro_default"));
	}

	public static boolean eventsIgnoreSystemVibro() {
		return getBoolean(
				RunningEnvironment.getInstance().getResId(
						"R.string.events_ignore_system_vibro_key"),
				RunningEnvironment.getInstance().getResId(
						"R.bool.events_ignore_system_vibro_default"));
	}

	public static boolean eventsLightning() {
		return getBoolean(
				RunningEnvironment.getInstance().getResId(
						"R.string.events_lightning_key"),
				RunningEnvironment.getInstance().getResId(
						"R.bool.events_lightning_default"));
	}

	private static Uri getSound(int key, Uri defaultUri, int defaultResource) {
		String defaultValue = RunningEnvironment.getInstance().getString(
				defaultResource);
		String value = getString(key, defaultValue);
		if (TextUtils.isEmpty(value)) {
			return null;
		}
		if (defaultValue.equals(value)) {
			setString(key, defaultUri.toString());
			return defaultUri;
		}
		return Uri.parse(value);
	}

	private static String getString(int key, String def) {
		return getSharedPreferences().getString(
				RunningEnvironment.getInstance().getString(key), def);
	}

	private static void setString(int key, String value) {
		Editor editor = getSharedPreferences().edit();
		editor.putString(RunningEnvironment.getInstance().getString(key), value);
		editor.commit();
	}

	private static boolean getBoolean(int key, boolean def) {
		return getSharedPreferences().getBoolean(
				RunningEnvironment.getInstance().getString(key), def);
	}

	private static boolean getBoolean(int key, int def) {
		return getBoolean(key, RunningEnvironment.getInstance().getResources()
				.getBoolean(def));
	}

	protected static void setBoolean(int key, boolean value) {
		Editor editor = getSharedPreferences().edit();
		editor.putBoolean(RunningEnvironment.getInstance().getString(key),
				value);
		editor.commit();
	}

	private static SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(RunningEnvironment
				.getInstance().getApplicationContext());
	}
}
