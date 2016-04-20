package com.android.overlay;

/**
 * Listener for request to wipe all sensitive application data.
 */
public interface OnWipeListener extends BaseManagerInterface {

	/**
	 * Wipe all sensitive application data.
	 * 
	 * WILL BE CALLED FROM BACKGROUND THREAD. DON'T CHANGE OR ACCESS
	 * APPLICATION'S DATA HERE!
	 */
	void onWipe();

}
