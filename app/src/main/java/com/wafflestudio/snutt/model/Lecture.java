package com.wafflestudio.snutt.model;

import com.google.gson.JsonArray;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class Lecture {
    private String classification;
    private String department;
    private String academic_year;
    private String course_number;
    private String lecture_number;
    private String course_title;
    private int credit;
    private String class_time;
    private JsonArray class_time_json;
    private String location;
    private String instructor;
    private int quota;
    private int enrollment;
    private String remark;
    private String category;
    private int colorIndex; //색상
    private Color color;
    private boolean isCustom = false;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }

    public String getLecture_number() {
        return lecture_number;
    }

    public void setLecture_number(String lecture_number) {
        this.lecture_number = lecture_number;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getClass_time() {
        return class_time;
    }

    public void setClass_time(String class_time) {
        this.class_time = class_time;
    }

    public JsonArray getClass_time_json() {
        return class_time_json;
    }

    public void setClass_time_json(JsonArray class_time_json) {
        this.class_time_json = class_time_json;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public int getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(int enrollment) {
        this.enrollment = enrollment;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public int getBgColor() {
        int bgColor = android.graphics.Color.parseColor(color.getBg());
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        String bg = String.format("#%06X", (0xFFFFFF & bgColor));
        color.setBg(bg);
    }

    public int getFgColor() {
        int fgColor = android.graphics.Color.parseColor(color.getFg());
        return fgColor;
    }

    public void setFgColor(int fgColor) {
        String fg = String.format("#%06X", (0xFFFFFF & fgColor));
        color.setFg(fg);
    }
}
