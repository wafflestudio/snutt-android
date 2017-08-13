package com.wafflestudio.snutt_staging.manager;

import android.util.Log;
import android.widget.Toast;

import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.model.Tag;
import com.wafflestudio.snutt_staging.model.TagList;

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

    private List<String> tags;
    private List<String> classification, credit, academic_year, instructor, department, category, time;
    private List<Tag> myTags;
    private Map<String, String> field;
    private boolean searchEmptyClass;

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
        field = new HashMap<>();
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
        void notifyTagChanged(boolean anim);
    }

    private OnTagChangedListener listener;

    public void registerListener(OnTagChangedListener fragment) {
        this.listener = fragment ;
    }

    public void unregisterListener() {
        this.listener = null;
    }

    public void reset() {
        tags = new ArrayList<>();
        classification = new ArrayList<>();
        credit = new ArrayList<>();
        academic_year = new ArrayList<>();
        instructor = new ArrayList<>();
        department = new ArrayList<>();
        category = new ArrayList<>();
        time = new ArrayList<>();
        field = new HashMap<>();
        myTags = new ArrayList<>();
        searchEmptyClass = false;
    }

    public boolean addTag(String name) {
        if (field.get(name) == null) {
            Log.e(TAG, "invalid tag name!!!");
            Toast.makeText(app, "유효하지 않은 테그입니다", Toast.LENGTH_SHORT).show();
            return false;
        }
        String val = field.get(name);
        switch (val) {
            case "classification":
                classification.add(name);
                myTags.add(0, new Tag(name, "classification"));
                break;
            case "credit":
                credit.add(name);
                myTags.add(0, new Tag(name, "credit"));
                break;
            case "academic_year":
                academic_year.add(name);
                myTags.add(0, new Tag(name, "academic_year"));
                break;
            case "instructor":
                instructor.add(name);
                myTags.add(0, new Tag(name, "instructor"));
                break;
            case "department":
                department.add(name);
                myTags.add(0, new Tag(name, "department"));
                break;
            case "category":
                category.add(name);
                myTags.add(0, new Tag(name, "category"));
                break;
            /*case "time":
                time.add(name);
                myTags.add(0, new Tag(name, "time"));
                break;*/
        }
        Log.d(TAG, "a tag is successfully added!!!");
        notifyTagChanged(true);
        return true;
    }

    public void removeTag(int position) {
        String name = myTags.get(position).getName();
        String val = field.get(name);
        switch (val) {
            case "classification":
                classification.remove(name);
                break;
            case "credit":
                credit.remove(name);
                break;
            case "academic_year":
                academic_year.remove(name);
                break;
            case "instructor":
                instructor.remove(name);
                break;
            case "department":
                department.remove(name);
                break;
            case "category":
                category.remove(name);
                break;
            /*case "time":
                time.remove(name);
                break;*/
        }
        myTags.remove(position);
        notifyTagChanged(true);
    }

    public List<String> getTags() {
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

                for (String tag : tagList.getClassification()) {
                    field.put(tag, "classification");
                    tags.add(tag);
                }
                for (String tag : tagList.getCredit()) {
                    field.put(tag, "credit");
                    tags.add(tag);
                }
                for (String tag : tagList.getAcademic_year()) {
                    field.put(tag, "academic_year");
                    tags.add(tag);
                }
                for (String tag : tagList.getInstructor()) {
                    field.put(tag, "instructor");
                    tags.add(tag);
                }
                for (String tag : tagList.getDepartment()) {
                    field.put(tag, "department");
                    tags.add(tag);
                }
                for (String tag : tagList.getCategory()) {
                    field.put(tag, "category");
                    tags.add(tag);
                }
                notifyTagChanged(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "update new tags failed...");
            }
        });
    }

    public String getField(String tag) {
        return field.get(tag);
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

    private void notifyTagChanged(boolean anim) {
        if (listener == null) return;
        listener.notifyTagChanged(anim);
    }
}
