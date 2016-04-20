package com.android.overlay;

import android.app.Activity;

/**
 * Base listener to notify UI when data changes.
 * 
 * This listener should be registered from {@link Activity#onResume()} and
 * unregistered from {@link Activity#onPause()}.
 * 
 */
public interface BaseUIListener {
}
