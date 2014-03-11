package ta.car4rent.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class IntroduceFragmemt extends Fragment {

	private TextView tvIntroduceHTML;
	private TextView tvPhoneContact;
	private TextView tvEmailContact;
	

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
		
		tvPhoneContact = (TextView)rootView.findViewById(R.id.tvPhoneContact);
		tvPhoneContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ "0909390169"));
				getActivity().startActivity(intentCall);
			}
		});
		
		tvEmailContact = (TextView)rootView.findViewById(R.id.tvEmailContact);
		tvEmailContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentEmail = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:" + "4cars.vn@gmail.com"));
				intentEmail.putExtra("subject", "Liên hệ 4cars [" + SearchCarFragmemt.getCurrenttDateFormated() + "]");
				getActivity().startActivity(intentEmail);
			}
		});
		
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
