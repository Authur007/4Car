package ta.car4rent.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.adapters.ListCarRequestsAdapter;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.webservices.ApiUrl;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.facebook.android.BuildConfig;
import com.google.analytics.tracking.android.EasyTracker;

public class ListCarRequestesFragment extends Fragment implements
		OnItemClickListener {

	public static String selectedCarRequestStatus;
	
	// Setup for filter
	public final static int FILTER_MODE_ALL = 0;
	public final static int FILTER_MODE_OPEN = 1;
	public final static int FILTER_MODE_CLOSED = 2;

	public static JSONObject selectedCarRequestJSONObject = new JSONObject();

	// =========================[About load
	// content]===================================
	private static final int PAGE_SIZE = 10;
	public int mEndPageIndex = 0;
	private int numberOfCarRequests = 0;
	private boolean mustBeWaiting = false;

	// =========================[List view variables]==================================
	public ArrayList<JSONObject> mCarRequestsArrayList = null;
	private ListCarRequestsAdapter mCarRequestsListAdapter;
	private LoadMoreListView mLoadMoreListView;
	private ProgressBar progressBar;
	private TextView tvHasNoCarRequest;
	private boolean isLoadNew = true;

	/**
	 * Disable StricMode
	 */
	static {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	private static int mFilterMode = FILTER_MODE_OPEN;

	public void setFilterMode(int filterMode) {
		mFilterMode = filterMode;
	}

	public ListCarRequestesFragment() {
		// Empty constructor required for fragment subclasses
	}

	/**
	 * Search car with tow optional pageSize & pageIndex
	 * 
	 * @param pageSize
	 *            : the number of object will be return one time
	 * @param pageIndex
	 *            : the index of page in search result set
	 */
	private ArrayList<JSONObject> getCarRequests() {
		mustBeWaiting = true;

		ArrayList<JSONObject> carRequestsList = new ArrayList<JSONObject>();

		// URL post request
		String url = "";

		url = ApiUrl.SERVER_URL + "GetCarRequests/"
				+ mEndPageIndex + "/" + PAGE_SIZE + "/" + mFilterMode;

		String response = "[]";
		try {
			Log.i("START GET CAR REQUESTS -- GET JSON TASK", url);
			Log.i("TOKEN CAR REQUESTS -- GET JSON TASK", ConfigureData.token);

			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("token", ConfigureData.token);
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Content-type", "application/json");

			HttpResponse execute = client.execute(httpGet);
			InputStream content = execute.getEntity().getContent();

			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					content));
			response = "";
			String s = "";
			while ((s = buffer.readLine()) != null) {
				response += s;
			}

			Log.i("END GET CAR REQUESTS -- GET JSON TASK", response);

			// Convert result to JSON
			try {
				JSONObject o = new JSONObject(response);
				if (o.getBoolean("status")) {

					JSONArray arrCarRequest = o.getJSONArray("data");

					for (int j = 0; j < arrCarRequest.length(); j++) {
						carRequestsList.add(arrCarRequest.getJSONObject(j));
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

		return carRequestsList;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainActivity.Instance.showActionFilterSpinner(true);

		View rootView = inflater.inflate(R.layout.fragment_list_car_requested, container, false);
		ConfigureData.currentScreen = 2;

		tvHasNoCarRequest = (TextView) rootView.findViewById(R.id.tvHasNoCarRequest);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.loadMoreListView);
		mLoadMoreListView.setOnItemClickListener(this);
		isLoadNew = true;
		if (mCarRequestsArrayList == null) {
			mCarRequestsArrayList = new ArrayList<JSONObject>();
		} else {
			isLoadNew = false;
		}

		// create a template fragment manager
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mCarRequestsListAdapter = new ListCarRequestsAdapter(getActivity(), ft, mCarRequestsArrayList);

		mLoadMoreListView.setAdapter(mCarRequestsListAdapter);

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
	public void onResume() {
		// TODO Auto-generated method stub
		MainActivity.Instance.showActionFilterSpinner(true);
		progressBar.setVisibility(View.GONE);
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isLoadNew) {
			mLoadMoreListView.onLoadMore();
		} else {
			mCarRequestsListAdapter.notifyDataSetChanged();
		}

	}

	private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {
		ArrayList<JSONObject> arrLoadmoreCarRequestsPage;

		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}

			// Step 1. Get PAGE_SIZE car of the page after mEndPageIndex to
			// CarListAdapter
			arrLoadmoreCarRequestsPage = getCarRequests();
			numberOfCarRequests += arrLoadmoreCarRequestsPage.size();

			// hide load more progress at the end of list
			if (arrLoadmoreCarRequestsPage.size() == 0) {
				mLoadMoreListView.hideFooter();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (numberOfCarRequests <= 0) {
				mLoadMoreListView.hideFooter();
				mLoadMoreListView.setVisibility(View.GONE);
				tvHasNoCarRequest.setVisibility(View.VISIBLE);
			} else {
				mLoadMoreListView.setVisibility(View.VISIBLE);
				tvHasNoCarRequest.setVisibility(View.GONE);

				// when end of result
				if (numberOfCarRequests <= mCarRequestsArrayList.size()) {
					mLoadMoreListView.hideFooter();
				}

				if (arrLoadmoreCarRequestsPage.size() > 0) {
					mCarRequestsArrayList.addAll(arrLoadmoreCarRequestsPage);

					// We need notify the adapter that the data have been
					// changed
					mCarRequestsListAdapter.notifyDataSetChanged();

				}
			}

			mLoadMoreListView.onLoadMoreComplete();
			progressBar.setVisibility(View.GONE);

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			mLoadMoreListView.onLoadMoreComplete();
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this.getActivity()).activityStart(
				this.getActivity()); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this.getActivity()).activityStop(
				this.getActivity()); // Add this method.
	}

	/**
	 * called when gallery is clicked
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		try {
			int requestId = Integer.parseInt(view.getTag().toString());

			if (requestId != 0) {

				// Insert the fragment by replacing any existing
				CarRequestDetailFragment fragment = new CarRequestDetailFragment();
				fragment.setCarRequestID(requestId);
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fragmentManager.beginTransaction();
				ft.replace(R.id.content_frame, fragment);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

				// add to back track
				ft.addToBackStack("CarRequest");
				ft.commit();
			}
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroyView() {
		ConfigureData.currentScreen = 0;

		super.onDestroyView();
	}

}