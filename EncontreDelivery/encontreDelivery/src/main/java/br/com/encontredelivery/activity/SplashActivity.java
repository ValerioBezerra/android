package br.com.encontredelivery.activity;

import br.com.encontredelivery.R;
import br.com.encontredelivery.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Util.showHashKey(this);
		
        new Handler().postDelayed(new Runnable(){           
            public void run() {
            	Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
            	startActivity(intent);
            	finish();
            }
        }, 3000);         
	}

}
