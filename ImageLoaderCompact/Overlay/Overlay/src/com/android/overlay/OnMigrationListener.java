package com.android.overlay;

/**
 * Listener for database migration.
 */
public interface OnMigrationListener extends BaseManagerInterface {

	/**
	 * Called on database migration for each intermediate versions.
	 * 
	 * @param toVersion
	 */
	void onMigrate(int toVersion);

}
