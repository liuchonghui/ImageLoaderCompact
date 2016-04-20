package com.android.overlay;

/**
 * Listener for the error.
 */
public interface OnErrorListener extends BaseUIListener {

	/**
	 * Error occurred.
	 * 
	 * @param resourceId
	 *            String with error description.
	 */
	public void onError(int resourceId);
}
