package com.android.overlay;

/**
 * Listen for application fully initialized.
 */
public interface OnInitializedListener extends BaseManagerInterface {

	/**
	 * Called once on service start and all data were loaded.
	 * 
	 * Called from UI thread.
	 */
	void onInitialized();

}
