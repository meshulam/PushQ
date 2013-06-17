package meshlabs.pushq.data;

import android.database.Cursor;
import android.provider.BaseColumns;

public abstract class EntryTable implements BaseColumns {
	
	public static final String TABLE_NAME = "push_entries";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TARGET = "target";
	public static final String COLUMN_TIME = "pushTime";
	public static final String COLUMN_LAST_PUSH = "last_push";
	
	public static final String ORDER_BY = _ID + " DESC";
	
	public static final String[] ALL_COLUMNS =  {_ID, COLUMN_TITLE, COLUMN_TARGET, COLUMN_TIME, COLUMN_LAST_PUSH};
	
	public static final String SQL_CREATE_COMMAND = 
			"CREATE TABLE " + TABLE_NAME + "(" + EntryTable._ID + " INTEGER PRIMARY KEY, "+
					COLUMN_TITLE + " TEXT, " +
					COLUMN_TARGET + " TEXT, " +
					COLUMN_TIME + " INT DEFAULT 0, " +
					COLUMN_LAST_PUSH + " INT DEFAULT 0 )";
	
	public static final String SQL_DELETE_COMMAND = 
			"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	/**
	 * Get the _ID of a cursor's row or -1 if not pointing to a valid row. Assumes you queried using EntryColumns.ALL_COLUMNS
	 * @param c 
	 * @return
	 */
	public static int getId(Cursor c) {
		return c.getInt(0);
	}
	
	public static String getTitle(Cursor c) {
		return c.getString(1);
	}
	
	public static String getTarget(Cursor c) {
		return c.getString(2);
	}
	
	public static int getTime(Cursor c) {
		return c.getInt(3);
	}
	
	/**
	 * Stored as unix time
	 * @param c
	 * @return
	 */
	public static long getLastPushTime(Cursor c) {
		return c.getLong(4);
	}

	
	private EntryTable() { }

}