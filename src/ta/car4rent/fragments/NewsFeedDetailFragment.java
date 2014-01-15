package ta.car4rent.fragments;

import org.json.JSONException;

import com.facebook.android.BuildConfig;
import com.google.analytics.tracking.android.EasyTracker;

import ta.car4rent.R;
import ta.car4rent.adapters.GalleryImageAdapter;
import ta.car4rent.configures.ConfigureData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.TextView;

public class NewsFeedDetailFragment extends Fragment implements OnClickListener {
	View view = null;
	
	GalleryImageAdapter galImageAdapter;
	Gallery galleryImageNewsFeedItem;
	
	TextView tvEmail;
	TextView tvCarOwnerPhone;
	TextView tvCarOwnerAddress;
	TextView tvCarOwnerName;
	TextView tvCarOwnerInformation;
	TextView tvRentalCondition;

	TextView tvColor;
	TextView tvTransmission;
	TextView tvNumberSeat;
	TextView tvAirCondional;
	TextView tvFuel;
	TextView tvPrice;

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
		fillFullInfoData();
	}

	private void fillFullInfoData() {
		try {
			// Load Image for Gallery
			String[] imageURIs = ResultSeachFragmemt.selectedCarJSONObject.getString("image").split(",");
			GalleryImageAdapter galImageAdapter = new GalleryImageAdapter(ConfigureData.activityMain, imageURIs, 0);
			galleryImageNewsFeedItem.setAdapter(galImageAdapter);
			galleryImageNewsFeedItem.setAnimationDuration(500);
			galleryImageNewsFeedItem.setSpacing(0);
			
			tvEmail.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerEmail"));
		
			tvCarOwnerPhone.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerPhone")
				+ " - " + ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerAdditionalPhone"));
		
			tvCarOwnerAddress.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerAddress")
				+ " - " + ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerDistrict")
				+ " - " + ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerCity"));
		
			tvCarOwnerName.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerName"));
			tvColor.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("color"));
			tvTransmission.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("transmission"));
			tvNumberSeat.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("style"));
			// tvAirCondional.setText(ResultSeachFragmemt.selectedCarJSONObject.getString(name));
			tvFuel.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("fuel"));
			tvPrice.setText(ResultSeachFragmemt.selectedCarJSONObject.getString("price") + "\nVNÄ�/NgÃ y");
			
			tvCarOwnerInformation.setText(Html.fromHtml(ResultSeachFragmemt.selectedCarJSONObject.getString("carOwnerInformation")));
			tvRentalCondition.setText(Html.fromHtml(ResultSeachFragmemt.selectedCarJSONObject.getString("rentalCondition")));
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private void setOnClickListionerComponent() {
		tvEmail.setOnClickListener(this);
		tvCarOwnerPhone.setOnClickListener(this);
		tvCarOwnerAddress.setOnClickListener(this);

	}

	private void findViewByID() {
		galleryImageNewsFeedItem = (Gallery) getView().findViewById(R.id.imageAttach);
		
		tvEmail = (TextView) getView().findViewById(R.id.tvEmail);
		tvCarOwnerPhone = (TextView) getView().findViewById(R.id.tvCarOwnerPhone);
		tvCarOwnerAddress = (TextView) getView().findViewById(R.id.tvCarOwnerAddress);
		tvCarOwnerName = (TextView) getView().findViewById(R.id.tvCarOwnerName);
		tvCarOwnerInformation = (TextView) getView().findViewById(R.id.tvCarOwnerInformation);
		tvRentalCondition = (TextView) getView().findViewById(R.id.tvRentalCondition);

		tvAirCondional = (TextView) getView().findViewById(R.id.tvAirConditional);
		tvNumberSeat = (TextView) getView().findViewById(R.id.tvNumberSeat);
		tvColor = (TextView) getView().findViewById(R.id.tvColor);
		tvFuel = (TextView) getView().findViewById(R.id.tvFuel);
		tvTransmission = (TextView) getView().findViewById(R.id.tvTransmission);
		tvNumberSeat = (TextView) getView().findViewById(R.id.tvNumberSeat);
		tvPrice = (TextView) getView().findViewById(R.id.tvPrice);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tvEmail:
			Intent intentSendEmail = new Intent(Intent.ACTION_SEND);
			intentSendEmail.setType("text/html");
			intentSendEmail.putExtra(Intent.EXTRA_EMAIL, "fuckfuc");
			intentSendEmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail));
			intentSendEmail.putExtra(Intent.EXTRA_TEXT, "Gửi " + tvCarOwnerName.getText().toString() + "\n");

			startActivity(Intent.createChooser(intentSendEmail, "Send Email"));
			break;

		case R.id.tvCarOwnerPhone:
			Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ tvCarOwnerPhone.getText().toString()));
			startActivity(intentCall);
			break;

		case R.id.tvCarOwnerAddress:
			String geoCode = null;
			try {
				geoCode = "http://maps.google.com/maps?q="
						+ tvCarOwnerAddress.getText();

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
