package com.huhu.remotecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MouseActivity extends Activity {
	SharedPreferences sp;
	EditText text;
	View left, right, mouse, scoll;
	MouseListener mouseListener;
	TextListener textlistener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mouse);

		mouseListener = new MouseListener(this);
		textlistener = new TextListener(this);

		left = findViewById(R.id.left_button);
		right = findViewById(R.id.right_button);
		mouse = findViewById(R.id.mouse);
		scoll = findViewById(R.id.scoll);
		left.setOnTouchListener(mouseListener);
		right.setOnTouchListener(mouseListener);
		mouse.setOnTouchListener(mouseListener);
		scoll.setOnTouchListener(mouseListener);

		text = (EditText) findViewById(R.id.text);
		text.setOnKeyListener(textlistener);
		text.addTextChangedListener(textlistener);

		sp = getSharedPreferences("huhu", Context.MODE_PRIVATE);

	}

}
