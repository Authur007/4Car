package ta.car4rent.configures;

import java.util.HashMap;

import org.json.JSONObject;
import ta.car4rent.lazyloading.ImageLoader;
import android.app.Activity;

public class ConfigureData {
	// hold activity to use by all fragment
	public static Activity activityMain;
	public static Activity activityGoogleMaps;
	/**
	 * hold type of post warning Police =1 traffic =2 accident =3 status =4
	 */
	public static int flagTypePostWarningFragment = 0;
	public static boolean isPostNewsScreen = false;
	public static boolean isEnableShowChoosePostWarning = true;
	public static boolean isEnableShowPostWarningFragment = true;

	public static boolean isLogged = false;
	public static JSONObject userAccount;
	public static ImageLoader imageLoader;
	public static String userName;
	public static String password;

	// ============================================
	// STATIC SEARCH KEY
	// ============================================
	public static boolean isNeedToReload = true;
	// list data of spinner
	public static HashMap<String, JSONObject> hashMapCities = new HashMap<String, JSONObject>();
	public static String[] cities = null;

	public static HashMap<String, JSONObject> hashMapDistricsFrom = new HashMap<String, JSONObject>();
	public static String[] districsFrom = null;

	public static HashMap<String, JSONObject> hashMapDistricsTo = new HashMap<String, JSONObject>();
	public static String[] districsTo = null;

	public static HashMap<String, JSONObject> hashMapBrand = new HashMap<String, JSONObject>();
	public static String[] brands = null;

	public static HashMap<String, JSONObject> hashMapModel = new HashMap<String, JSONObject>();
	public static String[] models = null;

	public static HashMap<String, JSONObject> hashMapSeat = new HashMap<String, JSONObject>();
	public static String[] seats = null;

	public static HashMap<String, JSONObject> hashMapTransmission = new HashMap<String, JSONObject>();
	public static String[] transmissions = null;

	public static HashMap<String, JSONObject> hashMapCarOwner = new HashMap<String, JSONObject>();
	public static String[] carOwners = null;

	public static HashMap<String, JSONObject> hashMapPriceFrom = new HashMap<String, JSONObject>();
	public static String[] pricesFrom = null;

	public static HashMap<String, JSONObject> hashMapPriceTo = new HashMap<String, JSONObject>();
	public static String[] pricesTo = null;

	// ============================================
	// STATIC SEARCH RESULT
	// ============================================
	public static JSONObject jsonSearchResult = new JSONObject();
	
}
