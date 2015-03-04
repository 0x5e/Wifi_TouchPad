package com.huhu.remotecontrol;

import com.alibaba.fastjson.JSONObject;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class TextListener implements TextWatcher, OnKeyListener {
	Activity activity;

	TextListener(Activity m) {
		activity = m;
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.toString().equals(""))
			return;
		try {
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("text", s.toString());
			UDP.Send(jsonobj.toString());
		} catch (Exception e) {
		}
		s.clear();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int lengthBefore,
			int lengthAfter) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public static final int VK_BACK_SPACE = 0x08;
	public static final int VK_RETURN = 0x0D;
	public static final int VK_PAGE_UP = 0x21;
	public static final int VK_PAGE_DOWN = 0x22;
	public static final int VK_END = 0x23;
	public static final int VK_HOME = 0x24;
	public static final int VK_LEFT = 0x25;
	public static final int VK_UP = 0x26;
	public static final int VK_RIGHT = 0x27;
	public static final int VK_DOWN = 0x28;
	public static final int VK_SELECT = 0x29;

	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent event) {
		int vk_Code = 0;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_MOVE_END:
				vk_Code = VK_END;
				break;
			case KeyEvent.KEYCODE_MOVE_HOME:
				vk_Code = VK_HOME;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				vk_Code = VK_LEFT;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				vk_Code = VK_UP;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				vk_Code = VK_RIGHT;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				vk_Code = VK_DOWN;
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				vk_Code = VK_SELECT;
				break;
			case KeyEvent.KEYCODE_ENTER:
				vk_Code = VK_RETURN;
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				vk_Code = VK_PAGE_UP;
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				vk_Code = VK_PAGE_DOWN;
				break;
			case KeyEvent.KEYCODE_DEL:
				vk_Code = VK_BACK_SPACE;
				break;
			case KeyEvent.KEYCODE_BACK:
				activity.finish();
			default:
				return true;
			}
			try {
				JSONObject jsonobj = new JSONObject();
				JSONObject keybd = new JSONObject();
				keybd.put("wVk", vk_Code);
				keybd.put("dwFlags", 0);
				jsonobj.put("keybd", keybd);
				UDP.Send(jsonobj.toString());
			} catch (Exception e) {
			}
		}
		return true;
	}
}