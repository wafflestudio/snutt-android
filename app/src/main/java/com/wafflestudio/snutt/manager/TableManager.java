package com.wafflestudio.snutt.manager;

import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class TableManager {

    private List<Table> tables;
    private Map<String, Table> tableMap;

    private static TableManager singleton;

    /**
     * TableManager 싱글톤
     */

    private TableManager() {
        getDefaultTable();
    }

    public static TableManager getInstance() {
        if(singleton == null) {
            singleton = new TableManager();
        }
        return singleton;
    }

    public List<Table> getTableList() {
        return tables;
    }

    public void setTableList(List<Table> tables) {
        this.tables = tables;
    }

    public Table getTableById(String id) {
        return tableMap.get(id);
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
        tables.add(new Table("0","2016","1","모니터 ㅜㅜ",sampleList));
        tables.add(new Table("1","2015","2","후보 1",new ArrayList<Lecture>()));
        tables.add(new Table("2","2015","2","후보 2",new ArrayList<Lecture>()));
        tables.add(new Table("3","2015","1","최종안",new ArrayList<Lecture>()));
        tables.add(new Table("4","2015","1","제 1안",new ArrayList<Lecture>()));
        tables.add(new Table("5","2014","S","후.. 개발하기힘들다",new ArrayList<Lecture>()));

        tableMap = new HashMap<>();
        for(int i=0;i<tables.size();i++) {
            tableMap.put( String.valueOf(i) , tables.get(i));
        }
    }
}
