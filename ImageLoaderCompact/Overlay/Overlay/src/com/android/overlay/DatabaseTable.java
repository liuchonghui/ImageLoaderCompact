package com.android.overlay;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author liu_chonghui
 * 
 */
public interface DatabaseTable {

	/**
	 * Called on create database.
	 */
	void create(SQLiteDatabase db);

	/**
	 * Called on database migration.
	 */
	void migrate(SQLiteDatabase db, int toVersion);

	/**
	 * Called on clear database request.
	 */
	void clear();

}
