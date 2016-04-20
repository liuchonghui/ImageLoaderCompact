package com.android.overlay.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.overlay.DatabaseTable;
import com.android.overlay.OnClearListener;
import com.android.overlay.OnLoadListener;
import com.android.overlay.OnMigrationListener;
import com.android.overlay.RunningEnvironment;
import com.android.overlay.table.AbstractAccountTable;

/**
 * Management database file.
 * 
 * @author liu_chonghui
 */
public class DatabaseManager extends SQLiteOpenHelper implements
		OnLoadListener, OnClearListener {

	protected static String DATABASE_NAME = "overlay.db";
	protected static int DATABASE_VERSION = 1;

	protected final SQLiteException DOWNGRAD_EXCEPTION = new SQLiteException(
			"Database file was deleted");

	protected final ArrayList<DatabaseTable> registeredTables;

	protected static DatabaseManager instance;

	static {
		instance = new DatabaseManager();
		RunningEnvironment.getInstance().addManager(instance);
	}

	public static DatabaseManager getInstance() {
		return instance;
	}

	protected DatabaseManager() {
		super(RunningEnvironment.getInstance().getApplicationContext(),
				DATABASE_NAME, null, DATABASE_VERSION);
		registeredTables = new ArrayList<DatabaseTable>();
	}

	public void addTable(DatabaseTable table) {
		registeredTables.add(table);
	}

	@Override
	public void onLoad() {
		try {
			getWritableDatabase(); // Force onCreate or onUpgrade
		} catch (SQLiteException e) {
			if (e == DOWNGRAD_EXCEPTION) {
				return;
			} else {
				throw e;
			}
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (DatabaseTable table : registeredTables) {
			table.create(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// if (oldVersion > newVersion) {
		// File file = new File(db.getPath());
		// file.delete();
		// throw DOWNGRAD_EXCEPTION;
		// } else {
		while (oldVersion < newVersion) {
			oldVersion = oldVersion + 1;
			migrate(db, oldVersion);
			for (DatabaseTable table : registeredTables) {
				table.migrate(db, oldVersion);
			}
			for (OnMigrationListener listener : RunningEnvironment
					.getInstance().getManagers(OnMigrationListener.class)) {
				listener.onMigrate(oldVersion);
			}
		}
		// }
	}

	protected void migrate(SQLiteDatabase db, int toVersion) {
		switch (toVersion) {
		case 0:
			break;
		default:
			break;
		}
	}

	@Override
	public void onClear() {
		for (DatabaseTable table : registeredTables) {
			table.clear();
		}
	}

	public void removeAccount(String account) {
		for (DatabaseTable table : registeredTables) {
			if (table instanceof AbstractAccountTable) {
				((AbstractAccountTable) table).removeAccount(account);
			}
		}
	}

	/**
	 * Builds IN statement for specified collection of values.
	 * 
	 * @param <T>
	 * @param column
	 * @param values
	 * @return "column IN (value1, ... valueN)" or
	 *         "(column IS NULL AND column IS NOT NULL)" if ids is empty.
	 */
	public static <T> String in(String column, Collection<T> values) {
		if (values.isEmpty()) {
			return new StringBuilder("(").append(column)
					.append(" IS NULL AND ").append(column)
					.append(" IS NOT NULL)").toString();
		}

		StringBuilder builder = new StringBuilder(column);
		builder.append(" IN (");
		Iterator<T> iterator = values.iterator();
		while (iterator.hasNext()) {
			T value = iterator.next();
			if (value instanceof String) {
				builder.append(DatabaseUtils.sqlEscapeString((String) value));
			} else {
				builder.append(value.toString());
			}
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append(")");
		return builder.toString();
	}

	public static void execSQL(SQLiteDatabase db, String sql) {
		db.execSQL(sql);
	}

	public static void dropTable(SQLiteDatabase db, String table) {
		execSQL(db, "DROP TABLE IF EXISTS " + table + ";");
	}

	public static void renameTable(SQLiteDatabase db, String table,
			String newTable) {
		execSQL(db, "ALTER TABLE " + table + " RENAME TO " + newTable + ";");
	}

	public static String commaSeparatedFromCollection(Collection<String> strings) {
		StringBuilder builder = new StringBuilder();
		for (String value : strings) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(value.replace("\\", "\\\\").replace(",", "\\,"));
		}
		return builder.toString();
	}

	public static Collection<String> collectionFromCommaSeparated(String value) {
		Collection<String> collection = new ArrayList<String>();
		boolean escape = false;
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < value.length(); index++) {
			char chr = value.charAt(index);
			if (!escape) {
				if (chr == '\\') {
					escape = true;
					continue;
				} else if (chr == ',') {
					collection.add(builder.toString());
					builder = new StringBuilder();
					continue;
				}
			}
			escape = false;
			builder.append(chr);
		}
		collection.add(builder.toString());
		return Collections.unmodifiableCollection(collection);
	}

}
