package com.wafflestudio.snutt_staging.manager;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.model.Color;
import com.wafflestudio.snutt_staging.model.ColorList;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.Table;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 2. 7..
 */
public class LectureManager {
    private static final String TAG = "LECTURE_MANAGER" ;
    private static final String DEFAULT_NAME = "직접 지정하기";
    private static final int DEFAULT_FG = 0xff333333;
    private static final int DEFAULT_BG = 0xffe0e0e0;

    private SNUTTApplication app;
    private List<Lecture> lectures;
    private Lecture selectedLecture;
    private Random random = new Random();
    private int colorIndex = -1;
    private String searchedQuery;

    // Search query
    private List<Lecture> searchedLectures;

    // 사용자 정의 시간표
    private int customWday = -1;
    private float customDuration = -1;
    private float customStartTime = -1;
    private static LectureManager singleton;

    // color list
    private List<Color> colors;
    private List<String> colorNames;

    /**
     * LectureManager 싱글톤
     */

    private LectureManager(SNUTTApplication app) {
        this.app = app;
        this.lectures = new ArrayList<>();
        this.searchedLectures = new ArrayList<>();
        loadColorData();
    }

    private void loadColorData() {
        Type type1 = new TypeToken<List<Color>>(){}.getType();
        colors = new Gson().fromJson(PrefManager.getInstance().getLectureColors(), type1);

        Type type2 = new TypeToken<List<String>>(){}.getType();
        colorNames = new Gson().fromJson(PrefManager.getInstance().getLectureColorNames(), type2);

        if (colors == null) {
            colors = new ArrayList<Color>();
        }
        if (colorNames == null) {
            colorNames = new ArrayList<String>();
        }
    }

    public static LectureManager getInstance(SNUTTApplication app) {
        if (singleton == null) {
            singleton = new LectureManager(app);
        }
        return singleton;
    }

    public static LectureManager getInstance() {
        if (singleton == null) Log.e(TAG, "This method should not be called at this time!!");
        return singleton;
    }

    public interface OnLectureChangedListener {
        void notifyLectureChanged();
        void notifySearchedLectureChanged();
    }

    private List<OnLectureChangedListener> listeners = new ArrayList<>();

