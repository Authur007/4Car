package ta.car4rent.adapters;

import java.util.ArrayList;

import ta.car4rent.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<NavigationDrawerListItem> mNavigationDrawerListItems;

	/**
	 * Constructor
	 * 
	 * @param c
	 */
	public NavigationDrawerAdapter(Context c,
			ArrayList<NavigationDrawerListItem> navigationDrawerListItems) {
		mContext = c;
		mNavigationDrawerListItems = navigationDrawerListItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNavigationDrawerListItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// inflater to create the List Item
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listItem = null;
		
		switch (mNavigationDrawerListItems.get(position).getType()) {
		case NavigationDrawerListItem.SCREEN_IN_CATEGORY:
			
			listItem = inflater.inflate(R.layout.navigation_drawer_screen_in_category_item, parent, false);
			
			ImageView ivIconLeft = (ImageView) listItem.findViewById(R.id.ivIconLeft);
			ivIconLeft.setImageResource(mNavigationDrawerListItems.get(position).getIconLeft());
			
			TextView tvText = (TextView) listItem.findViewById(R.id.tvText);
			tvText.setText(mNavigationDrawerListItems.get(position).getText());

			TextView tvCounter = (TextView) listItem.findViewById(R.id.tvCounter);
			tvCounter.setText(mNavigationDrawerListItems.get(position).getCounter());
			
			break;
			
			
		case NavigationDrawerListItem.CATEGORY_NAME:
			
			listItem = inflater.inflate(R.layout.navigation_drawer_category_name_item, parent, false);
			listItem.setOnClickListener(null);
			TextView tvCategoryName = (TextView) listItem.findViewById(R.id.tvCategoryName);
			tvCategoryName.setText(mNavigationDrawerListItems.get(position).getText());
			break;
		}
		
		return listItem;
	}
}
