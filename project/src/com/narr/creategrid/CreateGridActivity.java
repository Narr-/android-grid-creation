package com.narr.creategrid;

import java.util.List;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateGridActivity extends Activity {

	private RadioGroup mRgCellEditModeChangeBtns;
	private GridDrawView mGDV;
	private Button mBtnCancel;
	private Button mBtnSave;

	private Toast mToast;
	private Toast mExitToast;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_grid_activity_layout);

		Logger.init(getString(R.string.app_name), true);
		// Logger.d(this, "onCreate()..!!");

		init();
	}

	@Override
	public void onBackPressed() {
		if (mExitToast.getView().getWindowToken() == null) {// check whether this toast view is showing or not
			mExitToast.show();
		}
		else {
			mExitToast.cancel();
			super.onBackPressed();
		}
	}

	private void init() {
		mGDV = (GridDrawView) findViewById(R.id.gdv);

		mRgCellEditModeChangeBtns = (RadioGroup) findViewById(R.id.rg_cell_edit_mode_change_btns);
		mRgCellEditModeChangeBtns.setOnCheckedChangeListener(mRgListener);

		mBtnCancel = (Button) findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(mBtnClickListener);
		mBtnSave = (Button) findViewById(R.id.btn_save);
		mBtnSave.setOnClickListener(mBtnClickListener);

		mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		mExitToast = Toast.makeText(this, R.string.back_btn_exit_msg, Toast.LENGTH_SHORT);
	}


	private RadioGroup.OnCheckedChangeListener mRgListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == R.id.rb_merge) {
				// Logger.d(GridCreationActivity.this, "[onCheckedChanged]R.id.rb_merge..!!");
				mGDV.setEditMode(true);
			}
			else if (checkedId == R.id.rb_divide) {
				// Logger.d(GridCreationActivity.this, "[onCheckedChanged]R.id.rb_divide..!!");
				mGDV.setEditMode(false);
				if (mGDV.areCellsAllDivided()) {
					mToast.setText(R.string.cell_divide_info_msg);
					mToast.show();
				}
			}
		}
	};


	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int btnId = v.getId();
			if (btnId == R.id.btn_cancel) {
				// Logger.d(GridCreationActivity.this, "[onClick]btn_cancel..!!");
				setResult(RESULT_CANCELED);
				mToast.cancel();
				mGDV.getToast().cancel();
				mExitToast.cancel();
				finish();
			}
			else if (btnId == R.id.btn_save) {
				// Logger.d(GridCreationActivity.this, "[onClick]btn_save..!!");
				mToast.setText(R.string.save_btn_info_msg);
				mToast.show();

				List<Rect> gridInfo = mGDV.getGridInfo();
				int size = gridInfo.size();
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						Rect cell = gridInfo.get(i);
						Logger.d(CreateGridActivity.this, "[onClick]btn_save, " +
								"rect(" + i + ") " + " (row, col, rowSpan, colSpan) ==> " +
								"(" + cell.left + ", " + cell.top + ", " + cell.right + ", " + cell.bottom + ")");
					}
				}
				else {
					Logger.d(CreateGridActivity.this, "[onClick]btn_save, cellInfos size 0..!!");
				}
			}
		}
	};
}