package Managers;

import java.io.InputStream;

import org.json.JSONObject;

import com.coffeecups.testproject.R;
import com.facebook.model.GraphUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.NoCopySpan.Concrete;

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
				+ "id integer primary key autoincrement," + "name text,"
				+ "userId text" + ");");
		db.execSQL("create table usersinfo ("
				+ "id integer primary key autoincrement," + "userId integer,"
				+ "name text," + "surname text," + "dob text," + "bio text,"
				+ "contacts text" + ");");
		// db.execSQL("INSERT INTO " + tableUsers +
		// " values(1,'Ivan Bordyug')");
		// db.execSQL("INSERT INTO " + tableUsersInfo
		// + " values(1,1, 'Ivan', 'Bordyug', '10.09.1990', '" + getBio()
		// + "', '" + getContacts() + "')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private String initializeUser(ContentValues cv, String userName,
			String userId) {
		database = getWritableDatabase();
		cv.put("name", userName);
		cv.put("userId", userId);
		database.insert(tableUsers, null, cv);
		return userId;
	}

	private void initializeUserInfo(GraphUser user) {
		ContentValues cv = new ContentValues();
		database = getWritableDatabase();
		cv.put("userId", initializeUser(cv, user.getUsername(), user.getId()));
		cv.put("name", user.getFirstName());
		cv.put("surname", user.getLastName());
		cv.put("dob", user.getBirthday());
		cv.put("bio", getBio());
		cv.put("contacts", getContacts());
		database.insert(tableUsersInfo, null, cv);
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

	public String addUser(GraphUser user) {
		if (!select(tableUsers, "userId=?", new String[] { user.getId() })
				.moveToFirst())
			initializeUserInfo(user);
		return user.getId();
	}
}
