package com.jcimadras.jcimadras.Adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.jcimadras.jcimadras.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

    public TextView Name, Desc, Date;
    public NetworkImageView Map;
    public LinearLayout Location;

    public EventViewHolder(View itemView) {
        super(itemView);
        Name = (TextView) itemView.findViewById(R.id.eventNameDisp);
        Desc = (TextView) itemView.findViewById(R.id.eventDescDisp);
        Date = (TextView) itemView.findViewById(R.id.dateDisp);
        Map = (NetworkImageView) itemView.findViewById(R.id.mapNIView);
        Location = (LinearLayout) itemView.findViewById(R.id.location);
    }
}
