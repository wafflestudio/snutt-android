package com.wafflestudio.snutt.activity.main;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.BaseActivity;
import com.wafflestudio.snutt.activity.main.about.AboutFragment;
import com.wafflestudio.snutt.activity.main.my_lecture.MyLectureFragment;
import com.wafflestudio.snutt.activity.main.timetable.TimetableFragment;
import com.wafflestudio.snutt.activity.main.timetable.TimetableView;
import com.wafflestudio.snutt.api.ServerConnection;
import com.wafflestudio.snutt.api.ServerConnection.ServerCallback;
import com.wafflestudio.snutt.data.Lecture;
import com.wafflestudio.snutt.dialog.SugangSelectorDialog;
import com.wafflestudio.snutt.util.App;
import com.wafflestudio.snutt.util.SharedPrefUtil;

public class MainActivity extends BaseActivity implements OnClickListener {
	public static String ACTIVE_TAB = "activeTab";
	
	// instance variables
	boolean doubleBackToExitPressedOnce = false;

	ActionBar actionBar;
	ViewPager mViewPager;
	public TabsAdapter mTabsAdapter;
	public static Tab[] mTabs;
	Animation actionBarIn;

	//검색
	boolean isSearchVisible = true;
	RelativeLayout searchLayout;
	Animation searchIn, searchOut;
	ListView searchListView;
	public static LectureAdapter mSearchAdapter;
	EditText searchQueryView;
	ImageButton searchOpenButton, searchCloseButton;
	InputMethodManager imm;
	
	//저장
	RelativeLayout buttonPanel;
	ImageButton saveButton;
	
	//수강편람 선택
	ImageButton sugangButton;
	
	//커스텀 시간표
	boolean customEditable = false;
	ImageButton customButton;

	//새로운 버전 배너
	LinearLayout bannerForUpgrade;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(this, mViewPager);

		actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.actionbar_custom_layout);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		mTabs = new Tab[3];
		mTabs[0] = actionBar.newTab().setIcon(R.drawable.ic_timetable);
		mTabs[1] = actionBar.newTab().setText(getResources().getString(R.string.my_lecture) + " (0학점)").setIcon(R.drawable.ic_my_lecture);
		mTabs[2] = actionBar.newTab().setText(getResources().getString(R.string.about)).setIcon(R.drawable.ic_about);
		mTabsAdapter.addTab(mTabs[0], TimetableFragment.class, null);
		mTabsAdapter.addTab(mTabs[1], MyLectureFragment.class, null);
		mTabsAdapter.addTab(mTabs[2], AboutFragment.class, null);

		if( savedInstanceState != null ){
			getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(ACTIVE_TAB));
		}
		
		actionBar.getCustomView().findViewById(R.id.syllabus_btn).setOnClickListener(this);
		actionBarIn = AnimationUtils.loadAnimation(this, R.anim.move_right_in);

		//검색창
		buttonPanel = (RelativeLayout) findViewById(R.id.button_panel);
		searchLayout = (RelativeLayout) findViewById(R.id.search);
		saveButton = (ImageButton) findViewById(R.id.save);
		sugangButton = (ImageButton) findViewById(R.id.sugang);
		customButton = (ImageButton) findViewById(R.id.edit_custom);
		
		saveButton.setOnClickListener(this);
		sugangButton.setOnClickListener(this);
		customButton.setOnClickListener(this);
		
		searchIn = AnimationUtils.loadAnimation(this, R.anim.move_up_in);
		searchOut = AnimationUtils.loadAnimation(this, R.anim.move_down_out);
		searchIn.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				searchLayout.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationEnd(Animation arg0) {
			}
		});
		searchOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationEnd(Animation arg0) {
				searchLayout.setVisibility(View.GONE);
			}
		});
		searchOpenButton = (ImageButton) findViewById(R.id.open_search);
		searchCloseButton = (ImageButton) findViewById(R.id.search_down);
		searchOpenButton.setOnClickListener(this);
		searchCloseButton.setOnClickListener(this);
		searchListView = (ListView) findViewById(R.id.search_list);
		mSearchAdapter = new LectureAdapter(this, R.layout.my_lecture_row, Lecture.lectures, LectureAdapter.TYPE_LECTURE);
		searchListView.setAdapter(mSearchAdapter);
		searchQueryView = (EditText) findViewById(R.id.search_query);
		searchQueryView.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			public void afterTextChanged(final Editable e) {
				new Thread(){
					@SuppressLint("DefaultLocale")
					public void run(){
						//강의 검색
						String str = e.toString();
						ArrayList<Lecture> searchLecture = new ArrayList<Lecture>();
						for (int i=0;i<Lecture.lectures.size();i++){
							Lecture lecture = Lecture.lectures.get(i);
							if (increasingOrderInclusion(lecture.course_title.toLowerCase(), str.toLowerCase())
									|| increasingOrderInclusion(lecture.department.toLowerCase(), str.toLowerCase())
									|| lecture.instructor.toLowerCase().contains(str.toLowerCase())
									|| lecture.course_number.toLowerCase().contains(str.toLowerCase())){
								searchLecture.add(lecture);
							}
						}
						mSearchAdapter.setObject(searchLecture);
						MainActivity.this.runOnUiThread(new Runnable(){
							public void run() {
								mSearchAdapter.notifyDataSetInvalidated();
							}
						});
						
					}
				}.start();
			}
		});
		imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);

		
		//수강편람 로드
		checkAppUpdate();
		checkUpdate();

		//버전 2.0 홍보
		bannerForUpgrade = (LinearLayout)findViewById(R.id.banner_upgrade);
		bannerForUpgrade.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showNewVersionMarket();
			}
		});

		if (!SharedPrefUtil.getInstance().getBoolean(SharedPrefUtil.PREF_KEY_UPGRADE_ALERT_SHOWN)) {
			SharedPrefUtil.getInstance().setBoolean(SharedPrefUtil.PREF_KEY_UPGRADE_ALERT_SHOWN, true);
			showUpgradeAlert();
		}

		int currentYear = SharedPrefUtil.getInstance().getInt(SharedPrefUtil.PREF_KEY_CURRENT_YEAR);
		String currentSemester = SharedPrefUtil.getInstance().getString(SharedPrefUtil.PREF_KEY_CURRENT_SEMESTER);
		loadData(currentYear, currentSemester);
		
		showSearch();
	}

	public void addLecture(String lectureLineString){
		//classification;department;academic_year;course_number;lecture_number;course_title;credit;class_time;location;instructor;quota;enrollment;remark;category;snuev_lec_id;snuev_eval_score
		String[] str = lectureLineString.replaceAll(";;", "; ;").split(";");

		String classification = str[0].trim();
		String department = str[1].trim();
		String academic_year = str[2].trim();
		String course_number = str[3].trim();
		String lecture_number = str[4].trim();
		String course_title = str[5].trim();
		int credit = Integer.parseInt(str[6]);
		String class_time = str[7].trim();
		String location = str[8].trim();
		String instructor = str[9].trim();
		int quota = Integer.parseInt(str[10]);
		int enrollment = Integer.parseInt(str[11]);
		String remark = str[12].trim();
		String category = str[13].trim();
		Lecture.lectures.add(new Lecture(classification, department, academic_year, course_number, lecture_number, course_title, credit, class_time, location, instructor, quota, enrollment, remark, category));
	}

	//수강편람 데이터를 읽음
	public void loadData(int year, String semester){
		if (year == 0 || semester == null){
			mTabs[0].setText(getResources().getString(R.string.timetable) + "\n(null)");
			return;
		}
		
		mTabs[0].setText(getResources().getString(R.string.timetable) + "\n(" + year + "-" + semester + ")");
		
		String filePath = getFilesDir() + "/sugang/" + year + "_" + semester + ".txt";

		Lecture.lectures.clear();
		Lecture.myLectures.clear();
		
		try {
			BufferedReader reader = null;
			File file = new File(filePath);
			if (file.exists()){
				InputStream inputStream = new FileInputStream(file);
				reader = new BufferedReader(new InputStreamReader(inputStream));
				
				reader.readLine();
				String updated_date = reader.readLine();
				Lecture.updatedDate = updated_date.trim();
				reader.readLine();
				String mLine = reader.readLine();
				while (mLine != null) {
					addLecture(mLine);
					mLine = reader.readLine();
				}
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		mSearchAdapter.notifyDataSetChanged();
		
		SharedPrefUtil.getInstance().setInt(SharedPrefUtil.PREF_KEY_CURRENT_YEAR, year);
		SharedPrefUtil.getInstance().setString(SharedPrefUtil.PREF_KEY_CURRENT_SEMESTER, semester);
		
		Lecture.loadMyLectures();
	}

	public void showNewVersionMarket() {
		final String appName = "company.fortytwo.slide.app";
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
		}
	}

	public void showUpgradeAlert() {
		new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.ic_about)
				.setTitle("새로운 SNUTT를 만나보세요!")
				.setMessage("SNUTT 2.0이 출시되었습니다. 지금 다운로드하러 가시겠습니까?")
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showNewVersionMarket();
						finish();
					}

				}).setNegativeButton(R.string.no, null).show();
	}

	public void confirmAppUpdate(){
		MainActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(MainActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.app_update_title)
				.setMessage(R.string.app_update_body)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String appName = App.appName;
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
						}
						finish();
					}

				}).setNegativeButton(R.string.no, null).show();
			}
		});
	}
	
	//새 버전의 앱이 나왔는지 체크
	void checkAppUpdate(){
		ServerConnection.versionCheck(new ServerCallback() {
			public void onSuccess(JSONObject result) {
				int latestVersion = result.optInt("latest_version");
				int currentVersion = App.getAppVersion();
				
				if (latestVersion > currentVersion){
					confirmAppUpdate();
				}
			}
			public void onFailure(Throwable e, String responseBody) {}
		});
	}
	//업데이트 여부를 체크함
	void checkUpdate(){
		ServerConnection.sugangCheck(this);
	}
	
	//오름차순으로 포함되어있으면 str1 > str2
	boolean increasingOrderInclusion(String str1, String str2){
		String a = str1.replace(" ", "");
		String b = str2.replace(" ", "");
		int i=0,j=0;
		while (i<a.length() && j<b.length()){
			if (a.charAt(i) == b.charAt(j)) j++;
			else i++;
		}
		return (j == b.length());
	}

	//내 강의에 변경이 생길 때마다 호출
	public static void myLectureChanged(){
		if (TimetableView.mInstance != null)
			TimetableView.mInstance.invalidate();
		if (MyLectureFragment.mAdapter != null)
			MyLectureFragment.mAdapter.notifyDataSetChanged();
		//학점 반영
		int credit = 0;
		for (int i=0;i<Lecture.myLectures.size();i++){
			credit += Lecture.myLectures.get(i).credit;
		}
		mTabs[1].setText(App.getAppContext().getResources().getString(R.string.my_lecture) + " (" + credit + "학점)");
	}
	
	//강의가 선택되어있으면 tab -> custom view로 전환
	public void setActionbar(){
		//search가 열려있는 상태에서 selectedLecture가 존재
		if (isSearchVisible && Lecture.selectedLecture != null){
			if (actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS){
				actionBar.getCustomView().startAnimation(actionBarIn);
			}
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowCustomEnabled(true);
			Lecture lecture = Lecture.selectedLecture;
			String btnText = getResources().getString(R.string.syllabus) + "\n(" + lecture.course_number;
			if (lecture.lecture_number.length() > 0) btnText += " " + lecture.lecture_number + ")";
			else btnText += ") ";
			((Button)actionBar.getCustomView().findViewById(R.id.syllabus_btn)).setText(btnText);
		}
		//search가 닫혀있는 상태에서 selectedMyLectuer가 존재
		else if (!isSearchVisible && Lecture.selectedMyLecture != null){
			if (actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS){
				actionBar.getCustomView().startAnimation(actionBarIn);
			}
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowCustomEnabled(true);
			Lecture lecture = Lecture.selectedMyLecture;
			String btnText = getResources().getString(R.string.syllabus) + "\n(" + lecture.course_number;
			if (lecture.lecture_number.length() > 0) btnText += " " + lecture.lecture_number + ")";
			else btnText += ") ";
			((Button)actionBar.getCustomView().findViewById(R.id.syllabus_btn)).setText(btnText);
		}
		else {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setDisplayShowCustomEnabled(false);
		}
	}

	public void showSearch(){
		searchLayout.startAnimation(searchIn);
		isSearchVisible = true;
		imm.showSoftInput(searchQueryView, 0);
		buttonPanel.setVisibility(View.GONE);
		//내 강의 선택 해제
		MyLectureFragment.clearSelectedMyLecture();
		
		setActionbar();
	}
	public void hideSearch(){
		searchLayout.startAnimation(searchOut);
		isSearchVisible = false;
		imm.hideSoftInputFromWindow(searchQueryView.getWindowToken(), 0);
		buttonPanel.setVisibility(View.VISIBLE);
		
		setActionbar();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// save active tab
		// super.onSaveInstanceState(outState);
		outState.putInt(ACTIVE_TAB, getSupportActionBar().getSelectedNavigationIndex());
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (isSearchVisible)
				hideSearch();
			else
				showSearch();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


	@Override
	public void onBackPressed() {
		if (isSearchVisible){
			hideSearch();
			return;
		}

		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, getResources().getString(R.string.click_back_twice), Toast.LENGTH_LONG).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce=false;   

			}
		}, 2000);
	} 
	
	public void openSugangSelector(){
		new SugangSelectorDialog(this).show();
	}
	
	public boolean getCustomEditable(){
		return customEditable;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.open_search:
			showSearch();
			break;
		case R.id.search_down:
			hideSearch();
			break;
		case R.id.save:
			TimetableView.mInstance.saveImage();
			break;
		case R.id.syllabus_btn:
			Lecture lecture = null;
			//강의계획서
			if (isSearchVisible && Lecture.selectedLecture != null){
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				getSupportActionBar().setDisplayShowCustomEnabled(true);
				lecture = Lecture.selectedLecture;
			}
			//search가 닫혀있는 상태에서 selectedMyLectuer가 존재
			else if (!isSearchVisible && Lecture.selectedMyLecture != null){
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				getSupportActionBar().setDisplayShowCustomEnabled(true);
				lecture = Lecture.selectedMyLecture;
			}
			if (lecture != null){
				int year = SharedPrefUtil.getInstance().getInt(SharedPrefUtil.PREF_KEY_CURRENT_YEAR);
				String semester = SharedPrefUtil.getInstance().getString(SharedPrefUtil.PREF_KEY_CURRENT_SEMESTER);

				String openShtmFg = null, openDetaShtmFg = null;
				if (semester.equals("1")){
					openShtmFg = "U000200001";
					openDetaShtmFg = "U000300001";
				} else if (semester.equals("2")){
					openShtmFg = "U000200002";
					openDetaShtmFg = "U000300001";
				} else if (semester.equals("S")){
					openShtmFg = "U000200001";
					openDetaShtmFg = "U000300002";	
				} else if (semester.equals("W")){
					openShtmFg = "U000200002";
					openDetaShtmFg = "U000300002";
				}
				
				String url = "http://sugang.snu.ac.kr/sugang/cc/cc103.action?openSchyy="+year+"&openShtmFg="+openShtmFg+"&openDetaShtmFg="+openDetaShtmFg+"&sbjtCd="+lecture.course_number+"&ltNo="+lecture.lecture_number+"&sbjtSubhCd=000";
				
			    Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
			    startActivity( browse );			
			}
			
			break;
		case R.id.sugang:
			openSugangSelector();
			break;
		case R.id.edit_custom:
			if (customEditable){
				customButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_event));
				customButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_rect_white));
				customEditable = false;
				Toast.makeText(this, getString(R.string.edit_custom_off), Toast.LENGTH_SHORT).show();
			} else {
				customButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_edit));
				customButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_rect_blue));
				customEditable = true;
				Toast.makeText(this, getString(R.string.edit_custom_on), Toast.LENGTH_SHORT).show();
			}
			TimetableView.mInstance.resetCustomVariables();
			break;
		}
	} 


}