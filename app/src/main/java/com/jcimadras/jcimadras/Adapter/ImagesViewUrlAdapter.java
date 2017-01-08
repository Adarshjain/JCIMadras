package com.jcimadras.jcimadras.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jcimadras.jcimadras.ImageviewFull;
import com.jcimadras.jcimadras.MyMenuItemClickListener;
import com.jcimadras.jcimadras.R;

import java.util.ArrayList;

public class ImagesViewUrlAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

    private LayoutInflater li;
    private ArrayList<String> ImagesList = new ArrayList<>();
    private Context con;
    private View vi;
    private String Key;

    public ImagesViewUrlAdapter(Context con,String key, View view) {
        this.con = con;
        this.Key = key;
        this.vi = view;
        this.li = LayoutInflater.from(con);
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.image_grid_view_holder, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImagesViewHolder holder, int position) {
        final String url = ImagesList.get(position);
        Glide.with(con)
                .load(url)
                .into(holder.Image);
        holder.NameLL.setVisibility(View.GONE);
        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, ImageviewFull.class);
                Bundle b = new Bundle();
                b.putStringArrayList("paths", ImagesList);
                b.putInt("position", holder.getAdapterPosition());
                intent.putExtras(b);
                con.startActivity(intent);
            }
        });
        holder.OverflowMenu.setVisibility(View.VISIBLE);
        holder.OverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.OverflowMenu, holder.getAdapterPosition());
            }
        });
    }

    private void showPopupMenu(ImageView view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.cardview_overflow_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, con, li, vi, ImagesList,Key));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return ImagesList.size();
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.ImagesList = imagesList;
        notifyItemRangeChanged(0, imagesList.size());
        notifyDataSetChanged();
    }
}
