package com.wafflestudio.snutt2.manager;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wafflestudio.snutt2.SNUTTApplication;
import com.wafflestudio.snutt2.model.Color;
import com.wafflestudio.snutt2.model.ColorList;
import com.wafflestudio.snutt2.model.Lecture;
import com.wafflestudio.snutt2.model.Table;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 2. 7..
 */
public class LectureManager {
    private static final String TAG = "LECTURE_MANAGER" ;
    private static final String DEFAULT_NAME[] = {
            "석류",
            "감귤",
            "들국",
            "완두",
            "비취",
            "지중해",
            "하늘",
            "라벤더",
            "자수정",
            "직접 지정하기"
    };
    private static final int DEFAULT_FG[] = {0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xff333333};
    private static final int DEFAULT_BG[] = {0xffe54459, 0xfff58d3d, 0xfffac52d, 0xffa6d930 ,0xff2bc366, 0xff1bd0c9, 0xff1d99e9, 0xff4f48c4, 0xffaf56b3, 0xffe0e0e0};
    private SNUTTApplication app;
    private List<Lecture> lectures;
    private Lecture selectedLecture;
    private Lecture currentLecture;
    private String searchedQuery;

    // Search query
    private List<Lecture> searchedLectures;

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
        void notifyLecturesChanged();
        void notifySearchedLecturesChanged();
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

