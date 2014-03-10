package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServicePostComent extends WebserviceBasePOST {

	public void postComent(JSONObject objectComent) {
		mToken = ConfigureData.token;
		String Url = ApiUrl.SERVER_URL + "PostComment";
		postJSONObject(Url, objectComent);
	}

}
