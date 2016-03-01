package com.wafflestudio.snutt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 2. 23..
 */
public class MyLectureListAdapter extends RecyclerView.Adapter<MyLectureListAdapter.ViewHolder> {

    private static ViewHolder.ClickListener clickListener;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lecture lecture = myLecture.get(position);

        String text = new String();
        text += lecture.getCourse_title();
        text += " (" + lecture.getInstructor() + " / " + String.valueOf(lecture.getCredit()) + "학점)";
        holder.courseTitle.setText(text);
    }

    @Override
    public int getItemCount() {
        return myLecture.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;
        public TextView courseTitle;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.courseTitle = (TextView) view.findViewById(R.id.course_title);

            this.view.setOnClickListener(this);
        }

        public interface ClickListener {
            /**
             * Called when the view is clicked.
             *
             * @param v view that is clicked
             * @param position of the clicked item
             */

            public void onClick(View v, int position);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v,getPosition());
            }
        }
    }

    public void setOnItemClickListener(ViewHolder.ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
