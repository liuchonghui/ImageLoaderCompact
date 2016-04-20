package com.android.overlay.connection;

import com.android.overlay.BaseManagerInterface;

/**
 * Listener for connection state change.
 */
public interface OnDisconnectListener extends BaseManagerInterface {

	/**
	 * Disconnection occur on some reason.
	 * 
	 * @param connection
	 */
	void onDisconnect();

}
