package com.wafflestudio.snutt.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTUtils;
import com.wafflestudio.snutt.model.ClassTime;

import java.util.List;

/**
 * Created by makesource on 2016. 3. 6..
 */
public class ClassTimeAdapter extends BaseAdapter {
    private Context context;
    private List<ClassTime> times;
    private ViewHolder viewHolder;


    public ClassTimeAdapter(Context context, List<ClassTime> times) {
        this.context = context;
        this.times = times;
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
            viewHolder = new ViewHolder();
            viewHolder.tv_time = (TextView) v.findViewById(R.id.class_time);
            viewHolder.et_place = (EditText) v.findViewById(R.id.class_place);
            v.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) v.getTag();
        }

        ClassTime classTime = times.get(position);

        String time = SNUTTUtils.numberToWday(classTime.getDay()) + " " +
                SNUTTUtils.numberToTime(classTime.getStart()) + "~" +
                SNUTTUtils.numberToTime(classTime.getStart() + classTime.getLen());
        viewHolder.tv_time.setText(time);
        viewHolder.et_place.setText(classTime.getPlace());
        return v;
    }

    class ViewHolder{
        public TextView tv_time;
        public EditText et_place;
    }
}
