package com.jcimadras.jcimadras;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Adapter.PartViewHolder;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.ParticipantDetails;
import com.jcimadras.jcimadras.Pojo.SpecificEventDetails;

import java.util.HashMap;
import java.util.Map;

public class ViewParticipants extends AppCompatActivity implements View.OnClickListener {

    private String key, UserKey, UserName;
    private DatabaseReference reference;
    private EditText UniqueKey, Name;
    private LinearLayout DeleteLayout, MainLayout;
    private ProgressDialog pd;
    private RecyclerView partRecycler;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_participants);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading Participants");
        pd.show();

        Bundle b = getIntent().getExtras();
        key = b.getString("key");

        UniqueKey = (EditText) findViewById(R.id.uniqueKey);
        Name = (EditText) findViewById(R.id.Name);
        DeleteLayout = (LinearLayout) findViewById(R.id.deleteLayout);
        MainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        Button delete2 = (Button) findViewById(R.id.delete);
        delete2.setOnClickListener(this);
        Button delete = (Button) findViewById(R.id.deleteRegistration);
        delete.setOnClickListener(this);
        Button edit = (Button) findViewById(R.id.editRegistration);
        edit.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.empty_text);
        partRecycler = (RecyclerView) findViewById(R.id.partRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        partRecycler.setLayoutManager(layoutManager);
        partRecycler.setHasFixedSize(true);
        reference = FirebaseDatabase.getInstance().getReference("Participants/" + key);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (pd.isShowing())
                    pd.dismiss();
                if (dataSnapshot == null || dataSnapshot.getChildren() == null || dataSnapshot.getValue() == null) {
                    partRecycler.setVisibility(View.GONE);
                    tv.setText("No Participants or Network error");
                    tv.setVisibility(View.VISIBLE);
                } else {
                    partRecycler.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<ParticipantDetails, PartViewHolder> adapter = new FirebaseRecyclerAdapter<ParticipantDetails, PartViewHolder>(
                ParticipantDetails.class, R.layout.participant_viewholder, PartViewHolder.class, reference) {
            @Override
            protected void populateViewHolder(PartViewHolder holder, ParticipantDetails model, int position) {
                holder.jcName.setText((holder.getAdapterPosition() + 1) + ". Name of JC/Jcrt/Jr.JC : " + model.getName());
                holder.Senior.setText("Senior : " + model.getSenior());
                holder.Junior.setText("Junior : " + model.getJunior());
                holder.Guest.setText("Guest : " + model.getGuest());
                holder.Total.setText("Total : " + model.getTotal());
            }
        };
        partRecycler.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deleteRegistration:
                DeleteLayout.setVisibility(View.VISIBLE);
                MainLayout.setVisibility(View.GONE);
                break;
            case R.id.delete:
                delete();
                break;
            case R.id.editRegistration:
                Intent i = new Intent(this,EditParticipantsActivity.class);
                i.putExtra("key",key);
                startActivity(i);
                break;
        }
    }

    private void delete() {
        UserKey = UniqueKey.getText().toString();
        UserName = Name.getText().toString();

        if (UserKey.equals("") || UserName.equals(""))
            Message.ts(this, "Please enter both the fields!!", getLayoutInflater(), findViewById(R.id.toastbg));
        else {
            DatabaseReference ref = reference.child(UserKey);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot == null || dataSnapshot.getChildren() == null || dataSnapshot.getValue() == null) {
                        Message.ts(getApplicationContext(), "Please check Unique Key!!", getLayoutInflater(), findViewById(R.id.toastbg));
                    } else {
                        ParticipantDetails myMap = dataSnapshot.getValue(ParticipantDetails.class);
                        if (!myMap.getName().equals(UserName)) {
                            Message.ts(getApplicationContext(), "Please check Name!!", getLayoutInflater(), findViewById(R.id.toastbg));
                        } else {
                            deleteReference(myMap.getTotal());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void deleteReference(final int totalPart) {
        reference.child(UserKey).removeValue();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("events/" + key);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SpecificEventDetails details = dataSnapshot.getValue(SpecificEventDetails.class);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("total", (details.getTotal() - totalPart));
                reference.updateChildren(childUpdates);
                Message.ts(ViewParticipants.this, "Successfully deleted", getLayoutInflater(), findViewById(R.id.toastbg));
                DeleteLayout.setVisibility(View.GONE);
                MainLayout.setVisibility(View.VISIBLE);
                UniqueKey.setText("");
                Name.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }
}
