package com.android.overlay;

/**
 * Listen for application to be ready to load data.
 */
public interface OnLoadListener extends BaseManagerInterface {

	/**
	 * Called after service has been started before
	 * {@link OnInitializedListener}.
	 * 
	 * WILL BE CALLED FROM BACKGROUND THREAD. DON'T CHANGE OR ACCESS
	 * APPLICATION'S DATA HERE!
	 * 
	 * Used to load data from DB and post request to UI thread to update data.
	 */
	void onLoad();

}
