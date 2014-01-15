package ta.car4rent.fragments;

import java.util.Calendar;

import ta.car4rent.utils.CustomTimePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

public class TimePickerFragment extends DialogFragment {

	private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
	
	
	public TimePickerDialog.OnTimeSetListener getOnTimeSetListener() {
		return mOnTimeSetListener;
	}

	public void setOnTimeSetListener(
			TimePickerDialog.OnTimeSetListener onTimeSetListener) {
		this.mOnTimeSetListener = onTimeSetListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = 00;

		return new CustomTimePickerDialog(getActivity(), mOnTimeSetListener, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

		super.onDismiss(dialog);

	}
}
