package com.wafflestudio.snutt.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.SNUTTUtils;
import com.wafflestudio.snutt.model.Lecture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by makesource on 2016. 2. 7..
 */
public class LectureManager {

    private static final String TAG = "LECTURE_MANAGER" ;

    private Context context;
    private List<Lecture> lectures;
    private Lecture selectedLecture;
    private Random random = new Random();
    private int colorIndex = -1;

    // 사용자 정의 시간표
    private int customWday = -1;
    private float customDuration = -1;
    private float customStartTime = -1;
    private static LectureManager singleton;

    /**
     * LectureManager 싱글톤
     */

    private LectureManager(Context context) {
        this.context = context;
    }

    public static LectureManager getInstance(Context context) {
        if (singleton == null) {
            singleton = new LectureManager(context);
        }
        return singleton;
    }

    public static LectureManager getInstance() {
        if (singleton == null) Log.e(TAG, "This method should not be called at this time!!");
        return singleton;
    }

    public interface OnLectureChangedListener {
        void notifyLectureChanged();
    }

    private List<OnLectureChangedListener> listeners = new ArrayList<>();

    public void addListener(OnLectureChangedListener listener) {
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i).equals(listener)) {
                Log.w(TAG, "listener reference is duplicated !!");
                return;
            }
        }
        listeners.add(listener);
    }

    public void removeListener(OnLectureChangedListener listener) {
        for (int i=0;i<listeners.size(); i++) {
            OnLectureChangedListener reference = listeners.get(i);
            if (reference == listener) {
                listeners.remove(i);
                break;
            }
        }
    }

    ////////

    public List<Lecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    public Lecture getSelectedLecture() {
        return selectedLecture;
    }

    public void setSelectedLecture(Lecture selectedLecture) {
        this.selectedLecture = selectedLecture;
        notifyLectureChanged();
    }

    public void addLecture(Lecture lec) {
        if (alreadyOwned(lec)) {
            Log.w(TAG, "lecture is duplicated!! ");
            return ;
        }
        if (alreadyExistClassTime(lec)) {
            Log.d(TAG, "lecture is duplicated!! ");
            Toast.makeText(context, "강의시간이 겹칩니다", Toast.LENGTH_SHORT).show();
            return ;
        }
        int random = getRandomColor();
        lec.setColorIndex(getRandomColor());
        lec.setBgColor(SNUTTUtils.getBgColorByIndex(random));
        lec.setFgColor(SNUTTUtils.getFgColorByIndex(random));
        lectures.add(lec);
        notifyLectureChanged();
    }

    public void removeLecture(Lecture lec) {
        Lecture target = null;
        for (Lecture lecture : lectures) {
            if (isEqualLecture(lecture, lec)) {
                target = lecture;
                break;
            }
        }
        if (target != null) {
            lectures.remove(target);
            notifyLectureChanged();
            return ;
        }
        Log.w(TAG, "lecture is not exist!!");
    }

    // TODO : (SeongWon) 배경색, 글자색 업데이트?
    public void updateLecture(Lecture lec, String title, String inst, JsonArray timeJson) {
        lec.setCourse_title(title);
        lec.setInstructor(inst);
        lec.setClass_time_json(timeJson);
        notifyLectureChanged();
        Log.d(TAG, "lecture is updated!!");
        // server에 update하기
    }

    // TODO : (SeongWon) 배경색, 글자색 업데이트?
    public void updateLecture(Lecture lec, int bgColor, int fgColor) {
        lec.setBgColor(bgColor);
        lec.setFgColor(fgColor);
        notifyLectureChanged();
        Log.d(TAG, "lecture is updated!!");
        // server에 update하기
    }

    public void setNextColor(Lecture lec) {
        Log.d(TAG, "setNextColor method called!!");
        colorIndex = (colorIndex + 1) % 7;
        if (colorIndex == 0) colorIndex++;
        lec.setColorIndex(colorIndex);
        lec.setBgColor(SNUTTUtils.getBgColorByIndex(colorIndex));
        lec.setFgColor(SNUTTUtils.getFgColorByIndex(colorIndex));
        notifyLectureChanged();
    }

    //내 강의에 이미 들어있는지 -> course_number, lecture_number 비교
    public boolean alreadyOwned(Lecture lec){
        for (Lecture lecture : lectures){
            if (isEqualLecture(lecture, lec)) return true;
        }
        return false;
    }

    //이미 내 강의에 존재하는 시간인지
    public boolean alreadyExistClassTime(Lecture lec) {
        for (Lecture lecture : lectures){
            if (isDuplicatedClassTime(lecture, lec)) return true;
        }
        return false;
    }

    //이미 내 강의에 존재하는 시간인지 (day, time, duration 으로 비교), custom lecture 생성시
    public boolean alreadyExistClassTime(float duration) {
        for (Lecture lecture : lectures) {
            if (isDuplicatedClassTime(lecture, customWday, customStartTime, duration)) return true;
        }
        return false;
    }

    //주어진 요일, 시각을 포함하고 있는지
    public boolean contains(Lecture lec1, int given_day, float given_time) {
        for (JsonElement element1 : lec1.getClass_time_json()) {
            JsonObject class1 = element1.getAsJsonObject();

            int day1 = class1.get("day").getAsInt();
            float start1 = class1.get("start").getAsFloat();
            float len1 = class1.get("len").getAsFloat();
            float end1 = start1 + len1 - 0.001f;

            if (day1 != given_day) continue;
            if (start1 <= given_time && given_time <= end1) return true;
        }
        return false;
    }

    //사용자 정의 시간표 초기화
    public void resetCustomLecture() {
        customStartTime = customDuration = customWday = -1;
        notifyLectureChanged();
    }

    public void setCustomValue(int wday, float startTime, float duration) {
        Log.d(TAG, "setting custom lecture values..");
        customWday = wday;
        customStartTime = startTime;
        customDuration = duration;
        notifyLectureChanged();
    }

    public int getCustomWday() {
        return customWday;
    }

    public float getCustomStartTime() {
        return customStartTime;
    }

    public float getCustomDuration() {
        return customDuration;
    }

    public void setCustomDuration(float duration) {
        customDuration = duration;
        notifyLectureChanged();
    }

    //사용자 정의 시간표가 있는지
    public boolean existCustomLecture() {
        return (customWday != -1 && customStartTime != -1 && customDuration > 0);
    }

    private boolean isEqualLecture(Lecture lec1,Lecture lec2) {
        if (lec1.getCourse_number().equals(lec2.getCourse_number()) &&
                lec1.getLecture_number().equals(lec2.getLecture_number())) return true;

        return false;
    }

    private boolean isDuplicatedClassTime(Lecture lec1,Lecture lec2) {
        for (JsonElement element1 : lec1.getClass_time_json()) {
            JsonObject class1= element1.getAsJsonObject();

            int day1 = class1.get("day").getAsInt();
            float start1 = class1.get("start").getAsFloat();
            float len1 = class1.get("len").getAsFloat();
            float end1 = start1 + len1;
            for (JsonElement element2 : lec2.getClass_time_json()) {
                JsonObject class2 = element2.getAsJsonObject();

                int day2 = class2.get("day").getAsInt();
                float start2 = class2.get("start").getAsFloat();
                float len2 = class2.get("len").getAsFloat();
                float end2 = start2 + len2;

                if (day1 != day2) continue;
                if (!(end1 <= start2 || end2 <= start1)) return true;
            }
        }
        return false;
    }

    private boolean isDuplicatedClassTime(Lecture lec1,int day2,float start2, float len2) {
        for (JsonElement element1 : lec1.getClass_time_json()) {
            JsonObject class1= element1.getAsJsonObject();
            int day1 = class1.get("day").getAsInt();
            float start1 = class1.get("start").getAsFloat();
            float len1 = class1.get("len").getAsFloat();
            float end1 = start1 + len1;
            float end2 = start2 + len2;

            if (day1 != day2) continue;
            if (!(end1 <= start2 || end2 <= start1)) return true;
        }
        return false;
    }

    private int getRandomColor(){
        while (true){
            int colorIndex = random.nextInt(6) + 1;
            if (colorIndex != this.colorIndex){
                this.colorIndex = colorIndex;
                return colorIndex;
            }
        }
    }

    private void setDefaultLecture() {
        lectures = new ArrayList<>();
        Lecture sample = new Lecture();
        sample.setClassification("교양");
        sample.setDepartment("건설환경공학부");
        sample.setAcademic_year("2학년");
        sample.setCourse_number("035.001");
        sample.setCourse_title("컴퓨터의 개념 및 실습");
        sample.setLecture_number("001");
        sample.setLocation("301-1/301-2");
        sample.setCredit(3);
        sample.setClass_time("월(6-2)/수(6-2)");
        sample.setInstructor("몰라아직 ㅜㅜ");
        sample.setQuota(60);
        sample.setEnrollment(0);
        sample.setRemark("건설환경공학부만 수강가능");
        sample.setCategory("foundation_computer");
        sample.setColorIndex(1);
        lectures.add(sample);
    }

    private void notifyLectureChanged() {
        for (OnLectureChangedListener listener : listeners) {
            listener.notifyLectureChanged();
        }
    }

}
