package ta.car4rent.adapters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.fragments.NewsFeedDetailFragment;
import ta.car4rent.fragments.ResultSeachFragmemt;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

public class CarListAdapter extends BaseAdapter implements OnItemClickListener {

	private FragmentTransaction ft;
	private static LayoutInflater inflater = null;
	private ArrayList<JSONObject> data = new ArrayList<JSONObject>();
	
	private GalleryImageAdapter galImageAdapter;
	

	public CarListAdapter(FragmentTransaction ft, ArrayList<JSONObject> listCarResult) {
		data = listCarResult;
		this.ft =ft;
		inflater = (LayoutInflater) ConfigureData.activityMain.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			vi = inflater.inflate(R.layout.row_result_search_item, null);
		}
		
		try {
			// Load Image for Gallery
			String[] imageURIs = data.get(position).getString("image").split(",");
			galImageAdapter = new GalleryImageAdapter(ConfigureData.activityMain, imageURIs, position);
			Gallery galleryImageNewsFeedItem = (Gallery) vi.findViewById(R.id.imageAttach);
			galleryImageNewsFeedItem.setAdapter(galImageAdapter);
			galleryImageNewsFeedItem.setOnItemClickListener(this);
			galleryImageNewsFeedItem.setAnimationDuration(500);
			galleryImageNewsFeedItem.setSpacing(0);

			// Load car info
			int year = data.get(position).getInt("year");
			String make = data.get(position).getString("make");
			String model = data.get(position).getString("model");
			String numberPlate = data.get(position).getString("numberPlate");
			((TextView)vi.findViewById(R.id.tvCarName)).setText(year + " " + make + " " + model + " (" + numberPlate + ")");
			
			((TextView)vi.findViewById(R.id.tvColor)).setText(data.get(position).getString("color"));
			((TextView)vi.findViewById(R.id.tvTransmission)).setText(data.get(position).getString("transmission"));
			((TextView)vi.findViewById(R.id.tvNumberSeat)).setText(data.get(position).getString("style"));
			((TextView)vi.findViewById(R.id.tvDoors)).setText(data.get(position).getString("doors"));
			((TextView)vi.findViewById(R.id.tvFuel)).setText(data.get(position).getString("fuel"));
			((TextView)vi.findViewById(R.id.tvPrice)).setText(data.get(position).getString("price") + " "+ ConfigureData.activityMain.getString(R.string.vnd_day));
			
		} catch (JSONException e) {
			Log.e("ERROR SHOW CAR LIST INFO", e.toString());
			
		} 

		return vi;
	}

	/**
	 * called when gallery is clicked
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		
		int positionInList = Integer.parseInt(view.getTag().toString());
		ResultSeachFragmemt.selectCar(positionInList);
		
		Fragment newContent = new NewsFeedDetailFragment();
		ft.replace(R.id.content_frame, newContent);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// add to back track
		ft.addToBackStack("NewsFeedDetail");
		ft.commit();
	}


	
}
