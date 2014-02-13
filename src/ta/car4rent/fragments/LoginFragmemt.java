package ta.car4rent.fragments;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceGetGenerateToken;
import ta.car4rent.webservices.ServiceGetUserByUserName;
import ta.car4rent.webservices.ServiceLogin;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;

public class LoginFragmemt extends DialogFragment implements OnClickListener,
		OnPostJsonListener {
	private static final String TAG = "FACEBOOK LOGIN";
	private EditText etUserName;
	private EditText etPassword;
	private Button btnLogin;
	private Button btnRegister;
	private ProgressBar progressBar;

	// ========FACEBOOK========================
	public static UiLifecycleHelper uiHelper;
	LoginButton authButton;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};

	public LoginFragmemt() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(ConfigureData.activityMain, callback);
		uiHelper.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ConfigureData.currentScreen = 2;

		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		etUserName = (EditText) rootView.findViewById(R.id.etUserName);
		etPassword = (EditText) rootView.findViewById(R.id.etPassword);
		btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);
		btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(this);
		// hide register and when LoginFragment called by GoogleMaps
		if (ConfigureData.isCalledFromMaps) {
			btnRegister.setVisibility(View.GONE);

		}
		// facebook
		authButton = (LoginButton) rootView.findViewById(R.id.authButton);
		authButton.setFragment(this);

		// authButton.setReadPermissions(Arrays.asList("email",
		// "user_birthday","user_about_me"));
		// authButton.setReadPermissions(Arrays.asList("email"));

		authButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						ConfigureData.graphUser = user;
						fetchProfile();

					}

				});

		return rootView;
	}

	// ===========================================================
	// FACEBOOK LOGIN
	// ===========================================================

	private void fetchProfile() {
		if (ConfigureData.graphUser != null) {
			ConfigureData.isUsingFBLoginButton = true;
			getTokenLoginFacebook();

		}
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if ((exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(ConfigureData.activityMain)
					.setTitle(R.string.cancel)
					.setMessage("Not Allow Permission")
					.setPositiveButton("OK", null).show();
		}

	}

	/**
	 * Your activity would need to handle the results by overriding the
	 * onActivityResult() method. To allow the fragment to receive the
	 * onActivityResult() call rather than the activity, you can call the
	 * setFragment() method on the LoginButton instance
	 */

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	// ============================================================
	// NORMAL LOGIN
	// ============================================================

	@Override
	public void onClick(View v) {
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		try{
			StaticFunction.hideKeyboard(ConfigureData.activityGoogleMaps);
		}catch(Exception e){
			e.printStackTrace();
		}
		switch (v.getId()) {
		case R.id.btnLogin:
			if ("".equals(etUserName.getText().toString())
					|| "".equals(etPassword.getText().toString())) {
				Toast.makeText(
						ConfigureData.activityMain,
						ConfigureData.activityMain
								.getString(R.string.warning_fill_full_text), 0)
						.show();
			} else {
				sendRequstLogin(etUserName.getText().toString(), etPassword
						.getText().toString());
			}
			break;
		case R.id.btnRegister:
			goToRegisterScreen();
			break;

		default:
			break;
		}
	}

	public void goToRegisterScreen() {

		// Show the Account info Fragment
		Fragment fragment = new RegisterAccountFragmemt();
		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = ConfigureData.activityMain
				.getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.addToBackStack("LoginScreen").commit();
		((MainActivity) ConfigureData.activityMain)
				.setActionBarTitle(getString(R.string.label_login));
	}

	public void getTokenLoginFacebook() {
		ServiceGetGenerateToken serviceGetToken = new ServiceGetGenerateToken();
		serviceGetToken.getTokenByFBId(ConfigureData.graphUser.getId());
		serviceGetToken.addOnGetJsonListener(new OnGetJsonListener() {

			@Override
			public void onGetJsonFail(String response) {
				Log.d("login fb", response);

			}

			@Override
			public void onGetJsonCompleted(String response) {
				Log.d("login fb", response);
				try {
					ConfigureData.token = new JSONObject(response)
							.getString("token");
					ServiceGetUserByUserName serviceGetUser = new ServiceGetUserByUserName();
					serviceGetUser.getUserByFBId(ConfigureData.graphUser
							.getId());
					serviceGetUser
							.addOnGetJsonListener(new OnGetJsonListener() {

								@Override
								public void onGetJsonFail(String response) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onGetJsonCompleted(String response) {
									try {
										if (new JSONObject(response)
												.getBoolean("status")) {

											onLoginSuccessfull(response);

										} else {

											progressBar
													.setVisibility(View.GONE);
											goToRegisterScreen();

										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							});

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void sendRequstLogin(String userName, String pass) {
		ServiceLogin serviceLogin = new ServiceLogin();
		serviceLogin.addOnPostJsonListener(this);

		try {
			// Show progress
			progressBar.setVisibility(View.VISIBLE);
			JSONObject loginInfo = new JSONObject();
			loginInfo.put("userName", userName);
			ConfigureData.userName = userName;
			loginInfo.put("password", pass);
			ConfigureData.password = pass;
			serviceLogin.logon(loginInfo);
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPostJsonCompleted(String response) {
		Log.w("LOGIN SUCCESS", response);

		try {
			ConfigureData.token = new JSONObject(response).getString("token");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		onLoginSuccessfull(response);

	}

	@Override
	public void onPostJsonFail(String response) {
		if (ConfigureData.isUsingFBLoginButton) {
			progressBar.setVisibility(View.GONE);
			goToRegisterScreen();

		} else {
			Toast.makeText(ConfigureData.activityMain.getApplicationContext(),
					getString(R.string.str_login_fail), Toast.LENGTH_LONG)
					.show();

		}
	}

	public void onLoginSuccessfull(String response) {
		// Get info about Login process
		try {
			JSONObject responseJSON = new JSONObject(response);
			if (responseJSON.getBoolean("status")) {

				ConfigureData.userAccount = responseJSON.getJSONObject("user");
				ConfigureData.userId = ConfigureData.userAccount.getInt("id");
				ConfigureData.isLogged = true;

				if (ConfigureData.isCalledFromMaps) {
					ConfigureData.isCalledFromMaps = false;

					StaticFunction
							.hideKeyboard(ConfigureData.activityGoogleMaps);
					try {
						LoginFragmemt.this.dismiss();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (ConfigureData.isCalledFromCarRequested) {
					ConfigureData.isCalledFromCarRequested = false;
					StaticFunction
							.hideKeyboard(ConfigureData.activityMain);
					try {
						// Show the Account info Fragment
						Fragment fragment = new ManageCarRequestesFragment();
						// Insert the fragment by replacing any existing fragment
						FragmentManager fragmentManager = ConfigureData.activityMain
								.getSupportFragmentManager();
						FragmentTransaction fm = fragmentManager.beginTransaction();
						fm.replace(R.id.content_frame, fragment).commit();
						
					} catch (RuntimeException e) {
						e.printStackTrace();
					}

				} else {
					// Show the Account info Fragment
					Fragment fragment = new AccountFragmemt();
					// Insert the fragment by replacing any existing fragment
					FragmentManager fragmentManager = ConfigureData.activityMain
							.getSupportFragmentManager();
					FragmentTransaction fm = fragmentManager.beginTransaction();
					fm.replace(R.id.content_frame, fragment).commit();
				}

			} else {
				Toast.makeText(
						ConfigureData.activityMain.getApplicationContext(),
						getString(R.string.str_login_fail), Toast.LENGTH_LONG)
						.show();

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		super.onDestroy();

	}

	@Override
	public void onDestroyView() {
		ConfigureData.currentScreen = 0;
		super.onDestroyView();
	}

	@Override
	public void onResume() {

		uiHelper.onResume();
		// TODO Auto-generated method stub
		super.onResume();
		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(ConfigureData.activityMain);

		// updateUI();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
		uiHelper.onDestroy();
		// The rest of your onStart() code.
		EasyTracker.getInstance(ConfigureData.activityMain).activityStart(
				ConfigureData.activityMain); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(ConfigureData.activityMain).activityStop(
				ConfigureData.activityMain); // Add this method.
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

}
