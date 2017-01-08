package com.jcimadras.jcimadras.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jcimadras.jcimadras.R;

public class PartViewHolder extends RecyclerView.ViewHolder {

    public TextView jcName, Senior, Junior, Guest,Total;

    public PartViewHolder(View view) {
        super(view);
        jcName = (TextView) view.findViewById(R.id.jcName);
        Senior = (TextView) view.findViewById(R.id.senior);
        Junior = (TextView) view.findViewById(R.id.junior);
        Guest = (TextView) view.findViewById(R.id.guest);
        Total = (TextView) view.findViewById(R.id.total);
    }
}
