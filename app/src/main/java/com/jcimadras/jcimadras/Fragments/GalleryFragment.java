package com.jcimadras.jcimadras.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Adapter.ImagesViewHolder;
import com.jcimadras.jcimadras.AddImageActivity;
import com.jcimadras.jcimadras.Pojo.Images;
import com.jcimadras.jcimadras.R;
import com.jcimadras.jcimadras.SpecificGallery;

public class GalleryFragment extends Fragment {

    private DatabaseReference mDatabase;

    private RecyclerView mRecyclerView;
    private TextView tv;

    //<editor-fold defaultstate="collapsed" desc="unwanted">
    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }
    //</editor-fold>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        FloatingActionButton FAB = (FloatingActionButton) view.findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddImageActivity.class));
            }
        });

        tv = (TextView) view.findViewById(R.id.empty_text);

        mDatabase = FirebaseDatabase.getInstance().getReference("Gallery");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.galleryRecycler);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getChildren() == null || dataSnapshot.getValue() == null) {
                    mRecyclerView.setVisibility(View.GONE);
                    tv.setText("No Photos or Network error");
                    tv.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final Query myQuery = mDatabase;
        FirebaseRecyclerAdapter<Images, ImagesViewHolder> mAdapter = new FirebaseRecyclerAdapter<Images, ImagesViewHolder>(
                Images.class, R.layout.image_grid_view_holder, ImagesViewHolder.class, myQuery) {
            @Override
            protected void populateViewHolder(final ImagesViewHolder holder, final Images model, final int position) {

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), SpecificGallery.class);
                        i.putExtra("desc", model.getDesc());
                        i.putExtra("imageUrl", model.getImageUrl());
                        i.putExtra("imgPathName",model.getImagePathName());
                        startActivity(i);
                    }
                });
                holder.Name.setText(model.getDesc().split(",")[0]);
                Glide.with(getContext())
                        .load(model.getThumb())
                        .into(holder.Image);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }
}
