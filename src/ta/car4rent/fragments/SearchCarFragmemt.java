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
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
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
		OnItemSelectedListener, OnDateSetListener, OnTimeSetListener, OnCheckedChangeListener {

	// =========================[SEARCH REQUEST]============================
	public static JSONObject searchRequestJSONObject = new JSONObject();

	// =========================[SCREEN MODE]==============================
	public final static int SCREEN_MODE_ADVANCE_SEARCH_NO_DRIVER = 0;
	public final static int SCREEN_MODE_ADVANCE_SEARCH_HAS_DRIVER = 1;
	public final static int SCREEN_MODE_POST_CAR_REQUEST = 2;
	private static int CURRENT_SCREEN_MODE = SCREEN_MODE_ADVANCE_SEARCH_NO_DRIVER;

	// =========================[PICKER_MODE]==============================
	public final static int PICKER_TYPE_START_TIME = 0;
	public final static int PICKER_TYPE_END_TIME = 1;
	public final static int PICKER_TYPE_START_DATE = 2;
	public final static int PICKER_TYPE_END_DATE = 3;
	public final static int PICKER_TYPE_EXPIRATION_DATE = 4;

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
	private ScrollView scrollView;
	private TextView tvScreenName;
	private ProgressBar progressBar;
	private CheckBox checkBoxDriver;

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
	private Button btnPostCarRequest;
	private Button btnSearch;
	private Button btnExpirationDate;

	private LinearLayout layoutPlaceTo;
	private LinearLayout layoutMore;
	private LinearLayout layoutExpirationDate;
	private LinearLayout btnLayoutMore;

	private EditText edtNoteInfo;

	private String strStartDate = "30/12/2013";
	private String strEndDate = "31/12/2013";
	private String strExpirationDate = "29/12/2013";
	private String strStartTime = "20 : 20";
	private String strEndTime = "20 : 20";

	private int flagTimeDateChoose = 0;

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
		View rootView = inflater.inflate(R.layout.fragment_search_car,
				container, false);
		// Show waiting progress dialog
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Init components and setOnClickListener
		findViewById();

		setUiComponentEventListener();

		// Check search condition exist?
		if (citiesSpinnerDataList == null) {
			getDataFromServer();
			chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH_NO_DRIVER, false);
		} else {
			FillDataToComponent();
			
			int tmp = CURRENT_SCREEN_MODE;
			/**
			 * Set visible or gone for ui mode
			 */
			if (driversSpinnerDataList.getSelectedIndex() == 0) {
				checkBoxDriver.setOnCheckedChangeListener(null);
				checkBoxDriver.setChecked(false);
				checkBoxDriver.setOnCheckedChangeListener(this);
				chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH_NO_DRIVER, false);	
			} else {
				checkBoxDriver.setOnCheckedChangeListener(null);
				checkBoxDriver.setChecked(true);
				checkBoxDriver.setOnCheckedChangeListener(this);
				chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH_HAS_DRIVER, false);
			}
			
			if (tmp == SCREEN_MODE_POST_CAR_REQUEST) {
				chooseScreenMode(SCREEN_MODE_POST_CAR_REQUEST, false);
				edtNoteInfo.setText(strNoteInfo);
			}
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

								// Get districts list
								JSONArray districtsJsonArray = data
										.getJSONArray("districts");
								districtsSpinnerDataList = new SpinnerDataList(
										districtsJsonArray);
								// Get districtsFrom list
								JSONArray districtsFromJsonArray = data
										.getJSONArray("districtsFrom");
								districtsFromSpinnerDataList = new SpinnerDataList(
										districtsFromJsonArray);
								// Get districtsTo list
								JSONArray districtsToJsonArray = data
										.getJSONArray("districtsTo");
								districtsToSpinnerDataList = new SpinnerDataList(
										districtsToJsonArray);

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

						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							FillDataToComponent();
						}
					}
				});

		serviceGetAdvanceSearchConditions.getAdvanceSearchConditions();

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
		btnExpirationDate = (Button) getView().findViewById(R.id.btnExpirationDate);
		btnLayoutMore = (LinearLayout) getView().findViewById(R.id.btnLayoutMore);
		edtNoteInfo = (EditText) getView().findViewById(R.id.edtNoteInfo);
		edtNoteInfo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				strNoteInfo = edtNoteInfo.getText().toString();
			}
		});
		
		btnSearch = (Button) getView().findViewById(R.id.btnSearch);
		btnPostCarRequest = (Button) getView().findViewById(
				R.id.btnPostCarRequest);

	}

	/**
	 * Set visible or gone for ui mode
	 */
	private void chooseScreenMode(int mode, boolean reset) {

		if (mode == SCREEN_MODE_POST_CAR_REQUEST) {
			if (ConfigureData.isLogged == false) {
				Toast.makeText(getActivity(), "You need login first !", 1000)
						.show();
				return;
			}
		}

		CURRENT_SCREEN_MODE = mode;

		switch (CURRENT_SCREEN_MODE) {
		case SCREEN_MODE_ADVANCE_SEARCH_NO_DRIVER:
			
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
			edtNoteInfo.setVisibility(View.GONE);

			break;
		case SCREEN_MODE_ADVANCE_SEARCH_HAS_DRIVER:
			
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

			layoutPlaceTo.setVisibility(View.VISIBLE);
			layoutMore.setVisibility(View.GONE);
			edtNoteInfo.setVisibility(View.GONE);
			spinnerTransmission.setVisibility(View.GONE);
			break;
		case SCREEN_MODE_POST_CAR_REQUEST:

			spinnerMakeCar.setVisibility(View.VISIBLE);
			spinnerModelCar.setVisibility(View.VISIBLE);
			spinnerTransmission.setVisibility(View.VISIBLE);
			spinnerSeatNumber.setVisibility(View.VISIBLE);
			spinnerCarOwner.setVisibility(View.VISIBLE);

			layoutMore.setVisibility(View.VISIBLE);
			btnLayoutMore.setVisibility(View.VISIBLE);

			btnSearch.setVisibility(View.GONE);
			btnPostCarRequest.setVisibility(View.VISIBLE);
			layoutExpirationDate.setVisibility(View.VISIBLE);
			edtNoteInfo.setVisibility(View.VISIBLE);

			// Change label in UI
			((MainActivity) getActivity())
					.setActionBarTitle(getString(R.string.label_post_car_request));
			tvScreenName
					.setText(getString(R.string.post_car_request_screen_name));

			break;
		}

		// RESET VALUE
		if (reset) {
			
			spinnerCity.setSelection(0);
			citiesSpinnerDataList.setSelectedItem(0);
			spinnerCityFrom.setSelection(0);
			citiesFromSpinnerDataList.setSelectedItem(0);
			spinnerCityTo.setSelection(0);
			citiesToSpinnerDataList.setSelectedItem(0);
			spinnerDistricts.setSelection(0);
			districtsSpinnerDataList.setSelectedItem(0);
			spinnerDistrictsFrom.setSelection(0);
			districtsFromSpinnerDataList.setSelectedItem(0);
			spinnerDistrictsTo.setSelection(0);
			districtsToSpinnerDataList.setSelectedItem(0);
			spinnerPriceFrom.setSelection(0);
			pricesFromSpinnerDataList.setSelectedItem(0);
			spinnerPriceTo.setSelection(0);
			pricesToSpinnerDataList.setSelectedItem(0);
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

			// fill default data
			strStartDate = getCurrenttDateFormated();
			btnStartDate.setText(strStartDate);
			strEndDate = addStringDateFormated(strStartDate, 1);
			btnEndDate.setText(strEndDate);
			strExpirationDate = addStringDateFormated(strStartDate, -1);
			btnExpirationDate.setText(strExpirationDate);

			strStartTime = "20 : 20";
			btnStartTime.setText(strStartTime);
			strEndTime = "20 : 20";
			btnEndTime.setText(strEndTime);
		}
	}

	/**
	 * Set event listener for UI components
	 */
	private void setUiComponentEventListener() {

		// Set onClick Listener
		btnSearch.setOnClickListener(this);
		btnPostCarRequest.setOnClickListener(this);

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
				if (layoutMore.isShown()) {
					layoutMore.setVisibility(View.GONE);
				} else {
					layoutMore.setVisibility(View.VISIBLE);
					scrollView.fullScroll(View.FOCUS_DOWN);
				}
				
				spinnerMakeCar.setSelection(0);
				spinnerModelCar.setSelection(0);
				spinnerTransmission.setSelection(0);
				spinnerSeatNumber.setSelection(0);
				spinnerCarOwner.setSelection(0);
			}
		});

	}

	private void FillDataToComponent() {

		progressBar.setVisibility(View.GONE);

		// cities
		ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				citiesSpinnerDataList.getArraySpinnerText());
		citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCity.setAdapter(citiesAdapter);
		spinnerCity.setSelection(citiesSpinnerDataList.getSelectedIndex());

		// citiesFrom
		ArrayAdapter<String> citiesFromAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				citiesFromSpinnerDataList.getArraySpinnerText());
		citiesFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCityFrom.setAdapter(citiesFromAdapter);
		spinnerCityFrom.setSelection(citiesFromSpinnerDataList.getSelectedIndex());

		// citiesTo
		ArrayAdapter<String> citiesToAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				citiesToSpinnerDataList.getArraySpinnerText());
		citiesToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCityTo.setAdapter(citiesToAdapter);
		spinnerCityTo.setSelection(citiesToSpinnerDataList.getSelectedIndex());

		// price from
		ArrayAdapter<String> priceFromAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				pricesFromSpinnerDataList.getArraySpinnerText());
		priceFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPriceFrom.setAdapter(priceFromAdapter);
		spinnerPriceFrom.setSelection(pricesFromSpinnerDataList.getSelectedIndex());

		// price To
		ArrayAdapter<String> priceToAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				pricesToSpinnerDataList.getArraySpinnerText());
		priceToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPriceTo.setAdapter(priceToAdapter);
		spinnerPriceTo.setSelection(pricesToSpinnerDataList.getSelectedIndex());

		// seat number
		ArrayAdapter<String> seatNumberAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				seatNumberSpinnerDataList.getArraySpinnerText());
		seatNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSeatNumber.setAdapter(seatNumberAdapter);
		spinnerSeatNumber.setSelection(seatNumberSpinnerDataList.getSelectedIndex());
		
		// makes
		ArrayAdapter<String> makesAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				makesSpinnerDataList.getArraySpinnerText());
		makesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMakeCar.setAdapter(makesAdapter);
		spinnerMakeCar.setSelection(makesSpinnerDataList.getSelectedIndex());
		
		// model
		ArrayAdapter<String> modelsAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				modelsSpinnerDataList.getArraySpinnerText());
		modelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerModelCar.setAdapter(modelsAdapter);
		spinnerModelCar.setSelection(modelsSpinnerDataList.getSelectedIndex());
		
		// transmission
		ArrayAdapter<String> transmissionAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				transmissionsSpinnerDataList.getArraySpinnerText());
		transmissionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTransmission.setAdapter(transmissionAdapter);
		spinnerTransmission.setSelection(transmissionsSpinnerDataList.getSelectedIndex());
		
		// car owner
		ArrayAdapter<String> carOwnerAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				carOwnersSpinnerDataList.getArraySpinnerText());
		carOwnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCarOwner.setAdapter(carOwnerAdapter);
		spinnerCarOwner.setSelection(carOwnersSpinnerDataList.getSelectedIndex());

	}

	/**
	 * Get date as String
	 * 
	 * @param add
	 *            = 0
	 * @return current date
	 * 
	 * @param add
	 *            = 1
	 * @return the next date
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
			if (driversSpinnerDataList.getSelectedItem().getValue() == 1) {
				if (citiesFromSpinnerDataList.getSelectedIndex() == 0
						|| citiesToSpinnerDataList.getSelectedIndex() == 0) {
					Toast.makeText(ConfigureData.activityMain,
							"Error, you must be select cityTo & cityFrom", 1500)
							.show();
					return false;
				}
			}
		} catch (NullPointerException e) {
			// get error here driversSpinnerDataList.getSelectedItem().getValue()
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
				Fragment newContent = new ResultSeachFragmemt();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				final FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.content_frame, newContent);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// add to back stack
				ft.addToBackStack("Fragment Search");
				// just for test-> need to commit
				ft.commit();

				Toast.makeText(ConfigureData.activityMain,
						"SEARCH COMPLETED !", Toast.LENGTH_SHORT).show();

			}
			progressBar.setVisibility(View.GONE);
			break;
		case R.id.btnPostCarRequest:
			if (CURRENT_SCREEN_MODE != SCREEN_MODE_POST_CAR_REQUEST) {
				chooseScreenMode(SCREEN_MODE_POST_CAR_REQUEST, false);
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

									progressBar.setVisibility(View.GONE);

								}

								@Override
								public void onPostJsonCompleted(String response) {
									try {
										JSONObject jo = new JSONObject(response);
										if (jo.getBoolean("status") == true) {
											// Show dialog Post car request
											// complete
											Toast.makeText(getActivity(),
													jo.getString("message"),
													1000).show();
										} else {
											Toast.makeText(getActivity(),
													jo.getString("message"),
													1000).show();
										}
									} catch (JSONException e) {

									}

									progressBar.setVisibility(View.GONE);

								}
							});
					servicePostCarRequest
							.postCarRequest(createCarRequestInfo());
				}

			}

			break;
		case R.id.btnStartTime:
			flagTimeDateChoose = PICKER_TYPE_START_TIME;
			showTimePickerDialog(btnStartTime);

			break;

		case R.id.btnEndTime:
			flagTimeDateChoose = PICKER_TYPE_END_TIME;
			showTimePickerDialog(btnEndTime);

			break;

		case R.id.btnStartDate:
			flagTimeDateChoose = PICKER_TYPE_START_DATE;
			showDatePickerDialog(btnStartDate);

			break;

		case R.id.btnEndDate:
			flagTimeDateChoose = PICKER_TYPE_END_DATE;
			showDatePickerDialog(btnEndDate);

		case R.id.btnExpirationDate:
			flagTimeDateChoose = PICKER_TYPE_EXPIRATION_DATE;
			showDatePickerDialog(btnExpirationDate);
			break;
		default:
			break;
		}

	}

	private JSONObject createCarRequestInfo() {
		JSONObject carRequestInfo = new JSONObject();
		try {
			carRequestInfo.put("createBy",
					ConfigureData.userAccount.getInt("id"));
			carRequestInfo.put("information", edtNoteInfo.getText().toString());
			carRequestInfo.put("endDate", strEndDate);
			carRequestInfo.put("expirationDate", strExpirationDate);
			carRequestInfo.put("hasCarDriver", driversSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("startDate", strStartDate);
			carRequestInfo.put("cityId", citiesSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("fromPrice", pricesFromSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("toPrice", pricesToSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("districtId", districtsSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("cityIdFrom", citiesFromSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("districtIdFrom", districtsFromSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("cityIdTo", citiesToSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("districtIdTo", districtsToSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("makeId", makesSpinnerDataList.getSelectedItem()
					.getValue());
			carRequestInfo.put("modelId", modelsSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("styleId", seatNumberSpinnerDataList
					.getSelectedItem().getValue());
			carRequestInfo.put("transmissionId", transmissionsSpinnerDataList
					.getSelectedItem().getValue());
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		return carRequestInfo;
	}

	private boolean checkPostCarRequest() {
		// if has no car driver
		if (driversSpinnerDataList.getSelectedItem().getValue() == 0) {
			if (citiesSpinnerDataList.getSelectedIndex() == 0) {
				// Show error dialog
				Toast.makeText(getActivity(), "Error City must be not nul !",
						1000).show();
				return false;
			}
		} else {
			if (citiesFromSpinnerDataList.getSelectedIndex() == 0
					|| citiesToSpinnerDataList.getSelectedIndex() == 0) {
				// Show error dialog
				Toast.makeText(getActivity(),
						"Error CityFrom or CityTo must be not nul !", 1000)
						.show();
				return false;
			}
		}

		if ("".equals(edtNoteInfo.getText().toString())) {
			// Show error dialog
			Toast.makeText(getActivity(), "Error Note Info must be has text !",
					1000).show();
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
	public void showTimePickerDialog(View v) {
		TimePickerFragment timePickerFragment = new TimePickerFragment();
		timePickerFragment.setOnTimeSetListener(this);
		timePickerFragment.show(getActivity().getSupportFragmentManager(),
				"timePicker");

	}

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setOnDatasetListener(this);
		datePickerFragment.show(getActivity().getSupportFragmentManager(),
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
			pricesFromSpinnerDataList.setSelectedItem(pos);
			if (pos > 1) {
				spinnerPriceTo.setVisibility(View.VISIBLE);
			} else {
				spinnerPriceTo.setVisibility(View.GONE);
			}

			break;
		case R.id.spinnerPriceTo:
			pricesToSpinnerDataList.setSelectedItem(pos);

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
		String selectedDate = formatStringDate(day + "/" + (month + 1) + "/"
				+ year);

		switch (flagTimeDateChoose) {
		case PICKER_TYPE_START_DATE:
			// The selected date must be after the current day
			if (compareStringDate(getCurrenttDateFormated(), selectedDate) == 1) {
				strStartDate = selectedDate;
				strEndDate = addStringDateFormated(selectedDate, 1);
				strExpirationDate = addStringDateFormated(selectedDate, -1);
				btnStartDate.setText(strStartDate);
				btnEndDate.setText(strEndDate);
				btnExpirationDate.setText(strExpirationDate);
			}
			break;
		case PICKER_TYPE_END_DATE:
			if (compareStringDate(strStartDate, selectedDate) == -1) {
				strEndDate = selectedDate;
				btnEndDate.setText(strEndDate);
			}
			break;
		case PICKER_TYPE_EXPIRATION_DATE:
			// The selected date must be after start date and after the current
			// date
			if (compareStringDate(getCurrenttDateFormated(), selectedDate) == -1
					&& compareStringDate(selectedDate, strStartDate) == -1) {
				strExpirationDate = selectedDate;
				btnExpirationDate.setText(strExpirationDate);
			} else {
				// Error announce and ignore date change
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

		if (flagTimeDateChoose == PICKER_TYPE_START_TIME) {
			strStartTime = hour + " : " + minutes;
			btnStartTime.setText(strStartTime);
		} else if (flagTimeDateChoose == PICKER_TYPE_END_TIME) {
			strEndTime = hour + " : " + minutes;
			btnEndTime.setText(strEndTime);
		}
	}

	public void exportJsonObjectSearch() {

		try {
			// "startDate":"String content",
			searchRequestJSONObject.put("startDate", strStartDate);

			// "endDate":"String content",
			searchRequestJSONObject.put("endDate", strEndDate);

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
			searchRequestJSONObject.put("districtId", districtsSpinnerDataList
					.getSelectedItem().getValue());

			// "hasCarDriver":2147483647,
			searchRequestJSONObject.put("hasCarDriver", driversSpinnerDataList
					.getSelectedItem().getValue());

			// "cityIdFrom":2147483647,
			searchRequestJSONObject.put("cityIdFrom", citiesFromSpinnerDataList
					.getSelectedItem().getValue());

			// "districtIdFrom":2147483647,
			searchRequestJSONObject.put("districtIdFrom",
					districtsFromSpinnerDataList.getSelectedItem().getValue());

			// "cityIdTo":2147483647,
			searchRequestJSONObject.put("cityIdTo", citiesToSpinnerDataList
					.getSelectedItem().getValue());

			// "districtIdTo":2147483647,
			searchRequestJSONObject.put("districtIdTo",
					districtsToSpinnerDataList.getSelectedItem().getValue());

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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			driversSpinnerDataList.setSelectedItem(1);
			// Has driver
			chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH_HAS_DRIVER, true);
		} else {
			driversSpinnerDataList.setSelectedItem(0);
			// Has no driver
			chooseScreenMode(SCREEN_MODE_ADVANCE_SEARCH_NO_DRIVER, true);
		}
		
	}

}