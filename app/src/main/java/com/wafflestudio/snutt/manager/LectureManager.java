package com.wafflestudio.snutt.manager;

import com.wafflestudio.snutt.model.Lecture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 2. 7..
 */
public class LectureManager {

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
    }

    //내 강의에 이미 들어있는지 -> id 로 확인???
    public boolean alreadyOwned(){
   /*     for (int i=0;i<Lecture.myLectures.size();i++){
            if (Lecture.myLectures.get(i) == this)
                return true;
        }*/
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
}
