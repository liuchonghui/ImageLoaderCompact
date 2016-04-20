package com.android.overlay;

/**
 * Listen for application to being closing.
 */
public interface OnCloseListener extends BaseManagerInterface {

	/**
	 * Called after service have been stoped.
	 * 
	 * This function will be call from UI thread.
	 */
	public void onClose();
}
