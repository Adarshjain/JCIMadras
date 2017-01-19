package com.jcimadras.jcimadras.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.Images;
import com.jcimadras.jcimadras.R;

import java.io.IOException;
import java.util.ArrayList;

public class ImagesViewAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

    private int THUMBNAIL_SIZE = 128;
    private LayoutInflater li;
    private ArrayList<Images> ImagesList = new ArrayList<>();
    private Context con;

    public ImagesViewAdapter(Context con) {
        this.con = con;
        li = LayoutInflater.from(con);
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.image_grid_view_holder, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImagesViewHolder holder, int position) {
        Images IV = ImagesList.get(position);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(con.getContentResolver(), IV.getImageuri());
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
            holder.Image.setImageBitmap(ThumbImage);
            if (IV.getDesc() != null) holder.Name.setText(IV.getDesc());
            else holder.NameLL.setVisibility(View.GONE);
            holder.Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
                    alertDialogBuilder.setMessage("Are you sure?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    ImagesList.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } catch (IOException ignored) {
        }
    }

    @Override
    public int getItemCount() {
        return ImagesList.size();
    }

    public ArrayList<Images> getImagesList() {
        return ImagesList;
    }

    public void setImagesList(ArrayList<Images> imagesList) {
        this.ImagesList = imagesList;
        notifyItemRangeChanged(0, imagesList.size());
        notifyDataSetChanged();
    }


}
