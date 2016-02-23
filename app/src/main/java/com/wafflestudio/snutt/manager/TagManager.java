package com.wafflestudio.snutt.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wafflestudio.snutt.model.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 2. 23..
 */
public class TagManager {

    private static final String TAG = "TAG_MANAGER" ;

    private Context context;
    private List<Tag> tagList;
    private List<Tag> myTag;
    private static TagManager singleton;

    /**
     * TableManager 싱글톤
     */

    private TagManager(Context context) {
        getDefaultTag();
        this.myTag = new ArrayList<>();
        this.context = context;
    }

    public static TagManager getInstance(Context context) {
        if(singleton == null) {
            singleton = new TagManager(context);
        }
        return singleton;
    }

    public static TagManager getInstance() {
        if (singleton == null) Log.e(TAG, "This method should not be called at this time!!");
        return singleton;
    }

    public interface OnTagChangedListener {
        void notifyTagChanged();
    }

    private OnTagChangedListener listener;

    public void registerListener(OnTagChangedListener fragment) {
        this.listener = fragment ;
    }

    public void unregisterListener() {
        this.listener = null;
    }

    public boolean addTag(String name) {
        for (Tag tag : tagList) {
            if (tag.getName().equals(name)) {
                myTag.add(0, tag);
                notifyTagChanged();
                return true;
            }
        }
        Log.e(TAG, "invalid tag name!!!");
        Toast.makeText(context, "유효하지 않은 테그입니다.", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void removeTag(int position) {
        myTag.remove(position);
        notifyTagChanged();
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public List<Tag> getMyTag() {
        return myTag;
    }

    private void getDefaultTag() {
        tagList = new ArrayList<>();
        tagList.add(new Tag("컴공", "학과"));
        tagList.add(new Tag("김명수 교수", "교수"));
        tagList.add(new Tag("컴퓨터공학", "학과"));
        tagList.add(new Tag("독어 교육과", "학과"));
        tagList.add(new Tag("불어 교육과", "학과"));
        tagList.add(new Tag("자율전공학부", "학과"));
   }

    private void notifyTagChanged() {
        if (listener == null) return;
        listener.notifyTagChanged();
    }
}
