package com.huhu.remotecontrol;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	EditText KeyView;
	ToggleButton toggleButton;
	Button Btn1, Btn2;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		toggleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						KeyView.getWindowToken(), 0);
				if (toggleButton.isChecked()) {
					new Thread(runnable).start();
					KeyView.setEnabled(false);
				} else {
					try {
						JSONObject jsonobj = new JSONObject();
						jsonobj.put("exit", "");
						UDP.Send(jsonobj.toString());
						mHandler.obtainMessage(0, "断开连接").sendToTarget();
					} catch (Exception e) {
					}
					KeyView.setEnabled(true);
				}
			}
		});

		KeyView = (EditText) findViewById(R.id.editText1);
		KeyView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == KeyEvent.ACTION_DOWN
						&& !toggleButton.isChecked())
					toggleButton.performClick();
				return false;
			}
		});

		// 打开时读取保存的参数
		sp = getSharedPreferences("huhu", Context.MODE_PRIVATE);
		KeyView.setText(new String(Base64.decode(sp.getString("key", ""),
				Base64.NO_WRAP)));

		Btn1 = (Button) findViewById(R.id.button1);
		Btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						MouseActivity.class);
				startActivity(intent);
			}
		});

		Btn2 = (Button) findViewById(R.id.button2);
		Btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SensorActivity.class);
				startActivity(intent);
			}
		});
	}

	public boolean isWiFiActive() {
		WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
			return true;
		} else {
			return false;
		}
	}

	Runnable runnable = new Runnable() {
		public void run() {
			try {
				if (!isWiFiActive()) {
					mHandler.obtainMessage(0, "当前无WiFi连接").sendToTarget();
					mHandler.obtainMessage(1).sendToTarget();
					return;
				}

				String key = Base64.encodeToString(KeyView.getText().toString()
						.getBytes(), Base64.NO_WRAP);
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("auth", key);
				String ret = UDP.Login(jsonobj.toJSONString());
				Map<String, String> map = JSON.parseObject(ret,
						new TypeReference<Map<String, String>>() {
						});
				ret = map.get("state");
				if (ret.equals("correct")) {
					sp.edit().putString("key", key).commit();
					mHandler.obtainMessage(0, "连接成功").sendToTarget();
					return;
				} else if (ret.equals("wrong"))
					mHandler.obtainMessage(0, "连接失败,配对码错误").sendToTarget();
				else
					mHandler.obtainMessage(0, "参数有误").sendToTarget();

			} catch (Exception e) {
				mHandler.obtainMessage(0, "没有找到主机,请手动设定IP地址").sendToTarget();
			}

			mHandler.obtainMessage(1).sendToTarget();
		}
	};

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplication(), (String) msg.obj,
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				toggleButton.setChecked(false);
				KeyView.setEnabled(true);
				break;
			}
		}
	};

	private long mExitTime;

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			if (toggleButton.isChecked())
				toggleButton.performClick();
			finish();
		}
	}

}
