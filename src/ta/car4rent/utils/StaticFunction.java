package ta.car4rent.utils;

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
	

	/**
	 * Convert price string 1000000 to 1.000.000
	 * 
	 * @param price
	 * @return
	 */
	public static String formatVnMoney(String price) {
		String s = "";
		for (int i = price.length() - 1; i >= 0; i--) {
			if ((price.length() - i) % 3 == 0 && i != 0) {
				s = "." + price.charAt(i) + s;
			} else {
				s = price.charAt(i) + s;
			}
		}
		return s;
	}
}
