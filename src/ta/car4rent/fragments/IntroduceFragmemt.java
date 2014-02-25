package ta.car4rent.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.google.analytics.tracking.android.EasyTracker;

import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class IntroduceFragmemt extends Fragment {

	private TextView tvIntroduceHTML;

	public IntroduceFragmemt() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MainActivity.Instance.showActionFilterSpinner(false);
				
		ConfigureData.currentScreen = 2;

		String introHTML = get4CarsIntroHTML();
		View rootView = inflater.inflate(R.layout.fragment_introduce,
				container, false);
		// tvIntroduceHTML =
		// (TextView)rootView.findViewById(R.id.tvIntroduceHTML);
		// tvIntroduceHTML.setText(Html.fromHtml(introHTML));
		return rootView;
	}

	private String get4CarsIntroHTML() {
		String htmlText = "<h1>4Cars</h1>";
		try {
			InputStream is = getActivity().getApplicationContext().getAssets()
					.open("4cars_intro.html");
			Scanner sc = new Scanner(is);
			while (sc.hasNext()) {
				htmlText += sc.nextLine() + "\n";
			}

			// do something with stream
		} catch (IOException e) {
			e.printStackTrace();
		}

		return htmlText;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onDestroyView() {
		ConfigureData.currentScreen = 0;
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this.getActivity()).activityStart(
				this.getActivity()); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this.getActivity()).activityStop(
				this.getActivity()); // Add this method.
	}

}
