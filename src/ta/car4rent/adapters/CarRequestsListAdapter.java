package ta.car4rent.adapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.fragments.CarRequestDetailFragment;
import ta.car4rent.fragments.ManageCarRequestesFragment;
import ta.car4rent.webservices.OnGetJsonListener;
import ta.car4rent.webservices.ServiceCloseCarRequest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CarRequestsListAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
	private FragmentTransaction ft;
	private LayoutInflater inflater = null;
	private ArrayList<JSONObject> data = new ArrayList<JSONObject>();
	private int mRequestId;

	public CarRequestsListAdapter(Context ctx, FragmentTransaction ft, ArrayList<JSONObject> listCarResult) {
		mContext = ctx;
		this.ft = ft;
		data = listCarResult;
		inflater = (LayoutInflater) ConfigureData.activityMain.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		int requestId = 0;
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.row_car_requests_item, null);
		}
		
		try {
			requestId = data.get(position).getInt("id");
			// Load car request info
			String strCity = data.get(position).getString("city");
			if (!"".equals(strCity)) {
				((TextView)vi.findViewById(R.id.tvCity)).setVisibility(View.VISIBLE);
				((TextView)vi.findViewById(R.id.tvCity)).setText(ConfigureData.activityMain.getString(R.string.str_start_place) + " " + strCity);
			} else {
				((TextView)vi.findViewById(R.id.tvCity)).setVisibility(View.GONE);
			}

			String strDestination = data.get(position).getString("destination");
			if (!"".equals(strDestination)) {
				((TextView)vi.findViewById(R.id.tvDestination)).setVisibility(View.VISIBLE);
				((TextView)vi.findViewById(R.id.tvDestination)).setText(ConfigureData.activityMain.getString(R.string.str_destination_place) + " " + strDestination);
			} else {
				((TextView)vi.findViewById(R.id.tvDestination)).setVisibility(View.GONE);
			}
			
			String strFromPrice = data.get(position).getString("fromPrice");
			String strToPrice = data.get(position).getString("toPrice");
			if ("499999".equals(strFromPrice)) {
				((TextView)vi.findViewById(R.id.tvPrice)).setText(ConfigureData.activityMain.getString(R.string.str_price_below) + " 500.000 " + ConfigureData.activityMain.getString(R.string.vnd_day));
			} else if ("5999999".equals(strFromPrice)){
				((TextView)vi.findViewById(R.id.tvPrice)).setText(ConfigureData.activityMain.getString(R.string.str_price_above) + " 5.000.000 " + ConfigureData.activityMain.getString(R.string.vnd_day));
			} else if (strFromPrice.equals("0") && strToPrice.equals("0")){
				((TextView)vi.findViewById(R.id.tvPrice)).setVisibility(View.GONE);
			} else {
				((TextView)vi.findViewById(R.id.tvPrice)).setText(ConfigureData.activityMain.getString(R.string.str_price) + " " + convertPriceToDecimal(strFromPrice) + " - " + convertPriceToDecimal(strToPrice) + " " + ConfigureData.activityMain.getString(R.string.vnd_day));
			}
			
			String strCreateDate = data.get(position).getString("createDate").split(" ")[0];
			((TextView)vi.findViewById(R.id.tvCreateDate)).setText(strCreateDate);
			
			String strExpirationDate = data.get(position).getString("expirationDate").split(" ")[0];
			((TextView)vi.findViewById(R.id.tvExpirationDate)).setText(strExpirationDate);
			
			String strStartDate = data.get(position).getString("startDate").split(" ")[0];
			((TextView)vi.findViewById(R.id.tvStartDate)).setText(strStartDate);
			
			String strStatus = data.get(position).getString("status");
			if (strStatus.charAt(0) == 'M') {
				((TextView)vi.findViewById(R.id.tvStatus)).setText(strStatus);
				((ImageButton)vi.findViewById(R.id.ivStatus)).setImageResource(R.drawable.btn_open);
				((ImageButton)vi.findViewById(R.id.ivStatus)).setOnClickListener(this);
				((ImageButton)vi.findViewById(R.id.ivStatus)).setTag(requestId);
			} else {
				((TextView)vi.findViewById(R.id.tvStatus)).setText("Đã\nđóng");
				((ImageButton)vi.findViewById(R.id.ivStatus)).setImageResource(R.drawable.ic_close_default);
				((ImageButton)vi.findViewById(R.id.ivStatus)).setOnClickListener(null);
			}
			
			((ImageButton)vi.findViewById(R.id.transparentView)).setOnClickListener(this);
			((ImageButton)vi.findViewById(R.id.transparentView)).setTag(requestId);
			
		} catch (JSONException e) {
			Log.e("ERROR SHOW CAR REQUEST LIST", e.toString());
		} 

		return vi;
	}

	private String convertPriceToDecimal(String strPrice) {
		String s = "";
		for(int i = strPrice.length() - 1; i >= 0; i--) {
			if ((strPrice.length() - i) % 3 == 0 && i != 0) {
				s = "." + strPrice.charAt(i) + s;
			} else {
				s = strPrice.charAt(i) + s;
			}
		}
		
		return s;
	}

	@Override
	public void onClick(View v) {
		int requestId = Integer.parseInt(v.getTag().toString());
		mRequestId = requestId;
		
		// TODO Auto-generated method stub
		if (v.getId() == R.id.transparentView) {
			if (requestId != 0) {
				// Insert the fragment by replacing any existing
				CarRequestDetailFragment fragment = new CarRequestDetailFragment();
				fragment.setCarRequestID(requestId);			
				ft.replace(R.id.content_frame, fragment);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				
				// add to back track
				ft.addToBackStack("CarRequest");
				ft.commit();
			}
		} else {
			// Show confirm close dialog
			(new AlertDialog.Builder(mContext))
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Xác nhận !")
			.setMessage("Bạn có chắc muốn đóng tin này không.")
			.setNegativeButton("Không", null)
			.setPositiveButton("Có", changeStatus)
			.create()
			.show();
		}
	}
	
	// Handle when apply close request
	private DialogInterface.OnClickListener changeStatus = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ServiceCloseCarRequest serviceCloseCarRequest = new ServiceCloseCarRequest();
			serviceCloseCarRequest.addOnGetJsonListener(new OnGetJsonListener() {
				
				@Override
				public void onGetJsonFail(String response) {
					// show error
					AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
					alertDialog.setIcon(R.drawable.ic_error);
					alertDialog.setTitle("Rất tiếc!");
					alertDialog.setMessage("Đóng tin không thành công.");
					alertDialog.setButton("Kết thúc",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							});
					alertDialog.show();
				}
				
				@Override
				public void onGetJsonCompleted(String response) {
					// TODO Auto-generated method stub
					try {
						JSONObject responseObject = new JSONObject(response);
						if (responseObject.getBoolean("status")) {
							// Change status now
							// success
							AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
							alertDialog.setIcon(R.drawable.ic_launcher);
							alertDialog.setTitle("Đóng tin thành công !");
							alertDialog.setMessage("Nhấn kết thúc để tải lại dữ liệu.");
							alertDialog.setButton("Kết thúc",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											// Insert the fragment by replacing any existing
											// fragment
											ManageCarRequestesFragment fragment = new ManageCarRequestesFragment();
											ft.replace(R.id.content_frame, fragment).commit();

										}
									});
							alertDialog.show();
							
						}
					} catch (Exception e) {
						// show error
						AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
						alertDialog.setIcon(R.drawable.ic_error);
						alertDialog.setTitle("Ráº¥t tiáº¿c !");
						alertDialog.setMessage("Ä�Ã³ng tin khÃ´ng thÃ nh cÃ´ng.");
						alertDialog.setButton("Ä�á»“ng Ã½",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								});
						alertDialog.show();
						
						e.printStackTrace();
					}
				}
			});
			
			serviceCloseCarRequest.closeCarRequest(mRequestId);
			
		}
	};
	
}
