package com.huhu.remotecontrol;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

public class MouseListener implements OnTouchListener {
	InputMethodManager inputMethodManager;
	MouseLooperThread mThread;

	MouseListener(Activity activity) {
		mThread = new MouseLooperThread(activity);
		mThread.start();
	}

	public void MouseSend(int what, int arg1, int arg2) {
		mThread.mHandler.obtainMessage(what, arg1, arg2).sendToTarget();
	}

	float x = 0, y = 0;
	float min_move = 5.0f, min_scoll = 50.0f;
	long LongClick_time = 200;
	boolean isClick = false, longpressed = false, leftdown = false,
			rightdown = false;

	Runnable LongPress = new Runnable() {
		@Override
		public void run() {
			longpressed = true;
			MouseSend(0x0002, 0, 0);
		}
	};

	public void HideInput(View v) {
		if (inputMethodManager == null)
			inputMethodManager = (InputMethodManager) v.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isActive())
			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		HideInput(v);

		if (v.getId() == R.id.left_button) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				leftdown = true;
				MouseSend(0x0002, 0, 0);
				return false;
			case MotionEvent.ACTION_UP:
				leftdown = false;
				MouseSend(0x0004, 0, 0);
				return false;
			}
		} else if (v.getId() == R.id.right_button) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rightdown = true;
				MouseSend(0x0008, 0, 0);
				return false;
			case MotionEvent.ACTION_UP:
				rightdown = false;
				MouseSend(0x0010, 0, 0);
				return false;
			}
		} else if (v.getId() == R.id.mouse) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x = event.getX();
				y = event.getY();
				isClick = true;
				if (leftdown == false && rightdown == false)
					mThread.mHandler.postDelayed(LongPress, LongClick_time);
				return false;
			case MotionEvent.ACTION_MOVE:
				if (Math.abs(event.getX()) > min_move
						|| Math.abs(event.getY()) > min_move) {
					isClick = false;
					mThread.mHandler.removeCallbacks(LongPress);
				}
				MouseSend(0x0001, (int) (event.getX() - x),
						(int) (event.getY() - y));
				x = event.getX();
				y = event.getY();
				return false;
			case MotionEvent.ACTION_UP:
				mThread.mHandler.removeCallbacks(LongPress);
				if (leftdown == false && rightdown == false) {
					if (longpressed) {
						longpressed = false;
						MouseSend(0x0004, 0, 0);
					} else if (isClick)
						MouseSend(0x0006, 0, 0);
				}
				return false;
			}
		} else if (v.getId() == R.id.scoll) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				y = event.getY();
				return false;
			case MotionEvent.ACTION_MOVE:
				int dy = (int) ((event.getY() - y) / min_scoll);
				if (dy != 0) {
					MouseSend(0x0800, -dy, 0);
					y = event.getY();
				}
				return false;
			case MotionEvent.ACTION_UP:
				return false;
			}
		}
		return false;
	}

}