package ta.car4rent.webservices;

public interface SubjectGET {
	public void addOnGetJsonListener(OnGetJsonListener o);
	public void removeOnGetJsonListener(OnGetJsonListener o);
	public void notifyChange(int type);
}
