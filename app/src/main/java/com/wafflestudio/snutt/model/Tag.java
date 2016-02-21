package com.wafflestudio.snutt.model;

/**
 * Created by makesource on 2016. 2. 21..
 */
public class Tag {
    private String id;
    private String name;

    public Tag(String id,String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
