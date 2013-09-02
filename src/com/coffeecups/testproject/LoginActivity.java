package com.coffeecups.testproject;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;

import Managers.DBManager;
import Managers.TestsManager;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class LoginActivity extends Activity {
	LoginButton button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new TestsManager(this).runTestLogin();
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		button = (LoginButton) findViewById(R.id.login_button);
		button.performClick();
		// Check for an open session
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Get the user's data
			makeMeRequest(session);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
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

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
		}
	}

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (exception != null) {
				if (exception.getClass().isInstance(new FacebookAuthorizationException())) {
					failedInit();
				}
			}
			onSessionStateChange(session, state, exception);
		}
	};

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				if (session == Session.getActiveSession()) {
					if (user != null) {
						DBManager DbManager = new DBManager(LoginActivity.this);
						Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
						intent.putExtra("userId", DbManager.addUser(user));
						startActivity(intent);
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
		Toast.makeText(LoginActivity.this, R.string.enableInternetMsg, 5000).show();
		button.setVisibility(LoginButton.VISIBLE);
	}
}
