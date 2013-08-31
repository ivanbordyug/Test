package com.coffeecups.testproject;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeTabHost();
	}

	private void initializeTabHost() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec tabSpec = null;
		initUserInfoTab(tabHost, tabSpec);
	}

	private void initUserInfoTab(TabHost tabHost, TabSpec tabSpec) {
		tabSpec = tabHost.newTabSpec("UserInfo");
		tabSpec.setIndicator("User Info");
		tabSpec.setContent(new Intent(MainActivity.this,
				UserProfileActivity.class));
		tabHost.addTab(tabSpec);
	}
}
