package ta.car4rent.adapters;

import com.facebook.android.BuildConfig;

import ta.car4rent.configures.ConfigureData;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter {

	private Activity activity;
	private String[] imageURI;
	private int mPossitionInList;

	public GalleryImageAdapter(Activity context, String[] imageURI,
			int possitionInList) {

		this.activity = context;
		this.imageURI = imageURI;
		mPossitionInList = possitionInList;
	}

	@Override
	public int getCount() {
		return imageURI.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(ConfigureData.activityMain);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setPadding(3, 3, 3, 3);

			convertView = imageView;
		} else {
			imageView = (ImageView) convertView;
		}

		// Load image from the Internet
		try {
			ConfigureData.imageLoader.displayImage(imageURI[position], imageView, 200);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}

		convertView.setTag(mPossitionInList);
		return imageView;
	}

}
