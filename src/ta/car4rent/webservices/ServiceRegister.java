package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServiceRegister extends WebserviceBasePOST{

	public void registerUser(JSONObject userInfo) {
		mToken = ConfigureData.token;
		String Url = ApiUrl.SERVER_URL + "Register";
		postJSONObject(Url, userInfo);
	}
}
