package meshlabs.pushq.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PushQDbHelper extends SQLiteOpenHelper {
	private static final String TAG = "PushEntryDbHelper";
	
	public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "PushQ.db";
    
    public PushQDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
    	Log.i(TAG, "Creating PushQ DB");
        db.execSQL(EntryTable.SQL_CREATE_COMMAND);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(EntryTable.SQL_DELETE_COMMAND);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
