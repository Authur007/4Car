package ta.car4rent.activities;

import ta.car4rent.R;
import ta.car4rent.animation.MyAnimation;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.fragments.PostWarningFragment;
import ta.car4rent.gps_service.GPSTracker;
import ta.car4rent.objects.WarningsManager;
import ta.car4rent.slidingup.SlidingUpPanelLayout;
import ta.car4rent.slidingup.SlidingUpPanelLayout.PanelSlideListener;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class GoogleMapsActivity extends ActionBarActivity implements
		OnInfoWindowClickListener, OnClickListener, OnMarkerClickListener {

	// ======================================
	// VARIABLES AND STATIC FLAGS
	// ======================================
	//KEY JSON Warning
/*	{
		"deviceId":"String content",
		"reportSubTypeId":"String content",
		"reportTypeId":"String content",
		"longitude":"String content",
		"latitude":"String content",
		"note":"String content"
	}*/
	public static final String ID_DEVICE = "deviceId";
	public static final String ID_SUB_TYPE = "reportSubTypeId";
	public static final String ID_TYPE = "reportTypeId";
	public static final String LONGITUTE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String CONTENT = "note";
	
	
	
	// warnings manager will handle all data will be load
	WarningsManager warningsManager;

	// Google Map
	private GoogleMap googleMap;
	Circle myCircle;
	public static LatLng mCurrentLocation = null;
	private GPSTracker gps;
	// Component
	ImageButton btnWarning;
	ImageButton btnPostWarning;

	// popup window warning
	private PopupWindow pwindowWarning;
	ImageButton btnClosePopupWarning;

	// layout canh bao
	RelativeLayout btnAllWarning;
	RelativeLayout btnChotCongAn;
	RelativeLayout btnGiaoThong;
	RelativeLayout btnTaiNan;
	RelativeLayout btnTrangThai;

	// layout show data canh bao
	LinearLayout layoutChotCongAn;
	LinearLayout layoutGiaoThong;
	LinearLayout layoutTaiNan;
	LinearLayout layoutTrangThai;

	// textView to show number of warning
	TextView txtNumberAllWarning;
	TextView txtNumberPolice;
	TextView txtNumberTraffic;
	TextView txtNumberStatus;
	TextView txtNumberAccident;

	// popup window choose post warning
	private PopupWindow pwindowChoosePostWarning;
	ImageButton btnClosePopupChoosePostWarning;

	LinearLayout btnLayoutChotCongAn;
	LinearLayout btnLayoutGiaoThong;
	LinearLayout btnLayoutTaiNan;
	LinearLayout btnLayoutTrangThai;

	// sliding up panel menu
	SlidingUpPanelLayout slidingUp;
	MyAnimation myAnimation;
	LinearLayout layoutDrag;
	LinearLayout layoutContent;
	TextView txtTitle;
	TextView txtContent;

	// -------------------------------------------------------------------------------------

	// static variable
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		ConfigureData.activityGoogleMaps = this;
		setContentView(R.layout.activity_maps);
		getSupportActionBar().hide();

		btnWarning = (ImageButton) findViewById(R.id.btnWarning);
		btnPostWarning = (ImageButton) findViewById(R.id.btnPostWarning);
		txtTitle = (TextView) findViewById(R.id.txtTitleWarning);
		txtContent = (TextView) findViewById(R.id.txtContent);

		// setListioner
		btnWarning.setOnClickListener(this);
		btnPostWarning.setOnClickListener(this);

		// ==========================================
		// SLIDING UP PANEL
		// ==========================================
		// try to get Location
		mCurrentLocation = getGPSLocation(GoogleMapsActivity.this.getApplicationContext());
		slidingUp = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		slidingUp.setShadowDrawable(getResources().getDrawable(
				R.drawable.above_shadow));
		slidingUp.setAnchorPoint(0.3f);
		// drag only tile warring
		layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
		layoutDrag = (LinearLayout) findViewById(R.id.layoutDragArea);
		slidingUp.setDragView(layoutDrag);
		slidingUp.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {
			}

			@Override
			public void onPanelExpanded(View panel) {
			}

			@Override
			public void onPanelCollapsed(View panel) {
			}

			@Override
			public void onPanelAnchored(View panel) {
			}
		});

		// ==========================================
		// MAPS RENDER
		// ==========================================

		try {

			// Loading map
			initilizeMap();
			// config UI maps
			configMaps();
			// load data from internet
			warningsManager = new WarningsManager(this);
			warningsManager.loadDataWarning();

			// pin a icon into map
			warningsManager.drawWarningsIntoMap(
					WarningsManager.onlyShowWarning, googleMap);

			// set listener when marker is clicked
			googleMap.setOnInfoWindowClickListener(this);
			googleMap.setOnMarkerClickListener(this);
			googleMap
					.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

						@Override
						public boolean onMyLocationButtonClick() {
							mCurrentLocation = getGPSLocation(GoogleMapsActivity.this.getApplicationContext());
							drawCircle(mCurrentLocation, 3000);
							return false;
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * config UI maps
	 */
	private void configMaps() {
		// showing / hiding your current location
		googleMap.setMyLocationEnabled(true);

		// Enable / Disable zooming controls
		googleMap.getUiSettings().setZoomControlsEnabled(false);

		// Enable / Disable my location button
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);

		// Enable / Disable Compass icon
		googleMap.getUiSettings().setCompassEnabled(true);

		// Enable / Disable gesture
		googleMap.getUiSettings().setAllGesturesEnabled(true);

		// check gps
		if (mCurrentLocation == null) {
			// try to get Location
			mCurrentLocation = getGPSLocation(GoogleMapsActivity.this.getApplicationContext());
		}
		try {
			// draw a circle around your location with radius = 3km
			drawCircle(mCurrentLocation, 3000);

			// Moving Camera to a Location with animation
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(mCurrentLocation).zoom(13).build();
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		} catch (NullPointerException e) {
			Log.d("configMaps", "Không thể xác định tọa độ hiện tại !!");
		}

	}

	public void drawCircle(LatLng mLocation, int met) {
		if (myCircle != null) {
			myCircle.remove();
		}
		myCircle = googleMap.addCircle(new CircleOptions().center(mLocation)
				.radius(met).strokeColor(Color.BLUE).strokeWidth(2f)
				.fillColor(Color.argb(20, 0, 0, 255)));
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	// ======================================
	// EVENT LISTENER
	// ======================================

	@Override
	public void onInfoWindowClick(Marker marker) {
		int pos = Integer.parseInt(marker.getId().substring(1));
		warningsManager.getWarning(warningsManager.onlyShowWarning, pos);

	}

	/**
	 * call when click 2 imageButton Warning and PostWarning in the botton
	 */
	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.btnWarning:
			pwindowWarning = showPopupWarningWindow(this);
			fillNumberWarningIntoPopup();

			break;
		case R.id.btnPostWarning:
			if(ConfigureData.isEnableShowChoosePostWarning){
				pwindowChoosePostWarning = showPopupPostWarningChooseWindow(this);
			}
			

			break;
		default:
			break;
		}

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		txtTitle.setText(marker.getTitle());
		txtContent.setText(marker.getSnippet());
		return false;
	}

	// ===========================================
	// POPUP WINDOW WARNING
	// ===========================================

	private PopupWindow showPopupWarningWindow(Context mcon) {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) GoogleMapsActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.popup_warning,
					(ViewGroup) findViewById(R.id.popupWarning));
			PopupWindow pwindo = new PopupWindow(layout,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			pwindo.setAnimationStyle(R.style.AnimationPopup);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
			// set button click listioner
			btnClosePopupWarning = (ImageButton) layout
					.findViewById(R.id.btnClosePopup);
			btnClosePopupWarning
					.setOnClickListener(cancel_button_click_listener);

			// button layout
			btnAllWarning = (RelativeLayout) layout
					.findViewById(R.id.layout_btnAllWarning);
			btnChotCongAn = (RelativeLayout) layout
					.findViewById(R.id.layout_btnChotCongAn);
			btnGiaoThong = (RelativeLayout) layout
					.findViewById(R.id.layout_btnGiaoThong);
			btnTaiNan = (RelativeLayout) layout
					.findViewById(R.id.layout_btnTaiNan);
			btnTrangThai = (RelativeLayout) layout
					.findViewById(R.id.layout_btnTrangThai);

			// set onClick
			btnAllWarning.setOnClickListener(loadDataWarning_clickListener);
			btnChotCongAn.setOnClickListener(loadDataWarning_clickListener);
			btnGiaoThong.setOnClickListener(loadDataWarning_clickListener);
			btnTaiNan.setOnClickListener(loadDataWarning_clickListener);
			btnTrangThai.setOnClickListener(loadDataWarning_clickListener);

			// load layout show data warning
			layoutChotCongAn = (LinearLayout) layout
					.findViewById(R.id.layoutChotCongAn);
			layoutGiaoThong = (LinearLayout) layout
					.findViewById(R.id.layoutGiaoThong);
			layoutTaiNan = (LinearLayout) layout
					.findViewById(R.id.layoutTaiNan);
			layoutTrangThai = (LinearLayout) layout
					.findViewById(R.id.layoutTrangThai);
			// findView txtNumber
			txtNumberAllWarning = (TextView) layout
					.findViewById(R.id.txtNumberAll);
			txtNumberPolice = (TextView) layout
					.findViewById(R.id.txtNumberChotCongAn);
			txtNumberTraffic = (TextView) layout
					.findViewById(R.id.txtNumberGiaoThong);
			txtNumberStatus = (TextView) layout
					.findViewById(R.id.txtNumberTrangThai);
			txtNumberAccident = (TextView) layout
					.findViewById(R.id.txtNumberTaiNan);

			return pwindo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private OnClickListener cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pwindowWarning.dismiss();

		}
	};

	private OnClickListener loadDataWarning_clickListener = new OnClickListener() {
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), v.getId() + "", 0).show();
			switch (v.getId()) {
			case R.id.layout_btnAllWarning:
				if (!layoutChotCongAn.isShown()) {
					layoutChotCongAn.setVisibility(View.VISIBLE);
				}
				if (!layoutGiaoThong.isShown()) {
					layoutGiaoThong.setVisibility(View.VISIBLE);
				}
				if (!layoutTaiNan.isShown()) {
					layoutTaiNan.setVisibility(View.VISIBLE);
				}
				if (!layoutTrangThai.isShown()) {
					layoutTrangThai.setVisibility(View.VISIBLE);
				}

				break;
			case R.id.layout_btnChotCongAn:
				if (!layoutChotCongAn.isShown()) {
					layoutChotCongAn.setVisibility(View.VISIBLE);
				} else {
					layoutChotCongAn.setVisibility(View.GONE);
				}

				break;
			case R.id.layout_btnGiaoThong:
				if (!layoutGiaoThong.isShown()) {
					layoutGiaoThong.setVisibility(View.VISIBLE);
				} else {
					layoutGiaoThong.setVisibility(View.GONE);
				}
				break;
			case R.id.layout_btnTaiNan:
				if (!layoutTaiNan.isShown()) {
					layoutTaiNan.setVisibility(View.VISIBLE);
				} else {
					layoutTaiNan.setVisibility(View.GONE);
				}
				break;
			case R.id.layout_btnTrangThai:
				if (!layoutTrangThai.isShown()) {
					layoutTrangThai.setVisibility(View.VISIBLE);
				} else {
					layoutTrangThai.setVisibility(View.GONE);
				}
				break;

			default:
				break;
			}
		}
	};

	// --------------------------------------------
	// WORKING WITH DATA
	// --------------------------------------------
	private void fillNumberWarningIntoPopup() {
		txtNumberAllWarning.setText(warningsManager.getNumberAllWarning() + "");
		txtNumberPolice.setText(warningsManager.listChotCongAn.size() + "");
		txtNumberAccident.setText(warningsManager.listTaiNan.size() + "");
		txtNumberStatus.setText(warningsManager.listTrangThai.size() + "");
		txtNumberTraffic.setText(warningsManager.listGiaoThong.size() + "");

	}

	// ===========================================
	// POPUP WINDOW POST WARNING
	// ===========================================

	private PopupWindow showPopupPostWarningChooseWindow(Context mcon) {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) GoogleMapsActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.popup_post_warning_choose,
					(ViewGroup) findViewById(R.id.popupPostWarningChoose));
			PopupWindow pwindo = new PopupWindow(layout,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			pwindo.setAnimationStyle(R.style.AnimationPopup);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
			// findView and setClick here
			// set button click listioner
			btnClosePopupWarning = (ImageButton) layout
					.findViewById(R.id.btnClosePopupChoose);
			btnClosePopupWarning
					.setOnClickListener(choose_cancel_button_click_listener);

			btnLayoutChotCongAn = (LinearLayout) layout
					.findViewById(R.id.layoutChooseChotCongAn);
			btnLayoutGiaoThong = (LinearLayout) layout
					.findViewById(R.id.layoutChooseGiaoThong);
			btnLayoutTaiNan = (LinearLayout) layout
					.findViewById(R.id.layoutChooseTaiNan);
			btnLayoutTrangThai = (LinearLayout) layout
					.findViewById(R.id.layoutChooseTrangThai);

			// set click listener

			btnLayoutChotCongAn
					.setOnClickListener(choosePostWarning_clickListener);
			btnLayoutGiaoThong
					.setOnClickListener(choosePostWarning_clickListener);
			btnLayoutTaiNan.setOnClickListener(choosePostWarning_clickListener);
			btnLayoutTrangThai
					.setOnClickListener(choosePostWarning_clickListener);
			return pwindo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private OnClickListener choose_cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pwindowChoosePostWarning.dismiss();

		}
	};

	private OnClickListener choosePostWarning_clickListener = new OnClickListener() {
		public void onClick(View v) {

			Toast.makeText(getApplicationContext(), v.getId() + "", 0).show();
			Fragment fragment;

			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			fragment = new PostWarningFragment();
			ft.replace(R.id.maps_content, fragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			// add to back track
			ft.addToBackStack("Maps");

			switch (v.getId()) {
			case R.id.layoutChooseChotCongAn:
				ConfigureData.flagTypePostWarningFragment = PostWarningFragment.POST_POLICE;
				ft.commit();
				break;
			case R.id.layoutChooseGiaoThong:
				ConfigureData.flagTypePostWarningFragment = PostWarningFragment.POST_TRAFFIC;
				ft.commit();

				break;
			case R.id.layoutChooseTaiNan:
				ConfigureData.flagTypePostWarningFragment = PostWarningFragment.POST_ACCIDENT;
				ft.commit();
				break;
			case R.id.layoutChooseTrangThai:
				ConfigureData.flagTypePostWarningFragment = PostWarningFragment.POST_STATUS;
				ft.commit();
				break;

			default:
				break;
			}
			pwindowChoosePostWarning.dismiss();
		}
	};

	// ===========================================
	// GPS
	// ===========================================

	public LatLng getGPSLocation(Context mContext) {
		LatLng mCurrentLatLng = null;
		// create class object
		gps = new GPSTracker(mContext);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			mCurrentLatLng = new LatLng(latitude, longitude);
			Toast.makeText(this,
					mCurrentLatLng.latitude + " , " + mCurrentLatLng.longitude,
					0).show();
			// get address from location
			// new GetAddressTask(Config.activity).execute(location);

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			// gps.showSettingsAlert();

		}
		return mCurrentLatLng;
	}

	// ======================================
	// OTHERS
	// ======================================
}
