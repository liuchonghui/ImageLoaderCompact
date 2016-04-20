package com.android.overlay;

/**
 * Listener for optimization request on low memory.
 */
public interface OnLowMemoryListener extends BaseManagerInterface {

	/**
	 * Clears all caches.
	 */
	void onLowMemory();

}
