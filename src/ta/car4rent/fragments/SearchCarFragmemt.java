package ta.car4rent.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.CustomTimePickerDialog;
import ta.car4rent.utils.KEY_JSON;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceGetAdvanceSearchConditions;
import ta.car4rent.webservices.ServiceGetDistrics;
import ta.car4rent.webservices.ServiceGetModels;
import ta.car4rent.webservices.ServiceSearch;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SearchCarFragmemt extends Fragment implements OnClickListener,
		OnItemSelectedListener {
	// ======================================
	// VARIABLES AND STATIC FLAGS
	// ======================================

	// ---------------------------------------
	private ProgressBar progressBar;
	CheckBox checkBoxDriver;
	Spinner spinnerNumberSeat;
	Spinner spinnerFromPrice;
	static Spinner spinnerToPrice;
	Spinner spinnerFromCity;
	Spinner spinnerToCity;
	static Spinner spinnerFromDistric;

	static Spinner spinnerToDistric;
	Spinner spinnerBrandCar;
	Spinner spinnerModelCar;
	Spinner spinnerTransmission;
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

	// ==========================================
	// VARIABLES TO STORE POST VALUES
	// ==========================================
	int post_hasDriver = 0;
	int post_id_FromCity = 0;
	int post_id_FromDistric = 0;
	int post_id_ToCity = 0;
	int post_id_ToDistric = 0;
	int post_id_Brand = 0;
	int post_id_Model = 0;
	int post_id_FromPrice = 0;
	int post_id_ToPrice = 0;
	int post_id_Transmission = 0;
	int post_id_numberSeat = 0;
	int post_id_ownerCar = 0;
	// local variables to store values
	protected static String post_fromDate = "30/12/1992";
	protected static String post_toDate = "30/12/1992";
	protected static String fromTime = "20 : 20";
	protected static String toTime = "20 : 20";
	public static int flagTimeDateChoose = 0;
	// other
	int value_fromPrice = 0;

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

		// check data exit?
		if (ConfigureData.hashMapCities.isEmpty()) {
			ConfigureData.isNeedToReload = true;
		} else {
			ConfigureData.isNeedToReload = false;
			progressBar.setVisibility(View.GONE);
			FillDataToComponent();

		}
		if (ConfigureData.isNeedToReload) {
			getDataFromServer();
		}

	}

	private void FillDataToComponent() {
		// cities
		ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, ConfigureData.cities);
		citiesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFromCity.setAdapter(citiesAdapter);
		spinnerToCity.setAdapter(citiesAdapter);

		// price from
		ArrayAdapter<String> priceFromAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, ConfigureData.pricesFrom);
		priceFromAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFromPrice.setAdapter(priceFromAdapter);

		// price To
		ArrayAdapter<String> priceToAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, ConfigureData.pricesTo);
		priceToAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerToPrice.setAdapter(priceToAdapter);

		// seat
		ArrayAdapter<String> seatsAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, ConfigureData.seats);
		seatsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNumberSeat.setAdapter(seatsAdapter);

		// brand
		ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, ConfigureData.brands);
		brandAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerBrandCar.setAdapter(brandAdapter);

		// transmission
		ArrayAdapter<String> transmissionAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item,
				ConfigureData.transmissions);
		transmissionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTransmission.setAdapter(transmissionAdapter);

		// own car
		ArrayAdapter<String> carOwnerAdapter = new ArrayAdapter<String>(
				ConfigureData.activityMain,
				android.R.layout.simple_spinner_item, ConfigureData.carOwners);
		carOwnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerOwnCar.setAdapter(carOwnerAdapter);

	}

	private void getDataFromServer() {
		ServiceGetAdvanceSearchConditions serviceGetAdvanceSearchConditions = new ServiceGetAdvanceSearchConditions();
		serviceGetAdvanceSearchConditions.getAdvanceSearchConditions();
		serviceGetAdvanceSearchConditions
				.addOnGetJsonListener(new OnGetJsonListener() {

					@Override
					public void onGetJsonFail(String response) {

					}

					@Override
					public void onGetJsonCompleted(String response) {
						try {
							JSONObject responseJson = new JSONObject(response);
							if (responseJson.getBoolean("status")) {
								JSONObject data = responseJson
										.getJSONObject("data");

								JSONArray citiesJsonArray = data
										.getJSONArray(KEY_JSON.KEY_CITIES);
								ConfigureData.cities = new String[citiesJsonArray
										.length()];

								JSONArray priceFromJsonArray = data
										.getJSONArray(KEY_JSON.KEY_FROM_PRICES);
								ConfigureData.pricesFrom = new String[priceFromJsonArray
										.length()];

								JSONArray priceToJsonArray = data
										.getJSONArray(KEY_JSON.KEY_TO_PRICES);
								ConfigureData.pricesTo = new String[priceToJsonArray
										.length()];

								JSONArray brandJsonArray = data
										.getJSONArray(KEY_JSON.KEY_BRANDS);
								ConfigureData.brands = new String[brandJsonArray
										.length()];

								JSONArray seatJsonArray = data
										.getJSONArray(KEY_JSON.KEY_SEAT);
								ConfigureData.seats = new String[seatJsonArray
										.length()];

								JSONArray carOwnerJsonArray = data
										.getJSONArray(KEY_JSON.KEY_CAR_OWNER);
								ConfigureData.carOwners = new String[carOwnerJsonArray
										.length()];

								JSONArray transmissionsJsonArray = data
										.getJSONArray(KEY_JSON.KEY_TRANSMISSTIONS);
								ConfigureData.transmissions = new String[transmissionsJsonArray
										.length()];

								// cities
								for (int i = 0; i < citiesJsonArray.length(); i++) {
									JSONObject c = citiesJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c
											.getString(KEY_JSON.TAG_TEXT);
									ConfigureData.cities[i] = text;
									ConfigureData.hashMapCities.put(text, c);
								}
								// from price

								for (int i = 0; i < priceFromJsonArray.length(); i++) {
									JSONObject c = priceFromJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c
											.getString(KEY_JSON.TAG_TEXT);
									ConfigureData.pricesFrom[i] = text;
									ConfigureData.hashMapPriceFrom.put(text, c);
								}
								// to price

								for (int i = 0; i < priceToJsonArray.length(); i++) {
									JSONObject c = priceToJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c
											.getString(KEY_JSON.TAG_TEXT);
									ConfigureData.pricesTo[i] = text;
									ConfigureData.hashMapPriceTo.put(text, c);
								}

								// seat

								for (int i = 0; i < seatJsonArray.length(); i++) {
									JSONObject c = seatJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c
											.getString(KEY_JSON.TAG_TEXT);
									ConfigureData.seats[i] = text;
									ConfigureData.hashMapSeat.put(text, c);
								}

								// brand
								for (int i = 0; i < brandJsonArray.length(); i++) {
									JSONObject c = brandJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c
											.getString(KEY_JSON.TAG_TEXT);
									ConfigureData.brands[i] = text;
									ConfigureData.hashMapBrand.put(text, c);
								}

								// car owner
								for (int i = 0; i < carOwnerJsonArray.length(); i++) {
									JSONObject c = carOwnerJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c
											.getString(KEY_JSON.TAG_TEXT);
									ConfigureData.carOwners[i] = text;
									ConfigureData.hashMapCarOwner.put(text, c);
								}

								// transmission
								for (int i = 0; i < transmissionsJsonArray
										.length(); i++) {
									JSONObject c = transmissionsJsonArray
											.getJSONObject(i);

									// Storing each json item in variable
									String text = c
											.getString(KEY_JSON.TAG_TEXT);
									ConfigureData.transmissions[i] = text;
									ConfigureData.hashMapTransmission.put(text,
											c);
								}
								// load main key done
								progressBar.setVisibility(View.GONE);
							}

						} catch (JSONException e) {

							e.printStackTrace();
						}
						FillDataToComponent();
					}
				});

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
		spinnerTransmission = (Spinner) getView().findViewById(
				R.id.spinnerTransmission);
		spinnerNumberSeat = (Spinner) getView().findViewById(
				R.id.spinnerNumberSeat);
		spinnerOwnCar = (Spinner) getView().findViewById(R.id.spinnerOwnCar);
		spinnerToCity = (Spinner) getView().findViewById(R.id.spinnerToCity);
		spinnerToDistric = (Spinner) getView().findViewById(
				R.id.spinnerToDistric);
		spinnerToPrice = (Spinner) getView().findViewById(R.id.spinnerToPrice);
		spinnerModelCar = (Spinner) getView()
				.findViewById(R.id.spinnerModelCar);

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
		spinnerTransmission.setOnItemSelectedListener(this);
		spinnerNumberSeat.setOnItemSelectedListener(this);
		spinnerOwnCar.setOnItemSelectedListener(this);
		spinnerToCity.setOnItemSelectedListener(this);
		spinnerToDistric.setOnItemSelectedListener(this);
		spinnerToPrice.setOnItemSelectedListener(this);
		spinnerModelCar.setOnItemSelectedListener(this);

		// checkbox Driver
		checkBoxDriver
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean flag) {
						if (flag) {
							post_hasDriver = 1;
							spinnerTransmission.setVisibility(View.GONE);
							layoutPlaceTo.setVisibility(View.VISIBLE);

						} else {
							post_hasDriver = 0;
							layoutPlaceTo.setVisibility(View.GONE);
							spinnerTransmission.setVisibility(View.VISIBLE);
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
			progressBar.setVisibility(View.VISIBLE);
			
			Fragment newContent = new ResultSeachFragmemt();
			FragmentManager fm = getActivity().getSupportFragmentManager();
			final FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content_frame, newContent);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// add to back track
			ft.addToBackStack("Fragment Search");
			// ft.commit();

			JSONObject jsonSearchObject = exportJsonObjectSearch();

			if (jsonSearchObject != null) {
				ServiceSearch serviceSearch = new ServiceSearch();
				serviceSearch.advanceSearch(jsonSearchObject);
				serviceSearch.addOnPostJsonListener(new OnPostJsonListener() {

					@Override
					public void onPostJsonFail(String response) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPostJsonCompleted(String response) {
						try {
							ConfigureData.jsonSearchResult = new JSONObject(response);
							if (ConfigureData.jsonSearchResult.getBoolean("status")) {
								// save data and swich fragment to result
								progressBar.setVisibility(View.GONE);
								ft.commit();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
			}

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
				StaticFunction.hideKeyboard();
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

	public JSONObject exportJsonObjectSearch() {
		JSONObject jsonSearchObject = new JSONObject();
		try {
			jsonSearchObject.put(KEY_JSON.KEY_POST_BRAND_ID, post_id_Brand);
			jsonSearchObject.put(KEY_JSON.KEY_POST_CAROWNER_ID,
					post_id_ownerCar);
			jsonSearchObject.put(KEY_JSON.KEY_POST_CITY_ID,
					post_id_FromCity);
			jsonSearchObject.put(KEY_JSON.KEY_POST_CITY_ID_FROM,
					post_id_FromCity);
			jsonSearchObject.put(KEY_JSON.KEY_POST_CITY_ID_TO,
					post_id_ToCity);
			jsonSearchObject.put(KEY_JSON.KEY_POST_DISTRIC_ID,
					post_id_FromDistric);
			jsonSearchObject.put(KEY_JSON.KEY_POST_DISTRIC_ID_FROM,
					post_id_FromDistric);
			jsonSearchObject.put(KEY_JSON.KEY_POST_DISTRIC_ID_TO,
					post_id_ToDistric);
			jsonSearchObject.put(KEY_JSON.KEY_POST_END_DATE, post_toDate
					+ "-" + toTime);
			jsonSearchObject.put(KEY_JSON.KEY_POST_FROM_PRICE,
					post_id_FromPrice);
			jsonSearchObject.put(KEY_JSON.KEY_POST_HAS_DRIVER,
					post_hasDriver);
			jsonSearchObject.put(KEY_JSON.KEY_POST_MODEL_ID, post_id_Model);
			jsonSearchObject.put(KEY_JSON.KEY_POST_SEAT_ID,
					post_id_numberSeat);
			jsonSearchObject.put(KEY_JSON.KEY_POST_STAR_TDATE,
					post_fromDate + "-" + fromTime);
			jsonSearchObject.put(KEY_JSON.KEY_POST_TO_PRICE,
					post_id_ToPrice);
			jsonSearchObject.put(KEY_JSON.KEY_POST_TRANSMISSION_ID,
					post_id_Transmission);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonSearchObject;
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
				post_fromDate = day + "/" + rightMonth + "/" + year;
				btnFromDate.setText(post_fromDate);

			} else {
				post_toDate = day + "/" + rightMonth + "/" + year;
				try {
					if (!checkValidDate(post_fromDate, post_toDate)) {
						Toast.makeText(ConfigureData.activityMain,
								"Ngày trả xe phải sau ngày thuê xe", 0).show();
						post_toDate = post_fromDate;
						btnToDate.setText(post_toDate);
					} else {
						btnToDate.setText(post_toDate);
					}
				} catch (ParseException e) {

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

			if (pos != 0) {
				try {
					post_id_Brand = ConfigureData.hashMapBrand.get(
							ConfigureData.brands[pos]).getInt(KEY_JSON.TAG_ID);
					ServiceGetModels serviceGetModels = new ServiceGetModels();
					serviceGetModels.getModels(post_id_Brand);
					serviceGetModels
							.addOnGetJsonListener(new OnGetJsonListener() {

								@Override
								public void onGetJsonFail(String response) {

								}

								@Override
								public void onGetJsonCompleted(String response) {
									JSONObject responseJson;
									try {
										responseJson = new JSONObject(response);
										if (responseJson.getBoolean("status")) {
											JSONArray jsonArrayModels = responseJson
													.getJSONArray("data");
											ConfigureData.models = new String[jsonArrayModels
													.length()];
											// fill to hashMap
											for (int i = 0; i < jsonArrayModels
													.length(); i++) {
												JSONObject c = jsonArrayModels
														.getJSONObject(i);

												// Storing each json item in
												// variable
												String text = c
														.getString(KEY_JSON.TAG_TEXT);
												ConfigureData.models[i] = text;
												ConfigureData.hashMapModel.put(
														text, c);
											}
										}

									} catch (JSONException e) {

										e.printStackTrace();
									}

									if (ConfigureData.hashMapModel.size() > 1) {
										spinnerModelCar
												.setVisibility(View.VISIBLE);
										// own car
										ArrayAdapter<String> ModelsAdapter = new ArrayAdapter<String>(
												ConfigureData.activityMain,
												android.R.layout.simple_spinner_item,
												ConfigureData.models);
										ModelsAdapter
												.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										spinnerModelCar
												.setAdapter(ModelsAdapter);
									}
								}
							});

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			break;
		case R.id.spinnerFromCity:
			if (pos != 0) {
				try {
					post_id_FromCity = ConfigureData.hashMapCities.get(
							ConfigureData.cities[pos]).getInt(KEY_JSON.TAG_ID);
					ServiceGetDistrics serviceGetDistrics = new ServiceGetDistrics();
					serviceGetDistrics.getDistrics(post_id_FromCity);
					serviceGetDistrics
							.addOnGetJsonListener(new OnGetJsonListener() {

								@Override
								public void onGetJsonFail(String response) {

								}

								@Override
								public void onGetJsonCompleted(String response) {
									JSONObject responseJson;
									try {
										responseJson = new JSONObject(response);
										if (responseJson.getBoolean("status")) {
											JSONArray jsonArrayDistrics = responseJson
													.getJSONArray("data");
											ConfigureData.districsFrom = new String[jsonArrayDistrics
													.length()];
											// fill to hashMap
											for (int i = 0; i < jsonArrayDistrics
													.length(); i++) {
												JSONObject c = jsonArrayDistrics
														.getJSONObject(i);

												// Storing each json item in
												// variable
												String text = c
														.getString(KEY_JSON.TAG_NAME_DISTRICS);
												ConfigureData.districsFrom[i] = text;
												ConfigureData.hashMapDistricsFrom
														.put(text, c);
											}
										}

									} catch (JSONException e) {

										e.printStackTrace();
									}

									if (ConfigureData.hashMapDistricsFrom
											.size() > 1) {
										spinnerFromDistric
												.setVisibility(View.VISIBLE);
										// own car
										ArrayAdapter<String> DistricsFromAdapter = new ArrayAdapter<String>(
												ConfigureData.activityMain,
												android.R.layout.simple_spinner_item,
												ConfigureData.districsFrom);
										DistricsFromAdapter
												.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										spinnerFromDistric
												.setAdapter(DistricsFromAdapter);
									}
								}
							});

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			break;
		case R.id.spinnerFromDistric:
			if (pos != 0) {
				try {
					post_id_FromDistric = ConfigureData.hashMapDistricsFrom
							.get(ConfigureData.districsFrom[pos]).getInt(
									KEY_JSON.TAG_ID_DISTRICS);
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			break;
		case R.id.spinnerFromPrice:
			if (pos != 0) {
				spinnerToPrice.setVisibility(View.VISIBLE);
				try {
					post_id_FromPrice = ConfigureData.hashMapPriceFrom.get(
							ConfigureData.pricesFrom[pos]).getInt(
							KEY_JSON.TAG_ID);

				} catch (JSONException e) {

					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			break;
		case R.id.spinnerTransmission:
			if (pos != 0) {
				try {
					post_id_Transmission = ConfigureData.hashMapTransmission
							.get(ConfigureData.transmissions[pos]).getInt(
									KEY_JSON.TAG_ID);
				} catch (JSONException e1) {
					//
					e1.printStackTrace();
				}
			}

			break;
		case R.id.spinnerNumberSeat:
			if (pos != 0) {
				try {
					post_id_numberSeat = ConfigureData.hashMapSeat.get(
							ConfigureData.seats[pos]).getInt(KEY_JSON.TAG_ID);
				} catch (JSONException e1) {
					//
					e1.printStackTrace();
				}
			}

			break;
		case R.id.spinnerOwnCar:
			if (pos != 0) {
				try {
					post_id_ownerCar = ConfigureData.hashMapCarOwner.get(
							ConfigureData.carOwners[pos]).getInt(
							KEY_JSON.TAG_ID);
				} catch (JSONException e1) {

					e1.printStackTrace();
				}
			}
			break;
		case R.id.spinnerToCity:
			try {
				post_id_ToCity = ConfigureData.hashMapCities.get(
						ConfigureData.cities[pos]).getInt(KEY_JSON.TAG_ID);
				ServiceGetDistrics serviceGetDistrics = new ServiceGetDistrics();
				serviceGetDistrics.getDistrics(post_id_ToCity);
				serviceGetDistrics
						.addOnGetJsonListener(new OnGetJsonListener() {

							@Override
							public void onGetJsonFail(String response) {

							}

							@Override
							public void onGetJsonCompleted(String response) {
								JSONObject responseJson;
								try {
									responseJson = new JSONObject(response);
									if (responseJson.getBoolean("status")) {
										JSONArray jsonArrayDistrics = responseJson
												.getJSONArray("data");
										ConfigureData.districsTo = new String[jsonArrayDistrics
												.length()];
										// fill to hashMap
										for (int i = 0; i < jsonArrayDistrics
												.length(); i++) {
											JSONObject c = jsonArrayDistrics
													.getJSONObject(i);

											// Storing each json item in
											// variable
											String text = c
													.getString(KEY_JSON.TAG_NAME_DISTRICS);
											ConfigureData.districsTo[i] = text;
											ConfigureData.hashMapDistricsTo
													.put(text, c);
										}
									}

								} catch (JSONException e) {
									//
									e.printStackTrace();
								}

								if (ConfigureData.hashMapDistricsTo.size() > 1) {
									spinnerToDistric
											.setVisibility(View.VISIBLE);
									// own car
									ArrayAdapter<String> DistricsToAdapter = new ArrayAdapter<String>(
											ConfigureData.activityMain,
											android.R.layout.simple_spinner_item,
											ConfigureData.districsTo);
									DistricsToAdapter
											.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
									spinnerToDistric
											.setAdapter(DistricsToAdapter);
								}
							}
						});

			} catch (JSONException e) {
				//
				e.printStackTrace();
			}

			break;
		case R.id.spinnerToDistric:
			if (pos != 0) {
				try {
					post_id_ToDistric = ConfigureData.hashMapDistricsTo.get(
							ConfigureData.districsTo[pos]).getInt(
							KEY_JSON.TAG_ID_DISTRICS);
				} catch (JSONException e) {
					//
					e.printStackTrace();
				}
			}
			break;
		case R.id.spinnerToPrice:
			if (pos != 0) {
				try {

					post_id_ToPrice = ConfigureData.hashMapPriceTo.get(
							ConfigureData.pricesTo[pos])
							.getInt(KEY_JSON.TAG_ID);
					if (post_id_ToPrice < post_id_FromPrice) {
						Toast.makeText(ConfigureData.activityMain,
								getString(R.string.gia_sai), 0).show();
						spinnerToPrice.setSelection(0);
					}

				} catch (JSONException e) {
					//
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			break;
		case R.id.spinnerModelCar:
			if (pos != 0) {
				try {
					post_id_Model = ConfigureData.hashMapModel.get(
							ConfigureData.models[pos]).getInt(KEY_JSON.TAG_ID);
				} catch (JSONException e1) {
					//
					e1.printStackTrace();
				}
			}
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
}
