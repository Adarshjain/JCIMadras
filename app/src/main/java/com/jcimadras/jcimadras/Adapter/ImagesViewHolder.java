package com.jcimadras.jcimadras.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcimadras.jcimadras.R;

public class ImagesViewHolder extends RecyclerView.ViewHolder {

    public ImageView Image;
    ImageButton OverflowMenu;
    RelativeLayout NameLL;
    public TextView Name;
    public ProgressBar progressBar;

    ImagesViewHolder(View view) {
        super(view);
        Image = (ImageView) view.findViewById(R.id.image);
        NameLL = (RelativeLayout) view.findViewById(R.id.Namell);
        Name = (TextView) view.findViewById(R.id.Name);
        OverflowMenu = (ImageButton) view.findViewById(R.id.overflow);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }
}
