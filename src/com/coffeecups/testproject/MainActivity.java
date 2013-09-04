package com.coffeecups.testproject;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeTabHost();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainActivity.this.finish();
			moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.login_button) {
			MainActivity.this.finish();
			moveTaskToBack(true);
		}

	}

	private void initializeTabHost() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec tabSpec = null;
		initUserInfoTab(tabHost, tabSpec,
				getIntent().getExtras().getString("userId"));
		initUserAboutTab(tabHost, tabSpec,
				getIntent().getExtras().getString("userId"));
	}

	private void initUserInfoTab(TabHost tabHost, TabSpec tabSpec, String userId) {
		tabSpec = tabHost.newTabSpec("UserInfo");
		tabSpec.setIndicator("User Info");
		tabSpec.setContent(initUserProfileIntent(userId));
		tabHost.addTab(tabSpec);
	}

	private void initUserAboutTab(TabHost tabHost, TabSpec tabSpec,
			String userId) {
		tabSpec = tabHost.newTabSpec("UserAbout");
		tabSpec.setIndicator("User about");
		tabSpec.setContent(initUserAboutIntent(userId));
		tabHost.addTab(tabSpec);
	}

	private Intent initUserProfileIntent(String userId) {
		Intent intent = new Intent(new Intent(MainActivity.this,
				UserProfileActivity.class));
		intent.putExtra("userId", userId);
		return intent;
	}

	private Intent initUserAboutIntent(String userId) {
		Intent intent = new Intent(new Intent(MainActivity.this,
				UserAboutActivity.class));
		intent.putExtra("userId", userId);
		return intent;
	}
}
