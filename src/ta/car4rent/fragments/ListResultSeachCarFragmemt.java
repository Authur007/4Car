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
import ta.car4rent.activities.MainActivity;
import ta.car4rent.adapters.CarListAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.facebook.android.BuildConfig;
import com.google.analytics.tracking.android.EasyTracker;

public class ListResultSeachCarFragmemt extends Fragment{
	public static JSONObject selectedCarJSONObject = new JSONObject();
	
	//=========================[About load content]===================================
	private static final int PAGE_SIZE = 6;
	public static int mEndPageIndex = 0;
	private int numberOfCars = 0;
	private boolean mustBeWaiting = false;
	
	//=========================[List view variables]==================================
	public static ArrayList<JSONObject> mCarArrayList = null;
	private CarListAdapter mCarListAdapter;
	private LoadMoreListView mLoadMoreListView;
	private TextView tvHasNoCar;
	private boolean isLoadNew;
	
	/**
	 * Disable StricMode
	 */
	static {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}
	
	
	public ListResultSeachCarFragmemt() {
		// Empty constructor required for fragment subclasses
	}
	
	/**
	 * Search car with tow optional pageSize & pageIndex
	 * 
	 * @param pageSize : the number of object will be return one time
	 * @param pageIndex : the index of page in search result set
	 */
	private ArrayList<JSONObject> searchCar() {
		mustBeWaiting = true;
		
		ArrayList<JSONObject> carListResultSearch = new ArrayList<JSONObject>();
		
		try {
			SearchCarFragmemt.searchRequestJSONObject.put("pageSize", PAGE_SIZE);
			SearchCarFragmemt.searchRequestJSONObject.put("pageIndex", mEndPageIndex);
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		
		// URL post request
		String url = "http://4carsvn.cloudapp.net:4411/Common/AdvanceSearch";
		String response = "[]";
		try {
			Log.i("SEARCH REQUEST -- POST JSON TASK", SearchCarFragmemt.searchRequestJSONObject.toString());
			HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new StringEntity(SearchCarFragmemt.searchRequestJSONObject.toString(), HTTP.UTF_8));
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setHeader("Content-type", "application/json");
			
			HttpClient client = new DefaultHttpClient();					
			HttpResponse httpResponse = client.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			
			response = EntityUtils.toString(entity);
			Log.i("SEARCH RESPONSE -- POST JSON TASK", response);
			
			try {
				JSONObject o = new JSONObject(response);
				if (o.getBoolean("status")) {
					
					JSONArray arrCar = o.getJSONObject("data").getJSONArray("listOfCar");
					numberOfCars = o.getJSONObject("data").getInt("numberOfCars");
					
					for (int j = 0; j < arrCar.length(); j++) {
						carListResultSearch.add(arrCar.getJSONObject(j));
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

		mustBeWaiting = false;
		return carListResultSearch;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		MainActivity.Instance.showActionFilterSpinner(false);
		
		View rootView = inflater.inflate(R.layout.fragment_search_car_result, container, false);

		tvHasNoCar = (TextView) rootView.findViewById(R.id.tvHasNoCar);
		mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.loadMoreListView);
		isLoadNew = true;
		if (mCarArrayList == null) {
			mCarArrayList = new ArrayList<JSONObject>();
		} else {
			isLoadNew = false;
		}
		
		// create a template fragment manager
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mCarListAdapter = new CarListAdapter(ft, mCarArrayList);
		
		mLoadMoreListView.setAdapter(mCarListAdapter);

		// set a listener to be invoked when the list reaches the end
		mLoadMoreListView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				// In the AsyncTask we will be do 2 step below:
				// Step 1. Get PAGE_SIZE car of the page after mEndPageIndex to
				// Step 2. Delete PAGE_SIZE first car in carListAdapter
				if (!mustBeWaiting) {
						mEndPageIndex++;
						new LoadMoreDataTask().execute();
					
				}
			}
		});
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isLoadNew) {			
			mLoadMoreListView.onLoadMore();
		} else {
			mCarListAdapter.notifyDataSetChanged();
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
			arrLoadmoreCarPage = searchCar();
			
			// hide load more progress at the end of list
			if (arrLoadmoreCarPage.size() == 0) {
				mLoadMoreListView.hideFooter();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			if (numberOfCars <= 0) {
				mLoadMoreListView.hideFooter();
				mLoadMoreListView.setVisibility(View.GONE);
				tvHasNoCar.setVisibility(View.VISIBLE);
			} else {
				mLoadMoreListView.setVisibility(View.VISIBLE);
				tvHasNoCar.setVisibility(View.GONE);
				
				// when end of result
				if (numberOfCars <= mCarArrayList.size()) {
					mLoadMoreListView.hideFooter();
				}
				
				if (arrLoadmoreCarPage.size() > 0) {
					mCarArrayList.addAll(arrLoadmoreCarPage);

					// We need notify the adapter that the data have been changed
					mCarListAdapter.notifyDataSetChanged();

				}
			}

			mLoadMoreListView.onLoadMoreComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			mLoadMoreListView.onLoadMoreComplete();
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