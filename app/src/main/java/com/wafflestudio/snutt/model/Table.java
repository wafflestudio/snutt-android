package com.wafflestudio.snutt.model;

import java.util.List;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class Table {

    private String id;
    private String year;
    private String semester;
    private String title;
    private List<Lecture> lectures;

    public Table(String id, String year, String semester, String title, List<Lecture> lectures) {
        this.id = id;
        this.year = year;
        this.semester = semester;
        this.title = title;
        this.lectures = lectures;
    }

    public Table(String id,String title) {
        this.id = id;
        this.year = null;
        this.semester = null;
        this.title = title;
        this.lectures = null;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
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
        return this.year + '-' + this.semester;
    }
}
