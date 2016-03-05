package com.wafflestudio.snutt.model;

import android.util.Log;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class Table {

    private static final String TAG = "MODEL_TABLE" ;

    private String id;
    private int year;
    private int semester;
    private String title;
    private List<Lecture> lectures;

    public Table(String id, int year, int semester, String title, List<Lecture> lectures) {
        this.id = id;
        this.year = year;
        this.semester = semester;
        this.title = title;
        this.lectures = lectures;
    }

    public Table(String id,String title) {
        this.id = id;
        this.year = 0;
        this.semester = 0;
        this.title = title;
        this.lectures = null;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullSemester() {
        String yearString;
        String semesterString;

        yearString = String.valueOf(this.year);
        switch (this.semester) {
            case 1:
                semesterString = "1";
                break;
            case 2:
                semesterString = "S";
                break;
            case 3:
                semesterString = "2";
                break;
            case 4:
                semesterString = "W";
                break;
            default:
                semesterString = "";
                Log.e(TAG, "semester is out of range!!");
                break;
        }
        return yearString + '-' + semesterString;
    }
}
