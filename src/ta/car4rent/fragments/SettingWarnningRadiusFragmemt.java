package ta.car4rent.fragments;

import com.google.analytics.tracking.android.EasyTracker;

import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * This Fragment Should be change to Diaglog or someting else
 * @author anhle
 *
 */
public class SettingWarnningRadiusFragmemt extends Fragment implements OnSeekBarChangeListener{
	public static final int MAX_WARNING_RADIUS = 10;
	
	private SeekBar sbWarningRadius;
	private TextView tvWarningRadius;
	
	public SettingWarnningRadiusFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MainActivity.Instance.showActionFilterSpinner(false);
		
		ConfigureData.currentScreen = 2;

		View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
		tvWarningRadius = (TextView) rootView.findViewById(R.id.tvWarningRadius);
		tvWarningRadius.setText(ConfigureData.radiusWarning + " Km");
		
		sbWarningRadius = (SeekBar) rootView.findViewById(R.id.sbWarningRadius);
		sbWarningRadius.setMax(MAX_WARNING_RADIUS);
		sbWarningRadius.setProgress(ConfigureData.radiusWarning);
		sbWarningRadius.setOnSeekBarChangeListener(this);
		
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this.getActivity()).activityStart(this.getActivity()); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this.getActivity()).activityStop(this.getActivity()); // Add this method.
	}
	
	@Override
	public void onDestroyView() {
		ConfigureData.currentScreen = 0;
		super.onDestroyView();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
		ConfigureData.radiusWarning = progress;
		tvWarningRadius.setText(ConfigureData.radiusWarning + " Km");
		
		// Save to Shared preference
		ConfigureData.saveRadiusSharedPreference();		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
