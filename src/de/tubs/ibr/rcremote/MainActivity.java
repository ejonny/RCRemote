package de.tubs.ibr.rcremote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener;

public class MainActivity extends Activity {

	private final String TAG = "MainActivity";
	private Commander comm = null;
	private DualJoystickView joystick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		joystick = (DualJoystickView)findViewById(R.id.dualjoystickView);
		joystick.setOnJostickMovedListener(_listenerLeft, _listenerRight);
		joystick.setMovementRange(128, 128);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		comm = new Commander(prefs.getString("host", "0.0.0.0"), Integer.valueOf(prefs.getString("port", "8888")), Integer.valueOf(prefs.getString("freq", "10")));
		comm.start();
	}

	@Override
	protected void onStop() {
		try {
			comm.terminate();
		} catch (InterruptedException e) {
			Log.e(TAG, "interupted", e);
		}
		super.onStop();
	}

	private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {

		@Override
		public void OnMoved(int pan, int tilt) {
//			txtX1.setText(Integer.toString(pan));
//			txtY1.setText(Integer.toString(tilt));
			comm.set(0, pan, tilt);
		}

		@Override
		public void OnReleased() {
//			txtX1.setText("released");
//			txtY1.setText("released");
		}

		public void OnReturnedToCenter() {
//			txtX1.setText("stopped");
//			txtY1.setText("stopped");
			comm.set(0, 0, 0);
		};
	}; 

	private JoystickMovedListener _listenerRight = new JoystickMovedListener() {

		@Override
		public void OnMoved(int pan, int tilt) {
//			txtX2.setText(Integer.toString(pan));
//			txtY2.setText(Integer.toString(tilt));
			comm.set(1, pan, tilt);
		}

		@Override
		public void OnReleased() {
//			txtX2.setText("released");
//			txtY2.setText("released");
		}

		public void OnReturnedToCenter() {
//			txtX2.setText("stopped");
//			txtY2.setText("stopped");
			comm.set(1, 0, 0);
		};
	}; 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void showPreferences() {
		this.startActivity(new Intent(this, Preferences.class));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
    		case R.id.menu_settings:
    			showPreferences();
	    		return true;
	    	
	    	default:
	    		return super.onOptionsItemSelected(item);
	    }
	}
}
