package ta.car4rent.adapters;

import java.util.List;

import ta.car4rent.configures.ConfigureData;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter {

	private Activity activity;

	private static ImageView imageView;

	private Integer[] imageURI;

	private static ViewHolder holder;

	public GalleryImageAdapter(Activity context, Integer[] imageURI) {

		this.activity = context;
		this.imageURI = imageURI;

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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			holder = new ViewHolder();

			imageView = new ImageView(ConfigureData.activityMain);

			imageView.setPadding(3, 3, 3, 3);

			convertView = imageView;

			holder.imageView = imageView;

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.imageView.setImageResource(imageURI[position]);

		holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		holder.imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		return imageView;
	}

	private static class ViewHolder {
		ImageView imageView;
	}

}
