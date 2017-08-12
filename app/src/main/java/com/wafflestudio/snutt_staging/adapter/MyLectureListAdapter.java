package com.wafflestudio.snutt_staging.adapter;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 2. 23..
 */
public class MyLectureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MY_LECTURE_LIST_ADAPTER";
    private static ClickListener clickListener;
    private static LongClickListener longClickListener;
    private List<Lecture> myLecture;

    public MyLectureListAdapter(List<Lecture> myLecture) {
        this.myLecture = myLecture;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellLayoutView;
        ViewHolder viewHolder;

        cellLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_my_lecture, parent, false);
        // create ViewHolder
        viewHolder = new ViewHolder(cellLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Lecture lecture = myLecture.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.bindData(lecture);
    }

    @Override
    public int getItemCount() {
        if (myLecture != null) {
            return myLecture.size();
        }
        return 0;
    }

    // inner class to hold a reference to each item of RecyclerView
    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView title;
        private TextView subTitle;
        private TextView tag;
        private TextView classTime;
        private TextView location;

        private ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title);
            tag = (TextView) itemView.findViewById(R.id.tag);
            classTime = (TextView) itemView.findViewById(R.id.time);
            location = (TextView) itemView.findViewById(R.id.location);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private void bindData(Lecture lecture) {
            String titleText = lecture.getCourse_title();
            String subTitleText =  "(" + lecture.getInstructor() + " / " + String.valueOf(lecture.getCredit()) + "학점)";
            title.setText(titleText);
            subTitle.setText(subTitleText);

            int maxWidth = (int) (SNUTTUtils.getDisplayWidth() - SNUTTUtils.dpTopx(20 + 20 + 10));
            int subTitleWidth = (int) Math.min(getTextViewWidth(subTitle), maxWidth / 2);
            int titleWidth = (int) Math.min(getTextViewWidth(title), maxWidth - subTitleWidth);
            if (titleWidth + subTitleWidth < maxWidth) {
                subTitleWidth = maxWidth - titleWidth;
            }

            subTitle.setLayoutParams(new LinearLayout.LayoutParams(subTitleWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            title.setLayoutParams(new LinearLayout.LayoutParams(titleWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            String tagText = "";
            if (!Strings.isNullOrEmpty(lecture.getCategory())) {
                tagText += lecture.getCategory() + ", ";
            }
            if (!Strings.isNullOrEmpty(lecture.getDepartment())) {
                tagText += lecture.getDepartment() + ", ";
            }
            if (!Strings.isNullOrEmpty(lecture.getAcademic_year())) {
                tagText += lecture.getAcademic_year();
            }
            if (Strings.isNullOrEmpty(tagText)) tagText = "(없음)";
            tag.setText(tagText);

            String classTimeText = lecture.getSimplifiedClassTime();
            if (Strings.isNullOrEmpty(classTimeText)) classTimeText = "(없음)";
            classTime.setText(classTimeText);

            String locationText = lecture.getSimplifiedLocation();
            if (Strings.isNullOrEmpty(locationText)) locationText = "(없음)";
            location.setText(locationText);

        }

        private float getTextViewWidth(TextView textView) {
            textView.measure(0, 0);
            return textView.getMeasuredWidth();
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v,getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                longClickListener.onLongClick(v, getPosition());
            }
            return true;
        }
    }

    public interface ClickListener {
        public void onClick(View v, int position);
    }

    public interface LongClickListener {
        public void onLongClick(View v, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnItemLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
