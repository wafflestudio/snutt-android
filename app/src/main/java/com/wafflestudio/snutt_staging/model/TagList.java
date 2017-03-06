package com.wafflestudio.snutt_staging.model;

import java.util.List;

/**
 * Created by makesource on 2016. 5. 22..
 */
public class TagList {
    private List<String> classification;
    private List<String> department;
    private List<String> academic_year;
    private List<String> credit;
    private List<String> instructor;
    private List<String> category;

    public List<String> getClassification() {
        return classification;
    }

    public void setClassification(List<String> classification) {
        this.classification = classification;
    }

    public List<String> getDepartment() {
        return department;
    }

    public void setDepartment(List<String> department) {
        this.department = department;
    }

    public List<String> getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(List<String> academic_year) {
        this.academic_year = academic_year;
    }

    public List<String> getCredit() {
        return credit;
    }

    public void setCredit(List<String> credit) {
        this.credit = credit;
    }

    public List<String> getInstructor() {
        return instructor;
    }

    public void setInstructor(List<String> instructor) {
        this.instructor = instructor;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }
}
