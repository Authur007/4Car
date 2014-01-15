package ta.car4rent.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * DATE AND TIME PICKER
 * 
 * @author anhle
 * 
 */
public class DatePickerFragment extends DialogFragment {
	// implements DatePickerDialog.OnDateSetListener {

	private DatePickerDialog.OnDateSetListener mOnDatasetListener;

	public DatePickerDialog.OnDateSetListener getOnDatasetListener() {
		return mOnDatasetListener;
	}

	public void setOnDatasetListener(
			DatePickerDialog.OnDateSetListener onDatasetListener) {
		this.mOnDatasetListener = onDatasetListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), mOnDatasetListener, year, month, day);
	}

}
