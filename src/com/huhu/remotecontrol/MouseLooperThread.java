package com.huhu.remotecontrol;

import com.alibaba.fastjson.JSONObject;
import android.app.Activity;
import android.app.Service;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;

public class MouseLooperThread extends Thread {
	Activity activity;

	MouseLooperThread(Activity activity) {
		this.activity = activity;
	}

	public void run() {
		Looper.prepare();
		if (mHandler == null)
			mHandler = new MyHandler(activity);
		Looper.loop();
	}

	Handler mHandler;

	static class MyHandler extends Handler {

		Vibrator vibrator;

		MyHandler(Activity activity) {

			vibrator = (Vibrator) activity
					.getSystemService(Service.VIBRATOR_SERVICE);
		}

		public void handleMessage(Message msg) {
			if (msg.what == 0x0002 || msg.what == 0x0008 || msg.what == 0x0800)
				vibrator.vibrate(50);
			try {
				JSONObject jsonobj = new JSONObject();
				JSONObject mouse = new JSONObject();
				mouse.put("dwFlags", msg.what);
				if (msg.what != 0x0800) {// 滚轮
					mouse.put("dx", msg.arg1);
					mouse.put("dy", msg.arg2);
					mouse.put("dwData", 0);
				} else {
					mouse.put("dx", 0);
					mouse.put("dy", 0);
					mouse.put("dwData", 120 * msg.arg1);
				}
				jsonobj.put("mouse", mouse);
				UDP.Send(jsonobj.toString());
			} catch (Exception e) {
			}
		}
	}

}
