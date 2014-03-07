package ta.car4rent.webservices;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class WebserviceBaseGET implements SubjectGET{
	public static final int NOTIFY_GET_JSON_COMPLETED = 0;
	public static final int NOTIFY_GET_JSON_FAIL = 1;
	
	protected Vector<OnGetJsonListener> mOnGetJsonListeners = new Vector<OnGetJsonListener>();
	
	protected String mToken;
	protected String mResponse;
	protected JSONObject mRequestJSONObject;

	public void setToken(String token) {
		mToken = token;
	}
	
	public void getJSONObject(String request) {
		mResponse = "[]";
		getJSONTask task = new getJSONTask();
		task.execute(new String[] { request });
	}

	/**
	 * This is Asyntask Download web-page source via URL run in Background
	 * 
	 * @author anhle
	 * 
	 */
	protected class getJSONTask extends AsyncTask<String, Void, String> {
		Boolean isFail = true;
		
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				Log.i("START RUN GET JSON TASK URL", url);
				
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("token", mToken);
				
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
					
					isFail = false;

				} catch (Exception e) {
					response = e.toString();
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String r) {
			mResponse = r;
			if (isFail) {
				notifyChange(NOTIFY_GET_JSON_FAIL);
			} else {
				notifyChange(NOTIFY_GET_JSON_COMPLETED);
			}
		}
	}
	
	@Override
	public void notifyChange(int type) {
		Log.i("RESPONSE -- GET JSON TASK", mResponse);
		
		for (OnGetJsonListener o : mOnGetJsonListeners) {
			switch (type) {

			case NOTIFY_GET_JSON_COMPLETED:
				o.onGetJsonCompleted(mResponse);
				break;

			case NOTIFY_GET_JSON_FAIL:
				o.onGetJsonFail(mResponse);
				break;
			}
		}
	}

	@Override
	public void addOnGetJsonListener(OnGetJsonListener o) {
		// TODO Auto-generated method stub
		mOnGetJsonListeners.add(o);
	}

	@Override
	public void removeOnGetJsonListener(OnGetJsonListener o) {
		// TODO Auto-generated method stub
		mOnGetJsonListeners.remove(o);
	}

}
