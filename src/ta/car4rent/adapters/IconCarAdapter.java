package ta.car4rent.adapters;

import ta.car4rent.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class IconCarAdapter extends BaseAdapter{
	
	private Activity activity;
	private static LayoutInflater inflater = null;

	int arr_car_images[];
	public IconCarAdapter(Activity a, int[] data) {
		arr_car_images = data;
		activity = a;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr_car_images.length;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.row_icon_car, null);
		}
		
		ImageView imageIcon = (ImageView) vi.findViewById(R.id.iconCar);
		int id = arr_car_images[position];
		imageIcon.setImageResource(id);
		return vi;
	}

}
