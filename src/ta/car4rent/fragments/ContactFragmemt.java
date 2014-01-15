package ta.car4rent.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.ServiceContact;
import ta.car4rent.webservices.OnPostJsonListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ContactFragmemt extends Fragment implements OnPostJsonListener, OnClickListener {
	private ProgressBar progressBar;
	private EditText etName;
	private EditText etEmail;
	private EditText etContent;
	
	public ContactFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
		
		etName = (EditText)rootView.findViewById(R.id.etName);
		etEmail = (EditText)rootView.findViewById(R.id.etEmail);
		etContent = (EditText)rootView.findViewById(R.id.etContent);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		
		Button btnSend = (Button)rootView.findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);
				
		return rootView;
	}

	@Override
	public void onPostJsonCompleted(String response) {
		Log.i("CONTACT RESPONSE", response);
		
		try {
			JSONObject o = new JSONObject(response);
			
			Toast.makeText(this.getActivity(), o.getString("message"), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			
		}
		
		
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onPostJsonFail(String response) {
		// TODO Auto-generated method stub
		Toast.makeText(this.getActivity(), response, Toast.LENGTH_LONG).show();
		
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		if ("".equals(etName.getText().toString()) || "".equals(etEmail.getText().toString()) || "".equals(etContent.getText().toString())) {
			Toast.makeText(this.getActivity(), this.getActivity().getString(R.string.warning_fill_full_text), 2000).show();
		} else {
			ServiceContact serviceContact = new ServiceContact();
			serviceContact.addOnPostJsonListener(this);
			
			try {
				// Show progress
				progressBar.setVisibility(View.VISIBLE);				
				JSONObject contactInfo = new JSONObject();
				contactInfo.put("name", etName.getText().toString());
				contactInfo.put("email", etEmail.getText().toString());
				contactInfo.put("content", etContent.getText().toString());
				serviceContact.sendContact(contactInfo);
			} catch (JSONException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		super.onDestroy();
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