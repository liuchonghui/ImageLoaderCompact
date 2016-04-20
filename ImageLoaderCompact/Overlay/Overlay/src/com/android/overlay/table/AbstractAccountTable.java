package com.android.overlay.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.android.overlay.manager.DatabaseManager;
import com.android.overlay.table.AbstractTable;

/**
 * Table with account related information.
 */
public abstract class AbstractAccountTable extends AbstractTable {

	public static interface Fields extends BaseColumns {

		public static final String ACCOUNT = "account";

	}

	public void removeAccount(String account) {
		SQLiteDatabase db = DatabaseManager.getInstance().getWritableDatabase();
		db.delete(getTableName(), Fields.ACCOUNT + " = ?",
				new String[] { account });
	}

	public static String getAccount(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(Fields.ACCOUNT));
	}

}
