package com.wafflestudio.snutt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 2. 10..
 */
public class LectureListAdapter extends RecyclerView.Adapter<LectureListAdapter.ViewHolder> {


    private List<Lecture> lectures;

    public LectureListAdapter(List<Lecture> lectures) {
        this.lectures = lectures;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellLayoutView;
        ViewHolder viewHolder;

        cellLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_lecture, parent, false);
        // create ViewHolder
        viewHolder = new ViewHolder(cellLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lecture lecture = lectures.get(position);

        holder.courseTitle.setText(lecture.getCourse_title());
        holder.courseNumber.setText(lecture.getCourse_number());
        holder.lectureNumber.setText(lecture.getLecture_number());
        holder.classification.setText(lecture.getClassification());
        holder.department.setText(lecture.getDepartment());
        holder.classTime.setText(lecture.getClass_time());
        holder.location.setText(lecture.getLocation());
        holder.remark.setText(lecture.getRemark());

        holder.add.setVisibility(View.VISIBLE);
        holder.remove.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return lectures.size();
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
        notifyDataSetChanged();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView courseTitle;
        public TextView courseNumber;
        public TextView lectureNumber;
        public TextView classification;
        public TextView department;
        public TextView classTime;
        public TextView location;
        public TextView remark;

        public Button add;
        public Button remove;


        public ViewHolder(View view) {
            super(view);

            this.courseTitle = (TextView) view.findViewById(R.id.course_title);
            this.courseNumber = (TextView) view.findViewById(R.id.course_number);
            this.lectureNumber = (TextView) view.findViewById(R.id.lecture_number);
            this.classification = (TextView) view.findViewById(R.id.classification);
            this.department = (TextView) view.findViewById(R.id.department);
            this.classTime = (TextView) view.findViewById(R.id.class_time);
            this.location = (TextView) view.findViewById(R.id.location);
            this.remark = (TextView) view.findViewById(R.id.remark);

            this.add = (Button) view.findViewById(R.id.add);
            this.remove = (Button) view.findViewById(R.id.remove);
        }
    }

}