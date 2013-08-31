package Managers;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

public class TestsManager {
	Context context;

	public TestsManager(Context context) {
		this.context = context;
	}

	public void runTest() {
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
}
