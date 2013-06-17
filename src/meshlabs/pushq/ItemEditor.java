package meshlabs.pushq;

import meshlabs.pushq.data.EntryTable;
import meshlabs.pushq.data.PushQDbHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class ItemEditor extends Activity {
	private final static String TAG = "ItemEditor";
	
	PushQDbHelper dbHelper;
	
	long currentRowId = -1;
	//int timeValue = 0; // Store time of the entry so it can be passed to the 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        dbHelper = new PushQDbHelper(this);
        
        Intent i = getIntent();
        currentRowId = i.getLongExtra(ConfigActivity.INTENT_ID_KEY, -1);
        
        if (currentRowId < 0) {		// Inserting a new row
        	Log.i(TAG, "Editing new entry");
        } else {
        	Log.i(TAG, "Editing entry id="+currentRowId);
            populateFields();	
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_editor, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
   
    /*
    public static class TimePickerFragment extends DialogFragment {
    	private final int initialHour;
    	private final int initialMinute;
    	
    	public TimePickerFragment() {
    		// Use the current time as the default values for the picker
    		final Calendar c = Calendar.getInstance();
    		initialHour = c.get(Calendar.HOUR_OF_DAY);
    		initialMinute = c.get(Calendar.MINUTE);
    	}
    	
    	public TimePickerFragment(int time) {
    		initialHour = TimeHelper.getHours(time);
    		initialMinute = TimeHelper.getMinutes(time);
    	}
    	

    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {


    		// Create a new instance of TimePickerDialog and return it
    		return new TimePickerDialog(getActivity(), this, initialHour, initialMinute,
    				DateFormat.is24HourFormat(getActivity()));
    	}


    }
    
    // called when pressing time picker button
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(timeValue);
        newFragment.show(getFragmentManager(), "timePicker");
    }
    
    */
    
    /**
     * Populate form fields from currentRowId
     */
    private void populateFields() {
    	if (currentRowId < 0) return;
    	
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor c = db.query(EntryTable.TABLE_NAME, 
    			EntryTable.ALL_COLUMNS, 
    			EntryTable._ID +"=?",	// WHERE id= 
    			new String[] { Long.toString(currentRowId) },	//  rowid
    			null, null, null);
    	
    	if (!c.moveToFirst()) {
    		Log.i(TAG, "Empty cursor! no content to fill");
    		return;
    	}
    	
    	EditText titleView = (EditText) findViewById(R.id.form_title);
    	titleView.setText(c.getString(1));	// Order in PushQDbHelper.EntryColumns.ALL_COLUMNS
    	
    	EditText targetView = (EditText) findViewById(R.id.form_url);
    	targetView.setText(c.getString(2));
    	
    	TimePicker timeView = (TimePicker) findViewById(R.id.form_timepicker);
    	timeView.setCurrentHour(TimeHelper.getHours(c.getInt(3)));
    	timeView.setCurrentMinute(TimeHelper.getMinutes(c.getInt(3)));
    	
    }
    
    /**
     * Handle "remove item" button press
     * @param view
     */
    public void removeItem(View view) {
    	if (currentRowId < 0) {
    		Log.i(TAG, "Can't remove unsaved entry. Canceling.");
    	} else {
	    	
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			int result = db.delete(EntryTable.TABLE_NAME, 
					EntryTable._ID +"=?", new String[] { Long.toString(currentRowId) });
			
	    	Log.i(TAG, "Removing entry with id="+currentRowId+", deleted="+result);
    	}
    	finish();
    }
    
    /**
     * Handle "submit" button press
     */
    public void submitForm(View view) {
    	EditText titleView = (EditText) findViewById(R.id.form_title);
    	String title = titleView.getText().toString();
    	EditText urlView = (EditText) findViewById(R.id.form_url);
    	String url = urlView.getText().toString();
    	
    	TimePicker timeView = (TimePicker) findViewById(R.id.form_timepicker);
    	int time = TimeHelper.toTime(timeView.getCurrentHour(), timeView.getCurrentMinute());
    	
    	if (title != null && url != null) {
    		ContentValues entry = new ContentValues();
    		entry.put(EntryTable.COLUMN_TITLE, title);
    		entry.put(EntryTable.COLUMN_TARGET, url);
    		entry.put(EntryTable.COLUMN_TIME, time);
    		entry.put(EntryTable.COLUMN_LAST_PUSH, System.currentTimeMillis());
    		
    		if (currentRowId >= 0)
    			entry.put(EntryTable._ID, currentRowId);
    		
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			long value = db.insertWithOnConflict(EntryTable.TABLE_NAME, null, entry, SQLiteDatabase.CONFLICT_REPLACE);
			
			if (value >= 0) {
		    	Log.i(TAG, "submitted title="+title+" url="+url+" time="+time);
		    	Toast.makeText(this, "Added/updated PushQ '"+title+"'", Toast.LENGTH_SHORT);
		    	finish();
			} else {
				Toast.makeText(this, "Invalid entry! Can't save.", Toast.LENGTH_SHORT).show();
			}
    	}
    	

    }

}
