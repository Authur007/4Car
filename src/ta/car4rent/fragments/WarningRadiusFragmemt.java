package ta.car4rent.fragments;

import ta.car4rent.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This Fragment Should be change to Diaglog or someting else
 * @author anhle
 *
 */
public class WarningRadiusFragmemt extends Fragment{
	
	public WarningRadiusFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_introduce,
				container, false);
		return rootView;
	}

}
