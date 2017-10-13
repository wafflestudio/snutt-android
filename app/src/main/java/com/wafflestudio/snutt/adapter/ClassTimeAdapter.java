package com.wafflestudio.snutt.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTUtils;
import com.wafflestudio.snutt.model.ClassTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 3. 6..
 */
public class ClassTimeAdapter extends BaseAdapter {
    private static final String TAG = "CLASS_TIME_ADAPTER" ;

    private Context context;
    private List<ClassTime> times;
    private List<String> places;
    private MyWatcher watcher;

    public ClassTimeAdapter(Context context, List<ClassTime> times) {
        this.context = context;
        this.times = times;
        this.places = new ArrayList<>();
        for (ClassTime time : times) {
            this.places.add(time.getPlace());
        }
    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public Object getItem(int position) {
        return times.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(R.layout.cell_time, null);
        }

        ClassTime classTime = times.get(position);
        TextView tv_time = (TextView) v.findViewById(R.id.class_time);
        EditText et_place = (EditText) v.findViewById(R.id.class_place);

        String time = SNUTTUtils.numberToWday(classTime.getDay()) + " " +
                SNUTTUtils.numberToTime(classTime.getStart()) + "~" +
                SNUTTUtils.numberToTime(classTime.getStart() + classTime.getLen());
        tv_time.setText(time);

        if (watcher != null) et_place.removeTextChangedListener(watcher);
        et_place.setText(places.get(position));
        et_place.setHint(classTime.getPlace());
        watcher = new MyWatcher(position);
        et_place.addTextChangedListener(watcher);

        Log.d(TAG, "getView is called : " + String.valueOf(position));
        return v;
    }

    public JsonArray getClassTimeJson() {
        JsonArray ja = new JsonArray();
        for (int i=0;i<times.size();i++) {
            ClassTime time = times.get(i);
            JsonObject object = new JsonObject();
            object.addProperty("day", time.getDay());
            object.addProperty("start", time.getStart());
            object.addProperty("len", time.getLen());
            object.addProperty("_id", time.get_id());

            if (!Strings.isNullOrEmpty(places.get(i))) {
                object.addProperty("place", places.get(i));
            } else {
                object.addProperty("place", time.getPlace());
            }

            ja.add(object);
        }
        return ja;
    }
    private class MyWatcher implements TextWatcher {
        private int position;
        public MyWatcher(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();

            Log.d(TAG, String.valueOf(position) + " : " + text);
            places.set (position, text);
        }
    }
}

