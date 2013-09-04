package com.coffeecups.testproject;

import Managers.DBManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class UserAboutActivity extends Activity implements OnClickListener {
	TextWatcher watcher;
	TextView aboutTV;
	EditText aboutEdit;
	DBManager DbManager;
	String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_about);
		userId = getIntent().getExtras().getString("userId");

		aboutTV = (TextView) findViewById(R.id.aboutTV);
		aboutEdit = (EditText) findViewById(R.id.abouEditText);
		DbManager = new DBManager(this);
		aboutTV.setText(getUserAbout());
		findViewById(R.id.leaveAbout).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		initializeWatcher();
		final EditText editText = (EditText) findViewById(R.id.abouEditText);
		editText.requestFocus();
		showSoftKeyboard();
		editText.addTextChangedListener(watcher);
		// editText.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (keyCode == event.KEYCODE_ENTER) {
		// updateUser("about", editText.getText().toString());
		// editText.removeTextChangedListener(watcher);
		// }
		// return false;
		// }
		// });
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (aboutEdit != null)
				updateUser("about", aboutTV.getText().toString());
			UserAboutActivity.this.finish();
			moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	private void initializeWatcher() {

		watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				aboutTV.setText(validateString(s.toString()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		};
		aboutEdit.addTextChangedListener(watcher);
		aboutEdit.setText("");
	}

	private String validateString(String string) {
		String[] stringArr = string.split("");
		if (stringArr[stringArr.length - 1].matches("[a-zA-Z]")
				|| stringArr[stringArr.length - 1].equals("")) {
			return string;
		} else {
			return string.substring(0, stringArr.length - 1);
		}
	}

	private void updateUser(String fieldName, String value) {
		String id = "";
		Cursor cursor = getUser();
		if (cursor.moveToFirst()) {
			id = getFieldStringValue(cursor, "id");
			ContentValues cv = new ContentValues();
			cv.put(fieldName, value);
			DbManager.update("usersinfo", cv, "id = ?", new String[] { id });
		}
	}

	private Cursor getUser() {
		return DbManager.select("usersinfo", "userId = ?",
				new String[] { userId });

	}

	@Override
	protected void onPause() {
		if (aboutEdit != null)
			updateUser("about", aboutTV.getText().toString());
		super.onPause();
	}

	private String getFieldStringValue(Cursor cursor, String fieldName) {
		return cursor.getString(cursor.getColumnIndex(fieldName));
	}

	private String getUserAbout() {
		Cursor cursor = getUser();
		if (cursor.moveToFirst()) {
			return getFieldStringValue(cursor, "about");
		}
		return "null";
	}
}
