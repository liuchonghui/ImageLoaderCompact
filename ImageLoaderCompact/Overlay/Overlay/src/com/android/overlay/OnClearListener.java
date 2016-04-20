package com.android.overlay;

public interface OnClearListener extends BaseManagerInterface {

	/**
	 * Clear all local data.
	 * 
	 * WILL BE CALLED FROM BACKGROUND THREAD. DON'T CHANGE OR ACCESS
	 * APPLICATION'S DATA HERE!
	 */
	void onClear();

}
