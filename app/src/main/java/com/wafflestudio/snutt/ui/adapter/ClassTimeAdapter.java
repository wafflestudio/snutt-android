package com.wafflestudio.snutt.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wafflestudio.snutt.model.ClassTime;

import java.util.List;

/**
 * Created by makesource on 2016. 3. 6..
 */
public class ClassTimeAdapter extends BaseAdapter {
    private List<ClassTime> times;

    public ClassTimeAdapter(List<ClassTime> times) {
        this.times = times;
    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
