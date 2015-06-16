package com.enrique7mc.simplenotes;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import at.markushi.ui.CircleButton;

/**
 * Created by Enrique on 14/06/2015.
 */
public class MainFragment extends Fragment
		implements LoaderManager.LoaderCallbacks<Cursor>{
	private CursorAdapter cursorAdapter;
	private at.markushi.ui.CircleButton newButton;
	private static final String TAG = MainActivity.class.getSimpleName();
	public static final int EDITOR_REQUEST_CODE = 1;
	private String queryFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		cursorAdapter = new NotesCursorAdapter(getActivity(), null, 0);
		ListView list = (ListView) v.findViewById(android.R.id.list);
		list.setAdapter(cursorAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), EditorActivity.class);
				Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
				intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
				startActivityForResult(intent, EDITOR_REQUEST_CODE);
			}
		});
		newButton = (CircleButton) v.findViewById(R.id.newButton);
		newButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), EditorActivity.class);
				startActivityForResult(intent, EDITOR_REQUEST_CODE);
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_main, menu);

		MenuItem item = menu.findItem(R.id.action_search);
		SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
				| MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		MenuItemCompat.setActionView(item, sv);
		sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d("Search Submit", query);
				QueryDb(query);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d("Search", newText);
				QueryDb(newText);
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id){
			case R.id.action_create_sample:
				insertSampleData();
				return true;
			case R.id.action_delete_all:
				deleteAllNotes();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void deleteAllNotes() {
		DialogInterface.OnClickListener dialogClickListener =
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int button) {
						if (button == DialogInterface.BUTTON_POSITIVE) {
							getActivity().getContentResolver()
									.delete(NotesProvider.CONTENT_URI, null, null);
							restartLoader();

							Toast.makeText(getActivity(),
									getString(R.string.all_deleted),
									Toast.LENGTH_SHORT).show();
						}
					}
				};

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

	private void insertNote(String noteText) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.NOTE_TEXT, noteText);
		Uri noteUri = getActivity().getContentResolver().insert(NotesProvider.CONTENT_URI, values);
		Log.d(TAG, "Inserted note " + noteUri.getLastPathSegment());
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), NotesProvider.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		cursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);
	}

	private void QueryDb(String query) {
		queryFilter = String.format("%s LIKE ?", DBOpenHelper.NOTE_TEXT);
		Cursor cursor = getActivity().getContentResolver().query(NotesProvider.CONTENT_URI,
				DBOpenHelper.ALL_COLUMNS, queryFilter, new String[]{"%" + query + "%"}, null);
		cursorAdapter.swapCursor(cursor);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDITOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			restartLoader();
		}
	}
}
