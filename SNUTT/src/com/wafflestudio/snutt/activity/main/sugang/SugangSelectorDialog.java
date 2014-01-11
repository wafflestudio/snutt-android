package com.wafflestudio.snutt.activity.main.sugang;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.main.MainActivity;

public class SugangSelectorDialog extends Dialog implements OnClickListener{
	MainActivity mContext;
	LinearLayout wrapper;

	public SugangSelectorDialog(MainActivity context) {
		super(context);
		mContext = context;
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		setContentView(R.layout.sugang_selector_layout);
		wrapper = (LinearLayout) findViewById(R.id.sugang_content);
		
		makeList();
	}
	
	void makeList(){
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File[] files = new File(mContext.getFilesDir() + "/sugang").listFiles();
		
		//수강편람이 없음
		if (files == null){
			TextView textView = (TextView) inflater.inflate(R.layout.sugang_row, null);
			textView.setText(mContext.getString(R.string.no_sugang));
			wrapper.addView(textView);
			return;
		}
		
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File a, File b) {
				return sugangNameToInteger(b.getName()) - sugangNameToInteger(a.getName());
			}
			
		});
		
		for (File file : files){
			String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
			String[] split = fileName.split("_");
			int year = Integer.parseInt(split[0]);
			String semester = split[1];
			
			TextView textView = (TextView) inflater.inflate(R.layout.sugang_row, null);
			textView.setText(year + "-" + semester);
			textView.setTag(file.getName());
			textView.setOnClickListener(this);
			wrapper.addView(textView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.sugang_row:
			String filename = (String) v.getTag();
			String fileName = filename.substring(0, filename.lastIndexOf("."));
			String[] split = fileName.split("_");
			int year = Integer.parseInt(split[0]);
			String semester = split[1];

			mContext.loadData(year, semester);
			
			dismiss();
			break;
		}
	}
	
	int sugangNameToInteger(String filename){
		String fileName = filename.substring(0, filename.lastIndexOf("."));
		String[] split = fileName.split("_");
		int year = Integer.parseInt(split[0]);
		String semester = split[1];
		int semesterInt = 0;
		if (semester.equals("1")){
			semesterInt = 1;
		} else if (semester.equals("S")){
			semesterInt = 2;
		} else if (semester.equals("2")){
			semesterInt = 3;
		} else if (semester.equals("W")){
			semesterInt = 4;
		}
		
		return year*10 + semesterInt;
	}
	

}
