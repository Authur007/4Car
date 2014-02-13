package ta.car4rent.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class content list of data have text and value(id)
 * you can determine which item selected, you can use SpinnerDataList
 * to save data and status of Spinner ,checkBox, or something like that.
 *  
 * @author anhle
 *
 */
public class SpinnerDataList {
	private ArrayList<SpinnerDataItem> mArrItem = new ArrayList<SpinnerDataItem>();
	private int mSelectedIndex;
	
	public SpinnerDataList(ArrayList<SpinnerDataItem> items, int selectedIndex) {
		this.mArrItem = items;
		this.mSelectedIndex = selectedIndex;
	}
	
	/**
	 * Create SpinnerDataList from JSONArray has format
	 * 
	 * "key":[{
		"value":2147483647,
		"text":"String content",
		"selected":true
		}]
	 * 
	 * 
	 * @param items
	 * @throws JSONException
	 */
	public SpinnerDataList(JSONArray items) throws JSONException {		
		mSelectedIndex = 0;
		
		for (int i = 0; i < items.length(); i++) {
			SpinnerDataItem newItem = new SpinnerDataItem(items.getJSONObject(i).getInt("value"), items.getJSONObject(i).getString("text"));
			if (items.getJSONObject(i).getBoolean("selected") == true) {
				mSelectedIndex = i;
			}
			
			mArrItem.add(newItem);
		}
	}
	
	public String[] getArraySpinnerText() {
		String[] arrString = new String[mArrItem.size()];
		for (int i = 0; i < mArrItem.size(); i++) {
			arrString[i] = mArrItem.get(i).getText();
		}
		
		return arrString;
	}
	
	public int getSelectedIndex() {
		return mSelectedIndex;
	}
	
	public SpinnerDataItem getSelectedItem() {
		return mArrItem.get(mSelectedIndex);
	}
	
	public void setSelectedItem(int index) {
		mSelectedIndex = index;
	}
	
	public int getSize() {
		return mArrItem.size();
	}
	
}
