package com.android.overlay.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * get intents.
 * 
 * @author liu_chonghui
 * 
 */
public class IntentUtil {
	public static Intent createActivityInitValue(Context ctx,
			ComponentName component) {
		if (ctx == null || component == null) {
			return null;
		}

		Intent intent = new Intent();
		intent.setComponent(component);
		try {
			if (ctx.getPackageManager().resolveActivity(intent,
					PackageManager.MATCH_DEFAULT_ONLY) == null) {
				if (ctx.getPackageManager().resolveService(intent,
						PackageManager.MATCH_DEFAULT_ONLY) == null) {
					intent = null;
				}
			}
		} catch (Exception e) {
			intent = null;
		}

		return intent;
	}
}
