package com.zmyh.r.handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeiXinHandler {

	private final static String appId = "wxcbb993d73cd14439";

	public static String getShareAppId() {
		return appId;
	}

	private static WXMediaMessage getWinXinMessage(Context context,
												   String title, String description, String shareUrl) {

		Log.i("getWinXin", "微信message");
		WXWebpageObject wxObj = new WXWebpageObject();
		wxObj.webpageUrl = shareUrl;
		WXMediaMessage msg = new WXMediaMessage(wxObj);
		if (!title.equals("")) {
			msg.title = title;
		} else {
			msg.title = "苗木经纪人，苗木记录神器!~!~~";
		}
		if (!description.equals("")) {
			msg.description = description;
		} else {
			msg.description = "苗木经纪人，苗木记录神器!~!~~";
		}

		Bitmap bmp = BitmapTool.getLogo(context);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 80, 80, true);
		bmp.recycle();
		byte[] b = BitmapTool.bmpToByte(thumbBmp);
		msg.thumbData = b;

		return msg;

	}

	public static void shareWeiXin(Context context, String title,
								   String description, String shareUrl) {
		IWXAPI api = WXAPIFactory.createWXAPI(context,
				WeiXinHandler.getShareAppId(), true);
		api.registerApp(WeiXinHandler.getShareAppId());

		WXMediaMessage msg = getWinXinMessage(context, title, description,
				shareUrl);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	public static void shareFriend(Context context, String title,
								   String description, String shareUrl) {
		IWXAPI api = WXAPIFactory.createWXAPI(context,
				WeiXinHandler.getShareAppId(), true);
		api.registerApp(WeiXinHandler.getShareAppId());

		WXMediaMessage msg = getWinXinMessage(context, title, description,
				shareUrl);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

}
