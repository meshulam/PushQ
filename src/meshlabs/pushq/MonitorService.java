package meshlabs.pushq;

import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Service which should be running all the time. Starts a timer which checks once a minute if any tasks are due.
 * The real work is done in the MonitorTask.
 * 
 * @author matt
 *
 */
public class MonitorService extends Service {
	private final static String TAG = "MonitorService";
	Timer serviceTimer;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (serviceTimer != null) 
			serviceTimer.cancel();
		
		serviceTimer = new Timer("PushQ-ServiceTimer");
		serviceTimer.scheduleAtFixedRate(new MonitorTask(this), 5000, 1*60*1000);
		
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
