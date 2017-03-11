package com.wafflestudio.snutt_staging.model;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt_staging.SNUTTUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class Lecture {
    /*
     * 주의 !
     * 검색시 날아오는 lecture id 와
     * 내 시간표에 추가된 lecture id 는 서로 다른 값
     */
    private static DecimalFormat decimalFormat = new DecimalFormat();
    private String _id;
    private String classification;
    private String department;
    private String academic_year;
    private String course_number;
    private String lecture_number;
    private String course_title;
    private int credit;
    private String class_time; // lecture 검색시 띄어주는 class time
    private JsonArray class_time_json;
    private String location;
    private String instructor;
    private int quota;
    private int enrollment;
    private String remark;
    private String category;
    private int colorIndex; //색상
    private Color color;

    public Lecture() {
        this.color = new Color();
    }

    public Lecture(Lecture lec) {
        this._id = lec._id;
        this.classification = lec.classification;
        this.department = lec.department;
        this.academic_year = lec.academic_year;
        this.course_number = lec.course_number;
        this.lecture_number = lec.lecture_number;
        this.course_title = lec.course_title;
        this.credit = lec.credit;
        this.class_time = lec.class_time;
        this.class_time_json = lec.class_time_json;
        this.location = lec.location;
        this.instructor = lec.instructor;
        this.quota = lec.quota;
        this.enrollment = lec.enrollment;
        this.remark = lec.remark;
        this.category = lec.category;
        this.colorIndex = 0;
        this.color = new Color();
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

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
        if (Strings.isNullOrEmpty(course_number) &&
                Strings.isNullOrEmpty(lecture_number)) return true;
        return false;
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
        return color.getBg();
    }

    public void setBgColor(int bgColor) {
        color.setBg(bgColor);
    }

    public int getFgColor() {
        return color.getFg();
    }

    public void setFgColor(int fgColor) {
        color.setFg(fgColor);
    }


    //간소화된 강의 시간
    public String getSimplifiedClassTime() {
        String text = "";
        for (int i = 0;i < getClass_time_json().size();i ++) {
            JsonObject class1 = getClass_time_json().get(i).getAsJsonObject();

            int day = class1.get("day").getAsInt();
            float start = class1.get("start").getAsFloat();
            float len = class1.get("len").getAsFloat();
            String place = class1.get("place").getAsString();

            text += SNUTTUtils.numberToWday(day) + decimalFormat.format(start);
            if (i != getClass_time_json().size() - 1) text += "/";
        }
        if (Strings.isNullOrEmpty(text)) text = "(없음)";
        return text;
    }

    public String getSimplifiedLocation(){
        String text = "";
        for (int i = 0;i < getClass_time_json().size();i ++) {
            JsonObject class1 = getClass_time_json().get(i).getAsJsonObject();

            int day = class1.get("day").getAsInt();
            float start = class1.get("start").getAsFloat();
            float len = class1.get("len").getAsFloat();
            String place = class1.get("place").getAsString();

            text += place;
            if (i != getClass_time_json().size() - 1) text += "/";
        }
        if (Strings.isNullOrEmpty(text)) text = "(없음)";
        return text;
    }
}
