package com.wafflestudio.snutt2.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.SNUTTBaseActivity;
import com.wafflestudio.snutt2.adapter.ExpandableTableListAdapter;
import com.wafflestudio.snutt2.manager.PrefManager;
import com.wafflestudio.snutt2.manager.TableManager;
import com.wafflestudio.snutt2.model.Coursebook;
import com.wafflestudio.snutt2.model.Table;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 17..
 */
public class TableListActivity extends SNUTTBaseActivity {
    private static final String TAG = "TABLE_LIST_ACTIVITY";
    private static final String DIALOG_EDIT = "시간표 이름 변경";
    private static final String DIALOG_DELETE = "시간표 삭제";

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<Table>> mChildList = null;
    private LinearLayout placeholder;

    private List<Coursebook> coursebookList;

    private ExpandableListView mListView;
    private ExpandableTableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_table_list);
        setTitle("시간표 목록");

        placeholder = (LinearLayout) findViewById(R.id.placeholder);

        mListView = (ExpandableListView) findViewById(R.id.listView);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (mChildList.get(groupPosition).isEmpty()) {
                    Coursebook coursebook = coursebookList.get(groupPosition);
                    TableManager.getInstance().postTable(coursebook.getYear(), coursebook.getSemester(), "내 시간표", new Callback<List<Table>>() {
                        @Override
                        public void success(List<Table> tables, Response response) {
                            mAdapter = getAdapter(tables);
                            mListView.setAdapter(mAdapter);
                            for (int i = 0; i < mGroupList.size(); i++) {
                                mListView.expandGroup(i);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                    return true;
                }
                String tableId = mChildList.get(groupPosition).get(childPosition).getId();
                startTableView(tableId);
                finish();
                return true;
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    Log.d(TAG, groupPosition + " " + childPosition);
                    final Table table = mChildList.get(groupPosition).get(childPosition);

                    final CharSequence[] items = {DIALOG_EDIT, DIALOG_DELETE};
                    AlertDialog.Builder builder = new AlertDialog.Builder(TableListActivity.this);
                    builder.setTitle(table.getTitle())
                            .setItems(items, new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int index){
                                    if (items[index].equals(DIALOG_EDIT)) showEditDialog(table);
                                    else performDelete(table);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.

                    // Return true as we are handling the event.
                    return true;
                }

                return false;
            }
        });

        getTableList();
    }

    private void getTableList() {
        TableManager.getInstance().getCoursebook(new Callback<List<Coursebook>>() {
            @Override
            public void success(List<Coursebook> coursebooks, Response response) {
                coursebookList = coursebooks;
                TableManager.getInstance().getTableList(new Callback<List<Table>>() {
                    @Override
                    public void success(List<Table> tables, Response response) {
                        mAdapter = getAdapter(tables);
                        mListView.setAdapter(mAdapter);
                        for (int i = 0;i < mGroupList.size();i ++) {
                            mListView.expandGroup(i);
                        }
                        placeholder.setVisibility(TableManager.getInstance().hasTimetables() ? View.GONE : View.VISIBLE);
                    }
                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private String getFullSemester(int year, int semester) {
        String yearString;
        String semesterString;

        yearString = String.valueOf(year);
        switch (semester) {
            case 1:
                semesterString = "1";
                break;
            case 2:
                semesterString = "S";
                break;
            case 3:
                semesterString = "2";
                break;
            case 4:
                semesterString = "W";
                break;
            default:
                semesterString = "";
                Log.e(TAG, "semester is out of range!!");
                break;
        }
        return yearString + '-' + semesterString;
    }

    private void showEditDialog(final Table table) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_change_title, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("시간표 이름 변경");
        alert.setView(layout);
        alert.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing in here. because we override this button listener later
            }
        }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = ((EditText) layout.findViewById(R.id.title)).getText().toString();
                if (!Strings.isNullOrEmpty(title)) {
                    TableManager.getInstance().putTable(table.getId(), title, new Callback<List<Table>>() {
                        @Override
                        public void success(List<Table> tables, Response response) {
                            mAdapter = getAdapter(tables);
                            mListView.setAdapter(mAdapter);
                            for (int i = 0;i < mGroupList.size();i ++) {
                                mListView.expandGroup(i);
                            }
                            placeholder.setVisibility(TableManager.getInstance().hasTimetables() ? View.GONE : View.VISIBLE);
                        }
                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApp(), "시간표 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performDelete(Table table) {
        if (PrefManager.getInstance().getLastViewTableId().equals(table.getId())) {
            Toast.makeText(getApp(), "현재 보고있는 테이블은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return ;
        }
        TableManager.getInstance().deleteTable(table.getId(), new Callback<List<Table>>() {
            @Override
            public void success(List<Table> tables, Response response) {
                mAdapter = getAdapter(tables);
                mListView.setAdapter(mAdapter);
                for (int i = 0;i < mGroupList.size();i ++) {
                    mListView.expandGroup(i);
                }
                placeholder.setVisibility(TableManager.getInstance().hasTimetables() ? View.GONE : View.VISIBLE);
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private ExpandableTableListAdapter getAdapter(List<Table> tables) {
        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<Table>>();
        for (Coursebook coursebook : coursebookList) {
            mGroupList.add(getFullSemester(coursebook.getYear(), coursebook.getSemester()));
            mChildList.add(new ArrayList<Table>());
        }

        int index = 0;
        for (Table table : tables) {
            while (index < mGroupList.size() && !mGroupList.get(index).equals(table.getFullSemester())) index ++;
            if (index >= mGroupList.size()) break;
            mChildList.get(index).add(table);
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
        getTableList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
