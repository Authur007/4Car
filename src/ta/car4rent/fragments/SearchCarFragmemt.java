package ta.car4rent.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.CustomTimePickerDialog;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.ServiceGetAdvanceSearchConditions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class SearchCarFragmemt extends Fragment implements OnClickListener,
		OnItemSelectedListener {
	// ======================================
	// VARIABLES AND STATIC FLAGS
	// ======================================

	public static final String KEY_CITIES = "cities";
	public static final String KEY_DRIVER = "drivers";
	public static final String KEY_SEAT = "styles";
	public static final String KEY_FROM_DATE = "";
	public static final String KEY_TO_DATE = "";
	public static final String KEY_DISTRICS = "districts";

	public static final String KEY_BRANDS = "makes";
	public static final String KEY_MODELS = "models";
	public static final String KEY_CAR_OWNER = "carOwners";

	public static final String KEY_FROM_PRICES = "fromPrices";
	public static final String KEY_TO_PRICES = "toPrices";
	public static final String KEY_TRANSMISSTIONS = "transmissions";

	// key to get data from JsonObject
	/*
	 * { "selected" : false, "text" : "Tp. Đà Nẵng", "value" : 7
	 */
	public static final String TAG_ID = "value";
	public static final String TAG_TEXT = "text";

	// ---------------------------------------
	private ProgressBar progressBar;
	CheckBox checkBoxDriver;
	Spinner spinnerNumberSeat;
	Spinner spinnerFromPrice;
	static Spinner spinnerToPrice;
	Spinner spinnerFromCity;
	Spinner spinnerToCity;
	static Spinner spinnerFromDistric;

	static Spinner spinnerToDictric;
	Spinner spinnerBrandCar;
	Spinner spinnerTypeCar;
	Spinner spinnerloaiSo;
	Spinner spinnerOwnCar;

	static Button btnFromDate;
	static Button btnToDate;
	static Button btnFromHour;
	static Button btnToHour;

	Button btnPostNews;
	Button btnSearch;

	LinearLayout layoutPlaceTo;
	LinearLayout layoutMore;
	LinearLayout btnLayoutMore;

	EditText edtNote;

	// local variables to store values
	protected static String fromDate = "30/12/1992";
	protected static String toDate = "30/12/1992";
	protected static String fromTime;
	protected static String toTime;
	public static int flagTimeDateChoose = 0;

	// list data of spinner
	HashMap<String, JSONObject> hashMapCities = new HashMap<String, JSONObject>();
	String[] cities = null;
	HashMap<String, JSONObject> hashMapDistrics = new HashMap<String, JSONObject>();
	String[] districs = null;
	HashMap<String, JSONObject> hashMapBrand = new HashMap<String, JSONObject>();
	HashMap<String, JSONObject> hashMapModel = new HashMap<String, JSONObject>();
	HashMap<String, JSONObject> hashMapSeat = new HashMap<String, JSONObject>();
	HashMap<String, JSONObject> hashMapTransmission = new HashMap<String, JSONObject>();
	HashMap<String, JSONObject> hashMapCarOwner = new HashMap<String, JSONObject>();
	HashMap<String, JSONObject> hashMapPriceFrom = new HashMap<String, JSONObject>();
	String[] pricesFrom = null;
	HashMap<String, JSONObject> hashMapPriceTo = new HashMap<String, JSONObject>();
	String[] pricesTo = null;

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
		// init components and setOnClickListener
		findViewById();
		getDataFromServer();

	}

	private void cookDataFromJson() {
		ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, cities);
		citiesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFromCity.setAdapter(citiesAdapter);

		// price from
		ArrayAdapter<String> priceFromAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, pricesFrom);
		priceFromAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFromPrice.setAdapter(priceFromAdapter);

		// price To
		ArrayAdapter<String> priceToAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, pricesTo);
		priceToAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerToPrice.setAdapter(priceToAdapter);

	}

	private void getDataFromServer() {
		ServiceGetAdvanceSearchConditions serviceGetAdvanceSearchConditions = new ServiceGetAdvanceSearchConditions();
		serviceGetAdvanceSearchConditions.getAdvanceSearchConditions();
		serviceGetAdvanceSearchConditions
				.addOnGetJsonListener(new OnGetJsonListener() {

					@Override
					public void onGetJsonFail(String response) {
						Toast.makeText(getActivity(), "Cannot connect service",
								0).show();

					}

					@Override
					public void onGetJsonCompleted(String response) {
						try {
							JSONObject responseJson = new JSONObject(response);
							if (responseJson.getBoolean("status")) {
								JSONObject data = responseJson
										.getJSONObject("data");

								JSONArray citiesJsonArray = data
										.getJSONArray(KEY_CITIES);
								cities = new String[citiesJsonArray.length()];

								JSONArray driverJsonArray = data
										.getJSONArray(KEY_DRIVER);

								JSONArray priceFromJsonArray = data
										.getJSONArray(KEY_FROM_PRICES);
								pricesFrom = new String[priceFromJsonArray
										.length()];

								JSONArray priceToJsonArray = data
										.getJSONArray(KEY_TO_PRICES);
								pricesTo = new String[priceToJsonArray.length()];

								JSONArray brandJsonArray = data
										.getJSONArray(KEY_BRANDS);

								JSONArray seatJsonArray = data
										.getJSONArray(KEY_SEAT);

								// fill to hashMap

								for (int i = 0; i < citiesJsonArray.length(); i++) {
									JSONObject c = citiesJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c.getString(TAG_TEXT);
									cities[i] = text;
									hashMapCities.put(text, c);
								}
								// from price

								for (int i = 0; i < priceFromJsonArray.length(); i++) {
									JSONObject c = priceFromJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c.getString(TAG_TEXT);
									pricesFrom[i] = text;
									hashMapPriceFrom.put(text, c);
								}
								// to price

								for (int i = 0; i < priceToJsonArray.length(); i++) {
									JSONObject c = priceToJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c.getString(TAG_TEXT);
									pricesTo[i] = text;
									hashMapPriceTo.put(text, c);
								}

							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cookDataFromJson();
					}
				});

		/*
		 * String[] Months = new String[] { "Data1", "Data2" };
		 * ArrayAdapter<String> adapter = new
		 * ArrayAdapter<String>(getActivity(),
		 * android.R.layout.simple_spinner_item, Months);
		 * adapter.setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * spinnerBrandCar.setAdapter(adapter);
		 * spinnerFromCity.setAdapter(adapter);
		 * spinnerFromDistric.setAdapter(adapter);
		 * spinnerFromPrice.setAdapter(adapter);
		 * spinnerloaiSo.setAdapter(adapter);
		 * spinnerNumberSeat.setAdapter(adapter);
		 * spinnerOwnCar.setAdapter(adapter); spinnerToCity.setAdapter(adapter);
		 * spinnerToDictric.setAdapter(adapter);
		 * spinnerToPrice.setAdapter(adapter);
		 * spinnerTypeCar.setAdapter(adapter);
		 */

	}

	public void findViewById() {
		// show edittext if screen is stading at Post NewsFeed Screen
		edtNote = (EditText) getView().findViewById(R.id.edtNote);
		if (ConfigureData.isPostNewsScreen) {
			edtNote.setVisibility(View.VISIBLE);
		}
		layoutPlaceTo = (LinearLayout) getView().findViewById(
				R.id.layoutPlaceTo);
		layoutMore = (LinearLayout) getView().findViewById(R.id.layoutMore);

		btnLayoutMore = (LinearLayout) getView().findViewById(
				R.id.btnLayoutMore);
		btnLayoutMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (layoutMore.isShown()) {
					layoutMore.setVisibility(View.GONE);
				} else {
					layoutMore.setVisibility(View.VISIBLE);
				}
			}
		});

		checkBoxDriver = (CheckBox) getView().findViewById(R.id.checkBoxDriver);
		btnSearch = (Button) getView().findViewById(R.id.btnSearch);
		btnPostNews = (Button) getView().findViewById(R.id.btnPostNews);
		btnFromDate = (Button) getView().findViewById(R.id.btnFromDate);
		btnFromHour = (Button) getView().findViewById(R.id.btnFromHour);
		btnToHour = (Button) getView().findViewById(R.id.btnToHour);
		btnToDate = (Button) getView().findViewById(R.id.btnToDate);

		// Spinner
		spinnerBrandCar = (Spinner) getView()
				.findViewById(R.id.spinnerBrandCar);
		spinnerFromCity = (Spinner) getView()
				.findViewById(R.id.spinnerFromCity);
		spinnerFromDistric = (Spinner) getView().findViewById(
				R.id.spinnerFromDistric);
		spinnerFromPrice = (Spinner) getView().findViewById(
				R.id.spinnerFromPrice);
		spinnerloaiSo = (Spinner) getView().findViewById(R.id.spinnerLoaiSo);
		spinnerNumberSeat = (Spinner) getView().findViewById(
				R.id.spinnerNumberSeat);
		spinnerOwnCar = (Spinner) getView().findViewById(R.id.spinnerOwnCar);
		spinnerToCity = (Spinner) getView().findViewById(R.id.spinnerToCity);
		spinnerToDictric = (Spinner) getView().findViewById(
				R.id.spinnerToDistric);
		spinnerToPrice = (Spinner) getView().findViewById(R.id.spinnerToPrice);
		spinnerTypeCar = (Spinner) getView().findViewById(R.id.spinnerTypeCar);

		// set onClick Listioner
		btnSearch.setOnClickListener(this);
		btnPostNews.setOnClickListener(this);

		btnFromDate.setOnClickListener(this);
		btnToDate.setOnClickListener(this);
		btnFromHour.setOnClickListener(this);
		btnToHour.setOnClickListener(this);
		// spinner
		spinnerBrandCar.setOnItemSelectedListener(this);
		spinnerFromCity.setOnItemSelectedListener(this);
		spinnerFromDistric.setOnItemSelectedListener(this);
		spinnerFromPrice.setOnItemSelectedListener(this);
		spinnerloaiSo.setOnItemSelectedListener(this);
		spinnerNumberSeat.setOnItemSelectedListener(this);
		spinnerOwnCar.setOnItemSelectedListener(this);
		spinnerToCity.setOnItemSelectedListener(this);
		spinnerToDictric.setOnItemSelectedListener(this);
		spinnerToPrice.setOnItemSelectedListener(this);
		spinnerTypeCar.setOnItemSelectedListener(this);

		// checkbox Driver
		checkBoxDriver
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean flag) {
						if (flag) {
							spinnerloaiSo.setVisibility(View.GONE);
							layoutPlaceTo.setVisibility(View.VISIBLE);

						} else {
							layoutPlaceTo.setVisibility(View.GONE);
							spinnerloaiSo.setVisibility(View.VISIBLE);
						}
					}
				});

	}

	/**
	 * Call when click button
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnSearch:
			Fragment newContent = new ResultSeachFragmemt();
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content_frame, newContent);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// add to back track
			ft.addToBackStack("Fragment Search");
			ft.commit();

			break;
		case R.id.btnPostNews:
			if (!ConfigureData.isPostNewsScreen) {
				ConfigureData.isPostNewsScreen = true;
				edtNote.setVisibility(View.VISIBLE);
				TextView txtNothing = (TextView) getView().findViewById(
						R.id.txtNothing);
				txtNothing.setVisibility(View.GONE);
				btnSearch.setVisibility(View.GONE);
				layoutMore.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(ConfigureData.activityMain, "Post Success", 0)
						.show();
				// export JSon search new and send to serice here

			}

			break;
		case R.id.btnFromHour:
			flagTimeDateChoose = 2;
			showTimePickerDialog(btnFromHour);

			break;

		case R.id.btnToHour:
			flagTimeDateChoose = 4;
			showTimePickerDialog(btnToHour);

			break;

		case R.id.btnFromDate:
			flagTimeDateChoose = 1;
			showDatePickerDialog(btnFromDate);

			break;

		case R.id.btnToDate:
			flagTimeDateChoose = 3;
			showDatePickerDialog(btnToDate);

			break;
		default:
			break;
		}

	}

	// ======================================================
	// DATE AND TIME PICKER
	// ======================================================

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			int rightMonth = month + 1;
			if (flagTimeDateChoose == 1) {
				fromDate = day + "/" + rightMonth + "/" + year;
				btnFromDate.setText(fromDate);

			} else {
				toDate = day + "/" + rightMonth + "/" + year;
				try {
					if (!checkValidDate(fromDate, toDate)) {
						Toast.makeText(ConfigureData.activityMain,
								"Ngày trả xe phải sau ngày thuê xe", 0).show();
						toDate = fromDate;
						btnToDate.setText(toDate);
					} else {
						btnToDate.setText(toDate);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	public static boolean checkValidDate(String d1, String d2)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		Date date1 = sdf.parse(d1);
		Date date2 = sdf.parse(d2);
		if (date2.after(date1)) {
			return true;
		}
		return false;

	}

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = 00;

			return new CustomTimePickerDialog(getActivity(), this, hour,
					minute, DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			// TODO Auto-generated method stub
			super.onDismiss(dialog);

		}

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

			if (flagTimeDateChoose == 2) {
				fromTime = hour + " : " + minutes;
				btnFromHour.setText(fromTime);
			} else {
				toTime = hour + " : " + minutes;
				btnToHour.setText(toTime);
			}
		}

	}

	/**
	 * open fragment dialog in order to choose date time
	 */
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();

		newFragment.show(getActivity().getSupportFragmentManager(),
				"timePicker");
		Log.d("Ã¡d", "Ã¡d");

	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getActivity().getSupportFragmentManager(),
				"datePicker");
		Log.d("Ã¡d", "Ã¡d");
	}

	/**
	 * call when item on spinner selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		switch (parent.getId()) {
		case R.id.spinnerBrandCar:

			break;
		case R.id.spinnerFromCity:
			if (pos != 0) {
				spinnerFromDistric.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.spinnerFromDistric:

			break;
		case R.id.spinnerFromPrice:
			if (pos != 0) {
				spinnerToPrice.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.spinnerLoaiSo:

			break;
		case R.id.spinnerNumberSeat:

			break;
		case R.id.spinnerOwnCar:

			break;
		case R.id.spinnerToCity:
			if (pos != 0) {
				spinnerToDictric.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.spinnerToDistric:

			break;
		case R.id.spinnerToPrice:

			break;
		case R.id.spinnerTypeCar:

			break;

		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		ConfigureData.isPostNewsScreen = false;
		super.onDestroy();
	}
}
