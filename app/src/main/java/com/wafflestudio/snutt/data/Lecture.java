package com.wafflestudio.snutt.data;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.widget.Toast;

import com.wafflestudio.snutt.activity.main.MainActivity;
import com.wafflestudio.snutt.util.SharedPrefUtil;
import com.wafflestudio.snutt.util.TimetableUtil;

public class Lecture {	
	public static ArrayList<Lecture> lectures = new ArrayList<Lecture>();
	public static ArrayList<Lecture> myLectures = new ArrayList<Lecture>();
	public static Lecture selectedLecture, selectedMyLecture;
	public static String updatedDate;
	
	public String classification;
	public String department;
	public String academic_year;
	public String course_number;
	public String lecture_number;
	public String course_title;
	public int credit;
	public String class_time;
	public String location;
	public String instructor;
	public int quota;
	public int enrollment;
	public String remark;
	public String category;
	
	public int colorIndex; //색상
	
	public boolean isCustom = false;
	
	//사용자 정의 강의
	public Lecture(String course_title, String location, int wday, float startTime, float duration){
		this.classification = "사용자";
		this.department = "사용자";
		this.academic_year = "";
		this.course_number = "999.999";
		this.lecture_number = "";
		this.credit = 0;
		this.instructor = "";
		this.quota = 0;
		this.enrollment = 0;
		this.remark = "";
		this.category = "사용자";

		
		this.course_title = course_title;
		this.location = location;
		this.class_time = TimetableUtil.numberToWdayString(wday) + "(" + startTime + "-" + duration + ")";
		
		isCustom = true;
	}
	public Lecture(String course_title, String location, String classTime, int colorIndex){
		this.classification = "사용자";
		this.department = "사용자";
		this.academic_year = "";
		this.course_number = "999.999";
		this.lecture_number = "";
		this.credit = 0;
		this.instructor = "";
		this.quota = 0;
		this.enrollment = 0;
		this.remark = "";
		this.category = "사용자";

		
		this.course_title = course_title;
		this.location = location;
		this.class_time = classTime;
		this.colorIndex = colorIndex;
		
		isCustom = true;
	}
	
	public Lecture(String classification, String department, String academic_year, String course_number, 
			String lecture_number, String course_title, int credit, String class_time, 
			String location, String instructor, int quota, int enrollment, String remark, String category){
		this.classification = classification;
		this.department = department;
		this.academic_year = academic_year;
		this.course_number = course_number;
		this.lecture_number = lecture_number;
		this.course_title = course_title;
		this.credit = credit;
		this.class_time = class_time;
		this.location = location;
		this.instructor = instructor;
		this.quota = quota;
		this.enrollment = enrollment;
		this.remark = remark;
		this.category = category;
	}
	
	public String getClassification(){
		if (this.category.contains("core"))
			return "핵교";
		else
			return classification;
	}
	
	//간소화된 강의 시간
	public String getSimplifedClassTime(){
		return class_time.replaceAll("-[\\d.,]*\\)", ")").replaceAll(",[\\d.,]*\\)", ")").replaceAll("[()]", "");
	}
	
	//간소화된 장소 (중복 제거) 
	public String getSimplifiedLocation(){
		String[] arr = this.location.split("/");
		ArrayList<String> result = new ArrayList<String>();
		for (int i=0;i<arr.length;i++){
			int j;
			for (j=0;j<result.size();j++){
				if (result.get(j).equals(arr[i])){
					break;
				}
			}
			if (j == result.size())
				result.add(arr[i]);
		}
		String resultStr = "";
		for (int i=0;i<result.size();i++){
			resultStr += result.get(i);
			if (i != result.size() - 1)
				resultStr += "/";
		}
		return resultStr;
	}
	
	//주어진 요일, 시각을 포함하고 있는지 
	public boolean contains(int given_wday, float given_time){
		String[] classStr = this.class_time.split("/");
		for (int i=0;i<classStr.length;i++){
			String str = classStr[i];
			//str : 월(1.5-1.5)
			int wday;
			float startTime, duration;
			if (str.trim().length() == 0) continue;
			String[] str1 = str.split("\\(");
			String[] str2 = str1[1].split("\\)");
			String[] str3 = str2[0].split("-");
			wday = TimetableUtil.wdayToNumber(str1[0]);
			startTime = Float.parseFloat(str3[0]);
			duration = Float.parseFloat(str3[1]);
			
			if (wday == given_wday && (startTime <= given_time && given_time < startTime + duration)){
				return true;
			}
		}
		return false;
	}
	
	//내 강의에 이미 들어있는지
	public boolean alreadyOwned(){
		for (int i=0;i<Lecture.myLectures.size();i++){
			if (Lecture.myLectures.get(i) == this)
				return true;
		}
		return false;
	}
	
	//이미 내 강의에 존재하는 시간인지
	public boolean alreadyExistClassTime()
	{
		for (int i=0;i<Lecture.myLectures.size();i++){
			if (isDuplicatedClassTime(this, Lecture.myLectures.get(i))) return true;
		}
		return false;
	}
	
	static boolean increasing_sequence(float a, float b, float c){
		if (a < b && b < c) return true;
		return false;
	}
	static float parseFloat(String str){
		try {
			return Float.parseFloat(str);
		} catch (Exception e){
			return -1;
		}
	}
	
