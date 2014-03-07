package ta.car4rent.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceContact;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.google.analytics.tracking.android.EasyTracker;

public class ContactFragmemt extends Fragment implements OnPostJsonListener,
		OnClickListener {
	private ProgressBar progressBar;
	private EditText etName;
	private EditText etEmail;
	private EditText etContent;

	public ContactFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MainActivity.Instance.showActionFilterSpinner(false);
		
		ConfigureData.currentScreen = 2;

		View rootView = inflater.inflate(R.layout.fragment_contact, container,
				false);

		etName = (EditText) rootView.findViewById(R.id.etName);
		etEmail = (EditText) rootView.findViewById(R.id.etEmail);
		etContent = (EditText) rootView.findViewById(R.id.etContent);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

		Button btnSend = (Button) rootView.findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onPostJsonCompleted(String response) {
		Log.i("CONTACT RESPONSE", response);

		try {
			JSONObject o = new JSONObject(response);
			if (o.getBoolean("status")) {
				showMessageSendContactSuccess();
				etName.setText("");
				etEmail.setText("");
				etContent.setText("");

			} else {
				showMessageSendContactFail();
			}

		} catch (JSONException e) {
			showMessageSendContactFail();
		}

		progressBar.setVisibility(View.GONE);
	}

	@SuppressWarnings("deprecation")
	private void showMessageSendContactSuccess() {
		// Creating alert Dialog with one Button

		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.create();

		// Setting Dialog Title
		alertDialog.setTitle(getString(R.string.str_send_contact_success));

		// Setting Dialog Message
		alertDialog.setMessage(getString(R.string.thanks_for_contact));

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.icon_dialog_contact);

		// Setting OK Button
		alertDialog.setButton(getString(R.string.str_ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Fragment searchFragment = new SearchCarFragmemt();
						getActivity().getSupportFragmentManager()
								.beginTransaction()
								.replace(R.id.content_frame, searchFragment)
								.commit();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@SuppressWarnings("deprecation")
	private void showMessageSendContactFail() {
		// Creating alert Dialog with one Button

		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.create();

		// Setting Dialog Title
		alertDialog.setTitle(getString(R.string.str_send_contact_fail));

		// Setting Dialog Message
		alertDialog.setMessage(getString(R.string.err_send_contact));

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.ic_error);

		// Setting OK Button
		alertDialog.setButton(getString(R.string.str_ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onPostJsonFail(String response) {
		// TODO Auto-generated method stub
		showMessageSendContactFail();

		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		if ("".equals(etName.getText().toString())
				|| "".equals(etEmail.getText().toString())
				|| "".equals(etContent.getText().toString())) {
			Toast.makeText(
					this.getActivity(),
					this.getActivity().getString(
							R.string.warning_fill_full_text), 2000).show();
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
	public void onDestroyView() {
		ConfigureData.currentScreen = 0;

		super.onDestroyView();
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

}