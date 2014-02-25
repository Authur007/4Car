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
import android.widget.Toast;

public class CommentArrayRequestCarAdapter extends ArrayAdapter<JSONObject> {

	private ImageView avatar;
	private List<JSONObject> listCommentData = new ArrayList<JSONObject>();
	ImageLoader imageLoader;
	Activity activity;

	public CommentArrayRequestCarAdapter(Activity a, int textViewResourceId) {
		super(a, textViewResourceId);
		activity = a;
		imageLoader = new ImageLoader(activity);
		// get json array comment
		try {
			JSONArray arrayJsonComment = ConfigureData.carRequestDetailObject.getJSONArray("comment");
			if (arrayJsonComment.length() > 0) {
				for (int i = arrayJsonComment.length() - 1; i > -1; i--) {
					listCommentData.add(arrayJsonComment.getJSONObject(i));
//					Toast.makeText(a, "arrayJsonComment.length(): " + arrayJsonComment.length() , 2000).show();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void clear() {
		listCommentData.clear();
		super.clear();
	}

	@Override
	public void add(JSONObject object) {
		listCommentData.add(object);
		super.add(object);
	}

	public int getCount() {
		return this.listCommentData.size();
	}

	public JSONObject getItem(int index) {

		return this.listCommentData.get(index);

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		try {
			JSONObject coment = listCommentData.get(position);
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
			TextView commentText = (TextView) row.findViewById(R.id.tvCommentRequest);
			commentText.setText(coment.getString("comment") + "  ");

			// date time;
			TextView tvTime = (TextView) row.findViewById(R.id.tvTimeRequestComment);
			String strTime = coment.getString("date");
			if (strTime != "null") {
				tvTime.setText(strTime);
			} else {
				tvTime.setText("");
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