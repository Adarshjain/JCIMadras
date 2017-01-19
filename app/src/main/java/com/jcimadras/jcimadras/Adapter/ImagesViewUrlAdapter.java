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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jcimadras.jcimadras.ImageviewFull;
import com.jcimadras.jcimadras.MyMenuItemClickListener;
import com.jcimadras.jcimadras.R;

import java.util.ArrayList;

public class ImagesViewUrlAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

    private LayoutInflater li;
    private ArrayList<String> ImagesList = new ArrayList<>();
    private ArrayList<String> ImagesNameList = new ArrayList<>();
    private Context con;
    private View vi;
    private String Key;
    private StorageReference stRef;

    public ImagesViewUrlAdapter(Context con, String key, View view) {
        this.con = con;
        this.Key = key;
        this.vi = view;
        this.li = LayoutInflater.from(con);
        stRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.image_grid_view_holder, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImagesViewHolder holder, int position) {
        Glide.with(con)
                .using(new FirebaseImageLoader())
                .load(stRef.child("Gallery/Images/" + Key).child(ImagesNameList.get(position)))
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.error)
                .crossFade()
                .into(holder.Image);
        holder.NameLL.setVisibility(View.GONE);
        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, ImageviewFull.class);
                Bundle b = new Bundle();
                b.putStringArrayList("paths", ImagesNameList);
                b.putString("stRef", "Gallery/Images/" + Key);
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
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, con, li, vi, ImagesList, Key));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return ImagesNameList.size();
    }

    public void setImagesList(ArrayList<String> imagesList, ArrayList<String> imagesNameList) {
        this.ImagesList = imagesList;
        this.ImagesNameList = imagesNameList;
        notifyItemRangeChanged(0, imagesList.size());
        notifyDataSetChanged();
    }
}