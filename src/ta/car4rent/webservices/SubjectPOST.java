package ta.car4rent.webservices;

public interface SubjectPOST {
	public void addOnPostJsonListener(OnPostJsonListener o);
	public void removeOnPostJsonObserver(OnPostJsonListener o);
	public void notifyChange(int type);
}
