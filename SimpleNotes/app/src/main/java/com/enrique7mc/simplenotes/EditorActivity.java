package com.enrique7mc.simplenotes;

import android.net.Uri;
import android.support.v4.app.Fragment;


public class EditorActivity extends SingleFragmentActivity {
	protected OnBackPressedListener onBackPressedListener;

	@Override
	protected Fragment createFragment() {
		Uri uri = getIntent().getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
		return EditorFragment.newInstance(uri);
	}

	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
		this.onBackPressedListener = onBackPressedListener;
	}

	@Override
	public void onBackPressed() {
		if (onBackPressedListener != null) {
			onBackPressedListener.doBack();
		}
		else {
			super.onBackPressed();
		}
	}
}
