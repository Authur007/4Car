package ta.car4rent.utils;

import ta.car4rent.configures.ConfigureData;
import android.app.Activity;
import android.content.Context;

import android.view.inputmethod.InputMethodManager;

public class StaticFunction {
	/**
	 * Hides virtual keyboard
	 */
	public static void hideKeyboard(Activity activity) {
		try{
			InputMethodManager in = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			in.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		
	}
}
