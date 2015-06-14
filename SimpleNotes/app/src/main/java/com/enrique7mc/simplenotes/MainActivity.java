package com.enrique7mc.simplenotes;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends ActionBarActivity
		implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TAG = MainActivity.class.getSimpleName();
	private CursorAdapter cursorAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		insertNote("New note");

		String[] from = {DBOpenHelper.NOTE_TEXT};
		int[] to = {android.R.id.text1};

		cursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, null, from, to, 0);

		ListView list = (ListView) findViewById(android.R.id.list);
		list.setAdapter(cursorAdapter);

		getLoaderManager().initLoader(0, null, this);
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		return new CursorLoader(this, NotesProvider.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);
	}
}
