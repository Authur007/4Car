package ta.car4rent.animation;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class MyAnimation {
	// To animate view slide out from top to bottom
	public void slideToBottom(View view){
	TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
	animate.setDuration(500);
	animate.setFillAfter(true);
	view.startAnimation(animate);
	view.setVisibility(View.GONE);
	}
	 
	// To animate view slide out from bottom to top
	public void slideToTop(View view){
	TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
	animate.setDuration(500);
	animate.setFillAfter(true);
	view.startAnimation(animate);
	view.setVisibility(View.GONE);
	}
}
