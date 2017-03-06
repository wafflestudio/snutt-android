package com.wafflestudio.snutt_staging.manager;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 2. 7..
 */
public class LectureManager {

    private static final String TAG = "LECTURE_MANAGER" ;

    private SNUTTApplication app;
    private List<Lecture> lectures;
    private Lecture selectedLecture;
    private Random random = new Random();
    private int colorIndex = -1;

    // 사용자 정의 시간표
    private int customWday = -1;
    private float customDuration = -1;
    private float customStartTime = -1;
    private static LectureManager singleton;

    /**
     * LectureManager 싱글톤
     */

    private LectureManager(SNUTTApplication app) {
        this.app = app;
        this.lectures = new ArrayList<>();
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

    public void setLectures(List<Lecture> lecture_list) {
        lectures.clear();
        for (Lecture lecture : lecture_list) {
            lectures.add(lecture);
        }
        notifyLectureChanged();
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
                        Log.e(TAG, "post lecture request failed ...");
                        if (callback != null) callback.failure(error);
                    }
                });
                return;
            }
        }
        Log.w(TAG, "lecture is not exist!!");
    }

    // 배경색, 글자색 업데이트
    public void updateLecture(final Lecture lec, final int bgColor, final int fgColor) {
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
    }

    // 배경색, 글자색 업데이트
    public void setNextColor(final Lecture lec) {
        Log.d(TAG, "setNextColor method called!!");
        colorIndex = (colorIndex + 1) % 7;
        if (colorIndex == 0) colorIndex++;
        Lecture target = new Lecture();
        target.setColorIndex(colorIndex);
        target.setBgColor(SNUTTUtils.getBgColorByIndex(colorIndex));
        target.setFgColor(SNUTTUtils.getFgColorByIndex(colorIndex));

        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String id = PrefManager.getInstance().getLastViewTableId();
        String lecture_id = lec.getId();
        app.getRestService().putLecture(token, id, lecture_id, target, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "put lecture request success..");
                lec.setColorIndex(colorIndex);
                lec.setBgColor(SNUTTUtils.getBgColorByIndex(colorIndex));
                lec.setFgColor(SNUTTUtils.getFgColorByIndex(colorIndex));
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

}
