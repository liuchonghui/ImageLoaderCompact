package com.android.overlay;

/**
 * Listener for optimization request on trim memory.
 */
public interface OnTrimMemoryListener extends BaseManagerInterface {

	/**
	 * Clears all caches.
	 */
	void onTrimMemory(int level);

}