    public void addListener(OnLectureChangedListener listener) {
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i).equals(listener)) {
                Log.w(TAG, "listener reference is duplicated !!");
                return;
            }
        }
        listeners.add(listener);
    }

    public void removeListener(OnLectureChangedListener listener) {
        for (int i=0;i<listeners.size(); i++) {
            OnLectureChangedListener reference = listeners.get(i);
            if (reference == listener) {
                listeners.remove(i);
                break;
            }
        }
    }
    ////////

    public List<Lecture> getLectures() {
        return lectures;
    }

    public List<Lecture> getSearchedLectures() {
        return searchedLectures;
    }

    public void setLectures(List<Lecture> lecture_list) {
        lectures.clear();
        for (Lecture lecture : lecture_list) {
            lectures.add(lecture);
        }
        notifyLectureChanged();
    }

    public void setSearchedLectures(List<Lecture> lecture_list) {
        searchedLectures.clear();
        for (Lecture lecture : lecture_list) {
            searchedLectures.add(lecture);
        }
    }

    public Lecture getSelectedLecture() {
        return selectedLecture;
    }

    public void setSelectedLecture(Lecture selectedLecture) {
        this.selectedLecture = selectedLecture;
        notifyLectureChanged();
    }

    // this is for searched lecture
    public void addLecture(final Lecture lec, final Callback callback) {
        if (alreadyOwned(lec)) {
            Log.w(TAG, "lecture is duplicated!! ");
            return ;
        }
        if (alreadyExistClassTime(lec)) {
            Log.d(TAG, "lecture is duplicated!! ");
            Toast.makeText(app, "강의시간이 겹칩니다", Toast.LENGTH_SHORT).show();
            return ;
        }
        /*final Lecture target = new Lecture(lec);
        int random = getRandomColor();
        target.setColorIndex(getRandomColor());
        target.setBgColor(SNUTTUtils.getBgColorByIndex(random));
        target.setFgColor(SNUTTUtils.getFgColorByIndex(random));*/
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        String lecture_id = lec.getId();
        app.getRestService().postLecture(token, id, lecture_id, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "post lecture request success!!");
                setLectures(table.getLecture_list());
                notifyLectureChanged();
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "post lecture request failed ...");
                if (callback != null) callback.failure(error);
            }
        });
    }

    // this is for custom lecture
    public void createLecture(final Lecture lecture, final Callback<Table> callback) {
        Log.d(TAG, "create lecture method called!!");
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        app.getRestService().postLecture(token, id, lecture, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                setLectures(table.getLecture_list());
                notifyLectureChanged();
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                if (callback != null) callback.failure(error);
                Log.e(TAG, "post lecture request failed..");
            }
        });
    }

    public void removeLecture(Lecture lec, final Callback callback) {
        for (Lecture lecture : lectures) {
            if (isEqualLecture(lecture, lec)) {
                final Lecture target = lecture;
                String token = PrefManager.getInstance().getPrefKeyXAccessToken();
                String id = PrefManager.getInstance().getLastViewTableId();
                String lecture_id = target.getId();
                app.getRestService().deleteLecture(token, id, lecture_id, new Callback<Table>() {
                    @Override
                    public void success(Table table, Response response) {
                        Log.d(TAG, "remove lecture request success!!");
                        setLectures(table.getLecture_list());
                        notifyLectureChanged();
                        if (callback != null) callback.success(table, response);
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "remove lecture request failed ...");
                        if (callback != null) callback.failure(error);
                    }
                });
                return;
            }
        }
        Log.w(TAG, "lecture is not exist!!");
    }

    // reset lecture from my lecture list
    // _id 는 유지된다
    public void resetLecture(Lecture lec, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        String lecture_id = lec.getId();
        app.getRestService().resetLecture(token, id, lecture_id, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "reset lecture request success!!");
                setLectures(table.getLecture_list());
                notifyLectureChanged();
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "reset lecture request failed ...");
                if (callback != null) callback.failure(error);
            }
        });
    }

    // 배경색, 글자색 업데이트
    /*public void updateLecture(final Lecture lec, final int bgColor, final int fgColor) {
        Log.d(TAG, "update Lecture method (color) called!!");
        Lecture target = new Lecture();
        target.setBgColor(bgColor);
        target.setFgColor(fgColor);

        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        String lecture_id = lec.getId();
        app.getRestService().putLecture(token, id, lecture_id, target, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "put lecture request success.");
                lec.setBgColor(bgColor);
                lec.setFgColor(fgColor);
                notifyLectureChanged();
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "put lecture request failed..");
            }
        });
    }*/

    // 배경색, 글자색 업데이트
    public void setNextColor(final Lecture lec) {
        Log.d(TAG, "setNextColor method called!!");
        colorIndex = (colorIndex + 1) % 7;
        if (colorIndex == 0) colorIndex++;
        Lecture target = new Lecture();
        target.setColorIndex(colorIndex);
        target.setBgColor(LectureManager.getInstance().getBgColorByIndex(colorIndex));
        target.setFgColor(LectureManager.getInstance().getFgColorByIndex(colorIndex));

        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        String lecture_id = lec.getId();
        app.getRestService().putLecture(token, id, lecture_id, target, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "put lecture request success..");
                lec.setColorIndex(colorIndex);
                lec.setBgColor(LectureManager.getInstance().getBgColorByIndex(colorIndex));
                lec.setFgColor(LectureManager.getInstance().getFgColorByIndex(colorIndex));
                notifyLectureChanged();
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "put lecture request failed..");
            }
        });
    }

    public void updateLecture(final Lecture lecture, final Lecture target, final Callback<Table> callback) {
        Log.d(TAG, "update lecture method called!!");
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        String lecture_id = lecture.getId();
        app.getRestService().putLecture(token, id, lecture_id, target, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                setLectures(table.getLecture_list());
                notifyLectureChanged();
                if (callback != null) callback.success(table, response);
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) callback.failure(error);
                Log.e(TAG, "put lecture request failed..");
            }
        });
    }

    public void getCoursebookUrl(String courseNumber, String lectureNumber, final Callback callback) {
        int year = PrefManager.getInstance().getCurrentYear();
        int semester = PrefManager.getInstance().getCurrentSemester();
        Map query = new HashMap();
        query.put("year", year);
        query.put("semester", semester);
        query.put("course_number", courseNumber);
        query.put("lecture_number", lectureNumber);
        app.getRestService().getCoursebooksOfficial(query, new Callback<Map>() {
            @Override
            public void success(Map map, Response response) {
                Log.d(TAG, "get coursebook official request success!");
                if (callback != null) callback.success(map, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "get coursebook official request failed..");
                if (callback != null) callback.failure(error);
            }
        });
    }

    //내 강의에 이미 들어있는지 -> course_number, lecture_number 비교
    public boolean alreadyOwned(Lecture lec){
        for (Lecture lecture : lectures){
            if (isEqualLecture(lecture, lec)) return true;
        }
        return false;
    }

    //이미 내 강의에 존재하는 시간인지
    public boolean alreadyExistClassTime(Lecture lec) {
        for (Lecture lecture : lectures){
            if (isDuplicatedClassTime(lecture, lec)) return true;
        }
        return false;
    }

    //이미 내 강의에 존재하는 시간인지 (day, time, duration 으로 비교), custom lecture 생성시
    public boolean alreadyExistClassTime(float duration) {
        for (Lecture lecture : lectures) {
            if (isDuplicatedClassTime(lecture, customWday, customStartTime, duration)) return true;
        }
        return false;
    }

    //주어진 요일, 시각을 포함하고 있는지
    public boolean contains(Lecture lec1, int given_day, float given_time) {
        for (JsonElement element1 : lec1.getClass_time_json()) {
            JsonObject class1 = element1.getAsJsonObject();

            int day1 = class1.get("day").getAsInt();
            float start1 = class1.get("start").getAsFloat();
            float len1 = class1.get("len").getAsFloat();
            float end1 = start1 + len1;

            if (day1 != given_day) continue;
            if (!(end1 <= given_time || given_time <= end1)) return true;
        }
        return false;
    }

    //사용자 정의 시간표 초기화
    public void resetCustomLecture() {
        customStartTime = customDuration = customWday = -1;
        notifyLectureChanged();
    }

    public void setCustomValue(int wday, float startTime, float duration) {
        Log.d(TAG, "setting custom lecture values..");
        customWday = wday;
        customStartTime = startTime;
        customDuration = duration;
        notifyLectureChanged();
    }

    public int getCustomWday() {
        return customWday;
    }

    public float getCustomStartTime() {
        return customStartTime;
    }

    public float getCustomDuration() {
        return customDuration;
    }

    public void setCustomDuration(float duration) {
        customDuration = duration;
        notifyLectureChanged();
    }

    //사용자 정의 시간표가 있는지
    public boolean existCustomLecture() {
        return (customWday != -1 && customStartTime != -1 && customDuration > 0);
    }

    public void postSearchQuery(String text, final Callback callback) {
        Map query = new HashMap();
        query.put("year", PrefManager.getInstance().getCurrentYear());
        query.put("semester", PrefManager.getInstance().getCurrentSemester());
        query.put("title", text);
        query.put("classification", TagManager.getInstance().getClassification());
        query.put("credit", TagManager.getInstance().getCredit());
        query.put("academic_year", TagManager.getInstance().getAcademic_year());
        query.put("instructor", TagManager.getInstance().getInstructor());
        query.put("department", TagManager.getInstance().getDepartment());
        query.put("category", TagManager.getInstance().getCategory());
        searchedQuery = text;

        app.getRestService().postSearchQuery(query, new Callback<List<Lecture>>() {
            @Override
            public void success(List<Lecture> lectures, Response response) {
                Log.d(TAG, "post search query success!!");
                setSearchedLectures(lectures);
                if (callback != null) callback.success(lectures, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "post search query failed!!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void addProgressBar() {
        searchedLectures.add(null);
    }

    public void removeProgressBar() {
        searchedLectures.remove(searchedLectures.size() - 1);
    }

    public void loadData(int offset, final Callback callback) {
        Map query = new HashMap();
        query.put("year", PrefManager.getInstance().getCurrentYear());
        query.put("semester", PrefManager.getInstance().getCurrentSemester());
        query.put("title", searchedQuery);
        query.put("classification", TagManager.getInstance().getClassification());
        query.put("credit", TagManager.getInstance().getCredit());
        query.put("academic_year", TagManager.getInstance().getAcademic_year());
        query.put("instructor", TagManager.getInstance().getInstructor());
        query.put("department", TagManager.getInstance().getDepartment());
        query.put("category", TagManager.getInstance().getCategory());
        query.put("offset", offset);
        query.put("limit", 20);
        app.getRestService().postSearchQuery(query, new Callback<List<Lecture>>() {
            @Override
            public void success(List<Lecture> lectureList, Response response) {
                Log.d(TAG, "post search query success!!");
                removeProgressBar();
                for (Lecture lecture : lectureList) {
                    searchedLectures.add(lecture);
                }
                if (callback != null) callback.success(lectureList, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "post search query failed!!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public Lecture getLectureById(String id) {
        for (Lecture lecture : lectures) {
            if (lecture.getId().equals(id)) return lecture;
        }
        return null;
    }

    public void clearSearchedLectures() {
        searchedLectures.clear();
        selectedLecture = null;
        notifyLectureChanged();
        notifySelectedLectureChanged();
    }

    // for color list

    public void fetchColorList(String name, final Callback callback) {
        app.getRestService().getColorList(name, new Callback<ColorList>() {
            @Override
            public void success(ColorList colorList, Response response) {
                Log.d(TAG, "get color list request success");
                setColors(colorList.getColors());
                setColorNames(colorList.getNames());
                String colorListJson = new Gson().toJson(colors);
                String colorNameListJson = new Gson().toJson(colorNames);
                PrefManager.getInstance().setLectureColors(colorListJson);
                PrefManager.getInstance().setLectureColorNames(colorNameListJson);
                if (callback != null) callback.success(colorList, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get color list request failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    private void setColors(List<Color> colors) {
        this.colors = colors;
    }

    private void setColorNames(List<String> colorNames) {
        this.colorNames = colorNames;
    }

    public List<Color> getColorList() {
        return colors;
    }

    public List<String> getColorNameList() {
        return colorNames;
    }

    public int getBgColorByIndex(int index) {
        return colors.get(index - 1).getBg();
    }

    public int getFgColorByIndex(int index) {
        return colors.get(index - 1).getFg();
    }

    public int getDefaultBgColor() {
        return DEFAULT_BG;
    }

    public int getDefaultFgColor() {
        return DEFAULT_FG;
    }

    public String getColorNameByIndex(int index) {
        return colorNames.get(index - 1);
    }

    public String getDefaultColorName() {
        return DEFAULT_NAME;
    }

    private boolean isEqualLecture(Lecture lec1,Lecture lec2) {
        if (lec1.isCustom()) { // custom lecture 면 id 로 비교
            if (lec1.getId().equals(lec2.getId())) return true;
            return false;
        }
        if (lec1.getCourse_number().equals(lec2.getCourse_number()) && // custom lecture 가 아니면 강좌번호, 분반번호로 비교
                lec1.getLecture_number().equals(lec2.getLecture_number())) return true;
        return false;
    }

    private boolean isDuplicatedClassTime(Lecture lec1,Lecture lec2) {
        for (JsonElement element1 : lec1.getClass_time_json()) {
            JsonObject class1= element1.getAsJsonObject();

            int day1 = class1.get("day").getAsInt();
            float start1 = class1.get("start").getAsFloat();
            float len1 = class1.get("len").getAsFloat();
            float end1 = start1 + len1;
            for (JsonElement element2 : lec2.getClass_time_json()) {
                JsonObject class2 = element2.getAsJsonObject();

                int day2 = class2.get("day").getAsInt();
                float start2 = class2.get("start").getAsFloat();
                float len2 = class2.get("len").getAsFloat();
                float end2 = start2 + len2;

                if (day1 != day2) continue;
                if (!(end1 <= start2 || end2 <= start1)) return true;
            }
        }
        return false;
    }

    private boolean isDuplicatedClassTime(Lecture lec1,int day2,float start2, float len2) {
        for (JsonElement element1 : lec1.getClass_time_json()) {
            JsonObject class1= element1.getAsJsonObject();
            int day1 = class1.get("day").getAsInt();
            float start1 = class1.get("start").getAsFloat();
            float len1 = class1.get("len").getAsFloat();
            float end1 = start1 + len1;
            float end2 = start2 + len2;

            if (day1 != day2) continue;
            if (!(end1 <= start2 || end2 <= start1)) return true;
        }
        return false;
    }

    private int getRandomColor(){
        while (true){
            int colorIndex = random.nextInt(6) + 1;
            if (colorIndex != this.colorIndex){
                this.colorIndex = colorIndex;
                return colorIndex;
            }
        }
    }

    private void notifyLectureChanged() {
        for (OnLectureChangedListener listener : listeners) {
            listener.notifyLectureChanged();
        }
    }

    private void notifySelectedLectureChanged() {
        for (OnLectureChangedListener listener : listeners) {
            listener.notifySearchedLectureChanged();
        }
    }

}
