package ta.car4rent.adapters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.fragments.NewsFeedDetailFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

public class ResultSeachListAdapter extends BaseAdapter implements OnItemClickListener {
	// ==========================================
	// VARIABLES
	// ==========================================

	FragmentTransaction ft;
	// private ArrayList<NewsFeedItem> data;
	//store uri object's image
	private Integer[] imageURI = {R.drawable.thum_url, R.drawable.thum_url1, R.drawable.thum_url2};
	private static LayoutInflater inflater = null;
	// public ImageLoader imageLoader;
	private JSONArray data = new JSONArray();
	
	private GalleryImageAdapter galImageAdapter;
	

	public ResultSeachListAdapter(FragmentTransaction ft, JSONArray listResult) {
		// vitual data to test view
		data = listResult;
		
		
		
		
		this.ft =ft;
		//data = d;
		inflater = (LayoutInflater) ConfigureData.activityMain
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length();
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
		TextView txtTitleNewsFeedItem = (TextView) vi
				.findViewById(R.id.txtCarName);
		Gallery galleryImageNewsFeedItem = (Gallery) vi
				.findViewById(R.id.imageAttach);
		galImageAdapter = new GalleryImageAdapter(ConfigureData.activityMain, imageURI);
		
		galleryImageNewsFeedItem.setAdapter(galImageAdapter);
		galleryImageNewsFeedItem.setOnItemClickListener(this);
		galleryImageNewsFeedItem.setAnimationDuration(500);
		galleryImageNewsFeedItem.setSpacing(5);
		// Setting all value in listview
		//txtTitleNewsFeedItem.setText("Title Here");

		return vi;
	}

	/**
	 * called when gallery is clicked
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		Fragment newContent = new NewsFeedDetailFragment();
		ft.replace(R.id.content_frame, newContent);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// add to back track
		ft.addToBackStack("NewsFeedDetail");
		ft.commit();
	}


	
}
