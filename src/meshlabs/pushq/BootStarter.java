package meshlabs.pushq;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Start the service on boot. 
 * @author matt
 *
 */
public class BootStarter extends BroadcastReceiver {
	private final static String TAG = "BootStarter";

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equalsIgnoreCase(intent.getAction())) {
	    	ComponentName name = context.startService(new Intent(context, MonitorService.class));
	    	if (name != null) {
	    		Log.i(TAG, "Starting background service: "+name.flattenToString());
	    	} else {
	    		Log.i(TAG, "Starting background service (failed)");
	    	}
		}

	}

}
