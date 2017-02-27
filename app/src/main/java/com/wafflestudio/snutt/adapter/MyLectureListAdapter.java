package com.wafflestudio.snutt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.Lecture;

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

        String text = "" ;
        text += lecture.getCourse_title();
        text += " (" + lecture.getInstructor() + " / " + String.valueOf(lecture.getCredit()) + "학점)";
        ((ViewHolder)holder).courseTitle.setText(text);
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

        private View view;
        private TextView courseTitle;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
            this.courseTitle = (TextView) view.findViewById(R.id.course_title);

            this.view.setOnClickListener(this);
            this.view.setOnLongClickListener(this);
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
