package ta.car4rent.webservices;

public class ServiceGetDistrics extends WebserviceBaseGET{

	
	public void getDistrics(int id) {
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetDistricts/";		
		getJSONObject(url + id);
	}
	
}
