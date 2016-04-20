package com.android.overlay;

import com.android.overlay.connection.ConnectionType;

public interface OnConnectionChangedListener extends BaseUIListener {

	void onConnectionChanged(ConnectionType type);

	void onConnectionClosed();

}
