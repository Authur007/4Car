package ta.car4rent.configures;

import org.json.JSONObject;

import com.facebook.model.GraphUser;
import com.google.android.gms.maps.GoogleMap;

import ta.car4rent.lazyloading.ImageLoader;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

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

	public static ImageLoader imageLoader = null;
	public static boolean isLogged = false;
	public static String token = null;
	public static JSONObject userAccount = null;
	public static String userName = null;
	public static String password = null;
	public static int userId = 0;
	/**
	 * graph user return when login with facebook
	 */
	public static  boolean isUsingFBLoginButton = false;
	public static GraphUser graphUser = null;

	public static GoogleMap googleMap;
	public static boolean isCalledFromMaps = false;
	
	// Saved setting data
	private static SharedPreferences preferences;
	public static int radiusWarning;
	
	public static void loadSharedPreference() {
		preferences = PreferenceManager.getDefaultSharedPreferences(activityMain);
		radiusWarning = preferences.getInt("radiusWarning", 3);
	}
	
	public static void saveSharedPreference() {
		// Retrieve an editor to modify the shared preferences.
		SharedPreferences.Editor editor = preferences.edit();				
		editor.putInt("radiusWarning", radiusWarning);
		// Commit changes.
		editor.commit();
	}
}
