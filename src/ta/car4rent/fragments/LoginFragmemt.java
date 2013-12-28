package ta.car4rent.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.R.id;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.webservices.ServiceContact;
import ta.car4rent.webservices.ServiceLogin;
import ta.car4rent.webservices.OnPostJsonListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginFragmemt extends Fragment implements OnClickListener,
		OnPostJsonListener {
	private EditText etUserName;
	private EditText etPassword;
	private Button btnLogin;
	private ProgressBar progressBar;

	public LoginFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		etUserName = (EditText) rootView.findViewById(R.id.etUserName);
		etPassword = (EditText) rootView.findViewById(R.id.etPassword);
		btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		if ("".equals(etUserName.getText().toString())
				|| "".equals(etPassword.getText().toString())) {
			Toast.makeText(
					this.getActivity(),
					this.getActivity().getString(
							R.string.warning_fill_full_text), 2000).show();
		} else {
			ServiceLogin serviceLogin = new ServiceLogin();
			serviceLogin.addOnPostJsonListener(this);

			try {
				// Show progress
				progressBar.setVisibility(View.VISIBLE);
				JSONObject loginInfo = new JSONObject();
				loginInfo.put("userName", etUserName.getText().toString());
				ConfigureData.userName = etUserName.getText().toString();
				loginInfo.put("password", etPassword.getText().toString());
				ConfigureData.password = etPassword.getText().toString();
				serviceLogin.logon(loginInfo);
			} catch (JSONException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onPostJsonCompleted(String response) {
		Log.w("LOGIN SUCCESS", response);

		// Get info about Login process
		try {
			JSONObject responseJSON = new JSONObject(response);
			if (responseJSON.getBoolean("status")) {
				Toast.makeText(getActivity().getApplicationContext(), getString(R.string.str_login_successfully), Toast.LENGTH_LONG).show();
				ConfigureData.userAccount = responseJSON.getJSONObject("user");
				ConfigureData.isLogged = true;
				// Show the Account info Fragment
				Fragment fragment = new AccountFragmemt();
				// Insert the fragment by replacing any existing fragment
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
				
			} else {
				Toast.makeText(getActivity().getApplicationContext(),  getString(R.string.str_login_fail), Toast.LENGTH_LONG).show();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		progressBar.setVisibility(View.GONE);
		
	}

	@Override
	public void onPostJsonFail(String response) {
		Toast.makeText(getActivity().getApplicationContext(),  getString(R.string.str_login_fail), Toast.LENGTH_LONG).show();
		progressBar.setVisibility(View.GONE);
	}
}
