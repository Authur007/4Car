package ta.car4rent.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.activities.GoogleMapsActivity;
import ta.car4rent.adapters.NewsFeedWarningListAdapter;
import ta.car4rent.configures.ConfigureData;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

public class WarningsManager {
	// ======================================
	// VARIABLES AND STATIC FLAGS
	// ======================================
	public static Marker markerUser = null;
	// static flag
	public static final int ALL_WARNING = 0;
	public static final int POLICE_WARNING = 1;
	public static final int TRAFFIC_WARNING = 2;
	public static final int ACCIDENT_WARNING = 3;
	public static final int STATUS_WARNING = 4;
	// filter warnings to show on maps
	public static int onlyShowWarningsInMaps = 0;

	// static list
	private Activity activity;
	public List<JSONObject> listChotCongAn = new ArrayList<JSONObject>();
	public List<JSONObject> listGiaoThong = new ArrayList<JSONObject>();
	public List<JSONObject> listTaiNan = new ArrayList<JSONObject>();
	public List<JSONObject> listTrangThai = new ArrayList<JSONObject>();

	public HashMap<String, Integer> markerList = new HashMap<String, Integer>();

	/**
	 * Contrutor
	 * 
	 * @param activity
	 */
	public WarningsManager(Activity activity) {
		this.activity = activity;
	}

	// ======================================
	// CLASS LOGICS
	// ======================================

	/**
	 * reset all newsfeed
	 */
	public void resetAllList() {
		/*
		 * listChotCongAn.clear(); listGiaoThong.clear(); listTaiNan.clear();
		 * listTrangThai.clear(); markerList.clear();
		 */

	}

	/**
	 * add icon cho tung canh bao
	 */
	public void drawWarningsIntoMap(int key, GoogleMap googleMap) {
		try {
			googleMap.clear();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		List<JSONObject> listWarning = new ArrayList<JSONObject>();
		switch (key) {
		case ALL_WARNING:
			listWarning = getAllWarning();
			break;
		case POLICE_WARNING:
			listWarning = listChotCongAn;
			break;
		case TRAFFIC_WARNING:
			listWarning = listGiaoThong;
			break;
		case ACCIDENT_WARNING:
			listWarning = listTaiNan;
			break;
		case STATUS_WARNING:
			listWarning = listTrangThai;
			break;

		default:
			break;
		}
		drawIconCarUser(googleMap);
		// add all marker of warnings
		for (int i = 0; i < listWarning.size(); i++) {
			JSONObject warningObject = listWarning.get(i);
			// create marker
			Log.d("Json drawer", warningObject.toString());
			try {
				double lng = warningObject.getDouble("longitude");
				double lat = warningObject.getDouble("latitude");
				LatLng latLng = new LatLng(lat, lng);

				MarkerOptions markerOption = new MarkerOptions()
						.position(latLng)
						.title(warningObject.getString("reportType"))
						.snippet(warningObject.getString("address"));
				String reportSubType = warningObject.getString("reportSubType");

				if (reportSubType.equals(ConfigureData.activityGoogleMaps
						.getString(R.string.cong_khai))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.congkhai));
				} else if (reportSubType
						.equals(ConfigureData.activityGoogleMaps
								.getString(R.string.nup_lum))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.nuplum));
				} else if (reportSubType
						.equals(ConfigureData.activityGoogleMaps
								.getString(R.string.ket_cung))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ketxenang));
				} else if (reportSubType
						.equals(ConfigureData.activityGoogleMaps
								.getString(R.string.dong_duc))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.dongduc));
				} else if (reportSubType
						.equals(ConfigureData.activityGoogleMaps
								.getString(R.string.nhe_nhang))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.tainannhe));
				} else if (reportSubType
						.equals(ConfigureData.activityGoogleMaps
								.getString(R.string.nghiem_trong))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.tainannang));
				} else if (reportSubType
						.equals(ConfigureData.activityGoogleMaps
								.getString(R.string.trang_thai))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.trangthai));
				} else if (reportSubType
						.equals(ConfigureData.activityGoogleMaps
								.getString(R.string.giup_do))) {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.giupdo));
				} else {
					markerOption.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.color));
				}

				Marker marker = googleMap.addMarker(markerOption);
				markerList.put(marker.getId(), i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				// adding marker

			}
		}
	}

	public void drawIconCarUser(GoogleMap googleMap) {
		// add icon car at user currentLocaiton
		
		markerUser = googleMap
				.addMarker(new MarkerOptions()
						.position(GoogleMapsActivity.mCurrentLocation)
						.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
								BitmapFactory
										.decodeResource(
												ConfigureData.activityGoogleMaps
														.getResources(),
												ConfigureData.iconCarResId),
								72, 72, false))));
	}

	// ======================================
	// GETTERS AND SETTERS
	// ======================================
	/**
	 * getWarning by pos and flag listWarnign
	 * 
	 * @return
	 */

	public JSONObject getWarning(int flag, int pos) {
		JSONObject warning = null;
		switch (flag) {
		case ALL_WARNING:
			warning = getAllWarning().get(pos);
			break;
		case POLICE_WARNING:
			warning = listChotCongAn.get(pos);
			break;
		case TRAFFIC_WARNING:
			warning = listGiaoThong.get(pos);
			break;
		case ACCIDENT_WARNING:
			warning = listTaiNan.get(pos);
			break;
		case STATUS_WARNING:
			warning = listTrangThai.get(pos);
			break;
		}
		return warning;
	}

	public int getNumberAllWarning() {
		return listChotCongAn.size() + listGiaoThong.size() + listTaiNan.size()
				+ listTrangThai.size();
	}

	public List<JSONObject> getAllWarning() {
		List<JSONObject> listAllWarning = new ArrayList<JSONObject>();
		listAllWarning.addAll(listChotCongAn);
		listAllWarning.addAll(listGiaoThong);
		listAllWarning.addAll(listTaiNan);
		listAllWarning.addAll(listTrangThai);

		return listAllWarning;

	}
	// ======================================
	// OTHERS
	// ======================================

}
