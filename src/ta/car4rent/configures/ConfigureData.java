package ta.car4rent.configures;

import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.activities.GoogleMapsActivity;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.lazyloading.ImageLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.model.GraphUser;
import com.google.android.gms.maps.GoogleMap;

public class ConfigureData {
	// hold activity to use by all fragment
	public static MainActivity activityMain;
	public static GoogleMapsActivity activityGoogleMaps;
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
	public static boolean isCalledFromCarRequested = false;;

	// Saved setting data
	private static SharedPreferences preferences;
	public static int radiusWarning;
	public static int iconCarResId;
	// adapter to show full screen in detail screen
	public static String[] FullScreenImage;
	public static int currentScreen = 0;
	public static int randomLocationInMap =1;
	public static JSONObject carRequestDetailObject = new JSONObject();
	
	public static boolean isOnline() {
		boolean connected = false;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) activityMain.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isAvailable() && networkInfo.isConnected()) {
					connected = true;
				}
					
			}
		} catch (Exception e) {
			System.out.println("CheckConnectivity Exception: " + e.getMessage());
			Log.v("connectivity", e.toString());
		}
		
		return connected;
	}
	
	public static void loadSharedPreference() {
		preferences = PreferenceManager.getDefaultSharedPreferences(activityMain);
		radiusWarning = preferences.getInt("radiusWarning", 3);
		iconCarResId = preferences.getInt("iconCar", R.drawable.xe1);
	}
	
	public static void saveRadiusSharedPreference() {
		// Retrieve an editor to modify the shared preferences.
		SharedPreferences.Editor editor = preferences.edit();				
		editor.putInt("radiusWarning", radiusWarning);
		// Commit changes.
		editor.commit();
	}
	
	public static void saveIconCarSharedPreference() {
		// Retrieve an editor to modify the shared preferences.
		SharedPreferences.Editor editor = preferences.edit();				
		editor.putInt("iconCar", iconCarResId);
		// Commit changes.
		editor.commit();
	}
	
}
