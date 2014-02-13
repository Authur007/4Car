package ta.car4rent.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * DATE AND TIME PICKER
 * 
 * @author anhle
 * 
 */
public class DatePickerFragment extends DialogFragment {
	public static int pickerType = 0;
	private Calendar mCalendar;
	
	private DatePickerDialog.OnDateSetListener mOnDatasetListener;

	public DatePickerDialog.OnDateSetListener getOnDatasetListener() {
		return mOnDatasetListener;
	}

	public void setDefaultStringDate(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatter = sdf;
		mCalendar = Calendar.getInstance();
		try {
			Date date = formatter.parse(strDate);
			mCalendar.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setOnDatasetListener(
			DatePickerDialog.OnDateSetListener onDatasetListener) {
		this.mOnDatasetListener = onDatasetListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}
		
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH);
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), mOnDatasetListener, year, month, day);
	}

}
