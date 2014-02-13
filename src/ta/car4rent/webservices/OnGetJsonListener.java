package ta.car4rent.webservices;

public interface OnGetJsonListener {
	public void onGetJsonCompleted(String response);
	public void onGetJsonFail(String response);
}
