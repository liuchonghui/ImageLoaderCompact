package com.android.overlay.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.android.overlay.OnCloseListener;
import com.android.overlay.OnConnectionChangedListener;
import com.android.overlay.OnInitializedListener;
import com.android.overlay.RunningEnvironment;
import com.android.overlay.connection.ConnectionType;
import com.android.overlay.connection.NetworkState;
import com.android.overlay.connection.OnConnectedListener;
import com.android.overlay.connection.OnDisconnectListener;
import com.android.overlay.utils.NetUtils;

/**
 * Manage network connectivity.
 */
public class NetworkManager implements OnCloseListener, OnInitializedListener {

	protected final NetworkInterfaceWatcher connectivityWatcher;
	protected NetworkState state;
	protected ConnectionType type;

	private static final NetworkManager instance;

	static {
		instance = new NetworkManager(RunningEnvironment.getInstance()
				.getApplicationContext());
		RunningEnvironment.getInstance().addManager(instance);
	}

	public static NetworkManager getInstance() {
		return instance;
	}

	protected NetworkManager(Context application) {
		connectivityWatcher = new NetworkInterfaceWatcher() {

			@Override
			protected void networkUnavailable() {
				Log.i("NetworkManager", "network unavailable");
				state = NetworkState.unavailable;
				type = null;
				RunningEnvironment.getInstance().runInBackground(
						new Runnable() {
							@Override
							public void run() {
								onUnavailable();
							}
						});
			}

			@Override
			protected void nonWifiState() {
				Log.i("NetworkManager", "network connect(nonWifiState)");
				state = NetworkState.available;
				type = ConnectionType.MOBILE;
				RunningEnvironment.getInstance().runInBackground(
						new Runnable() {
							@Override
							public void run() {
								onAvailable(ConnectionType.MOBILE);
							}
						});
			}

			@Override
			protected void wifiState() {
				Log.i("NetworkManager", "network connect(wifiState)");
				state = NetworkState.available;
				type = ConnectionType.WIFI;
				RunningEnvironment.getInstance().runInBackground(
						new Runnable() {
							@Override
							public void run() {
								onAvailable(ConnectionType.WIFI);
							}
						});
			}

		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		RunningEnvironment.getInstance().getApplicationContext()
				.registerReceiver(connectivityWatcher, filter);

		state = NetworkState.unavailable;
		type = null;
		if (NetUtils.checkNetworkState(application)) {
			state = NetworkState.available;
			if (!NetUtils.checkWifiState(application)) {
				type = ConnectionType.MOBILE;
			} else {
				type = ConnectionType.WIFI;
			}
		}
	}

	public NetworkState getState() {
		return state;
	}

	public ConnectionType getType() {
		return type;
	}

	@Override
	public void onInitialized() {
	}

	@Override
	public void onClose() {
		if (connectivityWatcher != null) {
			try {
				RunningEnvironment.getInstance().getApplicationContext()
						.unregisterReceiver(connectivityWatcher);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Network is not available. Stop connections.
	 */
	private void onUnavailable() {
		for (OnDisconnectListener listener : RunningEnvironment.getInstance()
				.getManagers(OnDisconnectListener.class)) {
			listener.onDisconnect();
		}
		RunningEnvironment.getInstance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (OnConnectionChangedListener listener : RunningEnvironment
						.getInstance().getUIListeners(
								OnConnectionChangedListener.class)) {
					listener.onConnectionClosed();
				}
			}
		});
	}

	/**
	 * New network is available. Start connection.
	 */
	private void onAvailable(final ConnectionType type) {
		for (OnConnectedListener listener : RunningEnvironment.getInstance()
				.getManagers(OnConnectedListener.class)) {
			listener.onConnected(type);
		}
		RunningEnvironment.getInstance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (OnConnectionChangedListener listener : RunningEnvironment
						.getInstance().getUIListeners(
								OnConnectionChangedListener.class)) {
					listener.onConnectionChanged(type);
				}
			}
		});
	}

	/**
	 * WARNNING# Need Filter: ConnectivityManager.CONNECTIVITY_ACTION
	 */
	public static class NetworkInterfaceWatcher extends BroadcastReceiver {

		protected void networkUnavailable() {
			Log.i("NetworkWatcher", "network module unavailable");
		}

		/**
		 * This method is called when the NetworkInterfaceWatcher is receiving a
		 * change in network connectivity has occurred. Indicating that network
		 * connectivity module is valid.
		 */
		protected void networkAvailable() {
			Log.i("NetworkWatcher", "network module available");
		}

		/**
		 * This method is called when the NetworkInterfaceWatcher is receiving a
		 * change in network connectivity has occurred. Indicating that network
		 * connectivity module is invalid.
		 */
		protected void networkInvalid() {
			Log.i("NetworkWatcher", "network module missing");
		}

		/**
		 * This method is called when the NetworkInterfaceWatcher is receiving a
		 * change in network connectivity has occurred. Indicating that network
		 * connectivity is connecting in use.
		 */
		protected void networkConnect() {
			Log.i("NetworkWatcher", "network connecting");
		}

		/**
		 * This method is called when the NetworkInterfaceWatcher is receiving a
		 * change in network connectivity has occurred. Indicating that network
		 * connectivity is connecting refuse.
		 */
		protected void networkDisconnect() {
			Log.i("NetworkWatcher", "network disconnect");
		}

		/**
		 * This method is called when the NetworkInterfaceWatcher is receiving a
		 * change in network connectivity has occurred. Indicating that network
		 * connectivity is a non-wifi-state connection.
		 */
		protected void nonWifiState() {
			Log.i("NetworkWatcher", "network with non-wifi connecting");
		}

		/**
		 * This method is called when the NetworkInterfaceWatcher is receiving a
		 * change in network connectivity has occurred. Indicating that network
		 * connectivity is a wifi-state connection.
		 */
		protected void wifiState() {
			Log.i("NetworkWatcher", "network wifi connecting");
		}

		/**
		 * network connectivity changed, make sure called only once. 1 : false;
		 * 0 : true
		 */
		private static int flags = 0xFF;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!NetUtils.isNetworkAvailable(context)
					|| !NetUtils.checkNetworkState(context)) {
				networkUnavailable();
			}

			if (!NetUtils.isNetworkAvailable(context)) {
				Log.i("NetworkWatcher", "network invalid");
				if (((flags >> 0) & 0x01) == 1) {
					flags = flags & 0xFE | 0xFE;
					networkInvalid();
				}
				return;
			}
			Log.i("NetworkWatcher", "network available");
			if (((flags >> 1) & 0x01) == 1) {
				flags = flags & 0xFC | 0x01;
				networkAvailable();
			}

			if (!NetUtils.checkNetworkState(context)) {
				Log.i("NetworkWatcher", "network disconnect");
				if (((flags >> 2) & 0x01) == 1) {
					flags = flags & 0xFB | 0xF8;
					networkDisconnect();
				}
				return;
			}

			Log.i("NetworkWatcher", "network connect");
			if (((flags >> 3) & 0x01) == 1) {
				flags = flags & 0xF3 | 0x04;
				networkConnect();
			}

			if (!NetUtils.checkWifiState(context)) {
				Log.i("NetworkWatcher", "non-wifi state");
				if (((flags >> 4) & 0x01) == 1) {
					flags = flags & 0xEF | 0xE0;
					nonWifiState();
				}
				return;
			}

			Log.i("NetworkWatcher", "wifi state");
			if (((flags >> 5) & 0x01) == 1) {
				flags = flags & 0xCF | 0x10;
				wifiState();
			}
		}
	}
}
