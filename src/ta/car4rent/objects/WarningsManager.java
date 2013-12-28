package ta.car4rent.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.internal.ac;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.widget.Toast;

public class WarningsManager {
	// ======================================
	// VARIABLES AND STATIC FLAGS
	// ======================================

	// static flag
	public static final int ALL_WARNING = 0;
	public static final int POLICE_WARNING = 1;
	public static final int TRAFFIC_WARNING = 2;
	public static final int ACCIDENT_WARNING = 3;
	public static final int STATUS_WARNING = 4;
	// filter warnings to show on maps
	public static int onlyShowWarning = 0;

	// static list
	private Activity activity;
	public List<Warning> listChotCongAn = new ArrayList<Warning>();
	public List<Warning> listGiaoThong = new ArrayList<Warning>();
	public List<Warning> listTaiNan = new ArrayList<Warning>();
	public List<Warning> listTrangThai = new ArrayList<Warning>();

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
	 * add icon cho tung canh bao
	 */
	public void drawWarningsIntoMap(int key, GoogleMap googleMap) {
		List<Warning> listWarning = new ArrayList<Warning>();
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

		for (int i = 0; i < listWarning.size(); i++) {
			Warning canhBao = listWarning.get(i);
			// create marker
			MarkerOptions marker = new MarkerOptions()
					.position(canhBao.getLocation()).title(canhBao.getTitle())
					.snippet(canhBao.getContent());

			// adding marker
			googleMap.addMarker(marker);
		}

	}

	/**
	 * tao du lieu ao
	 */
	public void loadDataWarning() {
		Warning canhBao1 = new Warning();
		canhBao1.setLocation(new LatLng(10.82231f, 106.637973f));
		canhBao1.setTitle("Canh bao 1");
		listChotCongAn.add(canhBao1);
		// -----------------
		Warning canhBao2 = new Warning();
		canhBao2.setLocation(new LatLng(10.879183f, 106.761679f));
		canhBao2.setTitle("Canh bao 2");
		listGiaoThong.add(canhBao2);
		// ------------------
		Warning canhBao3 = new Warning();
		canhBao3.setLocation(new LatLng(10.78133f, 106.698606f));
		canhBao3.setTitle("Canh bao 3");
		listTaiNan.add(canhBao3);
		// --------------------
		Warning canhBao4 = new Warning();
		canhBao4.setLocation(new LatLng(10.823616f, 106.6369f));
		canhBao4.setTitle("Canh bao 4");
		listTrangThai.add(canhBao4);
		// ----------------------
		Warning canhBao5 = new Warning();
		canhBao5.setLocation(new LatLng(10.823553f, 106.639459f));
		canhBao5.setTitle("Canh bao 5");
		listGiaoThong.add(canhBao5);

	}

	/**
	 * 
	 * show dialog to view detail warring
	 */
	private void openDetailWarring(int flag, int pos) {

		Warning canhBao = getWarning(flag, pos);
		Toast.makeText(activity, canhBao.getTitle(), 0).show();
	}

	/**
	 * getWarning by pos and flag listWarnign
	 * 
	 * @return
	 */

	public Warning getWarning(int flag, int pos) {
		Warning warning = null;
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

	// ======================================
	// OTHERS
	// ======================================

	// ======================================
	// GETTERS AND SETTERS
	// ======================================

	public int getNumberAllWarning() {
		return listChotCongAn.size() + listGiaoThong.size() + listTaiNan.size()
				+ listTrangThai.size();
	}

	public  List<Warning> getAllWarning() {
		List<Warning> listAllWarning = new ArrayList<Warning>();
		listAllWarning.addAll(listChotCongAn);
		listAllWarning.addAll(listGiaoThong);
		listAllWarning.addAll(listTaiNan);
		listAllWarning.addAll(listTrangThai);

		return listAllWarning;

	}

	public  List<Warning> getListChotCongAn() {
		return listChotCongAn;
	}

	public  void setListChotCongAn(List<Warning> listChotCongAn) {
		listChotCongAn = listChotCongAn;
	}

	public  List<Warning> getListGiaoThong() {
		return listGiaoThong;
	}

	public  void setListGiaoThong(List<Warning> listGiaoThong) {
		listGiaoThong = listGiaoThong;
	}

	public  List<Warning> getListTrangThai() {
		return listTrangThai;
	}

	public  void setListTrangThai(List<Warning> listTrangThai) {
		listTrangThai = listTrangThai;
	}

	public  List<Warning> getListTaiNan() {
		return listTaiNan;
	}

	public  void setListTaiNan(List<Warning> listTaiNan) {
		listTaiNan = listTaiNan;
	}

}
