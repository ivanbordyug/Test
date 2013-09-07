package com.coffeecups.testproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.FacebookException;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

public class PickerActivity extends Activity {
	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	private FriendPickerFragment friendPickerFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pickers);

		Bundle args = getIntent().getExtras();

		friendPickerFragment = new FriendPickerFragment(args);

		// Set the listener to handle errors
		friendPickerFragment
				.setOnErrorListener(new PickerFragment.OnErrorListener() {
					@Override
					public void onError(PickerFragment<?> fragment,
							FacebookException error) {
						PickerActivity.this.onError(error);
					}
				});
		// Set the listener to handle button clicks
		friendPickerFragment
				.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
					@Override
					public void onDoneButtonClicked(PickerFragment<?> fragment) {
						finishActivity();
					}
				});

	}

	private void onError(Exception error) {
		onError(error.getLocalizedMessage(), false);
	}

	private void onError(String error, final boolean finishActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error_dialog_title)
				.setMessage(error)
				.setPositiveButton(R.string.error_dialog_button_text,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								if (finishActivity) {
									finishActivity();
								}
							}
						});
		builder.show();
	}

	private void finishActivity() {
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
		friendPickerFragment.loadData(false);
	}
}
