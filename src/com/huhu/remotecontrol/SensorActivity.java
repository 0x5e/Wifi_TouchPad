package com.huhu.remotecontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SensorActivity extends Activity {

	MouseListener mouseListener;
	TextListener textlistener;
	SharedPreferences sp;
	View left, right;
	EditText text;
	SeekBar sensitivity;
	ToggleButton power;
	RadioGroup sensor;

	SensorManager mManager;
	Sensor mSensor_acc, mSensor_gyro;
	SensorEventListener mListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor);

		mouseListener = new MouseListener(this);
		textlistener = new TextListener(this);

		text = (EditText) findViewById(R.id.text);
		text.setOnKeyListener(textlistener);
		text.addTextChangedListener(textlistener);

		left = findViewById(R.id.left_button);
		right = findViewById(R.id.right_button);
		left.setOnTouchListener(mouseListener);
		right.setOnTouchListener(mouseListener);

		sp = getSharedPreferences("huhu", Context.MODE_PRIVATE);

		sensitivity = (SeekBar) findViewById(R.id.sensitivity);
		sensitivity.setProgress(sp.getInt("sensitivity", 5));
		sensitivity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser) {
				sp.edit().putInt("sensitivity", progress).commit();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor_acc = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensor_gyro = mManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			@SuppressWarnings("deprecation")
			public void onSensorChanged(SensorEvent event) {
				float x = event.values[SensorManager.DATA_X];
				float y = event.values[SensorManager.DATA_Y];
				float z = event.values[SensorManager.DATA_Z];

				float sense = 0.5f + (float) sensitivity.getProgress()
						/ sensitivity.getMax();

				switch (sensor.getCheckedRadioButtonId()) {
				case R.id.acc:
					mouseListener.MouseSend(0x0001, (int) ((-x) * sense),
							(int) ((-y) * sense));
					break;
				case R.id.gyro:
					mouseListener.MouseSend(0x0001, (int) ((-z) * sense),
							(int) ((-x) * sense));
					break;
				}

			}
		};

		sensor = (RadioGroup) findViewById(R.id.sensor);
		power = (ToggleButton) findViewById(R.id.power);
		power.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (power.isChecked()) {
					switch (sensor.getCheckedRadioButtonId()) {
					case R.id.acc:
						if (mSensor_acc == null) {
							mHandler.obtainMessage(0, "没有找到加速度计")
									.sendToTarget();
							power.setChecked(false);
							return;
						}
						// 这里 SENSOR_DELAY_GAME 还可以是以下常量：
						// SENSOR_DELAY_FASTEST
						// SENSOR_DELAY_UI
						// SENSOR_DELAY_NORMAL
						mManager.registerListener(mListener, mSensor_acc,
								SensorManager.SENSOR_DELAY_FASTEST);
						break;
					case R.id.gyro:
						if (mSensor_gyro == null) {
							mHandler.obtainMessage(0, "没有找到陀螺仪").sendToTarget();
							power.setChecked(false);
							return;
						}
						mManager.registerListener(mListener, mSensor_gyro,
								SensorManager.SENSOR_DELAY_FASTEST);
						break;
					}
					findViewById(R.id.acc).setEnabled(false);
					findViewById(R.id.gyro).setEnabled(false);
				} else {
					mManager.unregisterListener(mListener);
					findViewById(R.id.acc).setEnabled(true);
					findViewById(R.id.gyro).setEnabled(true);
				}
			}
		});

	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplication(), (String) msg.obj,
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	@Override
	public void onResume() {
		if (power.isChecked()) {
			switch (sensor.getCheckedRadioButtonId()) {
			case R.id.acc:
				mManager.registerListener(mListener, mSensor_acc,
						SensorManager.SENSOR_DELAY_FASTEST);
				break;
			case R.id.gyro:
				mManager.registerListener(mListener, mSensor_gyro,
						SensorManager.SENSOR_DELAY_FASTEST);
				break;
			}
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		mManager.unregisterListener(mListener);
		super.onPause();
	}

}
