package ta.car4rent.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.ServiceCloseCarRequest;
import ta.car4rent.webservices.ServiceManagerGetCarRequested;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
	TextView tvCarStyle;
	TextView tvCarTransmission;

	// layout
	LinearLayout layoutPlaceTo;
	LinearLayout layoutTransmission;
	LinearLayout layoutCarMake;
	LinearLayout layoutCarModel;
	LinearLayout layoutCarStyle;
	LinearLayout layoutRequestCarInfoCar;
	// Comment button
	Button btnShowCommentRequest;
	Button btnCloseCarRequest;
	int mRequestId;

	View view = null;
	int carRequestID = 132;
	int hasDriver = 0;

	public void setCarRequestID(int requestId) {
		carRequestID = requestId;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainActivity.Instance.showActionFilterSpinner(false);
		
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
		tvCarMake = (TextView) view.findViewById(R.id.tvCarMake);
		tvCarModel = (TextView) view.findViewById(R.id.tvCarModel);
		tvCarStyle = (TextView) view.findViewById(R.id.tvCarStyle);
		tvCarTransmission = (TextView) view.findViewById(R.id.tvCarTransmission);
		tvCityFrom = (TextView) view.findViewById(R.id.tvCityFrom);
		tvCityTo = (TextView) view.findViewById(R.id.tvCityTo);
		tvDateExpiration = (TextView) view.findViewById(R.id.tvDateExpiration);
		tvDateFrom = (TextView) view.findViewById(R.id.tvDateFromRequest);
		tvDateTo = (TextView) view.findViewById(R.id.tvDateToRequest);
		tvDatePosted = (TextView) view.findViewById(R.id.tvDatePosted);
		tvDistricFrom = (TextView) view.findViewById(R.id.tvDistricFrom);
		tvDistricTo = (TextView) view.findViewById(R.id.tvDistricTo);
		tvHasDriver = (TextView) view.findViewById(R.id.tvDriverRequest);
		tvPosterEmail = (TextView) view.findViewById(R.id.tvPosterEmail);
		tvPosterName = (TextView) view.findViewById(R.id.tvPosterName);
		tvPosterPhone = (TextView) view.findViewById(R.id.tvPosterPhone);
		tvPosterRequirement = (TextView) view.findViewById(R.id.tvRequirementPoster);
		tvPriceFrom = (TextView) view.findViewById(R.id.tvPriceFromRequest);
		tvPriceTo = (TextView) view.findViewById(R.id.tvPriceToRequest);

		// layout
		layoutPlaceTo = (LinearLayout) view.findViewById(R.id.layoutPlaceToRequest);
		
		layoutTransmission = (LinearLayout) view.findViewById(R.id.layoutTransmission);
		layoutCarMake = (LinearLayout) view.findViewById(R.id.layoutCarMake);
		layoutCarModel = (LinearLayout) view.findViewById(R.id.layoutCarModel);
		layoutCarStyle = (LinearLayout) view.findViewById(R.id.layoutCarStyle);
		layoutRequestCarInfoCar = (LinearLayout) view.findViewById(R.id.layout_request_car_info_car);

		btnShowCommentRequest = (Button) view.findViewById(R.id.btnShowCommentRequest);
		btnShowCommentRequest.setOnClickListener(this);
		
		btnCloseCarRequest = (Button) view.findViewById(R.id.btnCloseCarRequest);
		btnCloseCarRequest.setOnClickListener(this);
		
		if (ManageCarRequestesFragment.selectedCarRequestStatus.charAt(0) != 'M') {
			btnCloseCarRequest.setVisibility(View.GONE);
			view.findViewById(R.id.txtNothing1).setVisibility(View.GONE);
		}
		
		 
		return view;
	}
	
	
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		MainActivity.Instance.showActionFilterSpinner(false);
		super.onResume();
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
						ConfigureData.carRequestDetailObject = responeObject.getJSONObject("data");
						hasDriver = ConfigureData.carRequestDetailObject.getInt("hasCarDriver");
						
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
			
			mRequestId = ConfigureData.carRequestDetailObject.getInt("id");
			
			String temp = "";
			
			temp = ConfigureData.carRequestDetailObject.getString("createdBy");
			tvPosterName.setText(temp.equals("null") ? "" : temp);
						
			temp = ConfigureData.carRequestDetailObject.getString("email");
			tvPosterEmail.setText((temp.equals("null")) ? "" : temp);

			temp = ConfigureData.carRequestDetailObject.getString("phone");
			tvPosterPhone.setText((temp.equals("null")) ? "" : temp);
			
			temp = ConfigureData.carRequestDetailObject.getString("createDate");
			tvDatePosted.setText(temp.equals("null") ? "" : temp);
			
			temp = ConfigureData.carRequestDetailObject.getString("expirationDate");
			tvDateExpiration.setText(temp.equals("null") ? "" : temp);

			temp = ConfigureData.carRequestDetailObject.getString("information");
			tvPosterRequirement.setText((temp.equals("null")) ? "" : temp);

			temp = ConfigureData.carRequestDetailObject.getString("startDate");
			tvDateFrom.setText((temp.equals("null")) ? "" : temp);
			
			temp = ConfigureData.carRequestDetailObject.getString("endDate");
			tvDateTo.setText((temp.equals("null")) ? "" : temp);
			
			tvHasDriver.setText((hasDriver == 0) ? getString(R.string.request_car_driver_no)
							: getString(R.string.request_car_driver_has));

			temp = ConfigureData.carRequestDetailObject.getString("fromPrice");
			tvPriceFrom.setText((temp.equals("null")) ? "" : StaticFunction.formatVnMoney(temp));

			temp = ConfigureData.carRequestDetailObject.getString("toPrice");
			tvPriceTo.setText((temp.equals("null")) ? "" : StaticFunction.formatVnMoney(temp));
			
			temp = ConfigureData.carRequestDetailObject.getString("cityFrom");
			tvCityFrom.setText((temp.equals("null")) ? "" : temp);
			
			temp = ConfigureData.carRequestDetailObject.getString("districtFrom");
			tvDistricFrom.setText((temp.equals("null")) ? "" : temp);
			
			if (hasDriver == 1) {
				temp = ConfigureData.carRequestDetailObject.getString("cityTo");
				tvCityTo.setText((temp.equals("null")) ? "" : temp);

				temp = ConfigureData.carRequestDetailObject.getString("districtTo");
				tvDistricTo.setText((temp.equals("null")) ? "" : temp);
				
				temp = ConfigureData.carRequestDetailObject.getString("cityFrom");
				tvCityFrom.setText((temp.equals("null")) ? "" : temp);
				
				temp = ConfigureData.carRequestDetailObject.getString("districtFrom");
				tvDistricFrom.setText((temp.equals("null")) ? "" : temp);
				
			} else {
				temp = ConfigureData.carRequestDetailObject.getString("city");
				tvCityFrom.setText((temp.equals("null")) ? "" : temp);
				
				temp = ConfigureData.carRequestDetailObject.getString("district");
				tvDistricFrom.setText((temp.equals("null")) ? "" : temp);
				
			}

			layoutRequestCarInfoCar.setVisibility(View.GONE);
			int showCount = 4;
			temp = ConfigureData.carRequestDetailObject.getString("make");
			tvCarMake.setText(temp);
			if (temp.equals("null")) {
				layoutCarMake.setVisibility(View.GONE);
				showCount--;
			}

			temp = ConfigureData.carRequestDetailObject.getString("model");
			tvCarModel.setText(temp);
			if (temp.equals("null")) {
				layoutCarModel.setVisibility(View.GONE);
				showCount--;
			}

			temp = ConfigureData.carRequestDetailObject.getString("style");
			tvCarStyle.setText(temp);
			if (temp.equals("null")) {
				layoutCarStyle.setVisibility(View.GONE);
				showCount--;
			}
			
			temp = ConfigureData.carRequestDetailObject.getString("transmission");
			tvCarTransmission.setText(temp);
			if (temp.equals("null")) {
				layoutTransmission.setVisibility(View.GONE);
				showCount--;
			}

			if (showCount > 0) {				
				layoutRequestCarInfoCar.setVisibility(View.VISIBLE);
			}


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
		case R.id.btnCloseCarRequest:
			// Show confirm close dialog
						(new AlertDialog.Builder(getActivity()))
						.setIcon(R.drawable.ic_launcher)
						.setTitle("Xác nhận !")
						.setMessage("Bạn có chắc muốn đóng tin này không.")
						.setNegativeButton("Không", null)
						.setPositiveButton("Có", changeStatus)
						.create()
						.show();
		default:
			break;
		}

	}
	
	// Handle when apply close request
		private DialogInterface.OnClickListener changeStatus = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ServiceCloseCarRequest serviceCloseCarRequest = new ServiceCloseCarRequest();
				serviceCloseCarRequest.addOnGetJsonListener(new OnGetJsonListener() {
					
					@Override
					public void onGetJsonFail(String response) {
						// show error
						AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
						alertDialog.setIcon(R.drawable.ic_error);
						alertDialog.setTitle("Rất tiếc!");
						alertDialog.setMessage("Đóng tin không thành công.");
						alertDialog.setButton("Kết thúc",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								});
						alertDialog.show();
					}
					
					@Override
					public void onGetJsonCompleted(String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject responseObject = new JSONObject(response);
							if (responseObject.getBoolean("status")) {
								// Change status now
								// success
								AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
								alertDialog.setIcon(R.drawable.ic_launcher);
								alertDialog.setTitle("Chúc mừng!");
								alertDialog.setMessage("Đóng tin thành công.");
								alertDialog.setButton("Đồng ý",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												FragmentManager fm = getActivity().getSupportFragmentManager();
												FragmentTransaction ft = fm.beginTransaction();
												// Insert the fragment by replacing any existing
												// fragment
												ManageCarRequestesFragment fragment = new ManageCarRequestesFragment();
												ft.replace(R.id.content_frame, fragment).commit();

											}
										});
								alertDialog.show();
								
							}
						} catch (Exception e) {
							// show error
							AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
							alertDialog.setIcon(R.drawable.ic_error);
							alertDialog.setTitle("Rất tiếc!");
							alertDialog.setMessage("Đóng tin không thành công.");
							alertDialog.setButton("Đồng ý",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									});
							alertDialog.show();
							
							e.printStackTrace();
						}
					}
				});
				
				serviceCloseCarRequest.closeCarRequest(mRequestId);
				
			}
		};

}
