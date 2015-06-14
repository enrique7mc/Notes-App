package com.enrique7mc.simplenotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Enrique on 14/06/2015.
 */
public class NotesCursorAdapter extends CursorAdapter {
	public NotesCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String noteText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
		noteText = removeLineFeed(noteText);

		TextView tv = (TextView) view.findViewById(R.id.tvNote);
		tv.setText(noteText);
	}

	private String removeLineFeed(String text) {
		int pos = text.indexOf(10);
		if (pos != -1) {
			text = text.substring(0, pos) + " ...";
		}

		return text;
	}
}
