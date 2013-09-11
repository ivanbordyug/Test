package com.coffeecups.testproject;

import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;

import org.json.JSONArray;
import org.json.JSONObject;

import Managers.DBManager;
import Managers.TestsManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;

public class UserProfileActivity extends Activity implements OnClickListener {
	String userId;
	DBManager DbManager;
	TextWatcher watcher;
	EditText etName;
	EditText etSurname;
	TextView tvName;
	TextView tvSurname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		findViewById(R.id.login_button).setOnClickListener(this);
		new TestsManager(this).runTestUP();
		DbManager = new DBManager(this);
		userId = getIntent().getExtras().getString("userId");
		etName = (EditText) findViewById(R.id.editTextName);
		etSurname = (EditText) findViewById(R.id.editTextSurname);
		getUserInfo();
		// initTextWatcher();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		testInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_button:
			Session session = Session.getActiveSession();
			session.closeAndClearTokenInformation();
			startActivity(new Intent(UserProfileActivity.this,
					LoginActivity.class));
			break;
		case R.id.name:
			initTextWatcherName();
			tvName.setVisibility(View.INVISIBLE);
			etName.setVisibility(View.VISIBLE);
			myTextChangedListener(tvName, etName, "name");
			break;
		case R.id.surname:
			initTextWatcherSurname();
			tvSurname.setVisibility(View.INVISIBLE);
			etSurname.setVisibility(View.VISIBLE);
			myTextChangedListener(tvSurname, etSurname, "surname");
			break;
		case R.id.dob:
			showDialog(0);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (etName != null)
				updateUser("name", etName.getText().toString());
			if (etSurname != null)
				updateUser("surname", etSurname.getText().toString());
			UserProfileActivity.this.finish();
			moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getUserInfo() {

		// DbManager.initializeDefaultUser();
		Cursor user = getUser();
		if (user.moveToFirst()) {
			Cursor userInfo = getUserInfoCursor();
			user.close();
			if (userInfo.moveToFirst()) {
				initializeUserInfoTab(userInfo);
				userInfo.close();
			}
		}
		DbManager.close();
	}

	private void initializeUserInfoTab(Cursor userInfo) {
		tvName = (TextView) findViewById(R.id.name);
		tvSurname = (TextView) findViewById(R.id.surname);
		TextView dob = (TextView) findViewById(R.id.dob);
		TextView bio = (TextView) findViewById(R.id.bio_content);
		ProfilePictureView imageProfilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);

		imageProfilePictureView.setProfileId(getFieldStringValue(userInfo,
				"userId"));
		etName.setText(etName.getText().toString()
				+ getFieldStringValue(userInfo, "name"));
		etName.setOnClickListener(this);

		tvSurname.setText(tvSurname.getText().toString()
				+ getFieldStringValue(userInfo, "surname"));
		tvSurname.setOnClickListener(this);

		dob.setText(dob.getText().toString()
				+ getFieldStringValue(userInfo, "dob"));
		dob.setOnClickListener(this);

		bio.setText(getFieldStringValue(userInfo, "bio"));

