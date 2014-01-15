package ta.car4rent.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.lazyloading.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ComentArrayAdapter extends BaseAdapter {

	private TextView comentText;
	private RelativeLayout wrapper;
	private ImageView avatar;
	private JSONArray comentArray;
	ImageLoader imageLoader;
	Activity activity;

	public ComentArrayAdapter(Activity a, JSONArray data) {
		activity = a;
		comentArray = data;
		imageLoader = new ImageLoader(activity);
	}

	public int getCount() {
		return this.comentArray.length();
	}

	public JSONObject getItem(int index) {
		try {
			return this.comentArray.getJSONObject(index);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		try {
			JSONObject coment = comentArray.getJSONObject(position);
			int userComentID = coment.getInt("userId");
			int currentUserId = ConfigureData.userId;
			boolean isComentLeft = !(userComentID == currentUserId);
			if (isComentLeft) {
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.row_coment_left, parent, false);
			} else {
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater
						.inflate(R.layout.row_coment_right, parent, false);
			}

			avatar = (ImageView) row.findViewById(R.id.avatar);
			String avatarURL = coment.getString("userPhoto");
			imageLoader.displayImage(avatarURL, avatar, 50);

			comentText = (TextView) row.findViewById(R.id.comment);
			comentText
					.setBackgroundResource(isComentLeft ? R.drawable.bubble_yellow
							: R.drawable.bubble_green);

			comentText.setText(coment.getString("comment") + "  ");

			// comentText.setGravity(isComentLeft ? Gravity.LEFT :
			// Gravity.RIGHT);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return row;

	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return id;
	}

}