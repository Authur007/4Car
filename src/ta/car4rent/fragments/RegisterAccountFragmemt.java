package ta.car4rent.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import ta.car4rent.webservices.OnUploadBitmapListener;
import ta.car4rent.webservices.ServiceGetDistricts;
import ta.car4rent.webservices.ServiceRegister;
import ta.car4rent.webservices.ServiceUploadUserAvatar;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;
import com.google.analytics.tracking.android.EasyTracker;

public class RegisterAccountFragmemt extends Fragment implements
		OnClickListener, android.content.DialogInterface.OnClickListener,
		OnItemSelectedListener {

	private ImageView ivAvatar;
	private ProgressBar progressBar;
	private EditText edtUserName;
	private EditText edtPassWord;
	private EditText edtConfirmPass;
	private EditText edtName;
	private EditText edtEmail;
	private EditText edtPhone;
	private EditText edtAddress;
	private Button btnRegister;
	LinearLayout layoutDistricts;
	// camera
	private static final int PICK_FROM_GALLERY = 101; // request code
	// request code for capture
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "4cars";
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Uri URI = null; // path of image
	private int columnIndex;
	private String photoTakedPath;

	private Spinner spinnerCity;
	private Spinner spinnerDistricts;

	private SpinnerDataList newAccCitiesSpinnerDataList;
	private SpinnerDataList newAccDistrictsSpinnerDataList;

	// facebook variable
	ProfilePictureView profilePictureView;

	public RegisterAccountFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MainActivity.Instance.showActionFilterSpinner(false);
		
		ConfigureData.currentScreen = 2;

		View rootView = inflater.inflate(R.layout.fragment_register_account,
				container, false);

		ivAvatar = (ImageView) rootView.findViewById(R.id.ivAvatar);
		edtUserName = (EditText) rootView.findViewById(R.id.edtUserName);
		edtPassWord = (EditText) rootView.findViewById(R.id.edtPass);
		edtConfirmPass = (EditText) rootView.findViewById(R.id.edtConfirmPass);
		edtName = (EditText) rootView.findViewById(R.id.edtFullName);
		edtEmail = (EditText) rootView.findViewById(R.id.edtEmail);
		edtPhone = (EditText) rootView.findViewById(R.id.edtPhoneNumber);
		spinnerCity = (Spinner) rootView.findViewById(R.id.spinnerCity);
		spinnerDistricts = (Spinner) rootView
				.findViewById(R.id.spinnerDistricts);
		edtAddress = (EditText) rootView.findViewById(R.id.edtAddress);
		btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
		edtAddress = (EditText) rootView.findViewById(R.id.edtAddress);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

		// Setting for update info button
		btnRegister.setOnClickListener(this);
		ivAvatar.setOnClickListener(this);
		spinnerCity.setOnItemSelectedListener(this);
		spinnerDistricts.setOnItemSelectedListener(this);
		layoutDistricts = (LinearLayout) rootView
				.findViewById(R.id.layoutDistricts);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		/**
		 * auto fill info when register by facebook
		 */
		if (ConfigureData.isUsingFBLoginButton
				&& ConfigureData.graphUser != null) {

			try {
				profilePictureView = (ProfilePictureView) getView()
						.findViewById(R.id.profilePicture);
				if (ConfigureData.graphUser != null) {
					profilePictureView.setProfileId(ConfigureData.graphUser
							.getId());
				}
				// hide some component
				LinearLayout layoutUser = (LinearLayout) getView()
						.findViewById(R.id.layoutUserName);
				layoutUser.setVisibility(View.GONE);
				LinearLayout layoutPass = (LinearLayout) getView()
						.findViewById(R.id.layoutPass);
				layoutPass.setVisibility(View.GONE);
				LinearLayout layoutPassConfirm = (LinearLayout) getView()
						.findViewById(R.id.layoutPassConfirm);
				layoutPassConfirm.setVisibility(View.GONE);
				// fill data
				edtName.setText(ConfigureData.graphUser.getName());
				edtUserName.setText(ConfigureData.graphUser.getId());
				edtPassWord.setText(ConfigureData.graphUser.getId());
				edtConfirmPass.setText(ConfigureData.graphUser.getId());

			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		// cities
		try {
			newAccCitiesSpinnerDataList = SearchCarFragmemt.citiesSpinnerDataList;
			ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(
					ConfigureData.activityMain,
					android.R.layout.simple_spinner_item,
					newAccCitiesSpinnerDataList.getArraySpinnerText());
			citiesAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerCity.setAdapter(citiesAdapter);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO UpdateInfo button Clicked
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		switch (v.getId()) {
		case R.id.btnRegister:
			progressBar.setVisibility(View.VISIBLE);
			// check vaild data input
			if (checkVaildData()) {
				final JSONObject registerObject = exportJsonObjectRegister();
				if (registerObject != null) {
					ServiceRegister serviceRegister = new ServiceRegister();
					serviceRegister.registerUser(registerObject);
					serviceRegister
							.addOnPostJsonListener(new OnPostJsonListener() {

								@Override
								public void onPostJsonFail(String response) {

								}

								@Override
								public void onPostJsonCompleted(String response) {
									try {
										JSONObject responseJSON = new JSONObject(
												response);
										Log.d("response register", response);
										if (responseJSON.getBoolean("status")) {
											// Upload completed
											int userID = responseJSON
													.getInt("id");

											if (ConfigureData.isUsingFBLoginButton) {
												try {
													ConfigureData.isUsingFBLoginButton = false;
													ImageView fbImage = ((ImageView) profilePictureView
															.getChildAt(0));
													Bitmap mBitmapAvatar = ((BitmapDrawable) fbImage
															.getDrawable())
															.getBitmap();
												} catch (NullPointerException e) {
													e.printStackTrace();
												}
											} else {
												Bitmap mBitmapAvatar = ((BitmapDrawable) ivAvatar
														.getDrawable())
														.getBitmap();
											}

											Bitmap mBitmapAvatar = ((BitmapDrawable) ivAvatar
													.getDrawable()).getBitmap();
											ConfigureData.userAccount = registerObject;
											ConfigureData.userAccount.put(
													"userId", userID);

											ConfigureData.userAccount.put(
													"image", mBitmapAvatar);

											Log.d("UserId", userID + "");

											if (mBitmapAvatar != null) {
												ServiceUploadUserAvatar serviceUploadAvatar = new ServiceUploadUserAvatar();
												serviceUploadAvatar
														.uploadUserAvatar(
																mBitmapAvatar,
																userID);
												serviceUploadAvatar
														.addOnUploadBitmapListener(new OnUploadBitmapListener() {

															@Override
															public void onUploadImageBitmapFail(
																	String response) {
																Log.d("Upload image",
																		"Fail");
															}

															@Override
															public void onUploadImageBitmapCompleted(
																	String response) {
																Log.d("Upload image",
																		"successfull");
															}
														});
											}
											gotoLoginFragment();
										} else {
											Toast.makeText(
													getActivity(),
													responseJSON
															.getString("message"),
													Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {

										e.printStackTrace();
									}

								}
							});
					progressBar.setVisibility(View.GONE);
				}
			}

			break;
		case R.id.ivAvatar:
			// open dialog for user select Take photo from camera or gallery
			AlertDialog dialog = createSelectTakePhotoModeDialog();
			dialog.show();
			break;

		}
	}

	public void gotoLoginFragment() {
		if (ConfigureData.isUsingFBLoginButton) {
			ConfigureData.isUsingFBLoginButton = false;
			Session session = Session.getActiveSession();
			if (session != null && !session.isClosed()) {
				session.closeAndClearTokenInformation();
			}
		}

		progressBar.setVisibility(View.GONE);
		Toast.makeText(getActivity(), getString(R.string.active_mail), 1)
				.show();
		// Show the Account info Fragment
		Fragment fragment = new LoginFragmemt();
		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}

	private JSONObject exportJsonObjectRegister() {

		JSONObject userInfo = new JSONObject();
		try {
			userInfo.put("userName", edtUserName.getText().toString());
			userInfo.put("password", edtPassWord.getText().toString());
			userInfo.put("name", edtName.getText().toString());
			userInfo.put("email", edtEmail.getText().toString());
			userInfo.put("phone", edtPhone.getText().toString());
			userInfo.put("cityId", newAccCitiesSpinnerDataList
					.getSelectedItem().getValue());
			userInfo.put("districtId", newAccDistrictsSpinnerDataList
					.getSelectedItem().getValue());
			userInfo.put("address", edtAddress.getText().toString());

		} catch (JSONException e) {

		}
		return userInfo;

	}

	private boolean checkVaildData() {
		if ("".equals(edtUserName.getText().toString())
				|| "".equals(edtEmail.getText().toString())) {

			Toast.makeText(this.getActivity(),
					this.getActivity().getString(R.string.username_not_null), 0)
					.show();

			return false;
		}

		if ("".equals(edtPassWord.getText().toString())
				|| "".equals(edtConfirmPass.getText().toString())
				|| !(edtPassWord.getText().toString().equals(edtConfirmPass
						.getText().toString()))) {

			Toast.makeText(this.getActivity(),
					this.getActivity().getString(R.string.wrong_pass), 0)
					.show();

			return false;
		}

		if (newAccCitiesSpinnerDataList.getSelectedIndex() < 1
				|| newAccDistrictsSpinnerDataList.getSelectedIndex() < 1) {
			Toast.makeText(this.getActivity(),
					this.getActivity().getString(R.string.city_not_null), 0)
					.show();
			return false;
		}

		return true;
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
						"Xin lỗi, máy của bạn không có Camera",
						Toast.LENGTH_LONG).show();
				// will close the app if the device does't have camera
			} else {
				openCamera();
			}
		}

		// set image for preview

	}

	@Override
	public void onDestroyView() {
		ConfigureData.currentScreen = 0;
		super.onDestroyView();
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
			newAccCitiesSpinnerDataList.setSelectedItem(pos);
			if (pos > 0) {

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
										newAccDistrictsSpinnerDataList = new SpinnerDataList(
												districtsItems, 0);
										ArrayAdapter<String> districtsAdapter = new ArrayAdapter<String>(
												ConfigureData.activityMain,
												android.R.layout.simple_spinner_item,
												newAccDistrictsSpinnerDataList
														.getArraySpinnerText());
										districtsAdapter
												.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										spinnerDistricts
												.setAdapter(districtsAdapter);

										// Show layoutDistricts
										layoutDistricts
												.setVisibility(View.VISIBLE);
									}
								} catch (JSONException e) {
									if (BuildConfig.DEBUG) {
										e.printStackTrace();
									}
								}

								progressBar.setVisibility(View.GONE);
							}
						});

				serviceGetDistricts.getDistricts(newAccCitiesSpinnerDataList
						.getSelectedItem().getValue());
			}
			break;
		case R.id.spinnerDistricts:
			newAccDistrictsSpinnerDataList.setSelectedItem(pos);
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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