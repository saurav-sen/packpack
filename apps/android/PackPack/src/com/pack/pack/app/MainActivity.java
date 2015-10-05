package com.pack.pack.app;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;

/**
 * 
 * @author Saurav
 *
 */
public class MainActivity extends Activity {

	private TableLayout loginPage;
	private int[] images;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setAutoChangeBackground();
		showButton();
	}
	
	private void setAutoChangeBackground() {
		images = new int[] {R.drawable.landscape_see_org};
		//images = new int[] {R.drawable.landscape_see};
		loginPage = (TableLayout)findViewById(R.id.loginPage);
		loginPage.setBackgroundResource(images[Math.abs(new Random().nextInt(images.length))]);
		/*new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						loginPage.setBackgroundResource(images[Math.abs(new Random().nextInt(images.length))]);
					}
				}, 2000);
			}
		}, 0, 11000);*/
	}
	
	private void showButton() {
		View loginBtn = findViewById(R.id.login);
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/**
				 * We need transfer data i.e. logged-in user details etc... at a later stage here.
				 */
				startActivity(new Intent(getApplicationContext(), HomeActivity.class));
				//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com")));
			}
		});
	}
}
