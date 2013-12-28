package ta.car4rent.webservices;

public interface OnPostJsonListener {
	public void onPostJsonCompleted(String response);
	public void onPostJsonFail(String response);
}
