package ta.car4rent.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.adapters.CarListAdapter;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceAdvanceSearch;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.facebook.android.BuildConfig;
import com.google.analytics.tracking.android.EasyTracker;

public class ResultSeachFragmemt extends Fragment{
	public static JSONObject selectedCarJSONObject = new JSONObject();
	
	//=========================[About load content]===================================
	private static final int PAGE_SIZE = 5;
	private static final int CONCURRENT_PAGE_AVAILABLE = 2;
	private int mStartPageIndex = 0;
	private int mEndPageIndex = mStartPageIndex + CONCURRENT_PAGE_AVAILABLE - 1;
	
	//=========================[List view variables]==================================
	public static ArrayList<JSONObject> mCarArrayList;
	private CarListAdapter mCarListAdapter;
	private PullAndLoadListView mCarPullAndLoadListView;
	
	
	/**
	 * Disable StricMode
	 */
	static {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}
	
	
	public ResultSeachFragmemt() {
		// Empty constructor required for fragment subclasses
	}
	
	/**
	 * Search car with tow optional pageSize & pageIndex
	 * 
	 * @param pageSize : the number of object will be return one time
	 * @param pageIndex : the index of page in search result set
	 */
	private ArrayList<JSONObject> searchCar(int pageSize, int pageIndex) {
		ArrayList<JSONObject> carListResulSearch = new ArrayList<JSONObject>();
		
		try {
			SearchCarFragmemt.searchRequestJSONObject.put("pageSize", pageSize);
			SearchCarFragmemt.searchRequestJSONObject.put("pageIndex", pageIndex);
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		
		// URL post request
		String url = "http://4carsvn.cloudapp.net:4411/Common/AdvanceSearch";
		String response = "[]";
		try {
			Log.i("REQUEST -- POST JSON TASK", SearchCarFragmemt.searchRequestJSONObject.toString());
			HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new StringEntity(SearchCarFragmemt.searchRequestJSONObject.toString(), HTTP.UTF_8));
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setHeader("Content-type", "application/json");
			
			HttpClient client = new DefaultHttpClient();					
			HttpResponse httpResponse = client.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			
			response = EntityUtils.toString(entity);
			try {
				JSONObject o = new JSONObject(response);
				if (o.getBoolean("status")) {
					JSONArray arrCar = o.getJSONObject("data").getJSONArray("listOfCar");
					for (int j = 0; j < arrCar.length(); j++) {
						carListResulSearch.add(arrCar.getJSONObject(j));
					}
				}
			} catch (JSONException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			}
			
		} catch (UnsupportedEncodingException e) {
			response = e.toString();
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			response = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			response = e.toString();
			e.printStackTrace();
		}
		
		return carListResulSearch;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_result_search, container, false);
		
		mCarPullAndLoadListView = (PullAndLoadListView) rootView.findViewById(R.id.pullAndLoadMoreListView);
		mCarArrayList = new ArrayList<JSONObject>();

		// create a template fragment manager	
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mCarListAdapter = new CarListAdapter(ft, mCarArrayList);
		
		mCarPullAndLoadListView.setAdapter(mCarListAdapter);
		
		// Set a listener to be invoked when the list should be refreshed.
		mCarPullAndLoadListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// In the AsyncTask we will be do 2 step below:				
				// Step 1. Get PAGE_SIZE car of the page before mStartPageIndex to
				// Step 2. Delete PAGE_SIZE last car in carListAdapter
				Log.i("REFRESH", "PULL TO REFRESH");
					new PullToRefreshDataTask().execute();
			}
		});

		// set a listener to be invoked when the list reaches the end
		mCarPullAndLoadListView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				// In the AsyncTask we will be do 2 step below:
				// Step 1. Get PAGE_SIZE car of the page after mEndPageIndex to
				// Step 2. Delete PAGE_SIZE first car in carListAdapter
				Log.i("LOADMORE", "PULL TO LOADMORE");
				new LoadMoreDataTask().execute();
			}
		});
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Get CONCURRENT_PAGE_AVAILABLE page and put to CarListAdapter
		 new GetFirstCarListTask().execute();

	}
	
	private class GetFirstCarListTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}

			for (int i = mStartPageIndex; i <= mEndPageIndex; i++) {
				ArrayList<JSONObject> carPageSearchResult = searchCar(PAGE_SIZE, i);
				mCarArrayList.addAll(carPageSearchResult);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mCarListAdapter.notifyDataSetChanged();
			mCarPullAndLoadListView.onLoadMoreComplete();
			mCarPullAndLoadListView.onRefreshComplete();
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			mCarListAdapter.notifyDataSetChanged();
			mCarPullAndLoadListView.onLoadMoreComplete();
			mCarPullAndLoadListView.onRefreshComplete();
		}
	}
	
	private class PullToRefreshDataTask extends AsyncTask<Void, Void, Void> {
		ArrayList<JSONObject> arrPullToRefreshCarPage;
		
		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}

			// Step 1. Get PAGE_SIZE car of the page before mStartPageIndex to
			// CarListAdapter
			if (mStartPageIndex > 0) {
				mStartPageIndex--;
				mEndPageIndex--;
			}
			arrPullToRefreshCarPage = searchCar(PAGE_SIZE, mStartPageIndex);
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (arrPullToRefreshCarPage.size() == 0) {
				// resume change
				mStartPageIndex = 0;
				mEndPageIndex = mStartPageIndex + CONCURRENT_PAGE_AVAILABLE - 1;
			} else {
				// Step 2. Delete PAGE_SIZE last car in CarListAdapter
				for (int i = 0; i < PAGE_SIZE; i++) {
					if (mCarArrayList.size() - 1 >= 0) {
						mCarArrayList.remove(mCarArrayList.size() - 1);
					}
				}				
				mCarArrayList.addAll(arrPullToRefreshCarPage);
			}
			// We need notify the adapter that the data have been changed
			mCarListAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the Refresh task, has finished
			mCarPullAndLoadListView.onRefreshComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			mCarPullAndLoadListView.onRefreshComplete();
		}
	}

	private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {
		ArrayList<JSONObject> arrLoadmoreCarPage;
		
		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}

			// Step 1. Get PAGE_SIZE car of the page after mEndPageIndex to
			// CarListAdapter
			mStartPageIndex++;
			mEndPageIndex++;
			arrLoadmoreCarPage = searchCar(PAGE_SIZE, mEndPageIndex);
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (arrLoadmoreCarPage.size() == 0) {
				// resume change
				mStartPageIndex--;
				mEndPageIndex--;
			} else {
				// Step 2. Delete PAGE_SIZE first car in CarListAdapter
				for (int i = 0; i < PAGE_SIZE; i++) {
					if (mCarArrayList.size() > 0) {
						mCarArrayList.remove(0);
					}
				}
				mCarArrayList.addAll(arrLoadmoreCarPage);
			}

			// We need notify the adapter that the data have been changed
			mCarListAdapter.notifyDataSetChanged();

			// Call onLoadMoreComplete when the LoadMore task, has finished
			mCarPullAndLoadListView.setSelection(mCarArrayList.size() - PAGE_SIZE);

			mCarPullAndLoadListView.onLoadMoreComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			mCarPullAndLoadListView.onLoadMoreComplete();
		}
	}

	public static void selectCar(int position) {
		selectedCarJSONObject = mCarArrayList.get(position);
	}

	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this.getActivity()).activityStart(this.getActivity()); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this.getActivity()).activityStop(this.getActivity()); // Add this method.
	}


}
