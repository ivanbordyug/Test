package Managers;

import com.coffeecups.testproject.LoginActivity;
import com.coffeecups.testproject.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class TestsManager {
	Context context;

	public TestsManager(Context context) {
		this.context = context;
	}

	public void runTestUP() {
		DBManager DbManager = new DBManager(context);
		ContentValues cv = new ContentValues();
		testAddingToDatabase(DbManager, cv);
		testReadingFromDB(DbManager);

	}

	private void testAddingToDatabase(DBManager DbManager, ContentValues cv) {
		cv.put("name", "Test");
		try {
			DbManager.insert("users", null, cv);
		} catch (Exception e) {
			Toast.makeText(context, "Test adding to db failed", 1000).show();
		}
	}

	private void testReadingFromDB(DBManager DbManager) {
		try {
			DbManager.select("usersinfo", "userId = ?", new String[] { "1" });
		} catch (Exception e) {
			Toast.makeText(context, "Test reading from db failed", 1000).show();
		}
	}

	public void runTestLogin() {
		if (!isOnline()) {
			Toast.makeText(context, R.string.enableInternetMsg, 5000).show();
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public boolean checkInfo(String name, String surname, String dob,
			String uid, DBManager manager) {
		Cursor cursor = manager.select("usersinfo", "userId = ?",
				new String[] { uid });
		if (cursor.moveToFirst()) {
			if (!new String("Name: "
					+ cursor.getString(cursor.getColumnIndex("name")))
					.equals(name)) {
				cursor.close();
				return false;
			}
			if (!new String("Surname: "
					+ cursor.getString(cursor.getColumnIndex("surname")))
					.equals(surname)) {
				cursor.close();
				return false;
			}
			if (!new String("Date of birth: "
					+ cursor.getString(cursor.getColumnIndex("dob")))
					.equals(dob)) {
				cursor.close();
				return false;
			}
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}
}
