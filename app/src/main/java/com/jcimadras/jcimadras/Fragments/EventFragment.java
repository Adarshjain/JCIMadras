package com.jcimadras.jcimadras.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Adapter.EventViewHolder;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.EventDetails;
import com.jcimadras.jcimadras.R;
import com.jcimadras.jcimadras.SpecificEvent;
import com.jcimadras.jcimadras.Volley.VolleySingleton;

public class EventFragment extends Fragment {
    private DatabaseReference mDatabase;

    private VolleySingleton mVolleySingleton = VolleySingleton.getInstance();
    private ImageLoader mImageLoader = mVolleySingleton.getImageLoader();

    private RecyclerView mRecyclerView;
    private TextView tv;

    //<editor-fold defaultstate="collapsed" desc="unwanted">
    public EventFragment() {
    }

    public static EventFragment newInstance() {
        return new EventFragment();
    }
    //</editor-fold>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        tv = (TextView) view.findViewById(R.id.empty_text);

        mDatabase = FirebaseDatabase.getInstance().getReference("events");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.eventRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
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
                    tv.setText("No Events or Network error");
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
        FirebaseRecyclerAdapter<EventDetails, EventViewHolder> mAdapter = new FirebaseRecyclerAdapter<EventDetails, EventViewHolder>(
                EventDetails.class, R.layout.event_viewholder, EventViewHolder.class, myQuery) {
            @Override
            protected void populateViewHolder(final EventViewHolder holder, final EventDetails model, final int position) {

                final String postKey = getRef(position).getKey();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SpecificEvent.class);
                        if (model.getLimit() <= model.getTotal() && model.getLimit() != -1) {
                            intent.putExtra("SeatFull", true);
                            Message.tl(getActivity(), "Seats Limit Reached!\nTry Again Later!!", getActivity().getLayoutInflater(), getActivity().findViewById(R.id.toastbg));
                        } else {
                            intent.putExtra("SeatFull", false);
                        }
                        intent.putExtra("key", postKey);
                        startActivity(intent);
                    }
                });
                if (model.getLimit() <= model.getTotal() && model.getLimit() != -1) {
                    holder.Name.setTextColor(Color.BLACK);
                    holder.Desc.setTextColor(Color.BLACK);
                    holder.Date.setTextColor(Color.BLACK);
                }
                holder.Name.setText(model.getEventName());
                holder.Desc.setText(model.getEventDesc());
                holder.Date.setText(model.getDate());
                if (model.getCoordinates() != null) {
                    holder.Location.setVisibility(View.VISIBLE);
                    String[] ltlng = model.getCoordinates().split(",");
                    String url = "http://maps.google.com/maps/api/staticmap?center=" + ltlng[0] + "," + ltlng[1] + "&zoom=17&size=640x640&sensor=false";
                    holder.Map.setImageUrl(url, mImageLoader);
                } else {
                    holder.Location.setVisibility(View.GONE);
                }

            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }
}
