package com.wafflestudio.snutt.activity.main.about;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.main.LectureAdapter;

public class AboutFragment extends SherlockFragment {
	public static LectureAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.about_layout, container, false);
		TextView aboutText = (TextView) v.findViewById(R.id.about_text);
		Linkify.addLinks(aboutText, Linkify.ALL);
		return v;
	}


}