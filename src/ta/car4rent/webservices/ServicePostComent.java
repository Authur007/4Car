package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServicePostComent extends WebserviceBasePOST {

	public void postComent(JSONObject objectComent) {
		mToken = ConfigureData.token;
		String Url = "http://4carsvn.cloudapp.net:4411/Common/PostComment";
		postJSONObject(Url, objectComent);
	}

}
