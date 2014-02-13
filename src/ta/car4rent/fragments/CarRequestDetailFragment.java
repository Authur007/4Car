package ta.car4rent.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.activities.GoogleMapsActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.objects.ComentArrayAdapter;
import ta.car4rent.objects.ComentArrayRequestCarAdapter;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceManagerGetCarRequested;
import ta.car4rent.webservices.ServiceManagerPostCarRequestComment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CarRequestDetailFragment extends Fragment implements
		OnClickListener {

	// ===========================================
	// VARIABLES
	// ===========================================
	ProgressBar progressBar;

	TextView tvPosterName;
	TextView tvPosterEmail;
	TextView tvPosterPhone;
	TextView tvPosterRequirement;
	TextView tvDatePosted;
	TextView tvDateExpiration;
	TextView tvDateFrom;
	TextView tvDateTo;
	TextView tvPriceFrom;
	TextView tvPriceTo;
	TextView tvCityFrom;
	TextView tvDistricFrom;
	TextView tvCityTo;
	TextView tvDistricTo;
	TextView tvHasDriver;
	TextView tvCarMake;
	TextView tvCarModel;
	TextView tvCarstyle;
	TextView tvCarTransmission;

	// layout
	LinearLayout layoutPlaceTo;
	LinearLayout layoutTransmission;
	LinearLayout layoutCarMake;
	LinearLayout layoutCarModel;
	LinearLayout layoutCarStyle;
	// Comment button
	Button btnShowCommentRequest;

	View view = null;
	int carRequestID = 132;
	int hasDriver = 0;

	public void setCarRequestID(int requestId) {
		carRequestID = requestId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//if (view == null) {
			view = inflater.inflate(R.layout.fragment_car_request_detail,
					container, false);

		/*} else {
			// If we are returning from a configuration change:
			// "view" is still attached to the previous view hierarchy
			// so we need to remove it and re-attach it to the current one
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
		}*/

		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		// findViewById
		tvCarMake = (TextView) view.findViewById(R.id.tvCarMakeRequest);
		tvCarModel = (TextView) view.findViewById(R.id.tvCarModelRequest);
		tvCarstyle = (TextView) view.findViewById(R.id.tvCarStyleRequest);
		tvCarTransmission = (TextView) view
				.findViewById(R.id.tvCarTransmissionRequest);
		tvCityFrom = (TextView) view.findViewById(R.id.tvPlaceCityFromRequest);
		tvCityTo = (TextView) view.findViewById(R.id.tvPlaceCityToRequest);
		tvDateExpiration = (TextView) view.findViewById(R.id.tvDateExpiration);
		tvDateFrom = (TextView) view.findViewById(R.id.tvDateFromRequest);
		tvDateTo = (TextView) view.findViewById(R.id.tvDateToRequest);
		tvDatePosted = (TextView) view.findViewById(R.id.tvDatePosted);
		tvDistricFrom = (TextView) view
				.findViewById(R.id.tvPlaceDistricFromRequest);
		tvDistricTo = (TextView) view
				.findViewById(R.id.tvPlaceDistricToRequest);
		tvHasDriver = (TextView) view.findViewById(R.id.tvDriverRequest);
		tvPosterEmail = (TextView) view.findViewById(R.id.tvEmailPoster);
		tvPosterName = (TextView) view.findViewById(R.id.tvPosterName);
		tvPosterPhone = (TextView) view.findViewById(R.id.tvPosterPhone);
		tvPosterRequirement = (TextView) view
				.findViewById(R.id.tvRequirementPoster);
		tvPriceFrom = (TextView) view.findViewById(R.id.tvPriceFromRequest);
		tvPriceTo = (TextView) view.findViewById(R.id.tvPriceToRequest);

		// layout
		layoutPlaceTo = (LinearLayout) view
				.findViewById(R.id.layoutPlaceToRequest);
		layoutTransmission = (LinearLayout) view
				.findViewById(R.id.layoutTransmission);

		layoutCarMake = (LinearLayout) view.findViewById(R.id.layoutCarMake);
		layoutCarModel = (LinearLayout) view.findViewById(R.id.layoutCarModel);
		layoutCarStyle = (LinearLayout) view.findViewById(R.id.layoutCarStyle);

		btnShowCommentRequest = (Button) view
				.findViewById(R.id.btnShowCommentRequest);
		btnShowCommentRequest.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		getDataFromServer(carRequestID);

	}

	private void getDataFromServer(int mcarRequestID) {
		ServiceManagerGetCarRequested serviceGetCarRequest = new ServiceManagerGetCarRequested();
		serviceGetCarRequest.getCarRequestedDetail(mcarRequestID);
		serviceGetCarRequest.addOnGetJsonListener(new OnGetJsonListener() {

			@Override
			public void onGetJsonFail(String response) {
				// TODO Auto-generated method stub
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onGetJsonCompleted(String response) {
				try {
					JSONObject responeObject = new JSONObject(response);
					if (responeObject.getBoolean("status")) {
						ConfigureData.carRequestDetailObject = responeObject
								.getJSONObject("data");
						hasDriver = ConfigureData.carRequestDetailObject
								.getInt("hasCarDriver");
						if (hasDriver == 1) {
							showHasDriverUI();
						} else {
							showNoHasDriverUI();
						}
						try {
							fillData();
						} catch (Exception e) {
							
						}
					} else {
						Log.d("CarRequestDetail", "false");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				progressBar.setVisibility(View.GONE);

			}
		});

	}

	private void fillData() {
		try {
			String temp = "";
			tvPosterName.setText(ConfigureData.carRequestDetailObject
					.getString("createdBy"));
			tvDatePosted.setText(ConfigureData.carRequestDetailObject
					.getString("createDate"));
			tvDateExpiration.setText(ConfigureData.carRequestDetailObject
					.getString("expirationDate"));

			temp = ConfigureData.carRequestDetailObject
					.getString("information");
			tvPosterRequirement.setText((temp.equals("null")) ? "" : temp);

			tvDateFrom.setText(ConfigureData.carRequestDetailObject
					.getString("startDate"));
			tvDateTo.setText(ConfigureData.carRequestDetailObject
					.getString("endDate"));
			tvHasDriver
					.setText((hasDriver == 0) ? getString(R.string.request_car_driver_no)
							: getString(R.string.request_car_driver_has));

			temp = ConfigureData.carRequestDetailObject.getString("fromPrice");
			tvPriceFrom.setText((temp.equals("null")) ? "" : temp);

			temp = ConfigureData.carRequestDetailObject.getString("toPrice");
			tvPriceTo.setText((temp.equals("null")) ? "" : temp);
			tvCityFrom.setText(ConfigureData.carRequestDetailObject
					.getString("city"));
			tvDistricFrom.setText(ConfigureData.carRequestDetailObject
					.getString("district"));
			tvCityTo.setText(ConfigureData.carRequestDetailObject
					.getString("cityTo"));
			tvDistricTo.setText(ConfigureData.carRequestDetailObject
					.getString("districtTo"));

			temp = ConfigureData.carRequestDetailObject.getString("make");
			if ((temp.equals("null"))) {
				layoutCarMake.setVisibility(View.GONE);
			} else {
				tvCarMake.setText(temp);
			}

			temp = ConfigureData.carRequestDetailObject.getString("model");
			if ((temp.equals("null"))) {
				layoutCarModel.setVisibility(View.GONE);
			} else {
				tvCarModel.setText(temp);
			}

			temp = ConfigureData.carRequestDetailObject.getString("style");
			if ((temp.equals("null"))) {
				layoutCarStyle.setVisibility(View.GONE);
			} else {
				tvCarstyle.setText(temp);
			}
			temp = ConfigureData.carRequestDetailObject
					.getString("transmission");
			if ((temp.equals("null"))) {
				tvCarTransmission.setVisibility(View.GONE);
			} else {
				tvCarTransmission.setText(temp);
			}

			tvPosterEmail.setText(ConfigureData.carRequestDetailObject
					.getString("email"));

			temp = ConfigureData.carRequestDetailObject.getString("phone");
			tvPosterPhone.setText((temp.equals("null")) ? "" : temp);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showNoHasDriverUI() {
		layoutPlaceTo.setVisibility(View.GONE);
		layoutTransmission.setVisibility(View.VISIBLE);

	}

	private void showHasDriverUI() {
		layoutTransmission.setVisibility(View.GONE);
		layoutPlaceTo.setVisibility(View.VISIBLE);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnShowCommentRequest:
			Fragment fragment = new CarRequestCommentFragment();
			((CarRequestCommentFragment) fragment).setCarRequestCommentId(carRequestID);
			getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("Comment").commit();

			break;

		default:
			break;
		}

	}

}