	//현재 내 강의를 저장 (강의번호 + 강좌번호 + colorIndex)
	public static void saveMyLectures(){
		String resultStr = "";
		String customStr = "";
		for (int i=0;i<myLectures.size();i++){
			Lecture lecture = myLectures.get(i);
			if (lecture.isCustom){
				customStr += lecture.course_title + " ; " + lecture.location + " ; " + lecture.class_time + " ; " + lecture.colorIndex + " / ";
			} else {
				resultStr += lecture.course_number + " ; " + lecture.lecture_number + " ; " + lecture.colorIndex + " / ";
			}
		}
		SharedPrefUtil.getInstance().setString(TimetableUtil.getMyLecturesPrefKey(), resultStr);
		SharedPrefUtil.getInstance().setString(TimetableUtil.getCustomLecturesPrefKey(), customStr);
	}
	
	//저장된 내 강의를 불러옴
	public static void loadMyLectures(){
		String ttStr = SharedPrefUtil.getInstance().getString(TimetableUtil.getMyLecturesPrefKey());
		String customStr = SharedPrefUtil.getInstance().getString(TimetableUtil.getCustomLecturesPrefKey());
		if (ttStr == null) ttStr = "";
		if (customStr == null) customStr = "";
		
		String[] lecturesStr = ttStr.split("/");
		if (Lecture.myLectures == null)
			Lecture.myLectures = new ArrayList<Lecture>();
		Lecture.myLectures.clear();

		try {
			for (int k=0;k<Lecture.lectures.size();k++){
				Lecture lecture = Lecture.lectures.get(k);
				for (int i=0;i<lecturesStr.length;i++){
					if (lecturesStr[i].trim().length() > 0){
						String[] str = lecturesStr[i].split(";");
						String courseNumber = str[0].trim();
						String lectureNumber = str[1].trim();
						int colorIndex = Integer.parseInt(str[2].trim());
						if (lecture.course_number.trim().equals(courseNumber) &&
								lecture.lecture_number.trim().equals(lectureNumber)){
							lecture.colorIndex = colorIndex;
							Lecture.myLectures.add(lecture);
						}
					}
				}
			}
			String[] customs = customStr.split("/");
			for (int i=0;i<customs.length-1;i++){
				String[] str = customs[i].split(";");
				String courseTitle = str[0].trim();
				String location = str[1].trim();
				String classTime = str[2].trim();
				int colorIndex = Integer.parseInt(str[3].trim());

				Lecture.myLectures.add(new Lecture(courseTitle, location, classTime, colorIndex));
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		MainActivity.myLectureChanged();
	}
	
	//두 강의의 시간이 겹치는지 체크
	public static boolean isDuplicatedClassTime(Lecture l1, Lecture l2)
	{
		String[] t1 = l1.class_time.split("/");
		String[] t2 = l2.class_time.split("/");
		for (int i=0;i<t1.length;i++){
			for (int j=0;j<t2.length;j++){
				if (t1[i].length() == 0 || t2[j].length() == 0) continue;
				//월(3-3), 월(4-2)
				char wday1 = t1[i].charAt(0);
				char wday2 = t2[j].charAt(0);
				String[] time1 = t1[i].replaceAll("[()]", "").substring(1).split("-");
				String[] time2 = t2[j].replaceAll("[()]", "").substring(1).split("-");
				if (time1.length > 1 && time2.length > 1){
					float start_time1 = parseFloat(time1[0]);
					float start_time2 = parseFloat(time2[0]);
					float duration1 = parseFloat(time1[1]);
					float duration2 = parseFloat(time2[1]);
					if (wday1 == wday2 && 
							(increasing_sequence(start_time1, start_time2, start_time1+duration1) ||
									increasing_sequence(start_time1, start_time2+duration2, start_time1+duration1) ||
									increasing_sequence(start_time2, start_time1, start_time2+duration2) ||
									increasing_sequence(start_time2, start_time1+duration1, start_time2+duration2) ||
									(start_time1 == start_time2 && duration1 == duration2)
									))
						return true;
				}
			}
		}
		return false;
	}
	
	static Random random = new Random();
	public static void addMyLecture(Context context, Lecture lecture){
		if (lecture.alreadyOwned()){
			Toast.makeText(context, "이미 넣은 강의 입니다.", Toast.LENGTH_SHORT).show();
		}
		else if (lecture.alreadyExistClassTime()){
			Toast.makeText(context, "강의 시간이 겹칩니다.", Toast.LENGTH_SHORT).show();
		}
		else {
			lecture.setRandomColor();
			Lecture.selectedMyLecture = Lecture.selectedLecture = null;
			Lecture.myLectures.add(lecture);
			MainActivity.myLectureChanged();
			Toast.makeText(context, "'" + lecture.course_title + "' 가 추가되었습니다.", Toast.LENGTH_SHORT).show();
		}
		saveMyLectures();
	}
	public static void removeMyLecture(Context context, Lecture lecture){
		Lecture.selectedMyLecture = Lecture.selectedLecture = null;
		Lecture.myLectures.remove(lecture);
		MainActivity.myLectureChanged();
		Toast.makeText(context, "'" + lecture.course_title + "' 가 제거되었습니다.", Toast.LENGTH_SHORT).show();
		saveMyLectures();
	}
	
	public void setRandomColor(){
		while (true){
			int colorIndex = random.nextInt(6) + 1;
			if (colorIndex != this.colorIndex){
				this.colorIndex = colorIndex;
				break;
			}
		}
	}
	public void setNextColor(){
		colorIndex = (colorIndex + 1) % 7;
		if (colorIndex == 0) colorIndex++;
	}

}
