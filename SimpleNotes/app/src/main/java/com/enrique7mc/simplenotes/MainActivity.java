package com.enrique7mc.simplenotes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends ActionBarActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		insertNote("New note");

		Cursor cursor = getContentResolver().query(NotesProvider.CONTENT_URI,
				DBOpenHelper.ALL_COLUMNS, null, null, null, null);

		String[] from = {DBOpenHelper.NOTE_TEXT};
		int[] to = {android.R.id.text1};
		CursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, from, to, 0);

		ListView list = (ListView) findViewById(android.R.id.list);
		list.setAdapter(cursorAdapter);
	}

	private void insertNote(String noteText) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.NOTE_TEXT, noteText);
		Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI, values);
		Log.d(TAG, "Inserted note " + noteUri.getLastPathSegment());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
