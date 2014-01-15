package ta.car4rent.adapters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.lazyloading.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.nfc.FormatException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsFeedWarningListAdapter extends BaseAdapter {

	// ==========================================
	// VARIABLES
	// ==========================================

	private Activity activity;
	private List<JSONObject> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public NewsFeedWarningListAdapter(Activity a, List<JSONObject> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.row_newsfeed_warning, null);
		}
		TextView row_ReportType = (TextView) vi
				.findViewById(R.id.txt_row_reportType);
		TextView row_SubType = (TextView) vi.findViewById(R.id.txt_row_subType);
		TextView row_Distance = (TextView) vi
				.findViewById(R.id.txt_row_Distance);
		TextView row_Address = (TextView) vi.findViewById(R.id.txt_row_address);
		TextView row_Note = (TextView) vi.findViewById(R.id.txt_row_Note);
		TextView row_Time = (TextView) vi.findViewById(R.id.txt_row_time);

		ImageView imageWarning = (ImageView) vi
				.findViewById(R.id.imageNewsFeedDetail);
		// pull data to component
		try {
			JSONObject itemNewsFeed = new JSONObject();
			itemNewsFeed = data.get(position);

			row_ReportType.setText(itemNewsFeed.getString("reportType"));
			row_SubType.setText(ConfigureData.activityGoogleMaps.getString(R.string.sub_type) + itemNewsFeed.getString("reportSubType"));
			row_Distance.setText(ConfigureData.activityGoogleMaps.getString(R.string.distance)+ "1km");
			row_Address.setText(ConfigureData.activityGoogleMaps.getString(R.string.address) + itemNewsFeed.getString("address"));
			row_Note.setText(ConfigureData.activityGoogleMaps.getString(R.string.coment)+ itemNewsFeed.getString("note"));
			row_Time.setText(ConfigureData.activityGoogleMaps.getString(R.string.time) + itemNewsFeed.getString("date"));

			String imageURL = itemNewsFeed.getString("imageUrl");

			imageLoader.displayImage(imageURL, imageWarning, 150);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
		}

		return vi;

	}

}
