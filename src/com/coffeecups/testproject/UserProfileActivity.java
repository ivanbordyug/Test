package com.coffeecups.testproject;

import org.json.JSONArray;
import org.json.JSONObject;

import Managers.DBManager;
import Managers.TestsManager;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);
		new TestsManager(this).runTest();
		getUserInfo();
	}

	private void getUserInfo() {
		DBManager DbManager = new DBManager(this);
		// DbManager.initializeDefaultUser();
		Cursor user = getUser(DbManager);
		if (user.moveToFirst()) {
			Cursor userInfo = getUserInfo(DbManager,
					Integer.valueOf(user.getString(0)));
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

	private Cursor getUser(DBManager DbManager) {
		return DbManager.select("users", "name = ?",
				new String[] { "Ivan Bordyug" });
	}

	private Cursor getUserInfo(DBManager DbManager, int userId) {
		return DbManager.select("usersinfo", "userId = ?",
				new String[] { String.valueOf(userId) });
	}

	private String getFieldStringValue(Cursor cursor, String fieldName) {
		return cursor.getString(cursor.getColumnIndex(fieldName));
	}
}
