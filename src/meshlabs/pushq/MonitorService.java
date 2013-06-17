package meshlabs.pushq;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MonitorService extends Service {
	private final static String TAG = "MonitorService";
	Timer serviceTimer;
	
	@Override
	public void onCreate() {
		if (serviceTimer != null) 
			serviceTimer.cancel();
		
		serviceTimer = new Timer("PushQ-ServiceTimer");
		serviceTimer.scheduleAtFixedRate(new MonitorTask(this), 5000, 1*60*1000);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		Toast.makeText(this, "Starting PushQ service", Toast.LENGTH_SHORT).show();
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy - stopping service");
		Toast.makeText(this, "Stopping PushQ service", Toast.LENGTH_SHORT).show();
		if (serviceTimer != null)
			serviceTimer.cancel();
	}

}
