package ta.car4rent.utils;

import ta.car4rent.configures.ConfigureData;
import android.content.Context;

import android.view.inputmethod.InputMethodManager;

public class StaticFunction {
	/**
	 * Hides virtual keyboard
	 */
	public static void hideKeyboard() {
		InputMethodManager in = (InputMethodManager) ConfigureData.activityMain
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(ConfigureData.activityMain.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
