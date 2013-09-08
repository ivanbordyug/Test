package com.coffeecups.testproject;

import Managers.DBManager;
import Managers.TestsManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.FacebookAuthorizationException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class LoginActivity extends Activity {
	LoginButton button;
	private static final int PICK_FRIENDS_ACTIVITY = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new TestsManager(this).runTestLogin();
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		button = (LoginButton) findViewById(R.id.login_button);
		// Check for an open session

	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Get the user's data
			makeMeRequest(session);
		} else {
			button.performClick();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == Activity.RESULT_OK) {
			// Do nothing for now
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LoginActivity.this.finish();
			moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
		}
	}

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (exception != null) {
				if (exception.getClass().isInstance(
						new FacebookAuthorizationException())) {
					failedInit();
				}
			}
			onSessionStateChange(session, state, exception);
		}
	};

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {

								DBManager DbManager = new DBManager(
										LoginActivity.this);
								Intent intent = new Intent(LoginActivity.this,
										MainActivity.class);
								intent.putExtra("userId",
										DbManager.addUser(user));
								startActivity(intent);
								LoginActivity.this.finish();
							}
						}
						if (response.getError() != null) {
							// Handle errors, will do so later.
							failedInit();
						}
					}
				});
		request.executeAsync();
	}

	private void failedInit() {
		Toast.makeText(LoginActivity.this, R.string.enableInternetMsg, 5000)
				.show();
		button.setVisibility(LoginButton.VISIBLE);
	}

	private void startPickFriendsActivity() {
		FriendPickerApplication application = (FriendPickerApplication) getApplication();
		application.setSelectedUsers(null);

		Intent intent = new Intent(this, PickFriendsActivity.class);
		// Note: The following line is optional, as multi-select behavior is
		// the default for
		// FriendPickerFragment. It is here to demonstrate how parameters
		// could be passed to the
		// friend picker if single-select functionality was desired, or if a
		// different user ID was
		// desired (for instance, to see friends of a friend).
		PickFriendsActivity.populateParameters(intent, null, true, true);
		startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);

	}
}
