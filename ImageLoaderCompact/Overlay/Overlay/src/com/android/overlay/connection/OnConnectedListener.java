package com.android.overlay.connection;

import com.android.overlay.BaseManagerInterface;

/**
 * Listener for connection state change.
 */
public interface OnConnectedListener extends BaseManagerInterface {

	void onConnected(ConnectionType type);

}
