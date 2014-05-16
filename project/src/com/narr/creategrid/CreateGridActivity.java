package com.narr.creategrid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class CreateGridActivity extends Activity {

	public static final String TAG = CreateGridActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_grid_activity_layout);

		Log.d(TAG, "onCreate()..!!");
	}
}
