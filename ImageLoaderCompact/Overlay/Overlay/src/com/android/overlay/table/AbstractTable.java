package com.android.overlay.table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.android.overlay.DatabaseTable;
import com.android.overlay.manager.DatabaseManager;

/**
 * @author liu_chonghui
 * 
 */
public abstract class AbstractTable implements DatabaseTable {

	protected abstract String[] getProjection();

	protected String getListOrder() {
		return null;
	}

	protected abstract String getTableName();

	public Cursor list() {
		SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
		return db.query(getTableName(), getProjection(), null, null, null,
				null, getListOrder());
	}

	@Override
	public void clear() {
		SQLiteDatabase db = DatabaseManager.getInstance().getWritableDatabase();
		db.delete(getTableName(), null, null);
	}

	@Override
	public void migrate(SQLiteDatabase db, int toVersion) {
	}

	protected void writeStringSegment(SQLiteStatement statement, int index,
			String value) {
		if (null == value) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, value);
		}
	}

	protected String getStringSegment(Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			return null;
		} else {
			return cursor.getString(index);
		}
	}

	protected byte[] getSerializedObject(Serializable s) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(s);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
		}
		byte[] result = baos.toByteArray();
		return result;
	}

	protected static Object readSerializedObject(byte[] in) {
		Object result = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(in);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			result = ois.readObject();
		} catch (Exception e) {
			result = null;
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
		}
		return result;
	}
}
