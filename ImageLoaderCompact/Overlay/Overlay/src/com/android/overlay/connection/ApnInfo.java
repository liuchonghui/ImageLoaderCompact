package com.android.overlay.connection;

/**
 * @author liu_chonghui
 * 
 */
public class ApnInfo {
	public String id = "";
	public String apn = "";
	public String proxy = "";
	public String name = "";
	public String port = "";
	public String type = "";
	public String mcc = "";
	public String mnc = "";
	public String numeric = "";
	public String current = "";

	public boolean hasProxy() {
		if (proxy != null && proxy.length() > 0 && port != null
				&& port.length() > 0) {
			return true;
		}
		return false;
	}
}
