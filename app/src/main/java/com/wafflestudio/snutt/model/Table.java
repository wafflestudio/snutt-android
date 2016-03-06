package com.wafflestudio.snutt.model;

import android.util.Log;

import com.google.common.base.Preconditions;

import java.util.Comparator;
import java.util.List;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class Table implements Comparable<Table> {

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

    public Table() {

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

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Table another) {
        if (getYear() > another.getYear()) return -1;
        if (getYear() < another.getYear()) return 1;
        if (getYear() == another.getYear()) {
            if (getSemester() > another.getSemester()) return -1;
            if (getSemester() < another.getSemester()) return 1;
            if (getSemester() == another.getSemester()) {
                // update time 기준으로 비교!
                return 0;
            }
        }
        return 0;
    }
}
