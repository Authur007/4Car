package ta.car4rent.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.adapters.ResultSeachListAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;

public class ResultSeachFragmemt extends Fragment{
	
	private PullAndLoadListView list;
	private ResultSeachListAdapter listAdapter;
	private Fragment newContent;
	public JSONArray listResult = new JSONArray();
	
	public ResultSeachFragmemt() {
		// Empty constructor required for fragment subclasses
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_result_search, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// create a template fragment manager	
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		list = (PullAndLoadListView) getView().findViewById(R.id.pullToRefreshListView);	
		// load data
		JSONObject x = new JSONObject();
		listResult.put(x);
		listResult.put(x);
		listResult.put(x);	
		
		listAdapter = new ResultSeachListAdapter(ft, listResult);	
		list.setAdapter(listAdapter);
		
		list.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				new PullToRefreshDataTask().execute();
				
			}
		});
		
		
		list.setOnLoadMoreListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				new LoadMoreDataTask().execute();
				
			}
		});
	}
	
	private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }

            // Simulates a background task
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            // load more data and add to list at here
            JSONObject x = new JSONObject();
            listResult.put(x);
           Log.d("ListResult", listResult.length() + "");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // We need notify the adapter that the data have been changed
           /**
            * The content of the adapter has changed but ListView did not receive a notification. 
            * Make sure the content of your adapter is not modified from a background thread, 
            * but only from the UI thread. [in ListView(2131165283, class com.costum.android.widget.
            * PullAndLoadListView) with Adapter(class android.widget.HeaderViewListAdapter)]

            */
        	//listAdapter.notifyDataSetChanged();

            // Call onLoadMoreComplete when the LoadMore task, has finished
            list.onLoadMoreComplete();
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            list.onLoadMoreComplete();
        }
    }
	
	
	private class PullToRefreshDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			//listResult.addFirst("Added after refresh...")
			//listAdapter.notifyDataSetChanged();
			list.onRefreshComplete();
			
			super.onPostExecute(result);
			
		}
	  
		@Override
		protected void onCancelled() {
			 // Notify the loading more operation has finished
			list.onLoadMoreComplete();
		}
	   
	}

}
