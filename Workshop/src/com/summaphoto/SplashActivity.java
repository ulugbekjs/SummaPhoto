package com.summaphoto;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.summaphoto.R;




/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		// load jodatime to memory - takes 2 seconds
		DateTime.now();
		
		closeScreen();
	}
	
	private void closeScreen() {
        Intent lIntent = new Intent();
        lIntent.setClass(this, SettingsActivity.class);
        startActivity(lIntent);
        finish();
    }

	
}
