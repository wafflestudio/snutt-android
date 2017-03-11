package com.wafflestudio.snutt_staging.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 2. 23..
 */
public class MyLectureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        protected View layout;
        protected TextView title;
        protected TextView tag;
        protected TextView classTime;
        protected TextView location;

        private ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            tag = (TextView) itemView.findViewById(R.id.tag);
            classTime = (TextView) itemView.findViewById(R.id.time);
            location = (TextView) itemView.findViewById(R.id.location);
            this.layout.setOnClickListener(this);
            this.layout.setOnLongClickListener(this);
        }

        private void bindData(Lecture lecture) {
            String titleText = "" ;
            titleText += lecture.getCourse_title();
            titleText += " (" + lecture.getInstructor() + " / " + String.valueOf(lecture.getCredit()) + "학점)";
            title.setText(titleText);

            String tagText = "";
            if (!Strings.isNullOrEmpty(lecture.getCategory())) {
                tagText += lecture.getCategory() + ", ";
            }
            if (!Strings.isNullOrEmpty(lecture.getDepartment())) {
                tagText += lecture.getDepartment() + ", ";
            }
            tagText += lecture.getAcademic_year();
            if (Strings.isNullOrEmpty(tagText)) tagText = "(없음)";
            tag.setText(tagText);

            String classTimeText = lecture.getSimplifiedClassTime();
            if (Strings.isNullOrEmpty(classTimeText)) classTimeText = "(없음)";
            classTime.setText(classTimeText);

            String locationText = lecture.getSimplifiedLocation();
            if (Strings.isNullOrEmpty(locationText)) locationText = "(없음)";
            location.setText(locationText);
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
