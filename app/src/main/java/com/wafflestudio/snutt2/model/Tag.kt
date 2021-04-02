package com.wafflestudio.snutt2.model;

/**
 * Created by makesource on 2016. 2. 21..
 */
public class Tag {

    private String name;
    private TagType tagType;

    public Tag(String name,TagType tagType) {
        this.name = name;
        this.tagType = tagType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

    public TagType getTagType() {
        return tagType;
    }

}