		addContacts(userInfo);
	}

	private void addContacts(Cursor userInfo) {
		try {
			JSONObject object = new JSONObject(getFieldStringValue(userInfo,
					"contacts")).getJSONObject("root")
					.getJSONObject("contacts");
			JSONArray contacts = object.getJSONArray("item");
			generateContacts(contacts);
		} catch (Exception e) {
		}
	}

	private void generateContacts(JSONArray contacts) {
		try {
			for (int i = 0; i < contacts.length(); i++) {
				JSONObject contact = contacts.getJSONObject(i);
				String name = contact.getString("name");
				String value = contact.getString("value");
				generateContact(name, value);
			}
		} catch (Exception e) {
		}
	}

	private void generateContact(String name, String value) {
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout rootContacts = (LinearLayout) findViewById(R.id.contacts);
		View view = inflater.inflate(R.layout.contacts_item, null);
		TextView nameTV = (TextView) view.findViewById(R.id.contactsName);
		nameTV.setText(name);
		TextView valueTV = (TextView) view.findViewById(R.id.contactsValue);
		valueTV.setText(value);
		rootContacts.addView(view);
	}

	private void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	private void myTextChangedListener(final TextView textView,
			final EditText curET, final String updateField) {
		curET.requestFocus();
		showSoftKeyboard();
		curET.addTextChangedListener(watcher);
		curET.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == event.KEYCODE_ENTER) {
					updateUser(updateField, curET.getText().toString());
					curET.removeTextChangedListener(watcher);
					textView.setVisibility(View.VISIBLE);
					curET.setVisibility(View.INVISIBLE);
				}
				return false;
			}
		});
	}

	private Cursor getUser() {
		return DbManager.select("users", "userId = ?", new String[] { userId });

	}

	private Cursor getUserInfoCursor() {
		return DbManager.select("usersinfo", "userId = ?",
				new String[] { userId });
	}

	private void updateUser(String fieldName, String value) {
		if (value.equals("")) {
			value = refreshField(fieldName);
		}
		String id = "";
		Cursor cursor = getUser();
		if (cursor.moveToFirst()) {
			id = getFieldStringValue(cursor, "id");
			ContentValues cv = new ContentValues();
			cv.put(fieldName, value);
			DbManager.update("usersinfo", cv, "id = ?", new String[] { id });
			cursor.close();
		}
	}

	private String refreshField(String fieldName) {
		String value = "";
		Cursor cursor = getUserInfoCursor();
		if (cursor.moveToFirst()) {
			value = getFieldStringValue(cursor, fieldName);
		}
		cursor.close();
		return value;
	}

	private String getFieldStringValue(Cursor cursor, String fieldName) {
		return cursor.getString(cursor.getColumnIndex(fieldName));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);
		switch (id) {
		case 0:
			return new DatePickerDialog(this, mDateSetListener, cyear, cmonth,
					cday);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if (isDayValid(year, monthOfYear, dayOfMonth)) {
				String date_selected = String.valueOf(monthOfYear + 1) + "/"
						+ String.valueOf(dayOfMonth) + "/"
						+ String.valueOf(year);
				TextView dob = (TextView) findViewById(R.id.dob);
				dob.setText(date_selected);
				updateUser("dob", date_selected);
			} else {
				Toast.makeText(UserProfileActivity.this, R.string.dateErrorMsg,
						3000).show();
			}
		}
	};

	private String validateString(String string) {
		String[] stringArr = string.split("");
		if (stringArr[stringArr.length - 1].matches("[a-zA-Z]")
				|| stringArr[stringArr.length - 1].equals("")) {
			return string;
		} else {
			return string.substring(0, stringArr.length - 1);
		}
	}

	private void initTextWatcherName() {
		tvName = (TextView) findViewById(R.id.name);

		watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				tvName.setText(validateString(s.toString()));
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

		etName.addTextChangedListener(watcher);
		etName.setText(tvName.getText());
	}

	private void initTextWatcherSurname() {
		tvSurname = (TextView) findViewById(R.id.surname);

		watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				tvSurname.setText(validateString(s.toString()));
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

		etSurname.addTextChangedListener(watcher);
		etSurname.setText(tvSurname.getText());
	}

	private boolean isDayValid(int year, int monthOfYear, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		Date today = c.getTime();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, monthOfYear);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		// and get that as a Date
		Date dateSpecified = c.getTime();

		// test your condition
		if (dateSpecified.before(today)) {
			return true;
		} else {
			return false;
		}
	}

	private void testInfo() {
		TestsManager tManager = new TestsManager(this);
		TextView name = (TextView) findViewById(R.id.name);
		TextView surname = (TextView) findViewById(R.id.surname);
		TextView dob = (TextView) findViewById(R.id.dob);
		if (!tManager.checkInfo(name.getText().toString().trim(), surname
				.getText().toString().trim(), dob.getText().toString().trim(),
				getIntent().getExtras().getString("userId"),
				new DBManager(this))) {
			Toast.makeText(this, "text", 1000).show();
		}
		testEditingInfo();
	}

	private void testEditingInfo() {
		DBManager DbManager = new DBManager(this);
		if (!(testElementsPresense() && testEditingName(DbManager)
				&& testEditingSurname(DbManager) && !isDayValid(3000, 12, 1))) {
			Toast.makeText(this, R.string.errorEditing, 1000).show();
		}
	}

	private boolean testEditingName(DBManager DbManager) {
		String testValue = "testValueName";
		String originalValue = getOriginValue("name");
		initTextWatcherName();
		myTextChangedListener(tvName, etName, "name");
		etName.setText(testValue);
		updateUser("name", tvName.getText().toString());
		if (validateData("name", testValue, DbManager)) {
			updateUser("name", originalValue);
			etName.setText("");
			tvName.setText(originalValue);
			return true;
		} else {
			updateUser("name", originalValue);
			etName.setText("");
			tvName.setText(originalValue);
			return false;
		}
	}

	private boolean testEditingSurname(DBManager DbManager) {
		String testValue = "testValueSurname";
		String originalValue = getOriginValue("surname");
		initTextWatcherSurname();
		myTextChangedListener(tvSurname, etSurname, "surname");
		etSurname.setText(testValue);
		updateUser("surname", tvSurname.getText().toString());
		if (validateData("surname", testValue, DbManager)) {
			updateUser("surname", originalValue);
			etSurname.setText("");
			tvSurname.setText(originalValue);
			return true;
		} else {
			updateUser("surname", originalValue);
			etSurname.setText("");
			tvSurname.setText(originalValue);
			return false;
		}
	}

	private String getOriginValue(String fieldName) {
		Cursor cursor = DbManager.select("usersinfo", "userId = ?",
				new String[] { userId });
		if (cursor.moveToFirst()) {
			String value = cursor.getString(cursor.getColumnIndex(fieldName));
			cursor.close();
			return value;
		} else {
			return "";
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

	private boolean testElementsPresense() {
		try {
			EditText etName = (EditText) findViewById(R.id.editTextName);
			EditText etSurname = (EditText) findViewById(R.id.editTextSurname);
			TextView dob = (TextView) findViewById(R.id.dob);
			TextView bio = (TextView) findViewById(R.id.bio_content);
			ProfilePictureView imageProfilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
