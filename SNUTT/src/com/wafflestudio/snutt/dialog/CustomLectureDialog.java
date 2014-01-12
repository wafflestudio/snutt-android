package com.wafflestudio.snutt.dialog;

import android.app.Dialog;
import android.app.Service;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.main.MainActivity;
import com.wafflestudio.snutt.data.Lecture;
import com.wafflestudio.snutt.util.TimetableUtil;

public class CustomLectureDialog extends Dialog implements android.view.View.OnClickListener {
	MainActivity mContext;
	int mWday;
	float mStartTime, mDuration;
	
	EditText courseTitleText, locationText;
	Button createButton, cancelButton;
	
	InputMethodManager imm;
	
	public CustomLectureDialog(MainActivity context, int wday, float startTime, float duration) {
		super(context);
		mContext = context;
		mWday = wday;
		mStartTime = startTime;
		mDuration = duration;

		String title = null;
		if ((int)(startTime / 0.5f) % 2 == 1){
			title = mContext.getString(R.string.custom_lecture).replace("1@", TimetableUtil.numberToWdayString(wday)).replace("2@", String.valueOf(startTime));
		} else {
			title = mContext.getString(R.string.custom_lecture).replace("1@", TimetableUtil.numberToWdayString(wday)).replace("2@", String.valueOf((int)startTime));
		}
		setTitle(title);
		
		setContentView(R.layout.custom_lecture_layout);	
		
		courseTitleText = (EditText) findViewById(R.id.course_title);
		locationText = (EditText) findViewById(R.id.location);
		createButton = (Button) findViewById(R.id.create);
		cancelButton = (Button) findViewById(R.id.cancel);
		
		createButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
		imm = (InputMethodManager)mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				imm.showSoftInput(courseTitleText, 0);
			}
		}, 100);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.create:
			String courseTitle = courseTitleText.getText().toString();
			String location = locationText.getText().toString();
			Lecture.addMyLecture(mContext, new Lecture(courseTitle, location, mWday, mStartTime, mDuration));
			dismiss();
			break;
		case R.id.cancel:
			dismiss();
			break;
		}
	}
	
}
