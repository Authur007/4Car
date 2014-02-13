package ta.car4rent.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import ta.car4rent.R;
import ta.car4rent.activities.GoogleMapsActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.OnUploadBitmapListener;
import ta.car4rent.webservices.ServicePostNewReport;
import ta.car4rent.webservices.ServiceUploadReportPhoto;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PostWarningFragment extends DialogFragment implements
		OnClickListener, android.content.DialogInterface.OnClickListener {

	// ===========================================
	// VARIABLES
	// ===========================================
	public static final int POST_POLICE = 1;
	public static final int POST_TRAFFIC = 2;
	public static final int POST_ACCIDENT = 3;
	public static final int POST_STATUS = 4;

	public static int idSubtype = 0;
	public String txtContentReport;

	ImageView imageType1;
	ImageView imageType2;

	TextView txtType1;
	TextView txtType2;
	Button txtTitleType;

	Button btnPost;
	Button btnCancel;
	ImageButton btnImageAttach;
	EditText edtPostContent;
	LinearLayout layoutChoseType1;
	LinearLayout layoutChoseType2;

	// camera
	private static final int PICK_FROM_GALLERY = 101; // repuest code
	// request code for capture
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "BlackmoonT92";
	public static final int MEDIA_TYPE_IMAGE = 1;
	Uri URI = null; // path of image
	Bitmap bitmap_image;
	int columnIndex;
	String attachmentFile;
	JSONObject jsonObjectWarning = new JSONObject();

	ProgressDialog processDialog;

	// ===========================================
	// CLASS DEFAULT
	// ===========================================
	public PostWarningFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ConfigureData.isEnableShowChoosePostWarning = false;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_post_warning,
				container, false);

		// Find View component
		btnPost = (Button) rootView.findViewById(R.id.btnPostInFragmentPost);
		btnCancel = (Button) rootView.findViewById(R.id.btnCancelPostWarning);
		edtPostContent = (EditText) rootView.findViewById(R.id.edtPostContent);
		btnImageAttach = (ImageButton) rootView
				.findViewById(R.id.btnAttachImage);

		layoutChoseType1 = (LinearLayout) rootView
				.findViewById(R.id.layoutChooseType1);
		layoutChoseType2 = (LinearLayout) rootView
				.findViewById(R.id.layoutChooseType2);
		// layout type
		imageType1 = (ImageView) rootView.findViewById(R.id.imageType1);
		imageType2 = (ImageView) rootView.findViewById(R.id.imageType2);

		txtType1 = (TextView) rootView.findViewById(R.id.txtSubType1);
		txtType2 = (TextView) rootView.findViewById(R.id.txtSubType2);

		txtTitleType = (Button) rootView.findViewById(R.id.txtTitleTypeWarning);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// get location
		GoogleMapsActivity gmaps = new GoogleMapsActivity();
		try {
			GoogleMapsActivity.mCurrentLocation = gmaps
					.getGPSLocation(ConfigureData.activityGoogleMaps);
		} catch (NullPointerException e) {
			Toast.makeText(ConfigureData.activityMain, "Vui lòng mở GPS!!!", 0)
					.show();
		}

		findViewAndSetListener();
		setupUI();

	}

	public void findViewAndSetListener() {

		// set onClick
		layoutChoseType1.setOnClickListener(this);
		layoutChoseType2.setOnClickListener(this);

		btnCancel.setOnClickListener(this);
		btnPost.setOnClickListener(this);
		btnImageAttach.setOnClickListener(this);
		/**
		 * Long click picture will delete this picture
		 */
		btnImageAttach.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(ConfigureData.activityGoogleMaps,
						getString(R.string.deleteImage), 0).show();
				// delete this picture
				btnImageAttach.setImageResource(R.drawable.camera);
				URI = Uri.parse("");
				return false;
			}
		});
	}

	/**
	 * call first to setup UI will be show. it depend on type of waring
	 */
	private void setupUI() {
		switch (ConfigureData.flagTypePostWarningFragment) {
		case PostWarningFragment.POST_POLICE:
			idSubtype = ConfigureData.flagTypePostWarningFragment * 2 - 1;
			txtTitleType.setText(getString(R.string.chot_cong_an));
			txtType1.setText(getString(R.string.cong_khai));
			txtType2.setText(getString(R.string.nup_lum));
			imageType1.setImageResource(R.drawable.police_4car);
			imageType2.setImageResource(R.drawable.police_4car_subtype2);

			layoutChoseType1.setBackgroundColor(getResources().getColor(
					R.color.green_clicked));
			break;
		case PostWarningFragment.POST_TRAFFIC:
			idSubtype = ConfigureData.flagTypePostWarningFragment * 2 - 1;
			txtTitleType.setText(getString(R.string.giao_thong));
			txtType1.setText(getString(R.string.ket_cung));
			txtType2.setText(getString(R.string.dong_duc));
			imageType1.setImageResource(R.drawable.traffic_4car_subtype1);
			imageType2.setImageResource(R.drawable.traffic_4car_subtype2);

			layoutChoseType1.setBackgroundColor(getResources().getColor(
					R.color.green_clicked));
			break;
		case PostWarningFragment.POST_ACCIDENT:
			idSubtype = ConfigureData.flagTypePostWarningFragment * 2 - 1;
			txtTitleType.setText(getString(R.string.tai_nan));
			txtType1.setText(getString(R.string.nhe_nhang));
			txtType2.setText(getString(R.string.nghiem_trong));
			imageType1.setImageResource(R.drawable.small_accident);
			imageType2.setImageResource(R.drawable.big_accident);

			layoutChoseType1.setBackgroundColor(getResources().getColor(
					R.color.green_clicked));
			break;
		case PostWarningFragment.POST_STATUS:
			idSubtype = ConfigureData.flagTypePostWarningFragment * 2 - 1;
			txtTitleType.setText(getString(R.string.trang_thai));
			txtType1.setText(getString(R.string.trang_thai));
			txtType2.setText(getString(R.string.giup_do));
			imageType1.setImageResource(R.drawable.status_4car_subtype1);
			imageType2.setImageResource(R.drawable.status_4car_subtype2);

			layoutChoseType1.setBackgroundColor(getResources().getColor(
					R.color.green_clicked));
			break;

		default:
			break;
		}

	}

	/**
	 * click button post, attach photo
	 */
	@Override
	public void onClick(View view) {
		StaticFunction.hideKeyboard(ConfigureData.activityGoogleMaps);

		switch (view.getId()) {
		case R.id.btnCancelPostWarning:
			// reset all fild
			ConfigureData.flagTypePostWarningFragment = 0;
			ConfigureData.isEnableShowChoosePostWarning = true;
			this.dismiss();
			// closeThisFragment();

			break;
		case R.id.btnAttachImage:
			// open gallery
			AlertDialog dialog = createAlertDialog();
			dialog.show();
			break;
		case R.id.btnPostInFragmentPost:
			processDialog = ProgressDialog.show(getActivity(),
					getString(R.string.loading), getString(R.string.uploading),
					false);
			processDialog.setCancelable(true);

			jsonObjectWarning = exportJsonObject();
			if (jsonObjectWarning != null) {

				ServicePostNewReport servicePostNewReport = new ServicePostNewReport();
				servicePostNewReport.postNewReport(jsonObjectWarning);
				servicePostNewReport
						.addOnPostJsonListener(new OnPostJsonListener() {

							@Override
							public void onPostJsonFail(String response) {
								processDialog.dismiss();
								closeThisFragment();

							}

							@Override
							public void onPostJsonCompleted(String response) {
								closeThisFragment();
								try {
									JSONObject responseJSON = new JSONObject(
											response);
									if (responseJSON.getBoolean("status")) {
										// Upload completed
										int id = responseJSON.getInt("id");
										if (id != 0 && bitmap_image != null) {
											ServiceUploadReportPhoto serviceUploadReportPhoto = new ServiceUploadReportPhoto();
											serviceUploadReportPhoto
													.uploadReportPhoto(
															bitmap_image, id);
											serviceUploadReportPhoto
													.addOnUploadBitmapListener(new OnUploadBitmapListener() {

														@Override
														public void onUploadImageBitmapFail(
																String response) {
															Log.d("Upload Report Picture",
																	response);
															processDialog
																	.dismiss();

														}

														@Override
														public void onUploadImageBitmapCompleted(
																String response) {
															processDialog
																	.dismiss();
															// closeThisFragment();
															Log.d("Upload Report Picture",
																	response);

														}

													});
											// closeThisFragment();
										} else {
											processDialog.dismiss();

											// closeThisFragment();

										}

									}
								} catch (JSONException e) {

									e.printStackTrace();
								} catch (NullPointerException e) {
									e.printStackTrace();

								}
							}

						});

				// resetAllComponent();

			}
			break;
		case R.id.layoutChooseType1:
			idSubtype = ConfigureData.flagTypePostWarningFragment * 2 - 1;
			layoutChoseType2.setBackgroundColor(getResources().getColor(
					R.color.orange));
			layoutChoseType1.setBackgroundColor(getResources().getColor(
					R.color.green_clicked));

			break;
		case R.id.layoutChooseType2:
			idSubtype = ConfigureData.flagTypePostWarningFragment * 2;
			layoutChoseType1.setBackgroundColor(getResources().getColor(
					R.color.orange));
			layoutChoseType2.setBackgroundColor(getResources().getColor(
					R.color.green_clicked));
			break;
		default:
			break;
		}

	}

	public void closeThisFragment() {
		ConfigureData.flagTypePostWarningFragment = 0;
		ConfigureData.isEnableShowChoosePostWarning = true;
		// retry data
		GoogleMapsActivity googleMapsActivity = new GoogleMapsActivity();
		googleMapsActivity.getWarningFromServer(PostWarningFragment.this);

	}

	// "deviceId":"String content",
	// "reportSubTypeId":"String content",
	// "reportTypeId":"String content",
	// "longitude":"String content",
	// "latitude":"String content",
	// "note":"String content"

	private JSONObject exportJsonObject() {
		Random random = new Random();

		txtContentReport = edtPostContent.getText().toString();
		JSONObject jsonWarning = new JSONObject();
		try {
			jsonWarning.put(GoogleMapsActivity.ID_DEVICE, "Android");
			jsonWarning.put(GoogleMapsActivity.ID_TYPE,
					ConfigureData.flagTypePostWarningFragment);
			jsonWarning.put(GoogleMapsActivity.ID_SUB_TYPE, idSubtype);
			jsonWarning.put(GoogleMapsActivity.CONTENT, txtContentReport);
			if (GoogleMapsActivity.mCurrentLocation != null) {
				float rlat = 0.02f;
				float rLong = 0.02f;
				switch (ConfigureData.randomLocationInMap % 4) {
				case 1:
					rlat = (0.02f + (float) (ConfigureData.randomLocationInMap / 4) / 50) / 1000f;
					// rLong = ((float)(ConfigureData.randomLocationInMap /
					// 4)/2) / 1000f;
					rLong /= 1000f;
					ConfigureData.randomLocationInMap++;
					Log.d("rnafom", 1 + "- " + rlat + ", " + rLong);
					break;

				case 2:
					// rlat = -((float)(ConfigureData.randomLocationInMap /
					// 4)/2) / 1000f;
					rlat = -rlat / 1000f;
					rLong = (0.02f + (float) (ConfigureData.randomLocationInMap / 4) / 50) / 1000f;
					ConfigureData.randomLocationInMap++;
					Log.d("rnafom", 2 + "- " + rlat + ", " + rLong);
					break;
				case 3:
					rlat = -(0.02f + (float) (ConfigureData.randomLocationInMap / 4) / 50) / 1000f;
					// rLong = -((float)(ConfigureData.randomLocationInMap /
					// 4)/2) / 1000f;
					rLong = -rLong/1000f;
					ConfigureData.randomLocationInMap++;
					Log.d("rnafom", 3 + "- " + rlat + ", " + rLong);
					break;
				case 0:
					rlat /=1000f;
					rLong = -(0.02f + (float) (ConfigureData.randomLocationInMap / 4) / 50) / 1000f;
					ConfigureData.randomLocationInMap++;
					Log.d("rnafom", 4 + "- " + rlat + ", " + rLong);
					break;
				}
				jsonWarning.put(GoogleMapsActivity.LONGITUTE,
						GoogleMapsActivity.mCurrentLocation.longitude + rLong
								+ "");
				jsonWarning.put(GoogleMapsActivity.LATITUDE,
						GoogleMapsActivity.mCurrentLocation.latitude + rlat
								+ "");

			} else {
				Toast.makeText(ConfigureData.activityGoogleMaps,
						"Can't get GPS", 0).show();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonWarning;

	}

	// ==============================================
	// GET IMAGE FROM CAMERA OR GALLERY
	// ==============================================

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		if (ConfigureData.activityGoogleMaps.getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/*
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	public void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		URI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, URI);
		Toast.makeText(ConfigureData.activityGoogleMaps,
				getString(R.string.rotation_camera), Toast.LENGTH_SHORT).show();
		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FROM_GALLERY
				&& resultCode == Activity.RESULT_OK) {
			/**
			 * Get Path
			 */
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = ConfigureData.activityGoogleMaps
					.getContentResolver().query(selectedImage, filePathColumn,
							null, null, null);
			cursor.moveToFirst();
			columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			attachmentFile = cursor.getString(columnIndex);
			Log.e("Attachment Path:", attachmentFile);
			URI = Uri.parse("file://" + attachmentFile);
			Log.e("URI Path:", URI.toString() + "");
			cursor.close();
		} else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				// successfully captured the image
				// display it in image view
				try {

					BitmapFactory.Options bounds = new BitmapFactory.Options();
					bounds.inJustDecodeBounds = true;
					Bitmap bm = BitmapFactory.decodeFile(URI.getPath(), bounds);

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

					matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2,
							(float) bm.getHeight() / 2);
					Bitmap.createBitmap(bm, 0, 0, bounds.outWidth,
							bounds.outHeight, matrix, true);

				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		try {
			bitmap_image = ta.car4rent.utils.Utils.decodeSampledBitmapFromFile(
					URI.getPath(), 320, 240);

		} catch (NullPointerException e) {
			bitmap_image = null;
		}
		btnImageAttach.setImageBitmap(bitmap_image);
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

	public AlertDialog createAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.str_get_image_from));
		builder.setIcon(R.drawable.ic_launcher);

		builder.setPositiveButton("Gallery", this);
		builder.setNeutralButton("Camera", this);
		return builder.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_POSITIVE) {
			openGallery();
		} else if (which == AlertDialog.BUTTON_NEUTRAL) {
			// Checking camera availability
			if (!isDeviceSupportCamera()) {
				Toast.makeText(ConfigureData.activityGoogleMaps,
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
	public void onDestroy() {

		ConfigureData.flagTypePostWarningFragment = 0;
		ConfigureData.isEnableShowChoosePostWarning = true;
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		StaticFunction.hideKeyboard(ConfigureData.activityGoogleMaps);

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

}