package com.enrique7mc.simplenotes;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
		implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int EDITOR_REQUEST_CODE = 1;
	private CursorAdapter cursorAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cursorAdapter = new NotesCursorAdapter(this, null, 0);

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

		switch (id){
			case R.id.action_create_sample:
				insertSampleData();
				break;
			case R.id.action_delete_all:
				deleteAllNotes();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void deleteAllNotes() {
		DialogInterface.OnClickListener dialogClickListener =
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int button) {
						if (button == DialogInterface.BUTTON_POSITIVE) {
							getContentResolver().delete(NotesProvider.CONTENT_URI, null, null);
							restartLoader();

							Toast.makeText(MainActivity.this,
									getString(R.string.all_deleted),
									Toast.LENGTH_SHORT).show();
						}
					}
				};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.are_you_sure))
				.setPositiveButton(getString(android.R.string.yes), dialogClickListener)
				.setNegativeButton(getString(android.R.string.no), null)
				.show();
	}

	private void insertSampleData() {
		insertNote("Simple note");
		insertNote("Multiline\nnote");
		insertNote("Very long note, this note has a lot of text that exceeds the" +
				"width of the screen");
		restartLoader();
	}

	private void restartLoader() {
		getLoaderManager().restartLoader(0, null, this);
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

	public void openEditorForNewNote(View view) {
		Intent intent = new Intent(this, EditorActivity.class);
		startActivityForResult(intent, EDITOR_REQUEST_CODE);
	}
}
