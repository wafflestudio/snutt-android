package com.wafflestudio.snutt.manager;

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
        tables = new ArrayList<>();
        tables.add(new Table("0","2015-2","후보 1",null));
        tables.add(new Table("1","2015-2","후보 2",null));
        tables.add(new Table("2","2015-1","최종안",null));
        tables.add(new Table("3","2015-1","제 1안",null));

        tableMap = new HashMap<>();
        for(int i=0;i<tables.size();i++) {
            tableMap.put( String.valueOf(i) , tables.get(i));
        }
    }
}
