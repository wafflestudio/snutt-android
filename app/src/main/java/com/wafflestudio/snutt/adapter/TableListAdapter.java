package com.wafflestudio.snutt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.manager.PrefManager;
import com.wafflestudio.snutt.model.Table;

import java.util.List;

/**
 * Created by makesource on 2016. 1. 20..
 */
public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.ViewHolder> {

    public static final String TABLE_SECTION_STRING = "table_section_string";

    private static final int TYPE_TABLE_SECTION = 0;
    private static final int TYPE_TABLE_CELL = 1;

    private List<Table> tables;

    public TableListAdapter(List<Table> tables) {
        this.tables = tables;
    }

    @Override
    public TableListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellLayoutView;
        ViewHolder viewHolder;

        switch(viewType) {
            case TYPE_TABLE_SECTION:
                // create a new view
                cellLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_table_section, parent, false);
                // create ViewHolder
                viewHolder = new ViewHolder(cellLayoutView);
                return viewHolder;
            case TYPE_TABLE_CELL:
                cellLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_table, parent, false);
                // create ViewHolder
                viewHolder = new ViewHolder(cellLayoutView);
                return viewHolder;

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous

        String id = tables.get(position).getId();

        if (id.equals(TABLE_SECTION_STRING)) {
            return TYPE_TABLE_SECTION;
        } else {
            return TYPE_TABLE_CELL;
        }
    }

    @Override
    public void onBindViewHolder(TableListAdapter.ViewHolder holder, int position) {
        Table table = tables.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_TABLE_SECTION:
                holder.tableSectionName.setText(table.getTitle());
                break;
            case TYPE_TABLE_CELL:
                holder.tableName.setText(table.getTitle());
                if (PrefManager.getInstance().getLastViewTableId().equals(table.getId())) {
                    holder.checked.setVisibility(View.VISIBLE);
                } else {
                    holder.checked.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tableSectionName;
        public TextView tableName;
        public ImageView checked;

        public ViewHolder(View view) {
            super(view);
            tableSectionName = (TextView) view.findViewById(R.id.cell_table_section);
            tableName = (TextView) view.findViewById(R.id.cell_table);
            checked = (ImageView) view.findViewById(R.id.checked);
        }
    }
}
