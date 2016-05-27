package com.wafflestudio.snutt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.view.TableView;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class TableFragment extends SNUTTBaseFragment implements LectureManager.OnLectureChangedListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static TableView mInstance;

    public TableFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TableFragment newInstance(int sectionNumber) {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table, container, false);
        mInstance = (TableView) rootView.findViewById(R.id.timetable);
        //mInstance.setExport(true);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_table, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if (id == R.id.action_more) {
             getMainActivity().startTableList();
             return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        LectureManager.getInstance().removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LectureManager.getInstance().addListener(this);
    }

    @Override
    public void notifyLectureChanged() {
        mInstance.invalidate();
    }
}
