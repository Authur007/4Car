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

public class ComentArrayRequestCarAdapter extends ArrayAdapter<JSONObject> {

	private TextView comentText;
	private TextView timeComment;
	private ImageView avatar;
	private List<JSONObject> listComent = new ArrayList<JSONObject>();
	ImageLoader imageLoader;
	Activity activity;

	public ComentArrayRequestCarAdapter(Activity a, int textViewResourceId) {
		super(a, textViewResourceId);
		activity = a;
		imageLoader = new ImageLoader(activity);
		// get json arraycoment
		try {
			JSONArray arrayJsonComment = ConfigureData.carRequestDetailObject
					.getJSONArray("comment");
			if (arrayJsonComment.length() > 0) {
				for (int i = arrayJsonComment.length() - 1; i > -1; i--) {
					listComent.add(arrayJsonComment.getJSONObject(i));

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			// int userComentID = coment.getInt("userId");

			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.row_coment_request_car, parent,
					false);

			avatar = (ImageView) row.findViewById(R.id.avatarRequest);
			String avatarURL = coment.getString("image");
			imageLoader.displayImage(avatarURL, avatar, 50);
			// username
			TextView tvUserName = (TextView) row
					.findViewById(R.id.tvUserNameRequest);
			tvUserName.setText(coment.getString("user") + "");
			// comment content
			comentText = (TextView) row.findViewById(R.id.tvCommentRequest);
			comentText.setText(coment.getString("comment") + "  ");

			// date time;
			TextView tvTime = (TextView) row
					.findViewById(R.id.tvTimeRequestComment);
			String strTime = coment.getString("date");
			if (strTime != "null") {
				tvTime.setText(strTime);
			}

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