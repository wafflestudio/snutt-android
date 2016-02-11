package com.wafflestudio.snutt.manager;

import android.util.Log;

import com.wafflestudio.snutt.model.Lecture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 2. 7..
 */
public class LectureManager {

    private static final String TAG = "LECTURE_MANAGER" ;

    private List<Lecture> lectures;
    private Lecture selectedLecture;

    private static LectureManager singleton;

    /**
     * LectureManager 싱글톤
     */

    private LectureManager() {
    }

    public static LectureManager getInstance() {
        if(singleton == null) {
            singleton = new LectureManager();
        }
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

    //내 강의에 이미 들어있는지 -> course_number, lecture_number 비교
    public boolean alreadyOwned(Lecture lec){
        for (int i=0;i<lectures.size();i++){
            if (isEqualLecture(lectures.get(i), lec)) return true;
        }
        return false;
    }

    //이미 내 강의에 존재하는 시간인지
    public boolean alreadyExistClassTime()
    {
      /*  for (int i=0;i<Lecture.myLectures.size();i++){
            if (isDuplicatedClassTime(this, Lecture.myLectures.get(i))) return true;
        }*/
        return false;
    }

    private boolean isEqualLecture(Lecture lec1,Lecture lec2) {
        if (lec1.getCourse_number().equals(lec2.getCourse_number()) &&
                lec1.getLecture_number().equals(lec2.getLecture_number())) return true;

        return false;
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
