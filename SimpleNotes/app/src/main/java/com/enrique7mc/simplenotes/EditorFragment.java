package com.enrique7mc.simplenotes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Enrique on 14/06/2015.
 */
public class EditorFragment extends Fragment implements OnBackPressedListener {
	private String action;
	private EditText editor;
	private String noteFilter;
	private String oldText;
	private Uri uri;

	public static EditorFragment newInstance(Parcelable uri) {
		Bundle args = new Bundle();
		args.putParcelable(NotesProvider.CONTENT_ITEM_TYPE, uri);

		EditorFragment fragment = new EditorFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_editor, container, false);
		editor = (EditText) v.findViewById(R.id.editText);

		((EditorActivity) getActivity()).setOnBackPressedListener(this);

		uri = getArguments().getParcelable(NotesProvider.CONTENT_ITEM_TYPE);
		if (uri == null) {
			action = Intent.ACTION_INSERT;
			getActivity().setTitle(getString(R.string.new_note));
		} else {
			action = Intent.ACTION_EDIT;
			getActivity().setTitle(getString(R.string.edit_note));
			noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

			Cursor cursor = getActivity().getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS,
					noteFilter, null, null);
			cursor.moveToFirst();
			oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
			editor.setText(oldText);
			editor.requestFocus();
		}

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (action.equals(Intent.ACTION_EDIT)) {
			inflater.inflate(R.menu.menu_editor, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id){
			case android.R.id.home:
				finishEditing();
				break;
			case R.id.action_delete:
				deleteNote();
				getActivity().finish();
				break;
			case R.id.action_share:
				shareNote();
				break;
		}

		return true;
	}

	private void shareNote() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT, editor.getText().toString());
		i = Intent.createChooser(i, getString(R.string.send_note));
		startActivity(i);
	}

	private void deleteNote() {
		getActivity().getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
		Toast.makeText(getActivity(), getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
		getActivity().setResult(Activity.RESULT_OK);
	}

	private void finishEditing() {
		String newText = editor.getText().toString().trim();
		switch (action){
			case Intent.ACTION_INSERT:
				if (newText.length() == 0) {
					getActivity().setResult(Activity.RESULT_CANCELED);
				} else{
					insertNote(newText);
				}
				break;
			case Intent.ACTION_EDIT:
				if (newText.length() == 0){
					deleteNote();
				} else if(oldText.equals(newText)) {
					getActivity().setResult(Activity.RESULT_CANCELED);
				} else {
					updateNote(newText);
				}
		}

		getActivity().finish();
	}

	private void updateNote(String noteText) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.NOTE_TEXT, noteText);
		getActivity().getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
		Toast.makeText(getActivity(), getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
		getActivity().setResult(Activity.RESULT_OK);
	}

	private void insertNote(String noteText) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.NOTE_TEXT, noteText);
		getActivity().getContentResolver().insert(NotesProvider.CONTENT_URI, values);
		getActivity().setResult(Activity.RESULT_OK);
	}

	@Override
	public void doBack() {
		getActivity().getSupportFragmentManager()
					 .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		finishEditing();
	}
}
