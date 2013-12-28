package ta.car4rent.fragments;

import ta.car4rent.R;
import ta.car4rent.utils.StaticFunction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostNewFeedFragmemt extends Fragment {

	public PostNewFeedFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_search_car,
				container, false);
		return rootView;
	}

	@Override
	public void onDestroyView() {
		StaticFunction.hideKeyboard();
		super.onDestroyView();
	}
}
