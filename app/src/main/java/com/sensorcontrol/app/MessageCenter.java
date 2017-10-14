package com.sensorcontrol.app;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizLogPrintLevel;
import com.sensorcontrol.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCenter {
	private static MessageCenter mCenter;
	int flag = 0;

	GosDeploy gosDeploy;

	private int SETCLOUD = 1111;

	private MessageCenter(Context c) {
		if (mCenter == null) {
			init(c);
		}
	}

	private void init(Context c) {

		gosDeploy = new GosDeploy(c);
		ConcurrentHashMap<String, String> appInfo = new ConcurrentHashMap();
		String AppID = GosDeploy.setAppID();
		String AppSecret = GosDeploy.setAppSecret();
		if (TextUtils.isEmpty(AppID) || AppID.contains("your_app_id") || TextUtils.isEmpty(AppSecret)
				|| AppSecret.contains("your_app_secret")) {
			String AppID_Toast = c.getString(R.string.AppID_Toast);
			if (flag == 0) {
				Toast.makeText(c, AppID_Toast, Toast.LENGTH_LONG).show();
			}
			flag++;
		} else {
			// 启动SDK
			ConcurrentHashMap<String, String> serverMap = new ConcurrentHashMap<String, String>();

			serverMap.put("openAPIInfo", TextUtils.isEmpty((String) GosDeploy.infoMap.get("openAPI_URL"))
					? "api.gizwits.com" : (String) GosDeploy.infoMap.get("openAPI_URL"));
			serverMap.put("siteInfo", TextUtils.isEmpty((String) GosDeploy.infoMap.get("site_URL")) ? "site.gizwits.com"
					: (String) GosDeploy.infoMap.get("site_URL"));
			serverMap.put("pushInfo", (String) GosDeploy.infoMap.get("push_URL"));
			//GizWifiSDK.sharedInstance().startWithAppID(c, AppID, GosDeploy.setProductKeyList(), serverMap);

			appInfo.put("appId", GosDeploy.setAppID());
			appInfo.put("appSecret", GosDeploy.setAppSecret());
//			GizWifiSDK.sharedInstance().startWithAppID(c, AppID, AppSecret, GosDeploy.setProductKeyList(), serverMap,
//					false);
			// 设置要过滤的设备 productKey 列表。不过滤则直接传 null
			List productInfo = new ArrayList();
			ConcurrentHashMap<String, Object> product = new ConcurrentHashMap<String, Object>();
			product.put("productKey", "6631adbb8ef44ad48e3bbe7def28b25c");
			product.put("productSecret", "39b30282945b442e82ba3643c05382f3");
			productInfo.add(product);
			GizWifiSDK.sharedInstance().startWithAppInfo(c,appInfo,productInfo,null,false);

		}
		hand.sendEmptyMessageDelayed(SETCLOUD, 3000);

	}

	public static MessageCenter getInstance(Context c) {
		if (mCenter == null) {
			mCenter = new MessageCenter(c);
		}
		return mCenter;
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			GizWifiSDK.sharedInstance().setLogLevel(GizLogPrintLevel.GizLogPrintAll);

		};
	};

}
