package com.wafflestudio.snutt_staging.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.model.Notification;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by makesource on 2017. 2. 27..
 */

public class NotificationAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum VIEW_TYPE {
        Notification(0),
        ProgressBar(1);
        private final int value;
        VIEW_TYPE(int value) {
            this.value = value;
        }
        public final int getValue() {
            return value;
        }
    }
    private static final String TAG = "NOTIFICATION_ADAPTER";
    private List<Notification> lists;


    public NotificationAdapter(List<Notification> lists) {
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE.Notification.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_notification, parent, false);
            return new NotificationViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_progressbar, parent, false);
            return new ProgressBarViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == VIEW_TYPE.Notification.getValue()) {
            ((NotificationViewHolder) holder).bindData(getItem(position));
        }
    }

    public Notification getItem(int position) {
        return lists.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        Notification item = lists.get(position);
        return (item == null) ? VIEW_TYPE.ProgressBar.getValue() : VIEW_TYPE.Notification.getValue();
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "notification list size : " + lists.size());
        return lists.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    private static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private ImageView image;

        private NotificationViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.notification_image);
            message = (TextView) view.findViewById(R.id.notification_text);
        }

        private void bindData(Notification notification) {
           // Log.d(TAG, "notification message : " + notification.getMessage());
            String text = notification.getMessage();
            switch (notification.getType()) {
                case 0:
                    image.setImageResource(R.drawable.noti_normal);
                    break;
                case 1:
                    image.setImageResource(R.drawable.noti_coursebook);
                    break;
                case 2:
                    image.setImageResource(R.drawable.noti_update);
                    break;
                case 3:
                    image.setImageResource(R.drawable.noti_remove);
                    break;
                default:
                    Log.e(TAG, "notification type is out of bound!!");
            }
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date date1 = format.parse(notification.getCreated_at());
                Date date2 = new Date();
                long diff = date2.getTime() - date1.getTime();
                long hours = diff / (1000 * 60 * 60);
                long days = hours / 24;
                long months = days / 30;
                long years = months / 12;
                text += " ";

                if (years > 0) {
                    text += "<font color='#808080'>" + years + "년 전</font>";
                } else if (months > 0) {
                    text += "<font color='#808080'>" + months + "달 전</font>";
                } else if (days > 0) {
                    text += "<font color='#808080'>" + days + "일 전</font>";
                } else if (hours > 0) {
                    text += "<font color='#808080'>" + hours + "시간 전</font>";
                } else {
                    text += "<font color='#808080'>방금</font>";
                }
            } catch (ParseException e) {
                Log.e(TAG, "notification created time parse error!");
                e.printStackTrace();
            }
            message.setText(Html.fromHtml(text));
        }
    }

    private static class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        private ProgressBarViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }
}
