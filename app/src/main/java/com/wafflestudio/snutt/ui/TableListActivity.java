package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.adapter.ExpandableTableListAdapter;
import com.wafflestudio.snutt.adapter.TableListAdapter;
import com.wafflestudio.snutt.manager.TableManager;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 1. 17..
 */
public class TableListActivity extends SNUTTBaseActivity {

    private List<Table> tables;
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<Table>> mChildList = null;
    private ArrayList<Table> mChildListContent = null;

    private ExpandableListView mListView;
    private ExpandableTableListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        tables = TableManager.getInstance().getTableList();
        mListView = (ExpandableListView) findViewById(R.id.listView);
        mAdapter = getAdapter();
        mListView.setAdapter(mAdapter);

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), "c click = " + childPosition,
                        Toast.LENGTH_SHORT).show();

                String tableId = mChildList.get(groupPosition).get(childPosition).getId();
                startTableView(tableId);
                finish();
                return false;
            }
        });


    }

    private ExpandableTableListAdapter getAdapter() {
        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<Table>>();

        if (tables.size()>0) {
            mGroupList.add(tables.get(0).getFullSemester());
            mChildListContent = new ArrayList<Table>();
            mChildListContent.add(tables.get(0));
        }

        for(int i=1;i<tables.size();i++) {
            Table table = tables.get(i);
            if( tables.get(i-1).getFullSemester().equals( table.getFullSemester() )) {
                mChildListContent.add(table);
            } else {
                mChildList.add(mChildListContent);

                mGroupList.add(table.getFullSemester());
                mChildListContent = new ArrayList<>();
                mChildListContent.add(table);
            }
        }
        if (tables.size()>0) {
            mChildList.add(mChildListContent);
        }

        return (ExpandableTableListAdapter) new ExpandableTableListAdapter(this, mGroupList, mChildList);
    }
}