    public void reset() {
        this.lectures = new ArrayList<>();
        this.searchedLectures = new ArrayList<>();
        this.currentLecture = null;
        this.selectedLecture = null;
        this.searchedQuery = null;
    }

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
        notifyLecturesChanged();
    }

    public void setSearchedLectures(List<Lecture> lecture_list) {
        searchedLectures.clear();
        for (Lecture lecture : lecture_list) {
            searchedLectures.add(lecture);
        }
        notifySearchedLecturesChanged();
    }

    public Lecture getSelectedLecture() {
        return selectedLecture;
    }

    public void setSelectedLecture(Lecture selectedLecture) {
        this.selectedLecture = selectedLecture;
        notifyLecturesChanged();
    }

    public int getSelectedPosition() {
        if (selectedLecture == null) return -1;
        return searchedLectures.indexOf(selectedLecture);
    }

    public Lecture getCurrentLecture() {
        return currentLecture;
    }

    public void setCurrentLecture(Lecture currentLecture) {
        this.currentLecture = currentLecture;
    }

    // this is for searched lecture
    public void addLecture(final Lecture lec, final Callback callback) {
        if (alreadyOwned(lec)) {
            Log.w(TAG, "lecture is duplicated!! ");
            return ;
        }
        if (alreadyExistClassTime(lec)) {
            Log.d(TAG, "lecture is duplicated!! ");
            Toast.makeText(app, "시간표의 시간과 겹칩니다", Toast.LENGTH_SHORT).show();
            return ;
        }
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        String lecture_id = lec.getId();
        app.getRestService().postLecture(token, id, lecture_id, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "post lecture request success!!");
                PrefManager.getInstance().updateNewTable(table);
                setLectures(table.getLecture_list());
                notifyLecturesChanged();
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "post lecture request failed ...");
                if (callback != null) callback.failure(error);
            }
        });
    }

    // this is for searched lecture
    public void removeLecture(Lecture target, final Callback callback) {
        for (Lecture lecture : lectures) {
            if (isEqualLecture(target, lecture)) {
                removeLecture(lecture.getId(), callback);
                return;
            }
        }
        Log.w(TAG, "lecture is not exist!!");
    }

    // this is for custom lecture
    public void createLecture(final Lecture lecture, final Callback<Table> callback) {
        Log.d(TAG, "create lecture method called!!");
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        app.getRestService().postLecture(token, id, lecture, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                PrefManager.getInstance().updateNewTable(table);
                setLectures(table.getLecture_list());
                notifyLecturesChanged();
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                if (callback != null) callback.failure(error);
                Log.e(TAG, "post lecture request failed..");
            }
        });
    }

    public void removeLecture(String lectureId, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        app.getRestService().deleteLecture(token, id, lectureId, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "remove lecture request success!!");
                PrefManager.getInstance().updateNewTable(table);
                setLectures(table.getLecture_list());
                setCurrentLecture(null);
                notifyLecturesChanged();
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "remove lecture request failed ...");
                if (callback != null) callback.failure(error);
            }
        });
    }

    // reset lecture from my lecture list
    // _id 는 유지된다
    public void resetLecture(final String lectureId, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        app.getRestService().resetLecture(token, id, lectureId, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "reset lecture request success!!");
                PrefManager.getInstance().updateNewTable(table);
                setLectures(table.getLecture_list());
                setCurrentLecture(getLectureById(lectureId));
                notifyLecturesChanged();
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "reset lecture request failed ...");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void updateLecture(final String lectureId, final Lecture target, final Callback<Table> callback) {
        Log.d(TAG, "update lecture method called!!");
        final String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        final String id = PrefManager.getInstance().getLastViewTableId();
        app.getRestService().putLecture(token, id, lectureId, target, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                PrefManager.getInstance().updateNewTable(table);
                setLectures(table.getLecture_list());
                setCurrentLecture(getLectureById(lectureId));
                notifyLecturesChanged();
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

    //주어진 요일, 시각을 포함하고 있는지
    public boolean contains(Lecture lec1, int given_day, float given_time) {
        for (JsonElement element1 : lec1.getClass_time_json()) {
            JsonObject class1 = element1.getAsJsonObject();

            int day1 = class1.get("day").getAsInt();
            float start1 = class1.get("start").getAsFloat();
            float len1 = class1.get("len").getAsFloat();
            float end1 = start1 + len1;
            float start2 = given_time;
            float len2 = 0.5f;
            float end2 = start2 + len2;

            if (day1 != given_day) continue;
            if (!(end1 <= start2 || end2 <= start1)) return true;
        }
        return false;
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
        if (TagManager.getInstance().getSearchEmptyClass()) {
            query.put("time_mask", getClassTimeMask());
        }
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
        if (TagManager.getInstance().getSearchEmptyClass()) {
            query.put("time_mask", getClassTimeMask());
        }
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

    public int[] getClassTimeMask() {
        int[] masks = new int[7];
        for (Lecture lecture : lectures) {
            for (int i = 0; i < lecture.getClass_time_mask().size(); i++) {
                int mask = lecture.getClass_time_mask().get(i).getAsInt();
                masks[i] = masks[i] | mask;
            }
        }
        for (int i = 0; i < 7; i ++) {
            masks[i] = masks[i] ^ (0x3FFFFFFF);
        }
        return masks;
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
        notifyLecturesChanged();
        notifySearchedLecturesChanged();
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
                notifyLecturesChanged();
                if (callback != null) callback.success(colorList, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get color list request failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public String getCreditText() {
        if (getTotalCredit() == -1) return "";
        return getTotalCredit() + "학점";
    }

    private int getTotalCredit() {
        int credit = 0;
        for (Lecture lecture: lectures) {
            credit += lecture.getCredit();
        }

        if (credit < 0) return -1;
        return credit;
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
        if (colors.size() == 0) return DEFAULT_BG[index - 1];
        return colors.get(index - 1).getBg();
    }

    public int getFgColorByIndex(int index) {
        if (colors.size() == 0) return DEFAULT_FG[index - 1];
        return colors.get(index - 1).getFg();
    }

    public String getColorNameByIndex(int index) {
        if (colorNames.size() == 0) return DEFAULT_NAME[index - 1];
        return colorNames.get(index - 1);
    }

    public int getDefaultBgColor() {
        return DEFAULT_BG[DEFAULT_BG.length - 1];
    }

    public int getDefaultFgColor() {
        return DEFAULT_FG[DEFAULT_FG.length - 1];
    }

    public String getDefaultColorName() {
        return DEFAULT_NAME[DEFAULT_NAME.length - 1];
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

    private void notifyLecturesChanged() {
        for (OnLectureChangedListener listener : listeners) {
            listener.notifyLecturesChanged();
        }
    }

    private void notifySearchedLecturesChanged() {
        for (OnLectureChangedListener listener : listeners) {
            listener.notifySearchedLecturesChanged();
        }
    }

}
