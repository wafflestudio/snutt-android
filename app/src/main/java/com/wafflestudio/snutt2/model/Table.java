package com.wafflestudio.snutt2.model;

import android.util.Log;

import java.util.List;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class Table implements Comparable<Table> {

    private static final String TAG = "MODEL_TABLE" ;

    private String _id;
    private int year;
    private int semester;
    private String title;
    private List<Lecture> lecture_list;

    public Table(String id, int year, int semester, String title, List<Lecture> lecture_list) {
        this._id = id;
        this.year = year;
        this.semester = semester;
        this.title = title;
        this.lecture_list = lecture_list;
    }

    public Table(String id,String title) {
        this._id = id;
        this.year = 0;
        this.semester = 0;
        this.title = title;
        this.lecture_list = null;
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
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public List<Lecture> getLecture_list() {
        return lecture_list;
    }

    public void setLecture_list(List<Lecture> lecture_list) {
        this.lecture_list = lecture_list;
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

    public String getCreditText() {
        if (getTotalCredit() == -1) return "";
        return getTotalCredit() + "학점";
    }

    private int getTotalCredit() {
        int credit = 0;
        for (Lecture lecture: lecture_list) {
            credit += lecture.getCredit();
        }

        if (credit < 0) return -1;
        return credit;
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
