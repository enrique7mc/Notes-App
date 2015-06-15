package com.enrique7mc.simplenotes;

import android.net.Uri;
import android.support.v4.app.Fragment;


public class EditorActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		Uri uri = getIntent().getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
		return EditorFragment.newInstance(uri);
	}
}
