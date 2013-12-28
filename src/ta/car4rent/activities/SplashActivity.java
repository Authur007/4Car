package ta.car4rent.activities;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.lazyloading.ImageLoader;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity implements AnimationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.animator.zoom_out);
		zoomAnimation.setAnimationListener(this);
		((ImageView)findViewById(R.id.imgLogo)).startAnimation(zoomAnimation);
		
		// Init global variable
		ConfigureData.imageLoader = new ImageLoader(this);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// Open MainActivity when zoom animation end
		Intent mainActivityIntent = new Intent(this, MainActivity.class);
		this.startActivity(mainActivityIntent);
		this.overridePendingTransition(0, 0);
		this.finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}