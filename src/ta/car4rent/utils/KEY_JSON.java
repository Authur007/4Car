package ta.car4rent.utils;

public class KEY_JSON {

	//===========================================
	// SEARCH
	//===========================================
	public static final String KEY_CITIES = "cities";
	public static final String KEY_DRIVER = "drivers";
	public static final String KEY_SEAT = "styles";
	public static final String KEY_FROM_DATE = "";
	public static final String KEY_TO_DATE = "";
	public static final String KEY_DISTRICS = "districts";

	public static final String KEY_BRANDS = "makes";
	public static final String KEY_MODELS = "models";
	public static final String KEY_CAR_OWNER = "carOwners";

	public static final String KEY_FROM_PRICES = "fromPrices";
	public static final String KEY_TO_PRICES = "toPrices";
	public static final String KEY_TRANSMISSTIONS = "transmissions";

	// key to get data from JsonObject
	/*
	 * { "selected" : false, "text" : "Tp. Đà Nẵng", "value" : 7
	 */
	public static final String TAG_ID = "value";
	public static final String TAG_TEXT = "text";
	public static final String TAG_ID_DISTRICS = "id";
	public static final String TAG_NAME_DISTRICS = "name";


	/**
	 *  <startDate>String content</startDate>
  <endDate>String content</endDate>
  <fromPrice>2147483647</fromPrice>
  <toPrice>2147483647</toPrice>
  <cityId>2147483647</cityId>
  <districtId>2147483647</districtId>
  <hasCarDriver>2147483647</hasCarDriver>
  <cityIdFrom>2147483647</cityIdFrom>
  <districtIdFrom>2147483647</districtIdFrom>
  <cityIdTo>2147483647</cityIdTo>
  <districtIdTo>2147483647</districtIdTo>
  <makeId>2147483647</makeId>
  <modelId>2147483647</modelId>
  <styleId>2147483647</styleId>
  <transmissionId>2147483647</transmissionId>
  <carOwnerId>2147483647</carOwnerId>
	 */
	
	//==============================================
	//KEY POST SEARCH
	//==============================================
	public static final String KEY_POST_START_DATE = "startDate";
	public static final String KEY_POST_END_DATE = "endDate";
	public static final String KEY_POST_FROM_PRICE = "fromPrice";
	public static final String KEY_POST_TO_PRICE = "toPrice";
	public static final String KEY_POST_CITY_ID = "cityId";
	public static final String KEY_POST_DISTRIC_ID = "districtId";
	
	public static final String KEY_POST_CITY_ID_FROM = "cityIdFrom";
	public static final String KEY_POST_DISTRIC_ID_FROM = "districtIdFrom";

	public static final String KEY_POST_CITY_ID_TO = "cityIdTo";
	public static final String KEY_POST_DISTRIC_ID_TO = "districtIdTo";


	public static final String KEY_POST_HAS_DRIVER = "hasCarDriver";
	public static final String KEY_POST_BRAND_ID = "makeId";
	public static final String KEY_POST_MODEL_ID = "modelId";

	public static final String KEY_POST_SEAT_ID = "styleId";
	public static final String KEY_POST_TRANSMISSION_ID = "transmissionId";
	public static final String KEY_POST_CAROWNER_ID = "carOwnerId";
	
	
}
