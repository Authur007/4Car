package ta.car4rent.activities;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.adapters.NewsFeedWarningListAdapter;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.fragments.LoginFragmemt;
import ta.car4rent.fragments.PostWarningFragment;
import ta.car4rent.gps_service.GPSTracker;
import ta.car4rent.objects.ComentArrayAdapter;
import ta.car4rent.objects.WarningsManager;
import ta.car4rent.slidingup.SlidingUpPanelLayout;
import ta.car4rent.slidingup.SlidingUpPanelLayout.PanelSlideListener;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServicePostComent;
import ta.car4rent.webservices.ServiceWarningGET;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
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
	// KEY JSON Warning
	/*
	 * { "deviceId":"String content", "reportSubTypeId":"String content",
	 * "reportTypeId":"String content", "longitude":"String content",
	 * "latitude":"String content", "note":"String content" }
	 */
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

	ImageButton btnGetGPS;
	// ==============================================
	// popup window warning newsfeed
	// ==============================================
	private PopupWindow pwindowWarning;
	ImageButton btnClosePopupWarning;

	// layout warning
	RelativeLayout btnAllWarning;
	RelativeLayout btnChotCongAn;
	RelativeLayout btnGiaoThong;
	RelativeLayout btnTaiNan;
	RelativeLayout btnTrangThai;

	Button btnRefresh;

	NewsFeedWarningListAdapter policeAdapter;
	NewsFeedWarningListAdapter trafficAdapter;
	NewsFeedWarningListAdapter accidentAdapter;
	NewsFeedWarningListAdapter statusAdapter;

	// textView to show number of warning
	TextView txtNumberAllWarning;
	TextView txtNumberPolice;
	TextView txtNumberTraffic;
	TextView txtNumberStatus;
	TextView txtNumberAccident;

	// ==============================================
	// popup window choose post warning
	// ==============================================
	private PopupWindow pwindowChoosePostWarning;
	ImageButton btnClosePopupChoosePostWarning;

	LinearLayout btnLayoutChotCongAn;
	LinearLayout btnLayoutGiaoThong;
	LinearLayout btnLayoutTaiNan;
	LinearLayout btnLayoutTrangThai;

	// sliding up panel menu
	String imageUrlWaring;
	SlidingUpPanelLayout slidingUp;
	LinearLayout layoutDrag;
	RelativeLayout layoutContent;
	TextView txtTypeWarning;
	TextView txtSubtype;

	TextView txtDetailNote;
	TextView txtDetailAddress;
	TextView txtDetailTime;
	TextView txtDetailDistance;

	ImageView imageWarning;
	Button btnShowInMap;
	Button btnSendComent;

	// ==============================================
	// popup window show listview Warning
	// ==============================================
	private ListPopupWindow pwindowShowListWarning;
	ImageButton btnClosePopupListWarning;
	// listView show data in popup NewsFeed
	ListView listViewWarning;
	// store key archor listpopup
	RelativeLayout archorListPopup;

	// focus object warning
	JSONObject ObjectMarkerClicked;
	ProgressDialog processDialog;
	Timer timerRefreshNews;
	Timer timerResfreshComent;
	// ==============================================
	// COMENT
	// ==============================================
	private ComentArrayAdapter commentAdapter;
	private ListView listComent;
	private EditText edtComent;
	private Button btnGoToLogin;

	// -------------------------------------------------------------------------------------

	// static variable
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		ConfigureData.activityGoogleMaps = this;
		setContentView(R.layout.activity_maps);
		getSupportActionBar().hide();
		try {
			// getLocation
			mCurrentLocation = getGPSLocation(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// gmaps
		btnGetGPS = (ImageButton) findViewById(R.id.btnGetGPS);

		// main view
		btnWarning = (ImageButton) findViewById(R.id.btnWarning);
		btnPostWarning = (ImageButton) findViewById(R.id.btnPostWarning);
		txtTypeWarning = (TextView) findViewById(R.id.txtTypeWarning);
		txtSubtype = (TextView) findViewById(R.id.txtSubtype);
		archorListPopup = (RelativeLayout) findViewById(R.id.mArchorListPopup);
		// component in show news detail
		imageWarning = (ImageView) findViewById(R.id.imageWarning);
		txtDetailNote = (TextView) findViewById(R.id.txt_detail_Note);
		txtDetailAddress = (TextView) findViewById(R.id.txt_detail_Address);
		txtDetailTime = (TextView) findViewById(R.id.txt_detail_Time);
		txtDetailDistance = (TextView) findViewById(R.id.txt_detail_Distance);

		btnShowInMap = (Button) findViewById(R.id.btnShowInMap);
		// comment
		listComent = (ListView) findViewById(R.id.listComent);
		// set adapter for list
		commentAdapter = new ComentArrayAdapter(GoogleMapsActivity.this,
				R.layout.row_coment_right);
		listComent.setAdapter(commentAdapter);

		edtComent = (EditText) findViewById(R.id.edtComent);
		btnGoToLogin = (Button) findViewById(R.id.btnGoToLogin);
		btnSendComent = (Button) findViewById(R.id.btnSendComent);
		btnSendComent.setOnClickListener(this);

		edtComent.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					String comentContent = edtComent.getText().toString();
					edtComent.setText("");
					StaticFunction
							.hideKeyboard(ConfigureData.activityGoogleMaps);
					try {
						int idRepost = ObjectMarkerClicked.getInt("id");
						postComent(idRepost, comentContent);

						return true;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NullPointerException e) {
						getWarningFromServer(null);
					}

				}
				return false;
			}

		});

		// setListioner
		btnWarning.setOnClickListener(this);
		btnPostWarning.setOnClickListener(this);
		btnShowInMap.setOnClickListener(this);
		imageWarning.setOnClickListener(this);

		// ==========================================
		// SLIDING UP PANEL
		// ==========================================

		// slding up init
		slidingUp = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		slidingUp.setShadowDrawable(getResources().getDrawable(
				R.drawable.above_shadow));
		slidingUp.setAnchorPoint(0.3f);
		// drag only tile warring
		layoutContent = (RelativeLayout) findViewById(R.id.layoutContent);
		layoutDrag = (LinearLayout) findViewById(R.id.layoutDragArea);
		slidingUp.setDragView(layoutDrag);
		slidingUp.setSlidingEnabled(false);
		slidingUp.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPanelExpanded(View panel) {

				if (!ConfigureData.isLogged) {
					edtComent.setVisibility(View.GONE);
					btnGoToLogin.setVisibility(View.VISIBLE);
					btnGoToLogin.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							slidingUp.collapsePane();
							goToLoginFromMapsActivity();

						}

						public void goToLoginFromMapsActivity() {
							ConfigureData.isCalledFromMaps = true;
							Fragment fragment = new LoginFragmemt();
							getSupportFragmentManager().beginTransaction()
									.replace(R.id.maps_content, fragment)
									.addToBackStack("Login").commit();
						}
					});

				} else {
					edtComent.setVisibility(View.VISIBLE);
					btnGoToLogin.setVisibility(View.GONE);
				}

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

			// set listener when marker is clicked
			googleMap.setOnInfoWindowClickListener(this);
			googleMap.setOnMarkerClickListener(this);
			googleMap
					.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

						@Override
						public boolean onMyLocationButtonClick() {
							mCurrentLocation = getGPSLocation(GoogleMapsActivity.this
									.getApplicationContext());
							drawCircle(mCurrentLocation,
									ConfigureData.radiusWarning * 1000);
							return false;
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

		// start timer to auto refresh
		timerRefreshNews = new Timer();
		startTimerRefreshNewsWarning();

		timerResfreshComent = new Timer();
		try {
			startTimerRefreshComent();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}

	/**
	 * timer to refresh newsfeed report
	 */
	protected void startTimerRefreshNewsWarning() {
		
		timerRefreshNews.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				// if post fragment is show, we don;t need to refresh data
				if (ConfigureData.isEnableShowChoosePostWarning) {
					mHandlerRefreshNewsWarning.obtainMessage(1).sendToTarget();
				}

			}
		}, 0, 30000);
	}

	public Handler mHandlerRefreshNewsWarning = new Handler() {
		public void handleMessage(Message msg) {
			getWarningFromServer(null);
		}
	};

	/**
	 * timer to refresh newsfeed report
	 */
	public void startTimerRefreshComent() {
		timerRefreshNews.schedule(new TimerTask() {
			public void run() {
				// if post fragment is show, we don;t need to refresh data
				mHandlerRefreshComent.obtainMessage(1).sendToTarget();

			}
		}, 0, 3000);
	}

	public Handler mHandlerRefreshComent = new Handler() {
		public void handleMessage(Message msg) {
			try {
				// get id warning is showing in sliding up content
				if (ObjectMarkerClicked != null) {
					int id = ObjectMarkerClicked.getInt("id");
					getComentByWarningId(id);
				}

			} catch (Exception e) {

			}

		}
	};

	public void goToLoginFromMapsActivity() {
		ConfigureData.isCalledFromMaps = true;
		Fragment fragment = new LoginFragmemt();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.maps_content, fragment).addToBackStack("Login")
				.commit();
	}

	public void getWarningFromServer(PostWarningFragment PostFragment) {
		if (PostFragment != null) {
			PostFragment.dismiss();
		}
		processDialog = new ProgressDialog(ConfigureData.activityGoogleMaps);
		processDialog.setMessage(ConfigureData.activityGoogleMaps
				.getString(R.string.loading));
		processDialog.setIndeterminate(false);
		processDialog.setCancelable(true);
		processDialog.show();
		// warningManager will be null when you return from postFragment
		warningsManager = new WarningsManager(ConfigureData.activityGoogleMaps);
		warningsManager.resetAllList();
		// googleMap
		if (googleMap == null) {
			googleMap = ConfigureData.googleMap;
		}

		ServiceWarningGET serviceWarningGet = new ServiceWarningGET();
		try {
			serviceWarningGet.getAllWarningNews(mCurrentLocation.longitude,
					mCurrentLocation.latitude, ConfigureData.radiusWarning);
		} catch (NullPointerException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					getString(R.string.gps_null), 0).show();
		}

		serviceWarningGet.addOnGetJsonListener(new OnGetJsonListener() {

			@Override
			public void onGetJsonFail(String response) {
				processDialog.dismiss();

			}

			@Override
			public void onGetJsonCompleted(String response) {

				try {
					JSONObject responseJson = new JSONObject(response);
					if (responseJson.getBoolean("status")) {
						JSONArray data = responseJson.getJSONArray("data");

						for (int i = 0; i < data.length(); i++) {
							JSONObject c = data.getJSONObject(i);
							String reportType = c.getString("reportType");
							if (reportType
									.equals(ConfigureData.activityGoogleMaps
											.getString(R.string.chot_cong_an))) {
								warningsManager.listChotCongAn.add(c);
							} else if (reportType
									.equals(ConfigureData.activityGoogleMaps
											.getString(R.string.giao_thong))) {
								warningsManager.listGiaoThong.add(c);

							} else if (reportType
									.equals(ConfigureData.activityGoogleMaps
											.getString(R.string.tai_nan))) {
								warningsManager.listTaiNan.add(c);
							} else if (reportType
									.equals(ConfigureData.activityGoogleMaps
											.getString(R.string.trang_thai))) {
								warningsManager.listTrangThai.add(c);
							}
						}

						fillNumberWarningIntoPopup();

						warningsManager.drawWarningsIntoMap(
								WarningsManager.onlyShowWarningsInMaps,
								googleMap);
						processDialog.dismiss();
					} else {
						processDialog.dismiss();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * config UI maps
	 */
	private void configMaps() {
		// showing / hiding your current location
		googleMap.setMyLocationEnabled(false);

		// set straffic enable
		googleMap.setTrafficEnabled(true);

		// Enable / Disable zooming controls
		googleMap.getUiSettings().setZoomControlsEnabled(false);

		// handle btnGetGps
		btnGetGPS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {

					mCurrentLocation = getGPSLocation(GoogleMapsActivity.this);
					drawCurrentLocation();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Enable / Disable Compass icon
		googleMap.getUiSettings().setCompassEnabled(true);

		// Enable / Disable gesture
		googleMap.getUiSettings().setAllGesturesEnabled(true);
		// draw icon car of user
		warningsManager.drawIconCarUser(googleMap);
		drawCurrentLocation();

	}

	public void drawCurrentLocation() {
		try {
			// draw a circle around your location with radius = 3km
			drawCircle(mCurrentLocation, ConfigureData.radiusWarning * 1000);

			// Moving Camera to a Location with animation
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(mCurrentLocation).zoom(15).build();
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
			ConfigureData.googleMap = googleMap;
			warningsManager = new WarningsManager(
					ConfigureData.activityGoogleMaps);

			// check if map is created successfully or not
			if (googleMap == null) {
				Log.d("GoogleMap", "initiizeMap error");
			}
		}

	}

	// ======================================
	// EVENT LISTENER
	// ======================================

	@Override
	public void onInfoWindowClick(Marker marker) {

		slidingUp.expandPane();

	}

	private void fillWarningIntoSlidingUp(JSONObject objectClicked) {
		commentAdapter.clear();

		// commentAdapter.notifyDataSetChanged();
		this.ObjectMarkerClicked = objectClicked;
		try {
			txtTypeWarning.setText(objectClicked.getString("reportType"));
			txtSubtype.setText(objectClicked.getString("reportSubType"));

			txtDetailDistance.setText(ConfigureData.activityGoogleMaps
					.getString(R.string.distance) + "1km");
			txtDetailAddress.setText(ConfigureData.activityGoogleMaps
					.getString(R.string.address)
					+ objectClicked.getString("address"));
			txtDetailNote.setText(ConfigureData.activityGoogleMaps
					.getString(R.string.coment)
					+ objectClicked.getString("note"));
			txtDetailTime
					.setText(ConfigureData.activityGoogleMaps
							.getString(R.string.time)
							+ objectClicked.getString("date"));

			imageUrlWaring = objectClicked.getString("imageUrl");
			if (imageUrlWaring != null) {
				ConfigureData.imageLoader.displayImage(imageUrlWaring,
						imageWarning, 250);
			} else {
				imageWarning.setImageResource(R.drawable.noimage);
			}

			double lng = objectClicked.getDouble("longitude");
			double lat = objectClicked.getDouble("latitude");
			LatLng latLng = new LatLng(lat, lng);
			goToLatLng(latLng);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		slidingUp.setSlidingEnabled(true);
		slidingUp.expandPane();
		int id = 0;
		try {
			id = objectClicked.getInt("id");
			getComentByWarningId(id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void postComent(final int idReport, String comentContent) {
		// get id of coment
		try {
			JSONObject objectComent = new JSONObject();
			try {
				objectComent.put("userId",
						ConfigureData.userAccount.getInt("id"));
				objectComent.put("reportId", idReport);
				objectComent.put("comment", comentContent);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			ServicePostComent servicePostComent = new ServicePostComent();
			servicePostComent.postComent(objectComent);
			servicePostComent.addOnPostJsonListener(new OnPostJsonListener() {

				@Override
				public void onPostJsonFail(String response) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPostJsonCompleted(String response) {
					getComentByWarningId(idReport);

				}
			});

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
	}

	public void getComentByWarningId(int id) {
		// show coment

		ServiceWarningGET serviceGetComent = new ServiceWarningGET();
		serviceGetComent.getReportComent(id);
		serviceGetComent.addOnGetJsonListener(new OnGetJsonListener() {

			@Override
			public void onGetJsonFail(String response) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetJsonCompleted(String response) {

				try {
					JSONObject responseJson = new JSONObject(response);
					if (responseJson.getBoolean("status")) {
						JSONArray comentArray = responseJson
								.getJSONArray("data");
						if (commentAdapter != null
								&& comentArray.length() == commentAdapter
										.getCount()) {

						} else {
							for (int i = commentAdapter.getCount(); i < comentArray
									.length(); i++) {
								commentAdapter.add(comentArray.getJSONObject(i));
							}
							// commentAdapter.notifyDataSetChanged();
							listComent.setSelection(commentAdapter.getCount() - 1);
						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

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

			if (ConfigureData.isEnableShowChoosePostWarning) {
				pwindowChoosePostWarning = showPopupPostWarningChooseWindow(this);
			}

			break;
		case R.id.btnShowInMap:

			try {
				double lng = ObjectMarkerClicked.getDouble("longitude");
				double lat = ObjectMarkerClicked.getDouble("latitude");
				LatLng latLng = new LatLng(lat, lng);
				goToLatLng(latLng);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			break;
		case R.id.imageWarning:
			ConfigureData.FullScreenImage = new String[] { imageUrlWaring };
			startActivity(new Intent(this, FullScreenImageActivity.class));
			break;
		case R.id.btnSendComent:
			// Perform action on key press
			String comentContent = edtComent.getText().toString();
			edtComent.setText("");
			StaticFunction.hideKeyboard(ConfigureData.activityGoogleMaps);
			try {
				int idRepost = ObjectMarkerClicked.getInt("id");
				postComent(idRepost, comentContent);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				getWarningFromServer(null);
			}
			break;
		default:
			break;
		}

	}

	public void goToLatLng(LatLng latLng) {
		// Moving Camera to a Location with animation
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latLng).zoom(18).build();
		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
		slidingUp.collapsePane();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		try {
			int pos = warningsManager.markerList.get(marker.getId());
			ObjectMarkerClicked = warningsManager.getWarning(
					WarningsManager.onlyShowWarningsInMaps, pos);
			fillWarningIntoSlidingUp(ObjectMarkerClicked);
		} catch (NullPointerException e) {
			e.printStackTrace();
			if (!marker.equals(WarningsManager.markerUser)) {
				getWarningFromServer(null);

			}
		}

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

			btnRefresh = (Button) layout.findViewById(R.id.btnRefreshNews);
			btnRefresh.setOnClickListener(refresh_button_click_listener);
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

	private OnClickListener refresh_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			// refresh
			getWarningFromServer(null);
		}
	};

	private OnClickListener loadDataWarning_clickListener = new OnClickListener() {
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.layout_btnAllWarning:
				WarningsManager.onlyShowWarningsInMaps = WarningsManager.ALL_WARNING;
				NewsFeedWarningListAdapter adapterAll = new NewsFeedWarningListAdapter(
						ConfigureData.activityGoogleMaps,
						warningsManager.getAllWarning());
				pwindowShowListWarning = showPopupListViewWarning(
						ConfigureData.activityGoogleMaps, adapterAll,
						WarningsManager.ALL_WARNING);
				pwindowWarning.dismiss();

				break;
			case R.id.layout_btnChotCongAn:
				WarningsManager.onlyShowWarningsInMaps = WarningsManager.POLICE_WARNING;
				NewsFeedWarningListAdapter adapterPolice = new NewsFeedWarningListAdapter(
						ConfigureData.activityGoogleMaps,
						warningsManager.listChotCongAn);
				pwindowShowListWarning = showPopupListViewWarning(
						ConfigureData.activityGoogleMaps, adapterPolice,
						WarningsManager.POLICE_WARNING);
				pwindowWarning.dismiss();

				break;
			case R.id.layout_btnGiaoThong:
				WarningsManager.onlyShowWarningsInMaps = WarningsManager.TRAFFIC_WARNING;

				NewsFeedWarningListAdapter adapterTraffic = new NewsFeedWarningListAdapter(
						ConfigureData.activityGoogleMaps,
						warningsManager.listGiaoThong);

				pwindowShowListWarning = showPopupListViewWarning(
						ConfigureData.activityGoogleMaps, adapterTraffic,
						WarningsManager.TRAFFIC_WARNING);
				pwindowWarning.dismiss();
				break;
			case R.id.layout_btnTaiNan:
				WarningsManager.onlyShowWarningsInMaps = WarningsManager.ACCIDENT_WARNING;

				NewsFeedWarningListAdapter adapterAccident = new NewsFeedWarningListAdapter(
						ConfigureData.activityGoogleMaps,
						warningsManager.listTaiNan);
				pwindowShowListWarning = showPopupListViewWarning(
						ConfigureData.activityGoogleMaps, adapterAccident,
						WarningsManager.ACCIDENT_WARNING);
				pwindowWarning.dismiss();
				break;
			case R.id.layout_btnTrangThai:
				WarningsManager.onlyShowWarningsInMaps = WarningsManager.STATUS_WARNING;

				NewsFeedWarningListAdapter adapterStatus = new NewsFeedWarningListAdapter(
						ConfigureData.activityGoogleMaps,
						warningsManager.listTrangThai);

				pwindowShowListWarning = showPopupListViewWarning(
						ConfigureData.activityGoogleMaps, adapterStatus,
						WarningsManager.STATUS_WARNING);
				pwindowWarning.dismiss();
				break;

			default:
				break;
			}

			warningsManager.drawWarningsIntoMap(
					warningsManager.onlyShowWarningsInMaps, googleMap);
		}

	};

	// --------------------------------------------
	// WORKING WITH DATA
	// --------------------------------------------
	public void fillNumberWarningIntoPopup() {

		try {
			txtNumberAllWarning.setText(warningsManager.getNumberAllWarning()
					+ "");
			txtNumberPolice.setText(warningsManager.listChotCongAn.size() + "");
			txtNumberAccident.setText(warningsManager.listTaiNan.size() + "");
			txtNumberStatus.setText(warningsManager.listTrangThai.size() + "");
			txtNumberTraffic.setText(warningsManager.listGiaoThong.size() + "");
		} catch (NullPointerException e) {
			Log.d("Error", warningsManager.getNumberAllWarning() + "");
		}
	}

	// ===========================================
	// POPUP WINDOW VIEW LIST WARNING
	// ===========================================

	private ListPopupWindow showPopupListViewWarning(Context mcon,
			NewsFeedWarningListAdapter adapter, final int key) {

		final ListPopupWindow pwindo = new ListPopupWindow(mcon);
		pwindo.setAnimationStyle(R.style.AnimationPopup);
		pwindo.setAdapter(adapter);

		pwindo.setAnchorView(archorListPopup);

		pwindo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				JSONObject itemSelected = new JSONObject();
				switch (key) {
				case WarningsManager.ALL_WARNING:
					itemSelected = warningsManager.getAllWarning().get(pos);
					break;
				case WarningsManager.POLICE_WARNING:
					itemSelected = warningsManager.listChotCongAn.get(pos);
					break;
				case WarningsManager.TRAFFIC_WARNING:
					itemSelected = warningsManager.listGiaoThong.get(pos);
					break;
				case WarningsManager.ACCIDENT_WARNING:
					itemSelected = warningsManager.listTaiNan.get(pos);
					break;
				case WarningsManager.STATUS_WARNING:
					itemSelected = warningsManager.listTrangThai.get(pos);
					break;
				default:
					break;
				}
				fillWarningIntoSlidingUp(itemSelected);
				pwindo.dismiss();
			}
		});
		pwindo.show();
		return pwindo;
	}

	// ===========================================
	// POPUP WINDOW POST WARNING
	// ===========================================

	private PopupWindow showPopupPostWarningChooseWindow(Context mcon) {
		// draw all warning in maps
		WarningsManager.onlyShowWarningsInMaps = WarningsManager.ALL_WARNING;
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

			// Toast.makeText(getApplicationContext(), v.getId() + "",
			// 0).show();
			Fragment fragment;

			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			fragment = new PostWarningFragment();
			ft.replace(R.id.maps_content, fragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			// ft.addToBackStack("MapsToPost");

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
			/*
			 * Toast.makeText(this, mCurrentLatLng.latitude + " , " +
			 * mCurrentLatLng.longitude, 0).show();
			 */
			// get address from location
			// new GetAddressTask(Config.activity).execute(location);

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings

			gps.showSettingsAlert();

		}
		return mCurrentLatLng;
	}

	// ======================================
	// OTHERS
	// ======================================
	@Override
	public void onStart() {
		super.onStart();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// The rest of your onStart() code.
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onResume() {

		initilizeMap();
		/*
		 * try { timerRefreshNews = new Timer(); timerResfreshComent = new
		 * Timer(); startTimerRefreshNewsWarning(); startTimerRefreshComent(); }
		 * catch (Exception e) { e.printStackTrace(); }
		 */
		super.onResume();
	}

	@Override
	protected void onPause() {
		/*
		 * try { timerRefreshNews.cancel(); timerResfreshComent.cancel(); }
		 * catch (Exception e) { e.printStackTrace(); }
		 */
		super.onPause();
	}

	@Override
	public void onStop() {
		try {
			timerRefreshNews.cancel();
			timerResfreshComent.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (pwindowShowListWarning != null
				&& pwindowShowListWarning.isShowing()) {

			pwindowShowListWarning.dismiss();
			return;

		}
		if (slidingUp.isAnchored() || slidingUp.isExpanded()) {
			slidingUp.expandPane(1f);
			return;
		}
		if (pwindowWarning != null && pwindowWarning.isShowing()) {
			pwindowWarning.dismiss();
			return;
		}
		if (pwindowChoosePostWarning != null
				&& pwindowChoosePostWarning.isShowing()) {
			pwindowChoosePostWarning.dismiss();
			return;
		}

		if (ConfigureData.flagTypePostWarningFragment != 0) {
			return;
		}

		super.onBackPressed();
	}

}
