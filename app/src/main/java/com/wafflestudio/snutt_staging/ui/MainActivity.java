package com.wafflestudio.snutt_staging.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.adapter.SectionsPagerAdapter;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.manager.NotiManager;
import com.wafflestudio.snutt_staging.manager.PrefManager;
import com.wafflestudio.snutt_staging.manager.TableManager;
import com.wafflestudio.snutt_staging.model.Table;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class MainActivity extends SNUTTBaseActivity {

    private static final String TAG = "MainActivity" ;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 2017;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    protected TabLayout tabLayout;
    private ViewPager mViewPager;
    private ImageView notiCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTabLayoutView(tabLayout);
        setUpAppBarLayout(appBarLayout);

        // 1. token 의 유무 검사
        if (PrefManager.getInstance().getPrefKeyXAccessToken() == null) {
            // 로그인 창으로 이동
            //startWelcome();
            startIntro();
            finish();
            return;
        }

        // 2. colorList 받아오기
        LectureManager.getInstance().fetchColorList("vivid_ios", null);

        // 3. 앱 내부에 저장된 시간표 뛰어주기
        // TODO : 저장된 정보를 불러와 보여주기, 없으면 empty상태로 띄어준다.
        String json = PrefManager.getInstance().getCurrentTable();
        if (json != null) {
            Table table = new Gson().fromJson(json, Table.class);
            getSupportActionBar().setTitle(table.getTitle());
            LectureManager.getInstance().setLectures(table.getLecture_list());
        }

        // 4. 서버에서 시간표 정보 얻어오기
        // TODO : 서버에서 마지막에 본 시간표 정보 받아오기
        String id = PrefManager.getInstance().getLastViewTableId();
        if (id != null) {
            TableManager.getInstance().getTableById(id, new Callback<Table>() {
                @Override
                public void success(Table table, Response response) {
                    getSupportActionBar().setTitle(table.getTitle());
                }
                @Override
                public void failure(RetrofitError error) {
                    // invalid token -> 로그인 화면으로
                    // invalid id -> 없어진 테이블
                }
            });
        } else {
            // 처음 로그인한 경우 -> 서버에서 default값을 요청
            TableManager.getInstance().getDefaultTable(new Callback<Table>() {
                @Override
                public void success(Table table, Response response) {
                    // default 가 존재하는 경우
                    getSupportActionBar().setTitle(table.getTitle());
                }

                @Override
                public void failure(RetrofitError error) {
                    // default가 존재하지 않거나, network error 인 경우
                    getSupportActionBar().setTitle("empty table");
                }
            });
        }

        // noti check
        NotiManager.getInstance().getNotificationCount(new Callback<Map<String,Integer>>() {
            @Override
            public void success(Map<String,Integer> map, Response response) {
                int count = map.get("count");
                Log.d(TAG, "notification count : " + count);
                if (notiCircle != null) {
                    notiCircle.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                }
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 0) {
                    showEditDialog(PrefManager.getInstance().getLastViewTableId());
                }
            }
        });
        checkForUpdates();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // TableListActivity에서 들어 오는 경우 -> 선택한 table 을 보여줘야 함
        String id = null;
        if (intent.getExtras() != null) { // intent의 강의 id 받아오기
            id = intent.getExtras().getString(INTENT_KEY_TABLE_ID);
        }
        if (id == null) {
            Log.e(TAG, "intent has no table id!!");
            return ;
        }
        // 서버에서 받아와서 다시 그리기
        TableManager.getInstance().getTableById(id, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                getSupportActionBar().setTitle(table.getTitle());
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
       //     return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
        unregisterManagers();
    }

    @Override
    public void onResume(){
        super.onResume();
        checkGoogleServiceVersion();
        updateTableTitle();
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    private void updateTableTitle() {
        String id = PrefManager.getInstance().getLastViewTableId();
        if (id == null) return;
        String title = TableManager.getInstance().getTableTitleById(id);
        if (title == null) return;
        getSupportActionBar().setTitle(title);
    }

    private boolean checkGoogleServiceVersion() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode == SUCCESS) {
            Log.d(TAG, "google play service is available.");
            return true;
        }
        if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
            Log.d(TAG, "google play service is user resolvable error.");
            GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    checkGoogleServiceVersion();
                }
            }).show();
        } else {
            Log.e(TAG, "google play service is not supported in this device.");
        }
        return false;
    }

    private void setTabLayoutView(TabLayout tabLayout) {
        for (int i=0;i<tabLayout.getTabCount();i++) {
            FrameLayout layout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView textView = (TextView) layout.findViewById(R.id.tab_title);
            textView.setText(getPageTitle(i));
            ImageView imageView = (ImageView) layout.findViewById(R.id.noti);
            imageView.setVisibility(View.GONE);
            tabLayout.getTabAt(i).setCustomView(layout);
            if (getPageTitle(i).equals("알림")) {
                notiCircle = imageView;
            }
        }
        // for initial state
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
    }

    private void showEditDialog(final String id) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_change_title, null);
        EditText titleText = (EditText) layout.findViewById(R.id.title);
        titleText.setHint(getSupportActionBar().getTitle());
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
                    TableManager.getInstance().putTable(id, title, new Callback<List<Table>>() {
                        @Override
                        public void success(List<Table> tables, Response response) {
                            getSupportActionBar().setTitle(title);
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

    private void setUpAppBarLayout(AppBarLayout appBarLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { // for pre-lollipop device
            appBarLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        }
    }

    protected void showTabLayout() {
        tabLayout.setVisibility(View.VISIBLE);
    }

    protected void hideTabLayout() {
        tabLayout.setVisibility(View.GONE);
    }


    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return "시간표";
            case 1:
                return "검색";
            case 2:
                return "내 강의";
            case 3:
                return "알림";
            case 4:
                return "설정";
        }
        return null;
    }

    public void onNotificationChecked() {
        Log.d(TAG, "on notification checked!");
        if (notiCircle != null) {
            notiCircle.setVisibility(View.GONE);
        }
    }
}
