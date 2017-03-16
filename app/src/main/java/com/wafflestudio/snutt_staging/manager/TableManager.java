package com.wafflestudio.snutt_staging.manager;

import android.util.Log;

import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.model.Coursebook;
import com.wafflestudio.snutt_staging.model.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class TableManager {

    private static final String TAG = "TABLE_MANAGER" ;

    private Table current;
    private List<Table> tables;
    private Map<String, Table> tableMap;
    private SNUTTApplication app;

    private static TableManager singleton;

    /**
     * TableManager 싱글톤
     */

    private TableManager(SNUTTApplication app) {
        this.app = app;
        this.tables = new ArrayList<>();
        this.tableMap = new HashMap<>();
    }

    public static TableManager getInstance(SNUTTApplication app) {
        if(singleton == null) {
            singleton = new TableManager(app);
        }
        return singleton;
    }

    public static TableManager getInstance() {
        if (singleton == null) Log.e(TAG, "This method should not be called at this time!!");
        return singleton;
    }

    public void getTableList(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getTableList(token, new Callback<List<Table>>() {
            @Override
            public void success(List<Table> table_list, Response response) {
                Log.d(TAG, "get table list request success!");
                tables.clear();
                tableMap.clear();
                for (Table table : table_list) addTable(table);
                if (callback != null) callback.success(tables, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "get table list request failed..!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void postTable(int year, int semester, String title, final Callback<List<Table>> callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("year", year);
        query.put("semester", semester);
        query.put("title", title);
        app.getRestService().postTable(token, query, new Callback<List<Table>>() {
            @Override
            public void success(List<Table> table_list, Response response) {
                Log.d(TAG, "post new table request success!!");
                tables.clear();
                tableMap.clear();
                for (Table table : table_list) addTable(table);
                if (callback != null) callback.success(tables, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "post new table request failed..!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    /*public void setTableList(List<Table> tables) {
        this.tables = tables;
    }*/

    public void getTableById(String id, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getTableById(token, id, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "get table by id success");
                LectureManager.getInstance().setLectures(table.getLecture_list());
                PrefManager.getInstance().updateNewTable(table);
                TagManager.getInstance().updateNewTag(table.getYear(), table.getSemester());
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get table by id is failed!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void getDefaultTable(final Callback<Table> callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getRecentTable(token, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "get recent table request success");
                LectureManager.getInstance().setLectures(table.getLecture_list());
                PrefManager.getInstance().updateNewTable(table);
                TagManager.getInstance().updateNewTag(table.getYear(), table.getSemester());
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get recent table request failed!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void getCoursebook(final Callback<List<Coursebook>> callback) {
        //String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getCoursebook(new Callback<List<Coursebook>>() {
            @Override
            public void success(List<Coursebook> coursebooks, Response response) {
                Log.d(TAG, "get coursebook request success.");
                if (callback != null) callback.success(coursebooks, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get coursebook request failed.");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void addTable(Table table) {
        tables.add(table);
        tableMap.put(table.getId(), table);
        Collections.sort(tables);
    }

    public void updateTables(Table table) {
        // TODO : (SeongWon) server에 update 요청 날리기
    }
}
