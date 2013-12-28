package com.wafflestudio.snutt.activity.main.my_lecture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.main.LectureAdapter;
import com.wafflestudio.snutt.activity.main.MainActivity;
import com.wafflestudio.snutt.data.Lecture;

public class MyLectureFragment extends SherlockFragment {
	public static LectureAdapter mAdapter;
	
	//내 강의 선택 초기화
	public static void clearSelectedMyLecture(){
		Lecture.selectedMyLecture = null;
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.my_lecture, container, false);
		ListView listView = (ListView) v.findViewById(R.id.my_lecture_list);
		mAdapter = new LectureAdapter((MainActivity) getActivity(), R.layout.my_lecture_row, Lecture.myLectures, LectureAdapter.TYPE_MY_LECTURE);
		listView.setAdapter(mAdapter);
		return v;
	}


}