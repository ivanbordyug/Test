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
import android.widget.Toast;

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
		aboutEdit = (EditText) findViewById(R.id.aboutEditText);
		DbManager = new DBManager(this);
		aboutTV.setText(getUserAbout());
		aboutTV.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!testUserAbout() && checkAllElementsPresense()) {
			Toast.makeText(this, R.string.errorEditing, 1000).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		initializeWatcher();
		aboutEdit.setVisibility(View.VISIBLE);
		aboutTV.setVisibility(View.INVISIBLE);
		aboutEdit.requestFocus();
		showSoftKeyboard();
		aboutEdit.addTextChangedListener(watcher);
		aboutEdit.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == event.KEYCODE_ENTER) {
				}
				return false;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		aboutEdit.setVisibility(View.INVISIBLE);
		aboutTV.setVisibility(View.VISIBLE);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (aboutEdit != null)
				updateUser("bio", aboutTV.getText().toString());
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
		aboutEdit.setText(aboutTV.getText());
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
			return getFieldStringValue(cursor, "bio");
		}
		return "null";
	}

	private boolean testUserAbout() {
		String originalValue = getUserAbout();
		String testValue = "testValueUserAbout";
		initializeWatcher();
		aboutEdit.setText(testValue);
		updateUser("bio", aboutTV.getText().toString());
		if (validateData("bio", testValue, DbManager)) {
			updateUser("bio", originalValue);
			aboutEdit.setText("");
			aboutTV.setText(originalValue);
			return true;
		} else {
			updateUser("bio", originalValue);
			aboutEdit.setText("");
			aboutTV.setText(originalValue);
			return false;
		}
	}

	private boolean validateData(String fieldName, String expectedValue,
			DBManager DbManager) {
		Cursor cursor = getUserInfoCursor();
		if (cursor.moveToFirst()) {
			if (cursor.getString(cursor.getColumnIndex(fieldName)).equals(
					expectedValue)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean checkAllElementsPresense() {
		try {
			TextView aboutTV = (TextView) findViewById(R.id.aboutTV);
			EditText aboutEdit = (EditText) findViewById(R.id.aboutEditText);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Cursor getUserInfoCursor() {
		return DbManager.select("usersinfo", "userId = ?",
				new String[] { userId });
	}
}
