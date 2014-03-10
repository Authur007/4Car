package ta.car4rent.webservices;

public class ServiceWarningGET extends WebserviceBaseGET {

	public void getAllWarningNews(double longitude, double latitude, int km) {
		String url = ApiUrl.SERVER_URL + "GetReports/"
				+ latitude + "/" + longitude + "/" + km;
		getJSONObject(url);
	}

	public void getReportComent(int postId) {
		String url =  ApiUrl.SERVER_URL + "GetReportComments/"
				+ postId;

		getJSONObject(url);
	}

}
