package ta.car4rent.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.BuildConfig;
import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.webservices.ServiceGetUserInfo;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.OnUploadBitmapListener;
import ta.car4rent.webservices.ServiceUpdateUserInfo;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AccountFragmemt extends Fragment implements OnClickListener,
		android.content.DialogInterface.OnClickListener, OnUploadBitmapListener {

	private ImageView ivAvatar;
	private ProgressBar progressBar;
	private EditText etName;
	private EditText etEmail;
	private EditText etPhone;
	private EditText etCity;
	private EditText etDistrict;
	private EditText etAddress;
	private Button btnUpdateInfo;
	private Button btnChangeAvatarImage;
	private Button btnChangeLocationIcon;

	private Boolean isUserAvatarChange;

	LinearLayout layoutChoseType;
	// camera
	private static final int PICK_FROM_GALLERY = 101; // request code
	// request code for capture
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "4cars";
	public static final int MEDIA_TYPE_IMAGE = 1;
	Uri URI = null; // path of image
	Bitmap bitmap_image;
	int columnIndex;
	String photoTakedPath;

	public AccountFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isUserAvatarChange = false;

		View rootView = inflater.inflate(R.layout.fragment_account, container,
				false);

		ivAvatar = (ImageView) rootView.findViewById(R.id.ivAvatar);

		etName = (EditText) rootView.findViewById(R.id.etName);
		etEmail = (EditText) rootView.findViewById(R.id.etEmail);
		etPhone = (EditText) rootView.findViewById(R.id.etPhone);
		etCity = (EditText) rootView.findViewById(R.id.etProvinceCity);
		etDistrict = (EditText) rootView.findViewById(R.id.etDistrict);
		etAddress = (EditText) rootView.findViewById(R.id.etAddress);
		btnUpdateInfo = (Button) rootView.findViewById(R.id.btnUpdateInfo);
		btnChangeAvatarImage = (Button) rootView
				.findViewById(R.id.btnChangeAvatarImage);
		btnChangeLocationIcon = (Button) rootView
				.findViewById(R.id.btnChangeLocationIcon);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

		// fill info after login
		try {
			etName.setText(ConfigureData.userAccount.getString("name"));
			etEmail.setText(ConfigureData.userAccount.getString("email"));
			etPhone.setText(ConfigureData.userAccount.getString("phone"));
			etCity.setText(ConfigureData.userAccount
					.getString("cityName"));
			etDistrict.setText(ConfigureData.userAccount
					.getString("districtName"));
			etAddress.setText(ConfigureData.userAccount.getString("address"));
			ConfigureData.imageLoader.displayImage(
					ConfigureData.userAccount.getString("image"), ivAvatar);
		} catch (JSONException e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}

		// Setting for update info button
		btnUpdateInfo.setOnClickListener(this);
		btnChangeAvatarImage.setOnClickListener(this);
		btnChangeLocationIcon.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO UpdateInfo button Clicked
		switch (v.getId()) {
		case R.id.btnUpdateInfo:
			progressBar.setVisibility(View.VISIBLE);
			// upload new avatar image
			if (isUserAvatarChange) {
				ServiceUploadUserAvatar serviceUploadUserAvatar = new ServiceUploadUserAvatar();
				serviceUploadUserAvatar.addOnUploadBitmapListener(this);

				reloadUserAccount();
				progressBar.setVisibility(View.VISIBLE);
				
				try {
					int userId = ConfigureData.userAccount.getInt("id");
					serviceUploadUserAvatar.uploadUserAvatar(((BitmapDrawable) ivAvatar.getDrawable())
							.getBitmap(), userId);	
				} catch (JSONException e) {
					if (BuildConfig.DEBUG) {
						e.printStackTrace();
					}
				}
				
			}

			ServiceUpdateUserInfo serviceUpdateUserInfo = new ServiceUpdateUserInfo();
			serviceUpdateUserInfo.addOnPostJsonListener(new OnPostJsonListener() {

				@Override
				public void onPostJsonFail(String response) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "Cập nhật thông tin thất bại, hãy kiểm tra kết nối mạng của bạn",
							Toast.LENGTH_SHORT).show();
					

					progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onPostJsonCompleted(String response) {
					// TODO Auto-generated method stub
					reloadUserAccount();
					Log.e("UPDATE USER INFO RESPONESE", response);
					
					try {
						JSONObject responseJSON = new JSONObject(response);
						if (responseJSON.getBoolean("status")) {
							// Upload completed
							Toast.makeText(getActivity(), responseJSON.getString("message"), Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), responseJSON.getString("message"),
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
					

					progressBar.setVisibility(View.GONE);
				}

			});

			try {
				
//				The following is an example request userInfo Json body:
//
//				{
//					"userId":2147483647,
//					"userName":"String content",
//					"password":"String content",
//					"name":"String content",
//					"email":"String content",
//					"phone":"String content",
//					"cityId":2147483647,
//					"districtId":2147483647,
//					"address":"String content"
//				}
				
				JSONObject userInfo = new JSONObject();
				userInfo.put("userId", ConfigureData.userAccount.getInt("id"));
				userInfo.put("userName", ConfigureData.userName);
				userInfo.put("password", ConfigureData.password);
				userInfo.put("name", etName.getText().toString());
				userInfo.put("email", etEmail.getText().toString());
				userInfo.put("phone", etPhone.getText().toString());
				userInfo.put("cityId", 1);
				userInfo.put("districtId", 1);
				userInfo.put("address", etAddress.getText().toString());

				progressBar.setVisibility(View.VISIBLE);
				serviceUpdateUserInfo.updateUserInfo(userInfo);
			} catch (JSONException e) {
				
			}
			break;
		case R.id.btnChangeAvatarImage:
			// open dialog for user select Take photo from camera or gallery
			AlertDialog dialog = createSelectTakePhotoModeDialog();
			dialog.show();
			break;
		case R.id.btnChangeLocationIcon:
			break;
		}
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
									ConfigureData.userAccount = object.getJSONObject("user");
								}
							} catch (JSONException e) {
								if (BuildConfig.DEBUG) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onGetJsonFail(
								String response) {
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
		builder.setTitle("Lấy ảnh từ...");
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
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	public void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		URI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, URI);
		Toast.makeText(ConfigureData.activityMain,
				"Xoay ngang màn hình để chụp cho hiển thị tốt nhất",
				Toast.LENGTH_SHORT).show();
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
	 * ------------ Helper Methods ----------------------
	 * */

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
	public void onUploadImageBitmapCompleted(String response) {
		try {
			JSONObject responseJSON = new JSONObject(response);
			if (responseJSON.getBoolean("status")) {
				// Upload completed

				Toast.makeText(getActivity(), "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), responseJSON.getString("message"),
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		progressBar.setVisibility(View.GONE);

	}

	@Override
	public void onUploadImageBitmapFail(String response) {
		Toast.makeText(getActivity(), "Cập nhật avatar thất bại, hãy kiểm tra kết nối mạng của bạn",
				Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) {
			Log.w("UPLOAD AVATAR FAIL", response);
		}
	}

}