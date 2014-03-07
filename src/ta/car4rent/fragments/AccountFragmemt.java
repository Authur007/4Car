package ta.car4rent.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.adapters.IconCarAdapter;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.OnUploadBitmapListener;
import ta.car4rent.webservices.ServiceGetCities;
import ta.car4rent.webservices.ServiceGetDistricts;
import ta.car4rent.webservices.ServiceGetUserInfo;
import ta.car4rent.webservices.ServiceUpdateUserInfo;
import ta.car4rent.webservices.ServiceUploadUserAvatar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;

public class AccountFragmemt extends Fragment implements OnClickListener,
		android.content.DialogInterface.OnClickListener,
		OnUploadBitmapListener, OnItemSelectedListener {

	private ImageView ivAvatar;
	private ImageView iconCar;
	private ProgressBar progressBar;
	private EditText etName;
	private EditText etEmail;
	private EditText etPhone;
	private EditText etAddress;
	private Button btnUpdateInfo;
	private Button btnChangePassword;
	private Boolean isUserAvatarChange;
	Button btnLogout;
	// facebook logout
	LoginButton btnFbLogout;

	// camera
	private static final int PICK_FROM_GALLERY = 101; // request code
	// request code for capture
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "4cars";
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Uri URI = null; // path of image
	private Bitmap bitmap_image;
	private int columnIndex;
	private String photoTakedPath;

	private Spinner spinnerCity;
	private ArrayList<String> mArrCityNames = new ArrayList<String>();
	private HashMap<Integer, String> mHashMapCityId; // Key is cityId
	private HashMap<String, Integer> mHashMapCityName; // Key is cityName
	private ArrayAdapter<String> citiesAdapter;

	private Spinner spinnerDistricts;
	private ArrayList<String> mArrDistrictsNames = new ArrayList<String>();
	private HashMap<Integer, String> mHashMapDistrictsId = new HashMap<Integer, String>();// Key
																							// is
																							// districtsId
	private HashMap<String, Integer> mHashMapDistrictsName = new HashMap<String, Integer>();// Key

	int arr_car_images[] = { R.drawable.xe1, R.drawable.xe2, R.drawable.xe3,
			R.drawable.xe4, R.drawable.xe5, R.drawable.xe6, R.drawable.xe7,
			R.drawable.xe8, R.drawable.xe9, R.drawable.xe10, R.drawable.xe11,
			R.drawable.xe12, R.drawable.xe13, R.drawable.xe14, R.drawable.xe15,
			R.drawable.xe16, R.drawable.xe17, R.drawable.xe18, R.drawable.xe19,
			R.drawable.xe20, R.drawable.xe21, R.drawable.xe22, R.drawable.xe23,
			R.drawable.xe24, R.drawable.xe25, R.drawable.xe26 }; // is
	// districtsName
	private ArrayAdapter<String> districtsAdapter;

	private int mCtityId;
	private int mDistrictId;

	public AccountFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainActivity.Instance.showActionFilterSpinner(false);
		
		isUserAvatarChange = false;
		ConfigureData.currentScreen = 2;

		View rootView = inflater.inflate(R.layout.fragment_account, container,
				false);

		ivAvatar = (ImageView) rootView.findViewById(R.id.ivAvatar);
		iconCar = (ImageView) rootView.findViewById(R.id.iconCar);

		etName = (EditText) rootView.findViewById(R.id.etName);
		etEmail = (EditText) rootView.findViewById(R.id.etEmail);
		etPhone = (EditText) rootView.findViewById(R.id.etPhone);
		spinnerCity = (Spinner) rootView.findViewById(R.id.spinnerCity);
		spinnerDistricts = (Spinner) rootView
				.findViewById(R.id.spinnerDistricts);
		etAddress = (EditText) rootView.findViewById(R.id.etAddress);
		btnUpdateInfo = (Button) rootView.findViewById(R.id.btnUpdateInfo);
		btnChangePassword = (Button) rootView
				.findViewById(R.id.btnChangePassword);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		btnFbLogout = (LoginButton) rootView.findViewById(R.id.btnLogoutFb);
		btnFbLogout.setFragment(this);
		btnLogout = (Button) rootView.findViewById(R.id.btnLogout);

		// Setting for update info button
		btnUpdateInfo.setOnClickListener(this);
		ivAvatar.setOnClickListener(this);
		btnChangePassword.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		iconCar.setOnClickListener(this);

		/**
		 * FILL DATA TO UI
		 */

		try {
			iconCar.setImageResource(ConfigureData.iconCarResId);
			
			if (ConfigureData.isUsingFBLoginButton) {
				btnChangePassword.setVisibility(View.GONE);
				btnFbLogout.setVisibility(View.VISIBLE);
				btnLogout.setVisibility(View.GONE);

				btnFbLogout
						.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

							@Override
							public void onUserInfoFetched(GraphUser user) {
								if (user == null) {
									// Reset account info to Logout
									resetDateLogout();

									// Move to account screen to login or
									// register
									// account
									LoginFragmemt loginFragmemt = new LoginFragmemt();

									// Insert the fragment by replacing any
									// existing
									// fragment
									FragmentManager fm = getActivity()
											.getSupportFragmentManager();
									fm.beginTransaction()
											.replace(R.id.content_frame,
													loginFragmemt).commit();
								}

							}
						});
			} else {
				btnLogout.setVisibility(View.VISIBLE);
				btnFbLogout.setVisibility(View.GONE);
			}
			ConfigureData.imageLoader
					.displayImage(ConfigureData.userAccount.getString("image"),
							ivAvatar, 100);

			etName.setText(ConfigureData.userAccount.getString("name"));
			etEmail.setText(ConfigureData.userAccount.getString("email"));
			etPhone.setText(ConfigureData.userAccount.getString("phone"));
			etAddress.setText(ConfigureData.userAccount.getString("address"));

		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		// get City list to show in spinner
		ServiceGetCities serviceGetCities = new ServiceGetCities();
		serviceGetCities.addOnGetJsonListener(new OnGetJsonListener() {

			@Override
			public void onGetJsonFail(String response) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetJsonCompleted(String response) {
				try {
					JSONObject responseJson = new JSONObject(response);
					if (responseJson.getBoolean("status")) {
						JSONArray citiesJSONArray = responseJson
								.getJSONArray("data");
						mArrCityNames = new ArrayList<String>();
						mHashMapCityId = new HashMap<Integer, String>();
						mHashMapCityName = new HashMap<String, Integer>();
						for (int i = 0; i < citiesJSONArray.length(); i++) {
							JSONObject cityObject = citiesJSONArray
									.getJSONObject(i);
							mHashMapCityId.put(cityObject.getInt("id"),
									cityObject.getString("name"));
							mHashMapCityName.put(cityObject.getString("name"),
									cityObject.getInt("id"));
							mArrCityNames.add(cityObject.getString("name"));
						}
						String[] arrCitiesName = new String[mArrCityNames
								.size()];
						mArrCityNames.toArray(arrCitiesName);
						citiesAdapter = new ArrayAdapter<String>(
								ConfigureData.activityMain,
								R.layout.spinner_item,
								arrCitiesName);
						citiesAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinnerCity.setAdapter(citiesAdapter);
						try{
							mCtityId = ConfigureData.userAccount.getInt("cityId");
						}catch (NullPointerException e) {
							e.printStackTrace();
							mCtityId = 1;
						}
						
						spinnerCity.setSelection(citiesAdapter
								.getPosition(mHashMapCityId.get(mCtityId)));
						citiesAdapter.notifyDataSetChanged();

						// Get District list and fill out the districts spinner
						ServiceGetDistricts serviceGetDistrics = new ServiceGetDistricts();
						serviceGetDistrics
								.addOnGetJsonListener(new OnGetJsonListener() {

									@Override
									public void onGetJsonFail(String response) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onGetJsonCompleted(
											String response) {
										// TODO Auto-generated method stub
										try {
											JSONObject responseJson = new JSONObject(
													response);
											if (responseJson
													.getBoolean("status")) {
												JSONArray districtsJSONArray = responseJson
														.getJSONArray("data");
												mArrDistrictsNames = new ArrayList<String>();
												mHashMapDistrictsId = new HashMap<Integer, String>();
												mHashMapDistrictsName = new HashMap<String, Integer>();
												for (int i = 0; i < districtsJSONArray
														.length(); i++) {
													JSONObject districtsObject = districtsJSONArray
															.getJSONObject(i);
													mHashMapDistrictsId.put(
															districtsObject
																	.getInt("id"),
															districtsObject
																	.getString("name"));
													mHashMapDistrictsName.put(
															districtsObject
																	.getString("name"),
															districtsObject
																	.getInt("id"));
													mArrDistrictsNames
															.add(districtsObject
																	.getString("name"));
												}

												String[] arrDistrictsName = new String[mArrDistrictsNames
														.size()];
												mArrDistrictsNames
														.toArray(arrDistrictsName);
												districtsAdapter = new ArrayAdapter<String>(
														ConfigureData.activityMain,
														R.layout.spinner_item,
														arrDistrictsName);
												districtsAdapter
														.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
												spinnerDistricts
														.setAdapter(districtsAdapter);
												mDistrictId = ConfigureData.userAccount
														.getInt("districtId");
												spinnerDistricts
														.setSelection(districtsAdapter
																.getPosition(mHashMapDistrictsId
																		.get(mDistrictId)));
												districtsAdapter
														.notifyDataSetChanged();

												spinnerCity
														.setOnItemSelectedListener(AccountFragmemt.this);
												spinnerDistricts
														.setOnItemSelectedListener(AccountFragmemt.this);
											}
										} catch (JSONException e) {

											e.printStackTrace();
										} catch (NullPointerException e) {
											e.printStackTrace();
										}

									}
								});

						serviceGetDistrics.getDistricts(mCtityId);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		});
		serviceGetCities.getCities();

		return rootView;
	}

	
	
	// ===========================================
	// POPUP WINDOW VIEW LIST CARS
	// ===========================================

	private ListPopupWindow showPopupListViewCars(Context mcon,
			IconCarAdapter adapter) {

		final ListPopupWindow pwindo = new ListPopupWindow(mcon);
		pwindo.setAnimationStyle(R.style.AnimationPopup);
		pwindo.setAdapter(adapter);
		/*Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;*/

		pwindo.setWidth(200);
		pwindo.setHeight(600);

		pwindo.setAnchorView(iconCar);

		pwindo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				iconCar.setImageResource(arr_car_images[pos]);
				try {

					ConfigureData.iconCarResId = arr_car_images[pos];
					ConfigureData.saveIconCarSharedPreference();

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				pwindo.dismiss();
			}
		});
		pwindo.show();
		return pwindo;
	}

	@Override
	public void onClick(View v) {
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();

		switch (v.getId()) {
		case R.id.iconCar:
			IconCarAdapter adapter = new IconCarAdapter(getActivity(),
					arr_car_images);
			showPopupListViewCars(getActivity(), adapter);
			break;

		case R.id.btnLogout:

			resetDateLogout();

			// Move to account screen to login or register account
			LoginFragmemt loginFragmemt = new LoginFragmemt();

			// Insert the fragment by replacing any existing
			// fragment
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, loginFragmemt).commit();

			break;
		case R.id.btnUpdateInfo:
			progressBar.setVisibility(View.VISIBLE);
			// upload new avatar image
			if (isUserAvatarChange) {
				ServiceUploadUserAvatar serviceUploadUserAvatar = new ServiceUploadUserAvatar();
				serviceUploadUserAvatar.addOnUploadBitmapListener(this);

				reloadUserAccount();
				progressBar.setVisibility(View.VISIBLE);

				try {
					ConfigureData.userId = ConfigureData.userAccount
							.getInt("id");
					serviceUploadUserAvatar.uploadUserAvatar(
							((BitmapDrawable) ivAvatar.getDrawable())
									.getBitmap(), ConfigureData.userId);
				} catch (JSONException e) {
					if (BuildConfig.DEBUG) {
						e.printStackTrace();
					}
				}

			}

			ServiceUpdateUserInfo serviceUpdateUserInfo = new ServiceUpdateUserInfo();
			serviceUpdateUserInfo
					.addOnPostJsonListener(new OnPostJsonListener() {

						@Override
						public void onPostJsonFail(String response) {
							// TODO Auto-generated method stub
							Toast.makeText(getActivity(),
									getString(R.string.str_error_conection),
									Toast.LENGTH_SHORT).show();

							progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onPostJsonCompleted(String response) {
							// TODO Auto-generated method stub
							reloadUserAccount();
							Log.e("UPDATE USER INFO RESPONESE", response);

							try {
								JSONObject responseJSON = new JSONObject(
										response);
								if (responseJSON.getBoolean("status")) {
									// Upload completed
									Toast.makeText(getActivity(),
											getString(R.string.successful),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(getActivity(),
											getString(R.string.failed),
											Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {

								e.printStackTrace();
							}

							progressBar.setVisibility(View.GONE);
						}

					});

			try {
				JSONObject userInfo = new JSONObject();
				userInfo.put("userId", ConfigureData.userAccount.getInt("id"));
				userInfo.put("userName", ConfigureData.userName);
				userInfo.put("password", ConfigureData.password);
				userInfo.put("name", etName.getText().toString());
				userInfo.put("email", etEmail.getText().toString());
				userInfo.put("phone", etPhone.getText().toString());
				userInfo.put("cityId", mCtityId);
				userInfo.put("districtId", mDistrictId);
				userInfo.put("address", etAddress.getText().toString());

				progressBar.setVisibility(View.VISIBLE);
				serviceUpdateUserInfo.updateUserInfo(userInfo);
			} catch (JSONException e) {

			}
			break;
		case R.id.ivAvatar:
			v.startAnimation(AnimationUtils.loadAnimation(getActivity(),
					R.animator.zoom_in));
			// open dialog for user select Take photo from camera or gallery
			AlertDialog dialog = createSelectTakePhotoModeDialog();
			dialog.show();

			break;
		case R.id.btnChangePassword:
			// Show Change Password Fragment
			ChangePasswordFragmemt changePasswordFragmemt = new ChangePasswordFragmemt();
			((MainActivity) getActivity())
					.setActionBarTitle(getString(R.string.label_change_password));

			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, changePasswordFragmemt).addToBackStack("ChangePass")
					.commit();

			break;

		}
	}

	public void resetDateLogout() {
		// Reset account info to Logout
		ConfigureData.userAccount = null;
		ConfigureData.isLogged = false;
		ConfigureData.token = null;
		ConfigureData.userName = null;
		ConfigureData.password = null;
		ConfigureData.graphUser = null;
	}

	public void reloadUserAccount() {
		try {
			ServiceGetUserInfo serviceGetUserInfo = new ServiceGetUserInfo();
			serviceGetUserInfo.addOnGetJsonListener(new OnGetJsonListener() {

				@Override
				public void onGetJsonCompleted(String response) {
					try {
						JSONObject object = new JSONObject(response);
						if (object.getBoolean("status")) {
							ConfigureData.userAccount = object
									.getJSONObject("user");
						}
					} catch (JSONException e) {
						if (BuildConfig.DEBUG) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onGetJsonFail(String response) {
				}
			});

			String userId = ConfigureData.userAccount.getString("id");
			serviceGetUserInfo.get(userId);
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public AlertDialog createSelectTakePhotoModeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.str_get_image_from));
		builder.setIcon(R.drawable.ic_launcher);

		builder.setPositiveButton("Gallery", this);
		builder.setNeutralButton("Camera", this);
		return builder.create();
	}

	// ==============================================
	// GET IMAGE FROM CAMERA OR GALLERY
	// ==============================================

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		return ConfigureData.activityMain.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA);
	}

	/*
	 * Capturing Camera Image will launch camera app request image capture
	 */
	public void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		URI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, URI);
		Toast.makeText(ConfigureData.activityMain,
				getString(R.string.rotation_camera), Toast.LENGTH_SHORT).show();
		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == PICK_FROM_GALLERY
					&& resultCode == Activity.RESULT_OK) {
				/**
				 * Get Path
				 */
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = ConfigureData.activityMain.getContentResolver()
						.query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				photoTakedPath = cursor.getString(columnIndex);
				Log.e("Attachment Path:", photoTakedPath);
				URI = Uri.parse("file://" + photoTakedPath);
				Log.e("URI Path:", URI.toString() + "");
				cursor.close();
			} else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
				if (resultCode == Activity.RESULT_OK) {
					// successfully captured the image
					// display it in image view
					try {

						BitmapFactory.Options bounds = new BitmapFactory.Options();
						bounds.inJustDecodeBounds = true;
						Bitmap bm = BitmapFactory.decodeFile(URI.getPath(),
								bounds);

						ExifInterface exif = new ExifInterface(new File(
								URI.toString()).getAbsolutePath());
						String orientString = exif
								.getAttribute(ExifInterface.TAG_ORIENTATION);
						int orientation = orientString != null ? Integer
								.parseInt(orientString)
								: ExifInterface.ORIENTATION_NORMAL;
						int rotationAngle = 0;
						if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
							rotationAngle = 90;
						if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
							rotationAngle = 180;
						if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
							rotationAngle = 270;

						Matrix matrix = new Matrix();

						matrix.setRotate(rotationAngle,
								(float) bm.getWidth() / 2,
								(float) bm.getHeight() / 2);
						Bitmap rotatedBitmap = Bitmap
								.createBitmap(bm, 0, 0, bounds.outWidth,
										bounds.outHeight, matrix, true);

						Log.e("bitmap size:", rotatedBitmap.getHeight()
								+ rotatedBitmap.getWidth() + "");
						Log.e("URI Path:", URI.toString() + "");
						// imgPreview.setImageBitmap(bitmap);
					} catch (NullPointerException e) {
						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}

			ivAvatar.setImageBitmap(ta.car4rent.utils.Utils
					.decodeSampledBitmapFromFile(URI.getPath(),
							ivAvatar.getWidth(), ivAvatar.getHeight()));

			isUserAvatarChange = true;
			ivAvatar.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	public void openGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.putExtra("return-data", true);
		startActivityForResult(
				Intent.createChooser(intent, "Complete action using"),
				PICK_FROM_GALLERY);

	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_POSITIVE) {
			openGallery();
		} else if (which == AlertDialog.BUTTON_NEUTRAL) {
			// Checking camera availability
			if (!isDeviceSupportCamera()) {
				Toast.makeText(ConfigureData.activityMain,
						getString(R.string.not_camera), Toast.LENGTH_LONG)
						.show();
				// will close the app if the device does't have camera
			} else {
				openCamera();
			}
		}

		// set image for preview

	}

	@Override
	public void onUploadImageBitmapCompleted(String response) {
		try {
			JSONObject responseJSON = new JSONObject(response);
			if (responseJSON.getBoolean("status")) {
				// Upload completed

				Toast.makeText(getActivity(), getString(R.string.successful),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(),
						getString(R.string.failed), Toast.LENGTH_SHORT)
						.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		progressBar.setVisibility(View.GONE);

	}

	@Override
	public void onUploadImageBitmapFail(String response) {
		// Toast.makeText(ConfigureData.activityMain,
		// Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) {
			Log.w("UPLOAD AVATAR FAIL", response);
		}
	}

	@Override
	public void onDestroy() {
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		super.onDestroy();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long arg3) {

		switch (parent.getId()) {
		case R.id.spinnerCity:
			// Get city id
			String cityName = mArrCityNames.get(pos);
			mCtityId = mHashMapCityName.get(cityName);

			// Get District list and fill out the districts spinner
			ServiceGetDistricts serviceGetDistrics = new ServiceGetDistricts();
			serviceGetDistrics.addOnGetJsonListener(new OnGetJsonListener() {

				@Override
				public void onGetJsonFail(String response) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetJsonCompleted(String response) {
					// TODO Auto-generated method stub
					try {
						JSONObject responseJson = new JSONObject(response);
						if (responseJson.getBoolean("status")) {
							JSONArray districtsJSONArray = responseJson
									.getJSONArray("data");
							mArrDistrictsNames = new ArrayList<String>();
							mHashMapDistrictsId = new HashMap<Integer, String>();
							mHashMapDistrictsName = new HashMap<String, Integer>();
							for (int i = 0; i < districtsJSONArray.length(); i++) {
								JSONObject districtsObject = districtsJSONArray
										.getJSONObject(i);
								mHashMapDistrictsId.put(
										districtsObject.getInt("id"),
										districtsObject.getString("name"));
								mHashMapDistrictsName.put(
										districtsObject.getString("name"),
										districtsObject.getInt("id"));
								mArrDistrictsNames.add(districtsObject
										.getString("name"));
							}

							String[] arrDistrictsName = new String[mArrDistrictsNames
									.size()];
							mArrDistrictsNames.toArray(arrDistrictsName);
							districtsAdapter = new ArrayAdapter<String>(
									ConfigureData.activityMain,
									R.layout.spinner_item,
									arrDistrictsName);
							districtsAdapter
									.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spinnerDistricts.setAdapter(districtsAdapter);
							if (arrDistrictsName.length > 1) {
								mDistrictId = mHashMapDistrictsName
										.get(arrDistrictsName[1]);
							} else {
								mDistrictId = mHashMapDistrictsName
										.get(arrDistrictsName[0]);
							}

							spinnerDistricts.setSelection(districtsAdapter
									.getPosition(mHashMapDistrictsId
											.get(mDistrictId)));
							districtsAdapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			serviceGetDistrics.getDistricts(mCtityId);

			Log.d("CITY ID CHANGE", mCtityId + "");

			break;
		case R.id.spinnerDistricts:
			mDistrictId = mHashMapDistrictsName
					.get(mArrDistrictsNames.get(pos));

			Log.d("DISTRICT ID CHANGE", mDistrictId + "");
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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
		ConfigureData.saveIconCarSharedPreference();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this.getActivity()).activityStop(
				this.getActivity()); // Add this method.
	}
}