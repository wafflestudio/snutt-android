package com.wafflestudio.snutt.activity.main;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.main.timetable.TimetableView;
import com.wafflestudio.snutt.data.Lecture;

public class LectureAdapter extends ArrayAdapter<Lecture> implements OnClickListener {
	public final static int TYPE_LECTURE = 0;
	public final static int TYPE_MY_LECTURE = 1;
	ArrayList<Lecture> mObjects;
	LayoutInflater mInflater;
	MainActivity mContext;
	int mType;

	public LectureAdapter(MainActivity context, int textViewResourceId, ArrayList<Lecture> lectures, int type) {
		super(context, textViewResourceId);
		mContext= context;
		mObjects = lectures;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mType = type;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.my_lecture_row, null);
		}
		Lecture lecture = null;
		if (mObjects.size() > position)
			lecture = mObjects.get(position);
		
		RelativeLayout row = (RelativeLayout) v.findViewById(R.id.lecture_row);
		row.setTag(lecture);
		row.setOnClickListener(this);
		
		if (lecture != null) {
			TextView courseTitle = (TextView) v.findViewById(R.id.course_title);
			Button addButton = (Button) v.findViewById(R.id.add);
			Button delButton = (Button) v.findViewById(R.id.remove);
			TextView classTime = (TextView) v.findViewById(R.id.class_time);
			TextView location = (TextView) v.findViewById(R.id.location);
			TextView classification = (TextView) v.findViewById(R.id.classification);
			TextView department = (TextView) v.findViewById(R.id.department);
			TextView courseNumber = (TextView) v.findViewById(R.id.course_number);
			TextView lectureNumber = (TextView) v.findViewById(R.id.lecture_number);
			TextView remark = (TextView) v.findViewById(R.id.remark);
			
			courseTitle.setText(lecture.course_title + " (" + lecture.instructor + " / "+ lecture.credit +"학점)");
			classTime.setText(lecture.getSimplifedClassTime());
			location.setText(lecture.getSimplifiedLocation());
			classification.setText(lecture.getClassification());
			department.setText(lecture.department);
			courseNumber.setText(lecture.course_number);
			lectureNumber.setText(lecture.lecture_number);
			remark.setText(lecture.remark);
			
			if (mType == TYPE_LECTURE){
				courseTitle.setTextColor(0xffffffff);
			} else {
				courseTitle.setTextColor(0xff000000);
			}
			//선택된 강의 : 비고를 보여줌
			if (mType == TYPE_LECTURE && lecture == Lecture.selectedLecture || mType == TYPE_MY_LECTURE && lecture == Lecture.selectedMyLecture){
				classTime.setVisibility(View.GONE);
				location.setVisibility(View.GONE);
				classification.setVisibility(View.GONE);
				department.setVisibility(View.GONE);
				courseNumber.setVisibility(View.VISIBLE);
				lectureNumber.setVisibility(View.VISIBLE);
				remark.setVisibility(View.VISIBLE);
			} else {
				classTime.setVisibility(View.VISIBLE);
				location.setVisibility(View.VISIBLE);
				classification.setVisibility(View.VISIBLE);
				department.setVisibility(View.VISIBLE);
				courseNumber.setVisibility(View.GONE);
				lectureNumber.setVisibility(View.GONE);
				remark.setVisibility(View.GONE);
			}
			
			row.setBackgroundColor(0x0);
			addButton.setVisibility(View.GONE);
			delButton.setVisibility(View.GONE);
			//선택된 강의면 색상을 바꿈
			if (mType == TYPE_LECTURE && lecture == Lecture.selectedLecture){
				row.setBackgroundColor(0xff333366);
				addButton.setTag(lecture);
				addButton.setVisibility(View.VISIBLE);
				addButton.setOnClickListener(this);
			}
			//내강의 - 선택된 강의면 색상을 바꿈
			else if (mType == TYPE_MY_LECTURE && lecture == Lecture.selectedMyLecture){
				row.setBackgroundColor(0xffccccff);
				delButton.setTag(lecture);
				delButton.setVisibility(View.VISIBLE);
				delButton.setOnClickListener(this);
			}
		}
		return v;
	}
	
	public void setObject(ArrayList<Lecture> lectures){
		mObjects = lectures;
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public Lecture getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public void onClick(View v) {
		Lecture lecture = (Lecture) v.getTag();
		switch (v.getId()){
		case R.id.lecture_row:
			//검색 강의
			if (mType == TYPE_LECTURE){
				if (Lecture.selectedLecture != lecture)
					Lecture.selectedLecture = lecture;
				else 
					Lecture.selectedLecture = null;
				notifyDataSetChanged();
				TimetableView.mInstance.invalidate();
			}
			//내 강의
			else if (mType == TYPE_MY_LECTURE) {
				if (Lecture.selectedMyLecture != lecture)
					Lecture.selectedMyLecture = lecture;
				else 
					Lecture.selectedMyLecture = null;
				notifyDataSetChanged();
			}
			mContext.setActionbar();
			break;
		case R.id.add:
			//강의 추가
			Lecture.addMyLecture(mContext, lecture);
			notifyDataSetChanged();
			mContext.setActionbar();
			break;
		case R.id.remove:
			//강의 제거
			Lecture.removeMyLecture(mContext, lecture);
			notifyDataSetChanged();
			mContext.setActionbar();
			break;
		}
	}


}