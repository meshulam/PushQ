package meshlabs.pushq;

import java.util.TimerTask;

import meshlabs.pushq.data.EntryTable;
import meshlabs.pushq.data.PushQDbHelper;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MonitorTask extends TimerTask {
	private final static String TAG = "TimerTask";
	
	private final Context context;
	private final PushQDbHelper dbHelper;
	
	//String url = "https://docs.google.com/forms/d/16gsZa7bGr3N2Eo-eK5eFyUdlahn5GX_2z1m1-LA_cGE/viewform";
	long[] vibratePattern = {0, 500, 500, 500};
	
	public MonitorTask(Context c) {
		context = c;
		dbHelper = new PushQDbHelper(context);
	}
	

	@Override
	public void run() {
		Log.i(TAG, "Checking for overdue events");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		// Get all entries in a cursor
		Cursor c = db.query(EntryTable.TABLE_NAME, EntryTable.ALL_COLUMNS, null, null, null, null, null);
		
		while (c.moveToNext()) {
			boolean shouldSend = shouldSendNotification(c);
			Log.i(TAG, "id="+EntryTable.getId(c)+" title="+EntryTable.getTitle(c)+" shouldSend? "+shouldSend);
			if (shouldSend) {
				sendNotification(EntryTable.getId(c), EntryTable.getTitle(c), EntryTable.getTarget(c));
				updateTimestamp(EntryTable.getId(c));
			}
		} 
		
	}
	
	/**
	 * Test whether it's time to send a notification for a certain entry.
	 * @param c Cursor with pointer at the row to test
	 * @return
	 */
	private boolean shouldSendNotification(Cursor c) {
		long now = System.currentTimeMillis();
		long lastScheduled = TimeHelper.lastTimestamp(EntryTable.getTime(c));
		
		// If we haven't pushed a notification since the most recent scheduled time, send one.
		if (EntryTable.getLastPushTime(c) < lastScheduled ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Update LAST_PUSH field for the given rowid with the current time.
	 * @param id
	 */
	private void updateTimestamp(int id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EntryTable.COLUMN_LAST_PUSH, System.currentTimeMillis());
		
		db.update(EntryTable.TABLE_NAME, values, EntryTable._ID+"=?", new String[] {Integer.toString(id)});
	}
	
	
	private void sendNotification(int id, String title, String url) {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(title)
		        .setContentText(url)
		        .setAutoCancel(true)
		        .setUsesChronometer(false)	
		        .setVibrate(vibratePattern);
		Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		PendingIntent pendingLaunchIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingLaunchIntent);
		
		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(id, mBuilder.build());
		Log.i(TAG, "sent notification with id="+id+" title="+title);
		
	}

}
