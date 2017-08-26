package com.wafflestudio.snutt_staging.manager;

import android.util.Log;

import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.model.Tag;
import com.wafflestudio.snutt_staging.model.TagList;
import com.wafflestudio.snutt_staging.model.TagType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 2. 23..
 */
public class TagManager {

    private static final String TAG = "TAG_MANAGER" ;

    // for search
    private List<Tag> tags;
    private Map<String, Tag> tagsMap;
    private boolean searchEmptyClass;

    // for api post
    private List<String> classification, credit, academic_year, instructor, department, category, time;
    private List<Tag> myTags;

    private SNUTTApplication app;
    private static TagManager singleton;

    /**
     * TableManager 싱글톤
     */

    private TagManager(SNUTTApplication app) {
        tags = new ArrayList<>();
        classification = new ArrayList<>();
        credit = new ArrayList<>();
        academic_year = new ArrayList<>();
        instructor = new ArrayList<>();
        department = new ArrayList<>();
        category = new ArrayList<>();
        time = new ArrayList<>();
        tagsMap = new HashMap<>();
        myTags = new ArrayList<>();
        searchEmptyClass = false;
        this.app = app;
    }

    public static TagManager getInstance(SNUTTApplication app) {
        if(singleton == null) {
            singleton = new TagManager(app);
        }
        return singleton;
    }

    public static TagManager getInstance() {
        if (singleton == null) Log.e(TAG, "This method should not be called at this time!!");
        return singleton;
    }

    public interface OnTagChangedListener {
        void notifyMyTagChanged(boolean anim);
        void notifyTagListChanged();
    }

    private OnTagChangedListener listener;

    public void registerListener(OnTagChangedListener fragment) {
        this.listener = fragment ;
    }

    public void unregisterListener() {
        this.listener = null;
    }

    public void reset() {
        tags.clear();
        classification.clear();
        credit.clear();
        academic_year.clear();
        instructor.clear();
        department.clear();
        category.clear();
        time.clear();
        tagsMap.clear();
        myTags.clear();
        searchEmptyClass = false;
    }

    public boolean addTag(String query) {
        if (!tagsMap.containsKey(query.toLowerCase())) return false;
        addTag(tagsMap.get(query));
        return true;
    }

    public boolean addTag(Tag tag) {
        TagType type = tag.getTagType();
        switch (type) {
            case CLASSIFICATION:
                classification.add(tag.getName());
                break;
            case CREDIT:
                credit.add(tag.getName());
                break;
            case ACADEMIC_YEAR:
                academic_year.add(tag.getName());
                break;
            case INSTRUCTOR:
                instructor.add(tag.getName());
                break;
            case DEPARTMENT:
                department.add(tag.getName());
                break;
            case CATEGORY:
                category.add(tag.getName());
                break;

        }
        Log.d(TAG, "a tag is successfully added!!!");
        myTags.add(0, tag);
        notifyMyTagChanged(true);
        return true;
    }

    public void removeTag(int position) {
        Tag tag = myTags.get(position);
        switch (tag.getTagType()) {
            case CLASSIFICATION:
                classification.remove(tag.getName());
                break;
            case CREDIT:
                credit.remove(tag.getName());
                break;
            case ACADEMIC_YEAR:
                academic_year.remove(tag.getName());
                break;
            case INSTRUCTOR:
                instructor.remove(tag.getName());
                break;
            case DEPARTMENT:
                department.remove(tag.getName());
                break;
            case CATEGORY:
                category.remove(tag.getName());
                break;
        }
        myTags.remove(position);
        notifyMyTagChanged(true);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Tag> getMyTags() {
        return myTags;
    }

    public void updateNewTag(int year, int semester) {
        app.getRestService().getTagList(year, semester, new Callback<TagList>() {
            @Override
            public void success(TagList tagList, Response response) {
                Log.d(TAG, "update new tags Success!!");
                reset();

                for (String name : tagList.getClassification()) {
                    Tag tag = new Tag(name, TagType.CLASSIFICATION);
                    tagsMap.put(name.toLowerCase(), tag);
                    tags.add(tag);
                }
                for (String name : tagList.getCredit()) {
                    Tag tag = new Tag(name, TagType.CREDIT);
                    tagsMap.put(name.toLowerCase(), tag);
                    tags.add(tag);
                }
                for (String name : tagList.getAcademic_year()) {
                    Tag tag = new Tag(name, TagType.ACADEMIC_YEAR);
                    tagsMap.put(name.toLowerCase(), tag);
                    tags.add(tag);
                }
                for (String name : tagList.getInstructor()) {
                    Tag tag = new Tag(name, TagType.INSTRUCTOR);
                    tagsMap.put(name.toLowerCase(), tag);
                    tags.add(tag);
                }
                for (String name : tagList.getDepartment()) {
                    Tag tag = new Tag(name, TagType.DEPARTMENT);
                    tagsMap.put(name.toLowerCase(), tag);
                    tags.add(tag);
                }
                for (String name : tagList.getCategory()) {
                    Tag tag = new Tag(name.toLowerCase(), TagType.CATEGORY);
                    tagsMap.put(name, tag);
                    tags.add(tag);
                }
                notifyMyTagChanged(false);
                notifyTagListChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "update new tags failed...");
            }
        });
    }

    /* Below method will used when post query */

    public List<String> getClassification() {
        return classification;
    }

    public void setClassification(List<String> classification) {
        this.classification = classification;
    }

    public List<String> getCredit() {
        List<String> integerCredit = new ArrayList<>();
        for (String c : credit) {
            integerCredit.add(c.substring(0, c.length() - 2));
        }
        return integerCredit;
    }

    public void setCredit(List<String> credit) {
        this.credit = credit;
    }

    public List<String> getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(List<String> academic_year) {
        this.academic_year = academic_year;
    }

    public List<String> getInstructor() {
        return instructor;
    }

    public void setInstructor(List<String> instructor) {
        this.instructor = instructor;
    }

    public List<String> getDepartment() {
        return department;
    }

    public void setDepartment(List<String> department) {
        this.department = department;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public boolean getSearchEmptyClass() {
        return searchEmptyClass;
    }

    public boolean toggleSearchEmptyClass() {
        this.searchEmptyClass = !this.searchEmptyClass;
        return searchEmptyClass;
    }

    public void setSearchEmptyClass(boolean searchEmptyClass) {
        this.searchEmptyClass = searchEmptyClass;
    }

    private void notifyMyTagChanged(boolean anim) {
        if (listener == null) return;
        listener.notifyMyTagChanged(anim);
    }

    private void notifyTagListChanged() {
        if (listener == null) return;
        listener.notifyTagListChanged();
    }
}
