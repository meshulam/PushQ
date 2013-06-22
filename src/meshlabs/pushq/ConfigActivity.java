package meshlabs.pushq;

import meshlabs.pushq.data.EntryTable;
import meshlabs.pushq.data.PushQDbHelper;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ConfigActivity extends ListActivity {
	private final static String TAG = "ConfigActivity";
	
	// extra data key in the intent we send to the editor
	public final static String INTENT_ID_KEY = "rowId";
	
	private PushQDbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_config); // not needed now that we're using ListActivity
        
        dbHelper = new PushQDbHelper(this);
    }
    
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	//ArrayAdapter adapter = new ArrayAdapter<PushItem>(this, R.layout.text_list_item, items.items);
    	//ListView view = (ListView) findViewById(R.id.config_listview);
    	//view.setAdapter(adapter);
    	
    	fillView();
    }
    
    private void fillView() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		// From database columns...
		String[] dataColumns = {EntryTable.COLUMN_TITLE,
								EntryTable.COLUMN_TIME };
		
		// ...to view items
		int[] viewIds = {android.R.id.text1, android.R.id.text2 };
		
		Cursor c = db.query(EntryTable.TABLE_NAME, 
				EntryTable.ALL_COLUMNS, 
				null, null, null, null, EntryTable.ORDER_BY);		
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, c, 
													dataColumns, viewIds, 0);
		adapter.setViewBinder(new EntryBinder());
		
		setListAdapter(adapter);
		
		
    }
    
    /**
     * Handle converting stored time format in DB to displayable value
     * @author matt
     *
     */
    class EntryBinder implements SimpleCursorAdapter.ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (columnIndex == 3) {			// Time column
				TextView tv = (TextView) view;
				int timeInternal = cursor.getInt(columnIndex);
				tv.setText(TimeHelper.asString(timeInternal));
				return true;
			}
			return false;
		}
    	
    }
    
    private void startService() {
    	ComponentName name = startService(new Intent(this, MonitorService.class));
    	if (name != null) {
    		Log.i(TAG, "Starting background service: "+name.flattenToString());
    	} else {
    		Log.i(TAG, "Starting background service (failed)");
    	}
    	
    }
    
    @Override
    protected void onListItemClick(ListView view, View item, int position, long rowId) {
    	Log.i(TAG, "Requesting to edit row="+rowId);
    	updateItemScreen(rowId);
    	
    }
    
    private void stopService() {
    	boolean success = stopService(new Intent(this, MonitorService.class));
    	Log.i(TAG, "Stopping background service success="+success);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_config, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_item:
            addItemScreen();
        	return true;
        case R.id.start_service:
        	startService();
        	return true;
        case R.id.stop_service:
        	stopService();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
    /**
     * Open the push task editor to modify the given task. 
     * @param rowId
     */
    private void updateItemScreen(long rowId) {
    	Intent intent = new Intent(this, ItemEditor.class);
    	intent.putExtra(INTENT_ID_KEY, rowId);
    	startActivity(intent);
    }
    
    /**
     * Open push task editor to add a new item
     */
    private void addItemScreen() {
    	Intent intent = new Intent(this, ItemEditor.class);
    	startActivity(intent);
    }
}
