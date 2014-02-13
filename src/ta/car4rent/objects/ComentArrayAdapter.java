package ta.car4rent.objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.ac;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ComentArrayAdapter extends ArrayAdapter<JSONObject> {

	private TextView comentText;
	private ImageView avatar;
	private List<JSONObject> listComent = new ArrayList<JSONObject>();
	ImageLoader imageLoader;
	Activity activity;

	public ComentArrayAdapter(Activity a, int textViewResourceId) {
		super(a, textViewResourceId);
		activity = a;
		imageLoader = new ImageLoader(activity);

	}
	@Override
	public void clear() {
		listComent.clear();
		super.clear();
	}
	
	@Override
	public void add(JSONObject object) {
		listComent.add(object);
		super.add(object);
	}

	public int getCount() {
		return this.listComent.size();
	}

	public JSONObject getItem(int index) {

		return this.listComent.get(index);

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		try {
			JSONObject coment = listComent.get(position);
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
			imageLoader.displayImage(avatarURL, avatar, 40);

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