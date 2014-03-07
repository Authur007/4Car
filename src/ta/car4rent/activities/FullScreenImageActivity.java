package ta.car4rent.activities;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.lazyloading.ImageLoader;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class FullScreenImageActivity extends Activity {
	int posImage = 0;
	int numberOfImage;
	ImageView imageViewFullSceen;
	ImageLoader imageLoader;
	TextView txtNumberOfImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image_fullscreen);

		try {
			getActionBar().hide();
		} catch (Exception e) {
		}
		numberOfImage = ConfigureData.FullScreenImage.length;
		txtNumberOfImage = (TextView) findViewById(R.id.txtNumberOfImage);
		txtNumberOfImage.setText("1/" + numberOfImage);

		ImageView leftArrowImageView = (ImageView) findViewById(R.id.left_arrow_imageview);
		ImageView rightArrowImageView = (ImageView) findViewById(R.id.right_arrow_imageview);

		imageViewFullSceen = (ImageView) findViewById(R.id.imageViewFullScreen);
		imageLoader = new ImageLoader(this);
		imageLoader.displayImage(ConfigureData.FullScreenImage[0],
				imageViewFullSceen, 600);

		leftArrowImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (posImage > 0) {
					posImage--;
					showImage();
				} else {
					posImage = numberOfImage - 1;
					showImage();
				}

			}

		});

		rightArrowImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (posImage < numberOfImage - 1) {
					posImage++;
					showImage();
				} else {
					posImage = 0;
					showImage();
				}

			}
		});

	}

	public void showImage() {
		txtNumberOfImage.setText(posImage +1 + "/" + numberOfImage);
		imageLoader.displayImage(ConfigureData.FullScreenImage[posImage],
				imageViewFullSceen, 500);
	}

	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

}
