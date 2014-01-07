package de.tubs.ibr.rcremote;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class Preferences extends PreferenceActivity {
	
	public final static String PREFS_CHANGED_INTENT = "biz.morgenroth.homeautomation.PREFS_CHANGED_INTENT";

	@Override
	public void onBuildHeaders(List<Header> target) {
		super.onBuildHeaders(target);
		this.loadHeadersFromResource(R.xml.prefsheaders, target);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class ServerPreferences extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.prefsserver);
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		Intent i = new Intent(PREFS_CHANGED_INTENT);
		this.sendBroadcast(i);
	}

}
