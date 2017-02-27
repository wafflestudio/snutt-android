package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.adapter.ExpandableTableListAdapter;
import com.wafflestudio.snutt.manager.TableManager;
import com.wafflestudio.snutt.model.Table;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 17..
 */
public class TableListActivity extends SNUTTBaseActivity {

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<Table>> mChildList = null;
    private ArrayList<Table> mChildListContent = null;

    private ExpandableListView mListView;
    private ExpandableTableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_table_list);
        mListView = (ExpandableListView) findViewById(R.id.listView);
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

        TableManager.getInstance().getTableList(new Callback<List<Table>>() {
            @Override
            public void success(List<Table> tables, Response response) {
                mAdapter = getAdapter(tables);
                mListView.setAdapter(mAdapter);
                if (mGroupList.size() > 0) {
                    mListView.expandGroup(0);
                }
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private ExpandableTableListAdapter getAdapter(List<Table> tables) {
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

        return new ExpandableTableListAdapter(this, mGroupList, mChildList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_add) {
            startTableCreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TableManager.getInstance().getTableList(new Callback<List<Table>>() {
            @Override
            public void success(List<Table> tables, Response response) {
                mAdapter = getAdapter(tables);
                mListView.setAdapter(mAdapter);
                if (mGroupList.size() > 0) {
                    mListView.expandGroup(0);
                }
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
