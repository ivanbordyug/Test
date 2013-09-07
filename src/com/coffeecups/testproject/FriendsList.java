package com.coffeecups.testproject;

import java.util.Hashtable;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.Utility;

public class FriendsList extends Activity implements OnItemClickListener {
	private Handler mHandler;
	protected ListView friendsList;
	protected static JSONArray jsonArray;
	protected String graph_or_fql;

	/*
	 * Layout the friends' list
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler();
		setContentView(R.layout.activity_friends);

		Bundle extras = getIntent().getExtras();
		String apiResponse = extras.getString("API_RESPONSE");
		graph_or_fql = extras.getString("METHOD");
		try {
			if (graph_or_fql.equals("graph")) {
				jsonArray = new JSONObject(apiResponse).getJSONArray("data");
			} else {
				jsonArray = new JSONArray(apiResponse);
			}
		} catch (JSONException e) {
			showToast("Error: " + e.getMessage());
			return;
		}
		friendsList = (ListView) findViewById(R.id.selection_list);
		friendsList.setOnItemClickListener(this);
		friendsList.setAdapter(new FriendListAdapter(this));

		showToast("can post on wall");
	}

	/*
	 * Clicking on a friend should popup a dialog for user to post on friend's
	 * wall.
	 */

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		try {
			final long friendId;
			if (graph_or_fql.equals("graph")) {
				friendId = jsonArray.getJSONObject(position).getLong("id");
			} else {
				friendId = jsonArray.getJSONObject(position).getLong("uid");
			}
			String name = jsonArray.getJSONObject(position).getString("name");

			new AlertDialog.Builder(this)
					.setTitle("postOnWallTitle")
					.setMessage(String.format("post on wall", name))
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Bundle params = new Bundle();
									/*
									 * Source Tag: friend_wall_tag To write on a
									 * friend's wall, provide friend's UID in
									 * the 'to' parameter. More info on feed
									 * dialog -
									 * https://developers.facebook.com/docs
									 * /reference/dialogs/feed/
									 */
									params.putString("to",
											String.valueOf(friendId));
									params.putString("caption",
											getString(R.string.app_name));
									params.putString("description", "app desc");
									params.putString("picture", "picture");
									params.putString("name", "ection");
								}

							}).setNegativeButton("no", null).show();
		} catch (JSONException e) {
			showToast("Error: " + e.getMessage());
		}
	}

	/*
	 * Callback after the message has been posted on friend's wall.
	 */
	// public class PostDialogListener extends BaseDialogListener {
	// public void onComplete(Bundle values) {
	// final String postId = values.getString("post_id");
	// if (postId != null) {
	// showToast("Message posted on the wall.");
	// } else {
	// showToast("No message posted on the wall.");
	// }
	// }
	// }

	public void showToast(final String msg) {
		mHandler.post(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(FriendsList.this, msg,
						Toast.LENGTH_LONG);
				toast.show();
			}
		});
	}

	/**
	 * Definition of the list adapter
	 */
	public class FriendListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		FriendsList friendsList;

		public FriendListAdapter(FriendsList friendsList) {
			this.friendsList = friendsList;
			// if (Utility.model == null) {
			// Utility.model = new FriendsGetProfilePics();
			// }
			// Utility.model.setListener(this);
			mInflater = LayoutInflater.from(friendsList.getBaseContext());
		}

		@Override
		public int getCount() {
			return jsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			JSONObject jsonObject = null;
			try {
				jsonObject = jsonArray.getJSONObject(position);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.activity_main, null);
				ViewHolder holder = new ViewHolder();
				// holder.profile_pic = (ImageView) hView
				// .findViewById(R.id.profile_pic);
				// holder.name = (TextView) hView.findViewById(R.id.name);
				// holder.info = (TextView) hView.findViewById(R.id.info);
				hView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) hView.getTag();
			try {
				// if (graph_or_fql.equals("graph")) {
				// holder.profile_pic.setImageBitmap(Utility.model.getImage(
				jsonObject.getString("id");
				// jsonObject.getString("picture")));
				// } else {
				// holder.profile_pic.setImageBitmap(Utility.model.getImage(
				// jsonObject.getString("uid"),
				// jsonObject.getString("pic_square")));
				// }
			} catch (JSONException e) {
				holder.name.setText("");
			}
			try {
				holder.name.setText(jsonObject.getString("name"));
			} catch (JSONException e) {
				holder.name.setText("");
			}
			try {
				if (graph_or_fql.equals("graph")) {
					holder.info.setText(jsonObject.getJSONObject("location")
							.getString("name"));
				} else {
					JSONObject location = jsonObject
							.getJSONObject("current_location");
					holder.info.setText(location.getString("city") + ", "
							+ location.getString("state"));
				}

			} catch (JSONException e) {
				holder.info.setText("");
			}
			return hView;
		}
	}

	class ViewHolder {
		ImageView profile_pic;
		TextView name;
		TextView info;
	}

	public class FriendsGetProfilePics {
		Hashtable<String, Bitmap> friendsImages;
		Hashtable<String, String> positionRequested;
		BaseAdapter listener;
		int runningCount = 0;
		Stack<ItemPair> queue;

		/*
		 * 15 max async tasks at any given time.
		 */
		final int MAX_ALLOWED_TASKS = 15;

		public FriendsGetProfilePics() {
			friendsImages = new Hashtable<String, Bitmap>();
			positionRequested = new Hashtable<String, String>();
			queue = new Stack<ItemPair>();
		}

		/*
		 * Inform the listener when the image has been downloaded. listener is
		 * FriendsList here.
		 */
		public void setListener(BaseAdapter listener) {
			this.listener = listener;
			reset();
		}

		public void reset() {
			positionRequested.clear();
			runningCount = 0;
			queue.clear();
		}

		/*
		 * If the profile pictore has already been downloaded and cached, return
		 * it else excecute a new async task to fetch it - if total async tasks
		 * >15, queue the request.
		 */
		public Bitmap getImage(String uid, String url) {
			Bitmap image = (Bitmap) friendsImages.get(uid);
			if (image != null) {
				return image;
			}
			if (!positionRequested.containsKey(uid)) {
				positionRequested.put(uid, "");
				if (runningCount >= MAX_ALLOWED_TASKS) {
					queue.push(new ItemPair(uid, url));
				} else {
					runningCount++;
					new GetProfilePicAsyncTask().execute(uid, url);
				}
			}
			return null;
		}

		public void getNextImage() {
			if (!queue.isEmpty()) {
				ItemPair item = (ItemPair) queue.pop();
				new GetProfilePicAsyncTask().execute(item.uid, item.url);
			}
		}

		/*
		 * Start a AsyncTask to fetch the request
		 */
		private class GetProfilePicAsyncTask extends
				AsyncTask<Object, Void, Bitmap> {
			String uid;

			protected Bitmap doInBackground(Object... params) {
				this.uid = (String) params[0];
				String url = (String) params[1];
				return BitmapFactory.decodeResource(getResources(),
						R.id.com_facebook_picker_divider);
			}

			protected void onPostExecute(Bitmap result) {
				runningCount--;
				if (result != null) {
					friendsImages.put(uid, result);
					listener.notifyDataSetChanged();
					getNextImage();
				}
			}
		}

		class ItemPair {
			String uid;
			String url;

			public ItemPair(String uid, String url) {
				this.uid = uid;
				this.url = url;
			}
		}
	}
}
