package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServiceRegister extends WebserviceBasePOST{

	public void registerUser(JSONObject userInfo) {
		mToken = ConfigureData.token;
		String Url = "http://4carsvn.cloudapp.net:4411/Common/Register";
		postJSONObject(Url, userInfo);
	}
}
