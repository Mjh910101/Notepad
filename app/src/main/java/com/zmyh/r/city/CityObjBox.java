package com.zmyh.r.city;

import com.zmyh.r.box.CityObj;

public class CityObjBox {

	private static CityObj cityObj = null;

	public static void saveCityObj(CityObj obj) {
		if (cityObj != null) {
			cityObj = null;
		}
		cityObj = obj;
	}

	public static CityObj getSaveCityObj() {
		return cityObj;
	}
}
