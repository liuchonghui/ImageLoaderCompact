package com.android.overlay;

/**
 * Listen for application to being closed.
 */
public interface OnUnloadListener extends BaseManagerInterface {

	/**
	 * Called before application to be killed after
	 * {@link OnCloseListener#onClose()} has been called.
	 * 
	 * WILL BE CALLED FROM BACKGROUND THREAD. DON'T CHANGE OR ACCESS
	 * APPLICATION'S DATA HERE!
	 */
	public void onUnload();

}
