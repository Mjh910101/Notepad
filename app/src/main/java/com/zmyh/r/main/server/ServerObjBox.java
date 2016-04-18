package com.zmyh.r.main.server;

import com.zmyh.r.box.ServerObj;

public class ServerObjBox {

	private static ServerObj obj;

	public static void saveServerObj(ServerObj s) {
		deleteServerObj();
		obj = s;
	}

	public static ServerObj getServerObj() {
		return obj;
	}

	public static void deleteServerObj() {
		if (obj != null) {
			obj = null;
		}
	}
}
