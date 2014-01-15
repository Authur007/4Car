package ta.car4rent.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceChangePassword;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;

public class ChangePasswordFragmemt extends Fragment implements
		OnClickListener, OnPostJsonListener, OnKeyListener {
	private static final String TAG = "CHANGE PASSWORD";
	private EditText etCurrentPassword;
	private EditText etNewPassword;
	private EditText etConfirmNewPassword;
	private Button btnChangePassword;
	private ProgressBar progressBar;

	public ChangePasswordFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_change_password,
				container, false);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		etCurrentPassword = (EditText) rootView.findViewById(R.id.etCurrentPassword);
		etCurrentPassword.setOnKeyListener(this);
		etNewPassword = (EditText) rootView.findViewById(R.id.etNewPassword);
		etNewPassword.setOnKeyListener(this);
		etConfirmNewPassword = (EditText) rootView.findViewById(R.id.etConfirmNewPassword);
		etConfirmNewPassword.setOnKeyListener(this);
		btnChangePassword = (Button) rootView.findViewById(R.id.btnChangePassword);
		btnChangePassword.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		switch (v.getId()) {
		case R.id.btnChangePassword:
			changePassword();
			break;

		default:
			break;
		}
	}
	
	private void changePassword() {
		if ("".equals(etCurrentPassword.getText().toString())
				|| "".equals(etNewPassword.getText().toString())
				|| "".equals(etConfirmNewPassword.getText().toString())) {
				
				Toast.makeText(getActivity().getApplicationContext(),
						getString(R.string.warning_fill_full_text), Toast.LENGTH_LONG)
						.show();
				
						
			} else if (!etConfirmNewPassword.getText().toString().equals(etNewPassword.getText().toString())) {
				Toast.makeText(getActivity().getApplicationContext(),
						getString(R.string.str_confirm_password_not_match), Toast.LENGTH_LONG)
						.show();
				
			}
			
			ServiceChangePassword serviceChangePassword = new ServiceChangePassword();
			serviceChangePassword.addOnPostJsonListener(this);

			try {
				// Show progress
				progressBar.setVisibility(View.VISIBLE);
				JSONObject changePasswordInfo = new JSONObject();
				changePasswordInfo.put("oldPassword", etCurrentPassword
						.getText().toString());
				changePasswordInfo.put("userId",
						ConfigureData.userAccount.getInt("id"));
				changePasswordInfo.put("newPassword", etNewPassword.getText()
						.toString());
				serviceChangePassword.change(changePasswordInfo);
			} catch (JSONException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			}
	}

	@Override
	public void onPostJsonCompleted(String response) {
		Log.w("CHANGE PASSWORD SUCCESS", response);

		// Get info about Login process
		try {
			JSONObject responseJSON = new JSONObject(response);
			if (responseJSON.getBoolean("status")) {
				Toast.makeText(getActivity().getApplicationContext(),
						getString(R.string.str_login_successfully),
						Toast.LENGTH_LONG).show();
				
				ConfigureData.password = etNewPassword.getText().toString();
				
				// Show the Account info Fragment
				Fragment fragment = new AccountFragmemt();
				// Insert the fragment by replacing any existing fragment
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
				((MainActivity)getActivity()).setActionBarTitle(getString(R.string.label_account));
				
				Toast.makeText(getActivity().getApplicationContext(),
						getString(R.string.str_change_password_success), Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						getString(R.string.str_change_password_fail) + "\n" + responseJSON.getString("message"), Toast.LENGTH_LONG)
						.show();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		progressBar.setVisibility(View.GONE);

	}

	@Override
	public void onPostJsonFail(String response) {
		Toast.makeText(getActivity().getApplicationContext(),
				getString(R.string.str_change_password_fail), Toast.LENGTH_LONG).show();
		progressBar.setVisibility(View.GONE);
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

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_ENTER) {
			changePassword();
			return true;
		}
		return false;
	}
}
