package ta.car4rent.webservices;

public class ServiceWarningGET extends WebserviceBaseGET {

	public void getAllWarningNews(double longitude, double latitude, int km) {
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetReports/"
				+ latitude + "/" + longitude + "/" + km;
		getJSONObject(url);
	}

	public void getReportComent(int postId) {
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetReportComments/"
				+ postId;

		getJSONObject(url);
	}

}
