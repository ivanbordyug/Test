package com.coffeecups.testproject;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.widget.ProfilePictureView;

import Managers.DBManager;
import Managers.TestsManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		findViewById(R.id.login_button).setOnClickListener(this);
		new TestsManager(this).runTestUP();
		getUserInfo(getIntent().getExtras().getString("userId"));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Session session = Session.getActiveSession();
		if (session == null || session.isClosed()) {
			startActivity(new Intent(UserProfileActivity.this,
					LoginActivity.class));
		}
		testInfo();
	}

	private void testInfo() {
		TestsManager tManager = new TestsManager(this);
		TextView name = (TextView) findViewById(R.id.name);
		TextView surname = (TextView) findViewById(R.id.surname);
		TextView dob = (TextView) findViewById(R.id.dob);
		if (!tManager.checkInfo(name.getText().toString(), surname.getText()
				.toString(), dob.getText().toString(), getIntent().getExtras()
				.getString("userId"), new DBManager(this))) {
			Toast.makeText(this, "text", 1000).show();
		}
	}

	@Override
	public void onClick(View v) {
		Session session = Session.getActiveSession();
		session.closeAndClearTokenInformation();
		startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			UserProfileActivity.this.finish();
			moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getUserInfo(String userId) {
		DBManager DbManager = new DBManager(this);
		// DbManager.initializeDefaultUser();
		Cursor user = getUser(DbManager, userId);
		if (user.moveToFirst()) {
			Cursor userInfo = getUserInfo(DbManager, userId);
			user.close();
			if (userInfo.moveToFirst()) {
				initializeUserInfoTab(userInfo);
				userInfo.close();
			}
		}
		DbManager.close();
	}

	private void initializeUserInfoTab(Cursor userInfo) {
		TextView name = (TextView) findViewById(R.id.name);
		TextView surname = (TextView) findViewById(R.id.surname);
		TextView dob = (TextView) findViewById(R.id.dob);
		TextView bio = (TextView) findViewById(R.id.bio_content);
		ProfilePictureView imageProfilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);

		imageProfilePictureView.setProfileId(getFieldStringValue(userInfo,
				"userId"));
		name.setText(name.getText().toString() + " "
				+ getFieldStringValue(userInfo, "name"));

		surname.setText(surname.getText().toString() + " "
				+ getFieldStringValue(userInfo, "surname"));

		dob.setText(dob.getText().toString() + " "
				+ getFieldStringValue(userInfo, "dob"));

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

	private Cursor getUser(DBManager DbManager, String userId) {
		return DbManager.select("users", "userId = ?", new String[] { userId });

	}

	private Cursor getUserInfo(DBManager DbManager, String userId) {
		return DbManager.select("usersinfo", "userId = ?",
				new String[] { userId });
	}

	private String getFieldStringValue(Cursor cursor, String fieldName) {
		return cursor.getString(cursor.getColumnIndex(fieldName));
	}
}
