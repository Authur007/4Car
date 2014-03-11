package ta.car4rent.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.objects.SpinnerDataItem;
import ta.car4rent.objects.SpinnerDataList;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceGetAdvanceSearchConditions;
import ta.car4rent.webservices.ServiceGetDistricts;
import ta.car4rent.webservices.ServicePostCarRequest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class SearchCarFragmemt extends Fragment implements OnClickListener,
		OnItemSelectedListener, OnDateSetListener, OnTimeSetListener,
		OnCheckedChangeListener {

	// =========================[SEARCH REQUEST]============================
	public static JSONObject searchRequestJSONObject = new JSONObject();

	// =========================[SCREEN MODE]==============================
	public final static int SCREEN_MODE_ADVANCE_SEARCH = 0;
	public final static int SCREEN_MODE_POST_CAR_REQUEST = 1;
	private int CURRENT_SCREEN_MODE = SCREEN_MODE_ADVANCE_SEARCH;
	private static boolean isLayoutMoreVisible = false;

	// =========================[PICKER_MODE]==============================
	public final static int PICKER_TYPE_START_DATE = 0;
	public final static int PICKER_TYPE_END_DATE = 1;
	public final static int PICKER_TYPE_EXPIRATION_DATE = 2;
	public final static int PICKER_TYPE_START_TIME = 3;
	public final static int PICKER_TYPE_END_TIME = 4;

	// ==================[STATIC VARIABLE CAN BE REUSE]====================
	public static SpinnerDataList citiesSpinnerDataList;
	public static SpinnerDataList citiesFromSpinnerDataList;
	public static SpinnerDataList citiesToSpinnerDataList;
	public static SpinnerDataList districtsSpinnerDataList;
	public static SpinnerDataList districtsFromSpinnerDataList;
	public static SpinnerDataList districtsToSpinnerDataList;
	public static SpinnerDataList pricesFromSpinnerDataList;
	public static SpinnerDataList pricesToSpinnerDataList;
	public static SpinnerDataList makesSpinnerDataList;
	public static SpinnerDataList modelsSpinnerDataList;
	public static SpinnerDataList seatNumberSpinnerDataList; // styleId
	public static SpinnerDataList transmissionsSpinnerDataList;
	public static SpinnerDataList driversSpinnerDataList;
	public static SpinnerDataList carOwnersSpinnerDataList;
	public static String strNoteInfo;

	// ====================[UI COMPONENT VARIABLE]========================
	boolean mNeedToReset;
	private ScrollView scrollView;
	private TextView tvScreenName;
	private ProgressBar progressBar;
	private CheckBox checkBoxDriver;
	private DatePickerFragment mDatePickerFragment;
	private TimePickerFragment mTimePickerFragment;

	private Spinner spinnerCity;
	private Spinner spinnerCityFrom;
	private Spinner spinnerCityTo;
	private Spinner spinnerDistricts;
	private Spinner spinnerDistrictsFrom;
	private Spinner spinnerDistrictsTo;
	private Spinner spinnerPriceFrom;
	private Spinner spinnerPriceTo;

	private Spinner spinnerMakeCar;
	private Spinner spinnerModelCar;
	private Spinner spinnerTransmission;
	private Spinner spinnerSeatNumber;
	private Spinner spinnerCarOwner;

	private Button btnStartDate;
	private Button btnEndDate;
	private Button btnStartTime;
	private Button btnEndTime;
	private Button btnCancelPostCarRequest;
	private Button btnPostCarRequest;
	private Button btnSearchCar;
	private Button btnExpirationDate;
	private TextView txtNothing1;
	private TextView txtNothing2;

	private LinearLayout layoutPlaceTo;
	private LinearLayout layoutMore;
	private LinearLayout layoutExpirationDate;
	private LinearLayout btnLayoutMore;

	private EditText edtNoteInfo;

	private static String strStartDate = "30/12/2013";
	private static String strEndDate = "31/12/2013";
	private static String strExpirationDate = "29/12/2013";
	private static String strStartTime = "08:00";
	private static String strEndTime = "20:00";

	private int flagChoosePickerType = 0;

	public void setCurrentScreenMode(int mode) {
		CURRENT_SCREEN_MODE = mode;
	}

	public SearchCarFragmemt() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainActivity.Instance.showActionFilterSpinner(false);

		View rootView = null;
		if (!ConfigureData.isOnline()) {
			// Show error Internet connectivity and button retry
			rootView = inflater.inflate(
					R.layout.fragment_no_internet_connection, container, false);
			Button btnRetry = (Button) rootView.findViewById(R.id.btnRetry);
			btnRetry.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Create a new fragment and specify the planet to show
					// based on position
					SearchCarFragmemt fragment = new SearchCarFragmemt();
					// Insert the fragment by replacing any existing
					// fragment
					FragmentManager fragmentManager = getActivity()
							.getSupportFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.content_frame, fragment).commit();
					((MainActivity) getActivity())
							.setActionBarTitle(ConfigureData.activityMain
									.getString(R.string.label_search_car));
				}
			});
			return rootView;
		}

		ConfigureData.currentScreen = 1;
		rootView = inflater.inflate(R.layout.fragment_search_car, container,
				false);
		// Show waiting progress dialog
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (!ConfigureData.isOnline()) {
			return;
		}

		// Init components and setOnClickListener
		findViewById();

		// Check search condition exist?
		if (citiesSpinnerDataList == null) {
			// STARTUP MODE --> need to reset value
			mNeedToReset = true;
			getDataFromServer();
		} else {
			// RESUME MODE --> no need to reset value
			mNeedToReset = false;

			fillDataToComponent();

			switch (CURRENT_SCREEN_MODE) {
			case SCREEN_MODE_ADVANCE_SEARCH:
				chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH);
				break;

			case SCREEN_MODE_POST_CAR_REQUEST:
				chooseScreenMode(SCREEN_MODE_POST_CAR_REQUEST);
				edtNoteInfo.setText(strNoteInfo);
				break;

			}
			/**
			 * Set visible or gone for ui mode
			 */
			if (driversSpinnerDataList.getSelectedIndex() == 0) {
				checkBoxDriver.setChecked(false);
				switchCarDriverMode(false);
				if (districtsSpinnerDataList != null) {
					spinnerDistricts.setVisibility(View.VISIBLE);
				}
			} else {
				checkBoxDriver.setChecked(true);
				switchCarDriverMode(true);
				if (districtsFromSpinnerDataList != null) {
					spinnerDistrictsFrom.setVisibility(View.VISIBLE);
				}
				if (districtsToSpinnerDataList != null) {
					spinnerDistrictsTo.setVisibility(View.VISIBLE);
				}
			}

			if (isLayoutMoreVisible) {
				layoutMore.setVisibility(View.VISIBLE);
			}

			// After resume completee we have need reset when checked change
			mNeedToReset = true;
		}

	}

	/**
	 * Get search condition from server via GetAdvanceSearchConditions service
	 * 
	 */
	private void getDataFromServer() {
		if (citiesSpinnerDataList != null) {
			return;
		}

		ServiceGetAdvanceSearchConditions serviceGetAdvanceSearchConditions = new ServiceGetAdvanceSearchConditions();
		serviceGetAdvanceSearchConditions
				.addOnGetJsonListener(new OnGetJsonListener() {

					@Override
					public void onGetJsonFail(String response) {
						/**
						 * Get Advance search completed
						 */
						progressBar.setVisibility(View.GONE);
						showDialog(
								ConfigureData.activityMain
										.getString(R.string.err_connection),
								ConfigureData.activityMain
										.getString(R.string.please_check_your_conection),
								R.drawable.ic_error);
						showFregmentNoInternetConnection();
					}

					@Override
					public void onGetJsonCompleted(String response) {
						try {
							JSONObject responseJson = new JSONObject(response);
							if (responseJson.getBoolean("status")) {
								JSONObject data = responseJson
										.getJSONObject("data");

								// Get cites list
								JSONArray citiesJsonArray = data
										.getJSONArray("cities");
								citiesSpinnerDataList = new SpinnerDataList(
										citiesJsonArray);
								// Get citiesFrom list
								JSONArray citiesFromJsonArray = data
										.getJSONArray("citiesFrom");
								citiesFromSpinnerDataList = new SpinnerDataList(
										citiesFromJsonArray);
								// Get citiesTo list
								JSONArray citiesToJsonArray = data
										.getJSONArray("citiesTo");
								citiesToSpinnerDataList = new SpinnerDataList(
										citiesToJsonArray);

								// Get makes list
								JSONArray makesJsonArray = data
										.getJSONArray("makes");
								makesSpinnerDataList = new SpinnerDataList(
										makesJsonArray);

								// Get models list
								JSONArray modelsJsonArray = data
										.getJSONArray("models");
								modelsSpinnerDataList = new SpinnerDataList(
										modelsJsonArray);

								// Get seat number list
								JSONArray stylesJsonArray = data
										.getJSONArray("styles");
								seatNumberSpinnerDataList = new SpinnerDataList(
										stylesJsonArray);

								// Get carOwners list
								JSONArray carOwnersJsonArray = data
										.getJSONArray("carOwners");
								carOwnersSpinnerDataList = new SpinnerDataList(
										carOwnersJsonArray);

								// Get transmissions list
								JSONArray transmissionsJsonArray = data
										.getJSONArray("transmissions");
								transmissionsSpinnerDataList = new SpinnerDataList(
										transmissionsJsonArray);

								// Get drivers list
								JSONArray driversJsonArray = data
										.getJSONArray("drivers");
								driversSpinnerDataList = new SpinnerDataList(
										driversJsonArray);

								// Get fromPrices list
								JSONArray fromPricesJsonArray = data
										.getJSONArray("fromPrices");
								pricesFromSpinnerDataList = new SpinnerDataList(
										fromPricesJsonArray);

								// Get toPrices list
								JSONArray toPricesJsonArray = data
										.getJSONArray("toPrices");
								pricesToSpinnerDataList = new SpinnerDataList(
										toPricesJsonArray);

								/**
								 * Get Advance search completed
								 */
								progressBar.setVisibility(View.GONE);
							}

							// When get search condition complete
							fillDataToComponent();
							// Default is search no car Driver
							chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH);
							checkBoxDriver.setChecked(true);
							checkBoxDriver.setChecked(false);

							return;
						} catch (JSONException e) {
							e.printStackTrace();
						}

						showDialog(
								ConfigureData.activityMain
										.getString(R.string.err_connection),
								ConfigureData.activityMain
										.getString(R.string.please_check_your_conection),
								R.drawable.ic_error);
						progressBar.setVisibility(View.GONE);
						showFregmentNoInternetConnection();

					}
				});

		serviceGetAdvanceSearchConditions.getAdvanceSearchConditions();

	}

	private void showFregmentNoInternetConnection() {
		NoInternetConnectionFragmemt fragment = new NoInternetConnectionFragmemt();

		// Insert the fragment by replacing any existing
		// fragment
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

	}

	/**
	 * Find and construct for UI components
	 */
	public void findViewById() {

		scrollView = (ScrollView) getView().findViewById(R.id.scrollView);

		// Screen title
		tvScreenName = (TextView) getView().findViewById(R.id.tvScreenName);

		// Show/Hide layout
		layoutPlaceTo = (LinearLayout) getView().findViewById(
				R.id.layoutPlaceTo);
		layoutMore = (LinearLayout) getView().findViewById(R.id.layoutMore);
		layoutExpirationDate = (LinearLayout) getView().findViewById(
				R.id.layout_expiration_date);

		// Spinner
		spinnerCity = (Spinner) getView().findViewById(R.id.spinnerCity);
		spinnerDistricts = (Spinner) getView().findViewById(
				R.id.spinnerDistricts);
		spinnerCityFrom = (Spinner) getView()
				.findViewById(R.id.spinnerCityFrom);
		spinnerDistrictsFrom = (Spinner) getView().findViewById(
				R.id.spinnerFromDistricts);
		spinnerCityTo = (Spinner) getView().findViewById(R.id.spinnerCityTo);
		spinnerDistrictsTo = (Spinner) getView().findViewById(
				R.id.spinnerDistricstTo);

		spinnerPriceFrom = (Spinner) getView().findViewById(
				R.id.spinnerPriceFrom);
		spinnerPriceTo = (Spinner) getView().findViewById(R.id.spinnerPriceTo);

		spinnerMakeCar = (Spinner) getView().findViewById(R.id.spinnerMakeCar);
		spinnerModelCar = (Spinner) getView()
				.findViewById(R.id.spinnerModelCar);
		spinnerTransmission = (Spinner) getView().findViewById(
				R.id.spinnerTransmission);
		spinnerSeatNumber = (Spinner) getView().findViewById(
				R.id.spinnerSeatNumber);
		spinnerCarOwner = (Spinner) getView()
				.findViewById(R.id.spinnerCarOwner);

		checkBoxDriver = (CheckBox) getView().findViewById(R.id.checkBoxDriver);
		btnStartDate = (Button) getView().findViewById(R.id.btnStartDate);
		btnStartTime = (Button) getView().findViewById(R.id.btnStartTime);
		btnEndTime = (Button) getView().findViewById(R.id.btnEndTime);
		btnEndDate = (Button) getView().findViewById(R.id.btnEndDate);
		btnExpirationDate = (Button) getView().findViewById(
				R.id.btnExpirationDate);
		btnLayoutMore = (LinearLayout) getView().findViewById(
				R.id.btnLayoutMore);
		edtNoteInfo = (EditText) getView().findViewById(R.id.edtNoteInfo);
		edtNoteInfo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				strNoteInfo = edtNoteInfo.getText().toString();
			}
		});

		btnSearchCar = (Button) getView().findViewById(R.id.btnSearch);
		btnPostCarRequest = (Button) getView().findViewById(
				R.id.btnPostCarRequest);
		btnCancelPostCarRequest = (Button) getView().findViewById(
				R.id.btnCancelPostCarRequest);
		txtNothing1 = (TextView) getView().findViewById(R.id.txtNothing1);
		txtNothing2 = (TextView) getView().findViewById(R.id.txtNothing2);
	}

	/**
	 * Set visible or gone for ui mode
	 */
	private void chooseScreenMode(int mode) {

		// Check logined when switch to Post car request screen
		if (mode == SCREEN_MODE_POST_CAR_REQUEST) {
			if (ConfigureData.isLogged == false) {
				Toast.makeText(
						getActivity(),
						ConfigureData.activityMain
								.getString(R.string.err_you_need_login_first),
						Toast.LENGTH_SHORT).show();

				ConfigureData.isCalledFromSearchCar = true;
				Fragment fragment = new LoginFragmemt();
				// Insert the fragment by replacing any existing
				// fragment
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment).commit();

				return;
			}
		}

		// Everything is OK we will switch screen mode
		CURRENT_SCREEN_MODE = mode;

		switch (mode) {
		case SCREEN_MODE_ADVANCE_SEARCH:
			btnCancelPostCarRequest.setVisibility(View.GONE);
			txtNothing1.setVisibility(View.GONE);
			btnPostCarRequest.setVisibility(View.VISIBLE);
			txtNothing2.setVisibility(View.VISIBLE);
			btnSearchCar.setVisibility(View.VISIBLE);
			layoutExpirationDate.setVisibility(View.GONE);
			edtNoteInfo.setVisibility(View.GONE);

			try {
				// Change label in UI
				((MainActivity) getActivity())
						.setActionBarTitle(ConfigureData.activityMain
								.getString(R.string.label_search_car));
				tvScreenName.setText(ConfigureData.activityMain
						.getString(R.string.search_car_screen_name));
			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			}

			break;

		case SCREEN_MODE_POST_CAR_REQUEST:

			btnCancelPostCarRequest.setVisibility(View.VISIBLE);
			txtNothing1.setVisibility(View.VISIBLE);
			btnPostCarRequest.setVisibility(View.VISIBLE);
			txtNothing2.setVisibility(View.GONE);
			btnSearchCar.setVisibility(View.GONE);
			layoutExpirationDate.setVisibility(View.VISIBLE);
			edtNoteInfo.setVisibility(View.VISIBLE);

			// Change label in UI
			((MainActivity) getActivity())
					.setActionBarTitle(ConfigureData.activityMain
							.getString(R.string.label_post_car_request));
			tvScreenName.setText(ConfigureData.activityMain
					.getString(R.string.post_car_request_screen_name));

			// Change the default start date in Post car request
			if (compareStringDate(strStartDate, addStringDateFormated(getCurrenttDateFormated(), 2)) == -1) {
				strStartDate = addStringDateFormated(getCurrenttDateFormated(), 2);
				btnStartDate.setText(strStartDate);
				
				strExpirationDate = strStartDate;
				btnExpirationDate.setText(strExpirationDate);

				strEndDate = addStringDateFormated(getCurrenttDateFormated(), 3);
				btnEndDate.setText(strEndDate);
			}

			break;
		}
	}

	/**
	 * Set event listener for UI components
	 */
	private void setUiComponentEventListener() {

		// Set onClick Listener
		btnSearchCar.setOnClickListener(this);
		btnPostCarRequest.setOnClickListener(this);
		btnCancelPostCarRequest.setOnClickListener(this);

		btnStartDate.setOnClickListener(this);
		btnEndDate.setOnClickListener(this);
		btnExpirationDate.setOnClickListener(this);
		btnStartTime.setOnClickListener(this);
		btnEndTime.setOnClickListener(this);

		// Spinner item selected listener
		spinnerMakeCar.setOnItemSelectedListener(this);
		spinnerCity.setOnItemSelectedListener(this);
		spinnerDistricts.setOnItemSelectedListener(this);
		spinnerCityFrom.setOnItemSelectedListener(this);
		spinnerDistrictsFrom.setOnItemSelectedListener(this);
		spinnerPriceFrom.setOnItemSelectedListener(this);
		spinnerTransmission.setOnItemSelectedListener(this);
		spinnerSeatNumber.setOnItemSelectedListener(this);
		spinnerCarOwner.setOnItemSelectedListener(this);
		spinnerCityTo.setOnItemSelectedListener(this);
		spinnerDistrictsTo.setOnItemSelectedListener(this);
		spinnerPriceTo.setOnItemSelectedListener(this);
		spinnerModelCar.setOnItemSelectedListener(this);

		// Check box Driver
		checkBoxDriver.setOnCheckedChangeListener(this);

		// Layout show more
		btnLayoutMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(getActivity(),
						R.animator.zoom_in));
				if (layoutMore.isShown()) {
					layoutMore.setVisibility(View.GONE);
					isLayoutMoreVisible = false;
				} else {
					layoutMore.setVisibility(View.VISIBLE);
					scrollView.fullScroll(View.FOCUS_DOWN);
					isLayoutMoreVisible = true;
				}

				// reset when toggle
				spinnerMakeCar.setSelection(0);
				makesSpinnerDataList.setSelectedItem(0);
				spinnerModelCar.setSelection(0);
				modelsSpinnerDataList.setSelectedItem(0);
				spinnerTransmission.setSelection(0);
				transmissionsSpinnerDataList.setSelectedItem(0);
				spinnerSeatNumber.setSelection(0);
				seatNumberSpinnerDataList.setSelectedItem(0);
				spinnerCarOwner.setSelection(0);
				carOwnersSpinnerDataList.setSelectedItem(0);
			}
		});

	}

	private void fillDataToComponent() {

		// Fill default value
		if (mNeedToReset) {
			strStartDate = getCurrenttDateFormated();
			strEndDate = addStringDateFormated(strStartDate, 1);
			strExpirationDate = strStartDate;
			strStartTime = "08:00";
			strEndTime = "20:00";
		}

		btnStartDate.setText(strStartDate);
		btnEndDate.setText(strEndDate);
		btnExpirationDate.setText(strExpirationDate);
		btnStartTime.setText(strStartTime);
		btnEndTime.setText(strEndTime);

		progressBar.setVisibility(View.GONE);

		// cities
		ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				citiesSpinnerDataList.getArraySpinnerText());
		citiesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCity.setAdapter(citiesAdapter);
		spinnerCity.setSelection(citiesSpinnerDataList.getSelectedIndex(),
				false);

		// districts
		if (districtsSpinnerDataList != null) {
			ArrayAdapter<String> districtsAdapter = new ArrayAdapter<String>(
					ConfigureData.activityMain,
					android.R.layout.simple_spinner_item,
					districtsSpinnerDataList.getArraySpinnerText());
			districtsAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerDistricts.setVisibility(View.VISIBLE);
			spinnerDistricts.setAdapter(districtsAdapter);
			spinnerDistricts.setSelection(districtsSpinnerDataList
					.getSelectedIndex());
		}

		// citiesFrom
		ArrayAdapter<String> citiesFromAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				citiesFromSpinnerDataList.getArraySpinnerText());
		citiesFromAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCityFrom.setAdapter(citiesFromAdapter);
		spinnerCityFrom.setSelection(
				citiesFromSpinnerDataList.getSelectedIndex(), false);

		// districtsFrom
		if (districtsFromSpinnerDataList != null) {
			ArrayAdapter<String> districtsFromAdapter = new ArrayAdapter<String>(
					ConfigureData.activityMain,
					android.R.layout.simple_spinner_item,
					districtsFromSpinnerDataList.getArraySpinnerText());
			districtsFromAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerDistrictsFrom.setVisibility(View.VISIBLE);
			spinnerDistrictsFrom.setAdapter(districtsFromAdapter);
			spinnerDistrictsFrom.setSelection(districtsFromSpinnerDataList
					.getSelectedIndex());
		}

		// citiesTo
		ArrayAdapter<String> citiesToAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				citiesToSpinnerDataList.getArraySpinnerText());
		citiesToAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCityTo.setAdapter(citiesToAdapter);
		spinnerCityTo.setSelection(citiesToSpinnerDataList.getSelectedIndex(),
				false);

		// districtsTo
		if (districtsToSpinnerDataList != null) {
			ArrayAdapter<String> districtsToAdapter = new ArrayAdapter<String>(
					ConfigureData.activityMain,
					android.R.layout.simple_spinner_item,
					districtsToSpinnerDataList.getArraySpinnerText());
			districtsToAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerDistrictsTo.setVisibility(View.VISIBLE);
			spinnerDistrictsTo.setAdapter(districtsToAdapter);
			spinnerDistrictsTo.setSelection(districtsToSpinnerDataList
					.getSelectedIndex());
		}

		// price from
		ArrayAdapter<String> priceFromAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				pricesFromSpinnerDataList.getArraySpinnerText());
		priceFromAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPriceFrom.setAdapter(priceFromAdapter);
		spinnerPriceFrom.setSelection(pricesFromSpinnerDataList
				.getSelectedIndex());

		// price To
		ArrayAdapter<String> priceToAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				pricesToSpinnerDataList.getArraySpinnerText());
		priceToAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPriceTo.setAdapter(priceToAdapter);
		spinnerPriceTo.setSelection(pricesToSpinnerDataList.getSelectedIndex());

		// seat number
		ArrayAdapter<String> seatNumberAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				seatNumberSpinnerDataList.getArraySpinnerText());
		seatNumberAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSeatNumber.setAdapter(seatNumberAdapter);
		spinnerSeatNumber.setSelection(seatNumberSpinnerDataList
				.getSelectedIndex());

		// makes
		ArrayAdapter<String> makesAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				makesSpinnerDataList.getArraySpinnerText());
		makesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMakeCar.setAdapter(makesAdapter);
		spinnerMakeCar.setSelection(makesSpinnerDataList.getSelectedIndex());

		// model
		ArrayAdapter<String> modelsAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				modelsSpinnerDataList.getArraySpinnerText());
		modelsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerModelCar.setAdapter(modelsAdapter);
		spinnerModelCar.setSelection(modelsSpinnerDataList.getSelectedIndex());

		// transmission
		ArrayAdapter<String> transmissionAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				transmissionsSpinnerDataList.getArraySpinnerText());
		transmissionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTransmission.setAdapter(transmissionAdapter);
		spinnerTransmission.setSelection(transmissionsSpinnerDataList
				.getSelectedIndex());

		// car owner
		ArrayAdapter<String> carOwnerAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				carOwnersSpinnerDataList.getArraySpinnerText());
		carOwnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCarOwner.setAdapter(carOwnerAdapter);
		spinnerCarOwner.setSelection(carOwnersSpinnerDataList
				.getSelectedIndex());

		setUiComponentEventListener();
	}

	/**
	 * Get current date as String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrenttDateFormated() {
		final Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(c.getTime());
	}

	/**
	 * Add day of year
	 * 
	 * @param strDate
	 *            : dd/MM/yyyy
	 * @param addDay
	 * @return
	 */
	public static String addStringDateFormated(String strDate, int addDay) {
		Log.i("INPUT DATE: ", strDate);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatter = sdf;
		Calendar calendar = Calendar.getInstance();
		try {
			Date date = formatter.parse(strDate);
			calendar.setTime(date);
			Log.i("BEFORE ADD: " + addDay, sdf.format(calendar.getTime()));
			calendar.add(Calendar.DAY_OF_YEAR, addDay);
			Log.i("AFTER ADD: " + addDay, sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sdf.format(calendar.getTime());
	}

	/**
	 * Format String date to dd/MM/yyyy
	 * 
	 * @param strDate
	 * @return
	 */
	public static String formatStringDate(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatter = sdf;
		Calendar calendar = Calendar.getInstance();
		try {
			Date date = formatter.parse(strDate);
			calendar.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sdf.format(calendar.getTime());
	}

	private boolean checkSearchValueVaild() {

		try {
			// If has car driver: cityTo & sityFrom must be exist
			if (checkBoxDriver.isChecked()) {
				if (citiesFromSpinnerDataList.getSelectedIndex() == 0
						|| citiesToSpinnerDataList.getSelectedIndex() == 0) {

					showDialog(
							ConfigureData.activityMain.getString(R.string.err),
							ConfigureData.activityMain
									.getString(R.string.err_lackof_city),
							R.drawable.ic_error);
					return false;
				}
			}
		} catch (NullPointerException e) {
			// get error here
			// driversSpinnerDataList.getSelectedItem().getValue()
			e.printStackTrace();
		}
		return true;

	}

	/**
	 * Call when click button
	 */
	@Override
	public void onClick(View view) {

		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		switch (view.getId()) {
		case R.id.btnSearch:
			progressBar.setVisibility(View.VISIBLE);
			if (checkSearchValueVaild()) {
				// Create search request
				exportJsonObjectSearch();

				// Show search result screen
				Fragment newContent = new SeachCarListResultFragmemt();
				SeachCarListResultFragmemt.mCarArrayList = null;
				SeachCarListResultFragmemt.mEndPageIndex = 0;
				FragmentManager fm = getActivity().getSupportFragmentManager();
				final FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.content_frame, newContent);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// add to back stack
				ft.addToBackStack("Fragment Search");
				// just for test-> need to commit
				ft.commit();
			}
			progressBar.setVisibility(View.GONE);
			break;
		case R.id.btnPostCarRequest:
			if (CURRENT_SCREEN_MODE != SCREEN_MODE_POST_CAR_REQUEST) {
				chooseScreenMode(SCREEN_MODE_POST_CAR_REQUEST);
			} else {
				StaticFunction.hideKeyboard(ConfigureData.activityMain);
				// export JSon search new and send to service here
				if (checkPostCarRequest() == true) {
					progressBar.setVisibility(View.VISIBLE);
					ServicePostCarRequest servicePostCarRequest = new ServicePostCarRequest();
					servicePostCarRequest
							.addOnPostJsonListener(new OnPostJsonListener() {

								@Override
								public void onPostJsonFail(String response) {
									showDialog(
											ConfigureData.activityMain
													.getString(R.string.dialog_title_fail),
											ConfigureData.activityMain
													.getString(R.string.post_car_request_fail),
											R.drawable.ic_error);
									progressBar.setVisibility(View.GONE);
									btnPostCarRequest
											.setOnClickListener(SearchCarFragmemt.this);
								}

								@Override
								public void onPostJsonCompleted(String response) {
									try {
										JSONObject jo = new JSONObject(response);
										if (jo.getBoolean("status") == true) {
											// Show dialog Post car request
											// complete
											showDialog(
													ConfigureData.activityMain
															.getString(R.string.dialog_title_success),
													ConfigureData.activityMain
															.getString(R.string.post_car_request_success),
													R.drawable.ic_launcher);
											// Reset to default search
											chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH);
											checkBoxDriver.setChecked(true);
											checkBoxDriver.setChecked(false);
											edtNoteInfo.setText("");
											strNoteInfo = "";
										} else {
											showDialog(
													ConfigureData.activityMain
															.getString(R.string.dialog_title_fail),
													ConfigureData.activityMain
															.getString(R.string.post_car_request_fail),
													R.drawable.ic_error);
										}
									} catch (JSONException e) {
										if (BuildConfig.DEBUG) {
											e.printStackTrace();
										}
									}

									progressBar.setVisibility(View.GONE);
									btnPostCarRequest
											.setOnClickListener(SearchCarFragmemt.this);
								}
							});

					btnPostCarRequest.setOnClickListener(null);
					servicePostCarRequest
							.postCarRequest(createCarRequestInfo());
				}

			}

			break;

		case R.id.btnCancelPostCarRequest:
			chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH);

			break;

		case R.id.btnStartTime:
			flagChoosePickerType = PICKER_TYPE_START_TIME;
			showTimePickerDialog(strStartTime);

			break;

		case R.id.btnEndTime:
			flagChoosePickerType = PICKER_TYPE_END_TIME;
			showTimePickerDialog(strEndTime);

			break;

		case R.id.btnStartDate:
			DatePickerFragment.pickerType = PICKER_TYPE_START_DATE;
			showDatePickerDialog(strStartDate);

			break;

		case R.id.btnEndDate:
			DatePickerFragment.pickerType = PICKER_TYPE_END_DATE;
			showDatePickerDialog(strEndDate);
			break;

		case R.id.btnExpirationDate:
			DatePickerFragment.pickerType = PICKER_TYPE_EXPIRATION_DATE;
			showDatePickerDialog(strExpirationDate);
			break;
		default:
			break;
		}

	}

	private JSONObject createCarRequestInfo() {
		JSONObject carRequestInfo = new JSONObject();

		try {
			carRequestInfo.put("createBy", ConfigureData.userAccount.getInt("id"));
			carRequestInfo.put("information", edtNoteInfo.getText().toString());
			

			if (driversSpinnerDataList != null) {
				driversSpinnerDataList.getSelectedItem().setValue(
						checkBoxDriver.isChecked() ? 1 : 0);
			}

			if (driversSpinnerDataList != null) {
				carRequestInfo.put("hasCarDriver", driversSpinnerDataList
						.getSelectedItem().getValue());
			}
			
			if (strStartDate != null) {
				carRequestInfo.put("startDate", strStartDate + " " + strStartTime + ":00");
			}
			
			if (strEndDate != null) {
				carRequestInfo.put("endDate", strEndDate + " " + strEndTime + ":00");
			}

			if (strExpirationDate != null) {
				carRequestInfo.put("expirationDate", strExpirationDate);
			}
			
			if (citiesSpinnerDataList != null) {
				carRequestInfo.put("cityId", citiesSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (pricesFromSpinnerDataList != null) {
				carRequestInfo.put("fromPrice", pricesFromSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (pricesToSpinnerDataList != null) {
				carRequestInfo.put("toPrice", pricesToSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (districtsSpinnerDataList != null) {
				carRequestInfo.put("districtId", districtsSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (citiesFromSpinnerDataList != null) {
				carRequestInfo.put("cityIdFrom", citiesFromSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (districtsFromSpinnerDataList != null) {
				carRequestInfo.put("districtIdFrom",
						districtsFromSpinnerDataList.getSelectedItem()
								.getValue());
			}

			if (citiesToSpinnerDataList != null) {
				carRequestInfo.put("cityIdTo", citiesToSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (districtsToSpinnerDataList != null) {
				carRequestInfo.put("districtIdTo", districtsToSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (makesSpinnerDataList != null) {
				carRequestInfo.put("makeId", makesSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (modelsSpinnerDataList != null) {
				carRequestInfo.put("modelId", modelsSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (seatNumberSpinnerDataList != null) {
				carRequestInfo.put("styleId", seatNumberSpinnerDataList
						.getSelectedItem().getValue());
			}

			if (transmissionsSpinnerDataList != null) {
				carRequestInfo.put("transmissionId",
						transmissionsSpinnerDataList.getSelectedItem()
								.getValue());
			}

		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}

		return carRequestInfo;
	}

	private boolean checkPostCarRequest() {
		// if has no car driver
		if (!checkBoxDriver.isChecked()) {
			if (citiesSpinnerDataList.getSelectedIndex() == 0) {
				// Show error dialog
				showDialog(ConfigureData.activityMain.getString(R.string.err),
						ConfigureData.activityMain
								.getString(R.string.err_lackof_city),
						R.drawable.ic_error);
				return false;
			}
		} else {
			if (citiesFromSpinnerDataList.getSelectedIndex() == 0
					|| citiesToSpinnerDataList.getSelectedIndex() == 0) {
				// Show error dialog
				showDialog(ConfigureData.activityMain.getString(R.string.err),
						ConfigureData.activityMain
								.getString(R.string.err_lackof_city),
						R.drawable.ic_error);
				return false;
			}
		}

		// check price
		if (pricesFromSpinnerDataList.getSelectedIndex() >= 0
				&& pricesToSpinnerDataList.getSelectedIndex() > 0) {
			if (pricesFromSpinnerDataList.getSelectedIndex() == 2
					&& pricesToSpinnerDataList.getSelectedIndex() == 0) {
				showDialog(ConfigureData.activityMain.getString(R.string.err),
						ConfigureData.activityMain
								.getString(R.string.err_price_to_constraint),
						R.drawable.ic_error);
				return false;
			}
			if (pricesFromSpinnerDataList.getSelectedIndex() > 2
					&& pricesFromSpinnerDataList.getSelectedIndex() < pricesFromSpinnerDataList
							.getSize() - 1) {
				if (pricesFromSpinnerDataList.getSelectedIndex()
						- pricesToSpinnerDataList.getSelectedIndex() >= 3) {
					showDialog(
							ConfigureData.activityMain.getString(R.string.err),
							ConfigureData.activityMain
									.getString(R.string.err_price_to_constraint),
							R.drawable.ic_error);
					return false;
				}
			}
		}

		if ("".equals(edtNoteInfo.getText().toString())) {
			// Show error dialog
			showDialog(ConfigureData.activityMain.getString(R.string.err),
					ConfigureData.activityMain
							.getString(R.string.err_note_must_not_null),
					R.drawable.ic_error);
			return false;
		}
		return true;
	}

	/**
	 * Compare date
	 * 
	 * @param d1
	 * @param d2
	 * @return -1 if d1 < d2 0 if d1 == d2 1 if d1 > d2
	 * @throws ParseException
	 */
	public static int compareStringDate(String d1, String d2) {
		Log.i("Compare: d1 vs d2", d1 + " vs " + d2);

		int result = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date date1 = sdf.parse(d1);
			Date date2 = sdf.parse(d2);

			if (date2.after(date1)) {
				result = -1;
				Log.i("d1 < d2", "-1");
			} else if (date1.after(date2)) {
				result = 1;
				Log.i("d1 > d2", "1");
			} else {
				result = 0;
				Log.i("d1 = d2", "0");
			}

		} catch (ParseException e) {
			Log.i("Error compare", "");
		}

		return result;

	}

	/**
	 * open fragment dialog in order to choose date time
	 */
	public void showTimePickerDialog(String strDefaultTime) {
		if (mTimePickerFragment == null) {
			mTimePickerFragment = new TimePickerFragment();
		} else {
			mTimePickerFragment.dismiss();
			mTimePickerFragment = new TimePickerFragment();
		}

		mTimePickerFragment.setDefaultTime(strDefaultTime);
		mTimePickerFragment.setOnTimeSetListener(this);
		mTimePickerFragment.show(getActivity().getSupportFragmentManager(),
				"timePicker");

	}

	public void showDatePickerDialog(String strDefaultDate) {
		if (mDatePickerFragment == null) {
			mDatePickerFragment = new DatePickerFragment();
		} else {
			mDatePickerFragment.dismiss();
			mDatePickerFragment = new DatePickerFragment();
		}

		mDatePickerFragment.setDefaultStringDate(strDefaultDate);
		mDatePickerFragment.setOnDatasetListener(this);
		mDatePickerFragment.show(getActivity().getSupportFragmentManager(),
				"datePicker");
	}

	/**
	 * call when item on spinner selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {

		switch (parent.getId()) {
		case R.id.spinnerCity:
			Log.d("SPINNER CITY", "SELECTED: " + pos);

			citiesSpinnerDataList.setSelectedItem(pos);
			if (pos != 0) {
				// Show spinnerDistricts
				spinnerDistricts.setVisibility(View.VISIBLE);

				// Get district list
				progressBar.setVisibility(View.VISIBLE);
				ServiceGetDistricts serviceGetDistricts = new ServiceGetDistricts();
				serviceGetDistricts
						.addOnGetJsonListener(new OnGetJsonListener() {

							@Override
							public void onGetJsonFail(String response) {
								progressBar.setVisibility(View.GONE);
							}

							@Override
							public void onGetJsonCompleted(String response) {
								try {
									JSONObject responseJson = new JSONObject(
											response);
									if (responseJson.getBoolean("status")) {
										JSONArray jsonArrayDistrics = responseJson
												.getJSONArray("data");
										// [{
										// "id":2147483647,
										// "cityId":2147483647,
										// "name":"String content"
										// }]
										ArrayList<SpinnerDataItem> districtsItems = new ArrayList<SpinnerDataItem>();
										for (int i = 0; i < jsonArrayDistrics
												.length(); i++) {
											int id = jsonArrayDistrics
													.getJSONObject(i).getInt(
															"id");
											String name = jsonArrayDistrics
													.getJSONObject(i)
													.getString("name");
											districtsItems
													.add(new SpinnerDataItem(
															id, name));
										}

										// Fill data to UI
										districtsSpinnerDataList = new SpinnerDataList(
												districtsItems, 0);
										ArrayAdapter<String> districtsAdapter = new ArrayAdapter<String>(
												ConfigureData.activityMain,
												android.R.layout.simple_spinner_item,
												districtsSpinnerDataList
														.getArraySpinnerText());
										districtsAdapter
												.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										spinnerDistricts
												.setAdapter(districtsAdapter);

									}
								} catch (JSONException e) {
									if (BuildConfig.DEBUG) {
										e.printStackTrace();
									}
								}

								progressBar.setVisibility(View.GONE);
							}
						});

				serviceGetDistricts.getDistricts(citiesSpinnerDataList
						.getSelectedItem().getValue());
			}
			break;
		case R.id.spinnerCityFrom:
			citiesFromSpinnerDataList.setSelectedItem(pos);
			if (pos != 0) {
				// Show spinnerDistricts
				spinnerDistrictsFrom.setVisibility(View.VISIBLE);

				// Get district list
				progressBar.setVisibility(View.VISIBLE);
				ServiceGetDistricts serviceGetDistricts = new ServiceGetDistricts();
				serviceGetDistricts
						.addOnGetJsonListener(new OnGetJsonListener() {

							@Override
							public void onGetJsonFail(String response) {
								progressBar.setVisibility(View.GONE);
							}

							@Override
							public void onGetJsonCompleted(String response) {
								try {
									JSONObject responseJson = new JSONObject(
											response);
									if (responseJson.getBoolean("status")) {
										JSONArray jsonArrayDistrics = responseJson
												.getJSONArray("data");
										// [{
										// "id":2147483647,
										// "cityId":2147483647,
										// "name":"String content"
										// }]
										ArrayList<SpinnerDataItem> districtsItems = new ArrayList<SpinnerDataItem>();
										for (int i = 0; i < jsonArrayDistrics
												.length(); i++) {
											int id = jsonArrayDistrics
													.getJSONObject(i).getInt(
															"id");
											String name = jsonArrayDistrics
													.getJSONObject(i)
													.getString("name");
											districtsItems
													.add(new SpinnerDataItem(
															id, name));
										}

										districtsFromSpinnerDataList = new SpinnerDataList(
												districtsItems, 0);
										ArrayAdapter<String> districtsFromAdapter = new ArrayAdapter<String>(
												ConfigureData.activityMain,
												android.R.layout.simple_spinner_item,
												districtsFromSpinnerDataList
														.getArraySpinnerText());
										districtsFromAdapter
												.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										spinnerDistrictsFrom
												.setAdapter(districtsFromAdapter);
									}
								} catch (JSONException e) {
									if (BuildConfig.DEBUG) {
										e.printStackTrace();
									}
								}

								progressBar.setVisibility(View.GONE);
							}
						});

				serviceGetDistricts.getDistricts(citiesFromSpinnerDataList
						.getSelectedItem().getValue());
			}

			break;
		case R.id.spinnerCityTo:
			citiesToSpinnerDataList.setSelectedItem(pos);
			if (pos != 0) {
				// Show spinnerDistricts
				spinnerDistrictsTo.setVisibility(View.VISIBLE);

				// Get district list
				progressBar.setVisibility(View.VISIBLE);
				ServiceGetDistricts serviceGetDistricts = new ServiceGetDistricts();
				serviceGetDistricts
						.addOnGetJsonListener(new OnGetJsonListener() {

							@Override
							public void onGetJsonFail(String response) {
								progressBar.setVisibility(View.GONE);
							}

							@Override
							public void onGetJsonCompleted(String response) {
								try {
									JSONObject responseJson = new JSONObject(
											response);
									if (responseJson.getBoolean("status")) {
										JSONArray jsonArrayDistrics = responseJson
												.getJSONArray("data");
										// [{
										// "id":2147483647,
										// "cityId":2147483647,
										// "name":"String content"
										// }]
										ArrayList<SpinnerDataItem> districtsItems = new ArrayList<SpinnerDataItem>();
										for (int i = 0; i < jsonArrayDistrics
												.length(); i++) {
											int id = jsonArrayDistrics
													.getJSONObject(i).getInt(
															"id");
											String name = jsonArrayDistrics
													.getJSONObject(i)
													.getString("name");
											districtsItems
													.add(new SpinnerDataItem(
															id, name));
										}

										districtsToSpinnerDataList = new SpinnerDataList(
												districtsItems, 0);
										ArrayAdapter<String> districtsToAdapter = new ArrayAdapter<String>(
												ConfigureData.activityMain,
												android.R.layout.simple_spinner_item,
												districtsToSpinnerDataList
														.getArraySpinnerText());
										districtsToAdapter
												.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										spinnerDistrictsTo
												.setAdapter(districtsToAdapter);
									}
								} catch (JSONException e) {
									if (BuildConfig.DEBUG) {
										e.printStackTrace();
									}
								}

								progressBar.setVisibility(View.GONE);
							}
						});

				serviceGetDistricts.getDistricts(citiesToSpinnerDataList
						.getSelectedItem().getValue());
			}

			break;
		case R.id.spinnerDistricts:
			districtsSpinnerDataList.setSelectedItem(pos);

			break;
		case R.id.spinnerFromDistricts:
			districtsFromSpinnerDataList.setSelectedItem(pos);

			break;
		case R.id.spinnerDistricstTo:
			districtsToSpinnerDataList.setSelectedItem(pos);

			break;
		case R.id.spinnerPriceFrom:
			if (pos > 1 && pos < pricesFromSpinnerDataList.getSize() - 1) {
				if (pos - pricesToSpinnerDataList.getSelectedIndex() >= 3
						&& pricesToSpinnerDataList.getSelectedIndex() > 0) {
					Toast.makeText(
							getActivity(),
							ConfigureData.activityMain
									.getString(R.string.err_price_to_constraint),
							Toast.LENGTH_SHORT).show();
					spinnerPriceFrom.setSelection(pricesFromSpinnerDataList
							.getSelectedIndex());
				} else {
					pricesFromSpinnerDataList.setSelectedItem(pos);
					spinnerPriceFrom.setSelection(pricesFromSpinnerDataList
							.getSelectedIndex());
				}

			} else {
				pricesToSpinnerDataList.setSelectedItem(0);
				pricesFromSpinnerDataList.setSelectedItem(pos);
				spinnerPriceFrom.setSelection(pricesFromSpinnerDataList
						.getSelectedIndex());
			}

			break;
		case R.id.spinnerPriceTo:
			if (pos > 0
					&& pricesFromSpinnerDataList.getSelectedIndex() >= 0
					&& pricesFromSpinnerDataList.getSelectedIndex() <= pricesFromSpinnerDataList
							.getSize() - 1) {
				if (pos <= pricesFromSpinnerDataList.getSelectedIndex() - 3) {
					Toast.makeText(
							getActivity(),
							ConfigureData.activityMain
									.getString(R.string.err_price_to_constraint),
							Toast.LENGTH_SHORT).show();
					spinnerPriceTo.setSelection(pricesToSpinnerDataList
							.getSelectedIndex());
				} else {
					pricesToSpinnerDataList.setSelectedItem(pos);
					spinnerPriceTo.setSelection(pricesToSpinnerDataList
							.getSelectedIndex());
				}
			}

			break;
		case R.id.spinnerMakeCar:
			makesSpinnerDataList.setSelectedItem(pos);

			break;
		case R.id.spinnerModelCar:
			modelsSpinnerDataList.setSelectedItem(pos);

			break;
		case R.id.spinnerTransmission:
			transmissionsSpinnerDataList.setSelectedItem(pos);

			break;
		case R.id.spinnerSeatNumber:
			seatNumberSpinnerDataList.setSelectedItem(pos);

			break;
		case R.id.spinnerCarOwner:
			carOwnersSpinnerDataList.setSelectedItem(pos);

			break;

		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onDestroy() {
		ConfigureData.isPostNewsScreen = false;
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

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {

		String selectedDate = formatStringDate(day + "/" + (month + 1) + "/" + year);
		
		switch (DatePickerFragment.pickerType) {
		
		case PICKER_TYPE_START_DATE:
			if (compareStringDate(selectedDate, strEndDate) == 0 && (compareHourStr(strStartTime, strEndTime) == 0 || compareHourStr(strStartTime, strEndTime) == 1)) {
				Toast.makeText(
						getActivity(),
						ConfigureData.activityMain
								.getString(R.string.err_hour_constraint),
						Toast.LENGTH_SHORT).show();
				
				return;
			}
			
			if (CURRENT_SCREEN_MODE == SCREEN_MODE_ADVANCE_SEARCH) {
				// The selected date must be not before the current date
				if (!(compareStringDate(selectedDate, getCurrenttDateFormated()) == -1)) {
					strStartDate = selectedDate;
					btnStartDate.setText(strStartDate);

					if (!(compareStringDate(strStartDate, strEndDate) == -1)) {
						strEndDate = addStringDateFormated(selectedDate, 1);
						btnEndDate.setText(strEndDate);
					}
					strExpirationDate = strStartDate;
					btnExpirationDate.setText(strExpirationDate);
				} else {
					Toast.makeText(
							getActivity(),
							ConfigureData.activityMain
									.getString(R.string.err_start_date_constraint),
							Toast.LENGTH_SHORT).show();
				}
			} else if (CURRENT_SCREEN_MODE == SCREEN_MODE_POST_CAR_REQUEST) {
				// The selected date must be after the current day 2 days
				if (compareStringDate(
						addStringDateFormated(getCurrenttDateFormated(), 1),
						selectedDate) == -1) {
					strStartDate = selectedDate;
					btnStartDate.setText(strStartDate);

					if (!(compareStringDate(strStartDate, strEndDate) == -1)) {
						strEndDate = addStringDateFormated(selectedDate, 1);
						btnEndDate.setText(strEndDate);
					}
					strExpirationDate = strStartDate;
					btnExpirationDate.setText(strExpirationDate);
				} else {
					Toast.makeText(
							getActivity(),
							ConfigureData.activityMain
									.getString(R.string.err_start_date_constraint_when_post_car_request),
							Toast.LENGTH_SHORT).show();
				}

			}
			break;
			
		case PICKER_TYPE_END_DATE:			
			if (compareStringDate(selectedDate, strStartDate) == 0 && (compareHourStr(strStartTime, strEndTime) == 0 || compareHourStr(strStartTime, strEndTime) == 1)) {
				Toast.makeText(
						getActivity(),
						ConfigureData.activityMain
								.getString(R.string.err_hour_constraint),
						Toast.LENGTH_SHORT).show();
				
				return;
			}
			
			if (compareStringDate(strStartDate, selectedDate) == 1){
				Toast.makeText(
						getActivity(),
						ConfigureData.activityMain
								.getString(R.string.err_end_date_constraint),
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			strEndDate = selectedDate;
			btnEndDate.setText(strEndDate);
			break;
		case PICKER_TYPE_EXPIRATION_DATE:
			// The selected date and before the current and not after start
			// date
			if ((compareStringDate(selectedDate, getCurrenttDateFormated()) == 1)
					&& (compareStringDate(selectedDate, strStartDate) == -1)) {
				strExpirationDate = selectedDate;
				btnExpirationDate.setText(strExpirationDate);
			} else {
				Toast.makeText(
						getActivity(),
						ConfigureData.activityMain
								.getString(R.string.err_expiration_date_constraint),
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		String minutes = "30";
		String hour = "0";

		if (minute == 0) {
			minutes = "00";
		}

		if (hourOfDay < 10) {
			hour = "0" + hourOfDay;
		} else {
			hour = hourOfDay + "";
		}

		String strSelectedHour = hour + ":" + minutes; 
		switch (flagChoosePickerType) {
		case PICKER_TYPE_START_TIME:
			if (strEndDate.equals(strStartDate)) {
				if (compareHourStr(strSelectedHour, strEndTime) == -1) {
					strStartTime = strSelectedHour;
					btnStartTime.setText(strStartTime);					
				} else {
					Toast.makeText(
							getActivity(),
							ConfigureData.activityMain
									.getString(R.string.err_hour_constraint),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				strStartTime = strSelectedHour;
				btnStartTime.setText(strStartTime);
			}
			break;
		case PICKER_TYPE_END_TIME:
			if (strEndDate.equals(strStartDate)) {
				if (compareHourStr(strStartTime, strSelectedHour) == -1) {
					strEndTime = strSelectedHour;
					btnEndTime.setText(strEndTime);				
				} else {
					Toast.makeText(
							getActivity(),
							ConfigureData.activityMain
									.getString(R.string.err_hour_constraint),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				strEndTime = strSelectedHour;
				btnEndTime.setText(strEndTime);
			}
			
			break;
		}
		
	}

	/**
	 * Compare hour format hh:mm in string
	 * 
	 * @param h1
	 * @param h2
	 * @return -1 if h1 < h2; 0 if h1 == h2; 2 if h1 > h2
	 */
	private int compareHourStr(String h1, String h2) {
		int hour1 = Integer.parseInt(h1.split(":")[0]);
		int hour2 = Integer.parseInt(h2.split(":")[0]);
		int min1 =  Integer.parseInt(h1.split(":")[1]);
		int min2 =  Integer.parseInt(h2.split(":")[1]);
		
		int s1 = hour1*60 + min1;
		int s2 = hour2*60 + min2;
			
		if (s1 < s2) return -1;
		if (s1 == s2) return 0;
		
		return 1;
		
	}

	public void exportJsonObjectSearch() {

		searchRequestJSONObject = new JSONObject();
		try {
			// "startDate":"String content",
			searchRequestJSONObject.put("startDate", strStartDate + " "
					+ strStartTime + ":00");

			// "endDate":"String content",
			searchRequestJSONObject.put("endDate", strEndDate + " "
					+ strEndTime + ":00");

			// "fromPrice":2147483647,
			searchRequestJSONObject.put("fromPrice", pricesFromSpinnerDataList
					.getSelectedItem().getValue());

			// "toPrice":2147483647,
			searchRequestJSONObject.put("toPrice", pricesToSpinnerDataList
					.getSelectedItem().getValue());

			// "cityId":2147483647,
			searchRequestJSONObject.put("cityId", citiesSpinnerDataList
					.getSelectedItem().getValue());

			// "districtId":2147483647,
			if (districtsSpinnerDataList != null) {
				searchRequestJSONObject.put("districtId",
						districtsSpinnerDataList.getSelectedItem().getValue());
			}

			// "hasCarDriver":2147483647,
			searchRequestJSONObject.put("hasCarDriver",
					checkBoxDriver.isChecked() ? 1 : 0);

			// "cityIdFrom":2147483647,
			searchRequestJSONObject.put("cityIdFrom", citiesFromSpinnerDataList
					.getSelectedItem().getValue());

			// "districtIdFrom":2147483647,
			if (districtsFromSpinnerDataList != null) {
				searchRequestJSONObject.put("districtIdFrom",
						districtsFromSpinnerDataList.getSelectedItem()
								.getValue());
			}

			// "cityIdTo":2147483647,
			searchRequestJSONObject.put("cityIdTo", citiesToSpinnerDataList
					.getSelectedItem().getValue());

			// "districtIdTo":2147483647,
			if (districtsToSpinnerDataList != null) {
				searchRequestJSONObject
						.put("districtIdTo", districtsToSpinnerDataList
								.getSelectedItem().getValue());
			}

			// "makeId":2147483647,
			searchRequestJSONObject.put("makeId", makesSpinnerDataList
					.getSelectedItem().getValue());

			// "modelId":2147483647,
			searchRequestJSONObject.put("modelId", modelsSpinnerDataList
					.getSelectedItem().getValue());

			// "styleId":2147483647,
			searchRequestJSONObject.put("styleId", seatNumberSpinnerDataList
					.getSelectedItem().getValue());

			// "transmissionId":2147483647,
			searchRequestJSONObject.put("transmissionId",
					transmissionsSpinnerDataList.getSelectedItem().getValue());

			// "carOwnerId":2147483647,
			searchRequestJSONObject.put("carOwnerId", carOwnersSpinnerDataList
					.getSelectedItem().getValue());

			// "pageIndex":2147483647,
			searchRequestJSONObject.put("pageIndex", 0);

			// "pageSize":2147483647
			searchRequestJSONObject.put("pageSize", 15);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void switchCarDriverMode(boolean hasCarDriver) {
		if (hasCarDriver) {
			driversSpinnerDataList.setSelectedItem(1);
			// Has driver
			spinnerCity.setVisibility(View.GONE);
			spinnerCityFrom.setVisibility(View.VISIBLE);
			spinnerCityTo.setVisibility(View.VISIBLE);
			spinnerDistricts.setVisibility(View.GONE);
			spinnerDistrictsFrom.setVisibility(View.GONE);
			spinnerDistrictsTo.setVisibility(View.GONE);
			spinnerPriceFrom.setVisibility(View.VISIBLE);
			spinnerPriceTo.setVisibility(View.VISIBLE);

			spinnerMakeCar.setVisibility(View.VISIBLE);
			spinnerModelCar.setVisibility(View.VISIBLE);
			spinnerTransmission.setVisibility(View.VISIBLE);
			spinnerSeatNumber.setVisibility(View.VISIBLE);
			spinnerCarOwner.setVisibility(View.VISIBLE);
			spinnerTransmission.setVisibility(View.GONE);

			layoutPlaceTo.setVisibility(View.VISIBLE);
			layoutMore.setVisibility(View.GONE);

		} else {
			driversSpinnerDataList.setSelectedItem(0);
			// Has no driver
			spinnerCity.setVisibility(View.VISIBLE);
			spinnerCityFrom.setVisibility(View.GONE);
			spinnerCityTo.setVisibility(View.GONE);
			spinnerDistricts.setVisibility(View.GONE);
			spinnerDistrictsFrom.setVisibility(View.GONE);
			spinnerDistrictsTo.setVisibility(View.GONE);
			spinnerPriceFrom.setVisibility(View.VISIBLE);
			spinnerPriceTo.setVisibility(View.VISIBLE);

			spinnerMakeCar.setVisibility(View.VISIBLE);
			spinnerModelCar.setVisibility(View.VISIBLE);
			spinnerTransmission.setVisibility(View.VISIBLE);
			spinnerSeatNumber.setVisibility(View.VISIBLE);
			spinnerCarOwner.setVisibility(View.VISIBLE);

			layoutPlaceTo.setVisibility(View.GONE);
			layoutMore.setVisibility(View.GONE);
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switchCarDriverMode(isChecked);

		// Reset value
		if (mNeedToReset) {
			strStartDate = getCurrenttDateFormated();
			strEndDate = addStringDateFormated(strStartDate, 1);
			strExpirationDate = strStartDate;
			strStartTime = "08:00";
			strEndTime = "20:00";

			btnStartDate.setText(strStartDate);
			btnEndDate.setText(strEndDate);
			btnExpirationDate.setText(strExpirationDate);
			btnStartTime.setText(strStartTime);
			btnEndTime.setText(strEndTime);

			if (citiesSpinnerDataList != null) {
				citiesSpinnerDataList.setSelectedItem(0);
				spinnerCity.setSelection(0);
			}

			if (citiesFromSpinnerDataList != null) {
				citiesFromSpinnerDataList.setSelectedItem(0);
				spinnerCityFrom.setSelection(0);
			}

			if (citiesToSpinnerDataList != null) {
				citiesToSpinnerDataList.setSelectedItem(0);
				spinnerCityTo.setSelection(0);
			}

			if (districtsSpinnerDataList != null) {
				districtsSpinnerDataList.setSelectedItem(0);
				spinnerDistricts.setSelection(0);
			}

			if (districtsFromSpinnerDataList != null) {
				districtsFromSpinnerDataList.setSelectedItem(0);
				spinnerDistrictsFrom.setSelection(0);
			}

			if (districtsToSpinnerDataList != null) {
				districtsToSpinnerDataList.setSelectedItem(0);
				spinnerDistrictsTo.setSelection(0);
			}

			if (pricesFromSpinnerDataList != null) {
				pricesFromSpinnerDataList.setSelectedItem(0);
				spinnerPriceFrom.setSelection(0);
			}

			if (pricesToSpinnerDataList != null) {
				pricesToSpinnerDataList.setSelectedItem(0);
				spinnerPriceTo.setSelection(0);
			}

			if (makesSpinnerDataList != null) {
				makesSpinnerDataList.setSelectedItem(0);
				spinnerMakeCar.setSelection(0);
			}

			if (modelsSpinnerDataList != null) {
				modelsSpinnerDataList.setSelectedItem(0);
				spinnerModelCar.setSelection(0);
			}

			if (transmissionsSpinnerDataList != null) {
				transmissionsSpinnerDataList.setSelectedItem(0);
				spinnerTransmission.setSelection(0);
			}

			if (seatNumberSpinnerDataList != null) {
				seatNumberSpinnerDataList.setSelectedItem(0);
				spinnerSeatNumber.setSelection(0);
			}

			if (carOwnersSpinnerDataList != null) {
				carOwnersSpinnerDataList.setSelectedItem(0);
				spinnerCarOwner.setSelection(0);
			}
		}

	}

	@SuppressWarnings("deprecation")
	public void showDialog(String title, String content, int icon) {
		// Creating alert Dialog with one Button

		AlertDialog alertDialog = new AlertDialog.Builder(
				ConfigureData.activityMain).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(content);

		// Setting Icon to Dialog
		alertDialog.setIcon(icon);

		// Setting OK Button
		alertDialog.setButton(
				ConfigureData.activityMain.getString(R.string.str_ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}
				});

		// Showing Alert Message
		alertDialog.show();

	}

}