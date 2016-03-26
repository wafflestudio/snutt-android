package com.wafflestudio.snutt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTUtils;
import com.wafflestudio.snutt.model.ClassTime;
import com.wafflestudio.snutt.model.Lecture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 3. 22..
 */
public class ClassTimeAdapter2 extends RecyclerView.Adapter<ClassTimeAdapter2.ViewHolder> {

    private static final String TAG = "CLASS_TIME_ADAPTER" ;

    private Context context;
    private List<ClassTime> times;
    private List<String> places;


    public ClassTimeAdapter2(Context context, List<ClassTime> times) {
        this.context = context;
        this.times = times;
        this.places = new ArrayList<>();
        for (ClassTime time : times) {
            this.places.add(time.getPlace());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellLayoutView;
        ViewHolder viewHolder;

        cellLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_time, parent, false);
        // create ViewHolder
        viewHolder = new ViewHolder(cellLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.pos = position;
        ClassTime classTime = times.get(position);

        String time = SNUTTUtils.numberToWday(classTime.getDay()) + " " +
                SNUTTUtils.numberToTime(classTime.getStart()) + "~" +
                SNUTTUtils.numberToTime(classTime.getStart() + classTime.getLen());
        holder.tv_time.setText(time);

        holder.et_place.setText(places.get(position));
        holder.et_place.setHint(places.get(position));
      /*  holder.et_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!Strings.isNullOrEmpty(text)) {
                    Log.d(TAG, String.valueOf(holder.pos) + " : " + text);
                    places.set(viewHolder.pos, text);
                }
            }
        }););*/
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_time;
        public EditText et_place;
        public int pos;

        public ViewHolder(View view) {
            super(view);
            this.tv_time = (TextView) view.findViewById(R.id.class_time);
            this.et_place = (EditText) view.findViewById(R.id.class_place);
        }
    }

}
