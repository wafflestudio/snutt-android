package com.wafflestudio.snutt.manager;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.SNUTTApplication;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;

import java.util.ArrayList;
import java.util.Collection;
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

    private List<Table> tables;
    private Map<String, Table> tableMap;
    private SNUTTApplication app;

    private static TableManager singleton;

    /**
     * TableManager 싱글톤
     */

    private TableManager(SNUTTApplication app) {
        this.app = app;
        getDefaultTable();
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

    public List<Table> getTableList(final Callback callback) {
        tables = new ArrayList<>();
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getTableList(token, new Callback<List<Table>>() {
            @Override
            public void success(List<Table> tables, Response response) {
                Log.d(TAG, "get table list request success!");
                for (Table table : tables) {
                    addTable(table);
                }
                if (callback != null) callback.success(tables, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "get table list request failed..!");
                if (callback != null) callback.failure(error);
            }
        });
        return tables;
    }

    public void setTableList(List<Table> tables) {
        this.tables = tables;
    }

    public Table getTableById(String id) {
        return tableMap.get(id);
    }

    public Table getLastTable() {
        return (Table) tables.get(0);
    }

    public void addTable(Table table) {
        tables.add(table);
        tableMap.put(table.getId(), table);
        Collections.sort(tables);
    }

    public void updateTables(Table table) {
        // TODO : (SeongWon) server에 update 요청 날리기

    }

    private void getDefaultTable() {

        Lecture sample = new Lecture();
        sample.setClassification("교양");
        sample.setDepartment("건설환경공학부");
        sample.setAcademic_year("2학년");
        sample.setCourse_number("035.001");
        sample.setCourse_title("컴퓨터의 개념 및 실습");
        sample.setLecture_number("001");
        sample.setLocation("301-1/301-2");
        sample.setCredit(3);
        sample.setClass_time("월(6-2)/수(6-2)");
        sample.setInstructor("몰라아직 ㅜㅜ");
        sample.setQuota(60);
        sample.setEnrollment(0);
        sample.setRemark("건설환경공학부만 수강가능");
        sample.setCategory("foundation_computer");
        sample.setColorIndex(1);

        List<Lecture> sampleList = new ArrayList<>();
        sampleList.add(sample);

        tables = new ArrayList<>();
        tables.add(new Table("0",2016,1,"이번학기 시간표",new ArrayList<Lecture>()));
        tables.add(new Table("1",2015,3,"후보 1",new ArrayList<Lecture>()));
        tables.add(new Table("2",2015,3,"후보 2",new ArrayList<Lecture>()));
        tables.add(new Table("3",2015,1,"최종안",new ArrayList<Lecture>()));
        tables.add(new Table("4",2015,1,"제 1안",new ArrayList<Lecture>()));
        tables.add(new Table("5",2014,2,"후.. 개발하기힘들다",new ArrayList<Lecture>()));

        tableMap = new HashMap<>();
        for(int i=0;i<tables.size();i++) {
            tableMap.put( String.valueOf(i) , tables.get(i));
        }
    }
}
