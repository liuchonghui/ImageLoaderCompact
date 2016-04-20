package com.android.overlay;

/**
 * Listen for application to be ready to start load.
 */
public interface OnStartListener extends BaseManagerInterface {

	/**
	 * Called after service has been started before
	 * {@link OnLoadListener}.
	 * 
	 * WILL BE CALLED FROM BACKGROUND THREAD. DON'T CHANGE OR ACCESS
	 * APPLICATION'S DATA HERE!
	 * 
	 * Used to start data from UI thread before load data.
	 */
	void onStart();

}
