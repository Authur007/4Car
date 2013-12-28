package ta.car4rent.fragments;

import org.json.JSONException;

import ta.car4rent.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsFeedDetailFragment extends Fragment implements OnClickListener {
	View view = null;
	TextView txtEmail;
	TextView txtPhoneNumber;
	TextView txtAddressOwnCar;
	TextView txtAgent;
	TextView txtContentDetail;
	TextView txtCondition;

	TextView txtColorCar;
	TextView txtLoaiSo;
	TextView txtChair;
	TextView txtNumberSeat;
	TextView txtAirCondional;
	TextView txtEngine;
	TextView txtPrice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_newsfeed_detail,
					container, false);

		} else {
			// If we are returning from a configuration change:
			// "view" is still attached to the previous view hierarchy
			// so we need to remove it and re-attach it to the current one
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		findViewByID();
		setOnClickListionerComponent();

	}

	private void setOnClickListionerComponent() {
		txtEmail.setOnClickListener(this);
		txtPhoneNumber.setOnClickListener(this);
		txtAddressOwnCar.setOnClickListener(this);

	}

	private void findViewByID() {
		txtEmail = (TextView) getView().findViewById(R.id.txtEmail);
		txtPhoneNumber = (TextView) getView().findViewById(R.id.txtPhone);
		txtAddressOwnCar = (TextView) getView().findViewById(
				R.id.txtAddressOwnCar);
		txtAgent = (TextView) getView().findViewById(R.id.txtAgent);
		txtContentDetail = (TextView) getView().findViewById(R.id.txtContent);
		txtCondition = (TextView) getView().findViewById(R.id.txtCondition);

		txtAirCondional = (TextView) getView().findViewById(
				R.id.txtAirConditional);
		txtChair = (TextView) getView().findViewById(R.id.txtChair);
		txtColorCar = (TextView) getView().findViewById(R.id.txtColorCar);
		txtEngine = (TextView) getView().findViewById(R.id.txtEngine);
		txtLoaiSo = (TextView) getView().findViewById(R.id.txtLoaiSo);
		txtNumberSeat = (TextView) getView().findViewById(R.id.txtNumberSeat);
		txtPrice = (TextView) getView().findViewById(R.id.txtPrice);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.txtEmail:
			Intent intentSendEmail = new Intent(Intent.ACTION_SEND);
			intentSendEmail.setType("text/html");
			intentSendEmail.putExtra(Intent.EXTRA_EMAIL, txtEmail.getText());
			intentSendEmail.putExtra(Intent.EXTRA_SUBJECT, "Subject");
			intentSendEmail.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

			startActivity(Intent.createChooser(intentSendEmail, "Send Email"));
			break;

		case R.id.txtPhone:
			Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ txtPhoneNumber.getText().toString()));
			startActivity(intentCall);
			break;

		case R.id.txtAddressOwnCar:
			String geoCode = null;
			try {
				geoCode = "http://maps.google.com/maps?q="
						+ txtAddressOwnCar.getText();

			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoCode));
			startActivity(intent);

			break;
		default:
			break;
		}

	}
}
