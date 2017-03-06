package com.wafflestudio.snutt_staging.model;

/**
 * Created by makesource on 2016. 2. 21..
 */
public class Tag {

    private String name;
    private String category;

    public Tag(String name,String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
