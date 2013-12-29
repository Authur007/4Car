package ta.car4rent.webservices;

public class ServiceGetModels extends WebserviceBaseGET{

	public void getModels(int id) {
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetModels/";		
		getJSONObject(url + id);
	}
}
