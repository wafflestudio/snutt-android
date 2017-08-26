package com.wafflestudio.snutt_staging.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.TagManager;
import com.wafflestudio.snutt_staging.model.Tag;

import java.util.List;

/**
 * Created by makesource on 2016. 2. 21..
 */
public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

    private static final String TAG = "TAG_LIST_ADAPTER" ;

    private Context context;
    private List<Tag> tags;

    public TagListAdapter(Context context, List<Tag> tags) {
        this.context = context;
        this.tags = tags;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellLayoutView;
        ViewHolder viewHolder;

        cellLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_tag, parent, false);
        viewHolder = new ViewHolder(cellLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag tag = tags.get(position);

        GradientDrawable bgShape = (GradientDrawable) holder.itemView.getBackground();
        bgShape.setColor(SNUTTUtils.getTagColor(tag.getTagType()));
        holder.tagTitle.setText(tag.getName());
        holder.setClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, final int position) {
                TagManager.getInstance().removeTag(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View tagLayout;
        public TextView tagTitle;

        private ClickListener clickListener;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.tagLayout = view;
            this.tagTitle = (TextView) view.findViewById(R.id.tag_title);
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

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v,getPosition());
            }
        }
    }

}
