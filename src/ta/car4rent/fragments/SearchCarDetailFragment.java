package ta.car4rent.fragments;

import org.json.JSONException;

import ta.car4rent.R;
import ta.car4rent.activities.FullScreenImageActivity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.android.BuildConfig;
import com.google.analytics.tracking.android.EasyTracker;

public class SearchCarDetailFragment extends Fragment implements OnClickListener {
	View view = null;

	GalleryImageAdapter galImageAdapter;
	String[] imageURIs;
	Gallery galleryImageNewsFeedItem;

	TextView tvEmail;
	TextView tvCarOwnerPhone;
	TextView tvCarOwnerAdditionalPhone;
	TextView tvCarOwnerAddress;
	TextView tvCarOwnerName;
	TextView tvCarOwnerInformation;
	TextView tvRentalCondition;

	TextView tvCarName;
	TextView tvColor;
	TextView tvTransmission;
	TextView tvNumberSeat;
	TextView tvAirCondional;
	TextView tvFuel;
	TextView tvPrice;
	TextView tvNumberOfImage;

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
			view = inflater.inflate(R.layout.fragment_search_car_detail,
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
			imageURIs = ResultSeachFragmemt.selectedCarJSONObject.getString(
					"image").split(",");
			tvNumberOfImage.setText(imageURIs.length + "");

			galImageAdapter = new GalleryImageAdapter(
					ConfigureData.activityMain, imageURIs, 0);
			galleryImageNewsFeedItem.setAdapter(galImageAdapter);
			galleryImageNewsFeedItem.setAnimationDuration(500);
			galleryImageNewsFeedItem.setSpacing(0);

			tvEmail.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("carOwnerEmail"));

