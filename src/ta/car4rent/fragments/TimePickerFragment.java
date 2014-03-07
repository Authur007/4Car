package ta.car4rent.fragments;

import java.util.Calendar;

import ta.car4rent.utils.CustomTimePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.facebook.android.BuildConfig;

public class TimePickerFragment extends DialogFragment{

	private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
	private Calendar mCalendar;
	
	public TimePickerDialog.OnTimeSetListener getOnTimeSetListener() {
		return mOnTimeSetListener;
	}

	public void setOnTimeSetListener(
		TimePickerDialog.OnTimeSetListener onTimeSetListener) {
		this.mOnTimeSetListener = onTimeSetListener;
	}
	
	public void setDefaultTime(String strTime) {
		mCalendar = Calendar.getInstance();
		
		try {
			mCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strTime.trim().split(":")[0]));
			mCalendar.set(Calendar.MINUTE, Integer.parseInt(strTime.trim().split(":")[1]));
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}
		
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);

		return new CustomTimePickerDialog(getActivity(), mOnTimeSetListener, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

		super.onDismiss(dialog);

	}
}
