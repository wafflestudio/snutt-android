package com.wafflestudio.snutt.manager;

import android.util.Log;

import com.wafflestudio.snutt.model.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 2. 23..
 */
public class TagManager {

    private static final String TAG = "TAG_MANAGER" ;

    private List<Tag> tagList;
    private List<Tag> myTag;
    private static TagManager singleton;

    /**
     * TableManager 싱글톤
     */

    private TagManager() {
        getDefaultTag();
        myTag = new ArrayList<>();
    }

    public static TagManager getInstance() {
        if(singleton == null) {
            singleton = new TagManager();
        }
        return singleton;
    }

    public void addTag(Tag tag) {
        myTag.add(tag);
    }

    public void removeTag(Tag tag) {
        myTag.remove(tag);
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public List<Tag> getMyTag() {
        return myTag;
    }

    private void getDefaultTag() {
        tagList = new ArrayList<>();
        tagList.add(new Tag("1","컴공"));
        tagList.add(new Tag("2","김명수 교수"));
        tagList.add(new Tag("3", "컴퓨터공학"));
        tagList.add(new Tag("4", "가나다라마바사아아아아"));
        tagList.add(new Tag("5", "이히히히히히가나다라다라나마바"));
        tagList.add(new Tag("6", "하나이라나대나이자아"));
    }


}
