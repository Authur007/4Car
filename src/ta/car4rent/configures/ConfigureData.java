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
		 * hold type of post warning
		 * Police =1
		 * traffic =2
		 * accident =3
		 * status =4
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
		
		//============================================
		// STATIC SEARCH KEY
		//============================================
		public static HashMap<Integer, String> listCity = new HashMap<Integer, String>();
		public static HashMap<Integer, String> listDistric = new HashMap<Integer, String>();
		
}
