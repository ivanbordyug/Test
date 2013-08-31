package Managers;

import java.io.InputStream;

import org.json.JSONObject;

import com.coffeecups.testproject.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {
	Context context;
	private SQLiteDatabase database;
	private String tableUsers = "users";
	private String tableUsersInfo = "usersinfo";

	public DBManager(Context context) {
		super(context, "CoffeeDB", null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table users ("
				+ "id integer primary key autoincrement," + "name text" + ");");
		db.execSQL("create table usersinfo ("
				+ "id integer primary key autoincrement," + "userId integer,"
				+ "name text," + "surname text," + "dob text," + "bio text,"
				+ "contacts text" + ");");
		db.execSQL("INSERT INTO " + tableUsers + " values(1,'Ivan Bordyug')");
		db.execSQL("INSERT INTO " + tableUsersInfo
				+ " values(1,1, 'Ivan', 'Bordyug', '10.09.1990', '" + getBio()
				+ "', '" + getContacts() + "')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void initializeDefaultUser() {
		ContentValues cv = new ContentValues();
		initializeUserInfo(cv, "Ivan Bordyug");
	}

	private long initializeUser(ContentValues cv, String userName) {
		database = getWritableDatabase();
		cv.put("name", userName);
		long userId = database.insert(tableUsers, null, cv);
		// database.close();
		return userId;
	}

	private void initializeUserInfo(ContentValues cv, String userName) {
		database = getWritableDatabase();
		cv.put("userId", initializeUser(cv, userName));
		cv.put("name", "Ivan");
		cv.put("surname", "Bordyug");
		cv.put("dob", "10.09.1990");
		cv.put("bio", "b i o");
		cv.put("contacts", "c o n t a c t s");
		database.insert(tableUsersInfo, null, cv);
		// database.close();
	}

	public void insert(String table, String nullColumnHack, ContentValues values) {
		database = getWritableDatabase();
		database.insert(table, nullColumnHack, values);
		// database.close();
	}

	public Cursor select(String tableName, String selection,
			String[] selectionArgs) {
		database = this.getWritableDatabase();
		Cursor cursor = database.query(tableName, null, selection,
				selectionArgs, null, null, null);
		// database.close();
		return cursor;
	}

	private String getBio() {
		try {
			InputStream is = context.getResources().openRawResource(R.raw.bio);
			byte[] b = new byte[is.available()];
			is.read(b);
			return new String(b);
		} catch (Exception e) {
		}
		return null;
	}

	private String getContacts() {
		try {
			InputStream is = context.getResources().openRawResource(
					R.raw.contacts);
			byte[] b = new byte[is.available()];
			is.read(b);
			String str = new String(b);
			JSONObject obj = new JSONObject(str);
			return obj.toString();
		} catch (Exception e) {
		}
		return null;
	}
}