			tvCarOwnerPhone.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("carOwnerPhone"));
			String carOwnerPhoneAdd = ResultSeachFragmemt.selectedCarJSONObject
					.getString("carOwnerAdditionalPhone").toString();
			if (carOwnerPhoneAdd != null) {
				tvCarOwnerAdditionalPhone.setVisibility(View.VISIBLE);
				tvCarOwnerAdditionalPhone.setText(carOwnerPhoneAdd);
			} else {
				tvCarOwnerAdditionalPhone.setVisibility(View.GONE);
			}

			tvCarOwnerAddress.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("carOwnerAddress")
					+ " - "
					+ ResultSeachFragmemt.selectedCarJSONObject
							.getString("carOwnerDistrict")
					+ " - "
					+ ResultSeachFragmemt.selectedCarJSONObject
							.getString("carOwnerCity"));

			tvCarOwnerName.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("carOwnerName"));

			// Load car name info
			int year = ResultSeachFragmemt.selectedCarJSONObject.getInt("year");
			String make = ResultSeachFragmemt.selectedCarJSONObject
					.getString("make");
			String model = ResultSeachFragmemt.selectedCarJSONObject
					.getString("model");
			String numberPlate = ResultSeachFragmemt.selectedCarJSONObject
					.getString("numberPlate");
			tvCarName.setText(year + " " + make + " " + model + " ("
					+ numberPlate + ")");

			tvColor.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("color"));
			tvTransmission.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("transmission"));
			tvNumberSeat.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("style"));
			// tvAirCondional.setText(ResultSeachFragmemt.selectedCarJSONObject.getString(name));
			tvFuel.setText(ResultSeachFragmemt.selectedCarJSONObject
					.getString("fuel"));

			
			String price = ResultSeachFragmemt.selectedCarJSONObject.getString("price");
			String s = "";
			for (int i = price.length() - 1; i >= 0; i--) {
				if ((price.length() - i) % 3 == 0 && i != 0) {
					s = "." + price.charAt(i) + s;
				} else {
					s = price.charAt(i) + s;
				}
			}
			
			if (ResultSeachFragmemt.selectedCarJSONObject.getBoolean("hasCarDriver")) {
				// price of hasdriver
				tvPrice.setText(s + "\n"
						+ "VND");
				
				// get timerent and distent rent
				int timeRent = ResultSeachFragmemt.selectedCarJSONObject.getInt("day");
				int hoursRent = ResultSeachFragmemt.selectedCarJSONObject.getInt("hours");
				((TextView) getView().findViewById(R.id.tvTimeRent)).setText(timeRent
						+ " "
						+ ConfigureData.activityMain.getString(R.string.day)+ " "+ hoursRent +" "+ ConfigureData.activityMain.getString(R.string.hours));
				((LinearLayout) getView().findViewById(R.id.layoutTimeRent))
						.setVisibility(View.VISIBLE);

				int kmRent = ResultSeachFragmemt.selectedCarJSONObject.getInt("km");

				((TextView) getView().findViewById(R.id.tvDistanceRent))
						.setText(kmRent + " Km");
				((LinearLayout) getView().findViewById(R.id.layoutDistanceRent))
						.setVisibility(View.VISIBLE);
				
				
				// show layout info car detail
				((LinearLayout)getView().findViewById(R.id.layoutInfoCarDetail)).setVisibility(View.VISIBLE);
				
				
				//((TextView) getView().findViewById(R.id.tvPricePerHours)).setText(ResultSeachFragmemt.selectedCarJSONObject.getInt("km"));
				//((TextView) getView().findViewById(R.id.tvPricePerKm)).setText(ResultSeachFragmemt.selectedCarJSONObject.getInt("km"));
				((TextView) getView().findViewById(R.id.tvVAT)).setText(ResultSeachFragmemt.selectedCarJSONObject.getBoolean("hasVAT")?getString(R.string.has):getString(R.string.no));
				((TextView) getView().findViewById(R.id.tvPhiCauDuong)).setText(ResultSeachFragmemt.selectedCarJSONObject.getBoolean("hasParkingFee")?getString(R.string.has):getString(R.string.no));
				((TextView) getView().findViewById(R.id.tvPhiXangDau)).setText(ResultSeachFragmemt.selectedCarJSONObject.getBoolean("hasGasFee")?getString(R.string.has):getString(R.string.no));
			}else{
				((TextView) getView().findViewById(R.id.tvPrice)).setText(s + "\n"
						+ ConfigureData.activityMain.getString(R.string.vnd_day));
			}
			tvCarOwnerInformation.setText(Html
					.fromHtml(ResultSeachFragmemt.selectedCarJSONObject
							.getString("carOwnerInformation")));
			// replaceAll("\\<[^>]*>","")
			tvRentalCondition.setText(Html
					.fromHtml(ResultSeachFragmemt.selectedCarJSONObject
							.getString("rentalCondition")));
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private void setOnClickListionerComponent() {
		tvEmail.setOnClickListener(this);
		tvCarOwnerPhone.setOnClickListener(this);
		tvCarOwnerAdditionalPhone.setOnClickListener(this);
		tvCarOwnerAddress.setOnClickListener(this);
		galleryImageNewsFeedItem
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int id, long pos) {
						ConfigureData.FullScreenImage = imageURIs;
						startActivity(new Intent(getActivity(),
								FullScreenImageActivity.class));
					}
				});

	}

	private void findViewByID() {
		galleryImageNewsFeedItem = (Gallery) getView().findViewById(
				R.id.imageAttach);

		tvNumberOfImage = (TextView) getView().findViewById(
				R.id.txtRowNumberImage);
		tvNumberOfImage.setVisibility(View.VISIBLE);

		tvEmail = (TextView) getView().findViewById(R.id.tvEmail);
		tvCarOwnerPhone = (TextView) getView().findViewById(
				R.id.tvCarOwnerPhone);

		tvCarOwnerAdditionalPhone = (TextView) getView().findViewById(
				R.id.tvCarOwnerAdditionalPhone);

		tvCarOwnerAddress = (TextView) getView().findViewById(
				R.id.tvCarOwnerAddress);
		tvCarOwnerName = (TextView) getView().findViewById(R.id.tvCarOwnerName);
		tvCarOwnerInformation = (TextView) getView().findViewById(
				R.id.tvCarOwnerInformation);
		tvRentalCondition = (TextView) getView().findViewById(
				R.id.tvRentalCondition);

		tvAirCondional = (TextView) getView().findViewById(
				R.id.tvAirConditional);
		tvNumberSeat = (TextView) getView().findViewById(R.id.tvNumberSeat);

		tvCarName = (TextView) getView().findViewById(R.id.tvCarName);
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
			// ACTION_SENDTO filters for email apps (discard bluetooth and
			// others)
			Intent intentEmail = new Intent(Intent.ACTION_SENDTO,
					Uri.parse("mailto:" + tvEmail.getText().toString()));
			intentEmail.putExtra("subject", getString(R.string.subject_mail));
			intentEmail.putExtra("body", getString(R.string.post) + " "
					+ tvCarOwnerName.getText().toString() + "\n");
			startActivity(intentEmail);

			break;

		case R.id.tvCarOwnerPhone:
			Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ tvCarOwnerPhone.getText().toString()));
			startActivity(intentCall);
			break;

		case R.id.tvCarOwnerAdditionalPhone:
			Intent intentCallAdd = new Intent(Intent.ACTION_DIAL,
					Uri.parse("tel:"
							+ tvCarOwnerAdditionalPhone.getText().toString()));
			startActivity(intentCallAdd);
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
