package ta.car4rent.objects;


public class SpinnerDataItem {
	private int mValue;
	private String mText;
	
	public SpinnerDataItem(int value, String text) {
		mValue = value;
		mText = text;
	}
	
	public int getValue() {
		return mValue;
	}
	public void setValue(int value) {
		this.mValue = value;
	}
	
	public String getText() {
		return mText;
	}
	public void setText(String text) {
		this.mText = text;
	}
}
