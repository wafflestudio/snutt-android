package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.adapter.TableListAdapter;
import com.wafflestudio.snutt.manager.TableManager;
import com.wafflestudio.snutt.model.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 1. 17..
 */
public class TableListActivity extends SNUTTBaseActivity {

    private List<Table> tables;
    private TableListAdapter adapter;
    private RecyclerView tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);
        tables = getTableList();
        adapter = new TableListAdapter(tables);
        tableView = (RecyclerView) findViewById(R.id.table_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApp());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tableView.setLayoutManager(layoutManager);

        tableView.setAdapter(adapter);


    }

    private List<Table> getTableList() {
        List<Table> tableList = new ArrayList<>(TableManager.getInstance().getTableList());
        tableList.add(new Table(TableListAdapter.TABLE_SECTION_STRING,"2015-1"));
        tableList.add(new Table(TableListAdapter.TABLE_SECTION_STRING,"2015-2"));
        return tableList;
    }

}
