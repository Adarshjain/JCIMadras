package com.jcimadras.jcimadras;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.ParticipantDetails;

import java.util.HashMap;
import java.util.Map;

public class EditParticipantsActivity extends AppCompatActivity implements View.OnClickListener {

    private String key;
    private int count = 0, oldTotal;
    private EditText JCName, Senior, Junior, Guest, UniqueKeyE;
    private ProgressDialog pd, pd1;
    private LinearLayout SubLayout;
    private DatabaseReference partRef;
    private Button update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_participants);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        key = b.getString("key");

        UniqueKeyE = (EditText) findViewById(R.id.uniqueKey);
        JCName = (EditText) findViewById(R.id.jcName);
        Senior = (EditText) findViewById(R.id.senior);
        Junior = (EditText) findViewById(R.id.junior);
        Guest = (EditText) findViewById(R.id.guest);
        SubLayout = (LinearLayout) findViewById(R.id.subMainLayout);
        Button go = (Button) findViewById(R.id.go);
        go.setOnClickListener(this);
        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go:
                go();
                break;
            case R.id.update:
                update();
                break;
        }
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

    private void update() {
        pd1 = new ProgressDialog(EditParticipantsActivity.this);
        pd1.setMessage("Please wait registering");
        pd1.setCancelable(false);

        String JCNameV, seniorS, juniorS, guestS;
        final int senior, junior, guest;

        JCNameV = JCName.getText().toString();
        seniorS = Senior.getText().toString();
        juniorS = Junior.getText().toString();
        guestS = Guest.getText().toString();


        ParticipantDetails details = new ParticipantDetails();
        if (!JCNameV.equals("")) {
            pd1.show();
            details.setName(JCNameV);

            if (!seniorS.equals("")) {
                senior = Integer.parseInt(seniorS);
                details.setSenior(senior);
                count += senior;
            }

            if (!juniorS.equals("")) {
                junior = Integer.parseInt(juniorS);
                details.setJunior(junior);
                count += junior;
            }

            if (!guestS.equals("")) {
                guest = Integer.parseInt(guestS);
                details.setGuest(guest);
                count += guest;
            }

            details.setTotal(count);

            final ParticipantDetails p = details;

            final DatabaseReference eveRef = FirebaseDatabase.getInstance().getReference("events/" + key);
            eveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int limit = Integer.parseInt(dataSnapshot.child("limit").getValue().toString());
                    int total = Integer.parseInt(dataSnapshot.child("total").getValue().toString());
                    if (limit < (total + count - oldTotal) && limit != -1) {
                        if (pd1.isShowing())
                            pd.dismiss();
                        Message.tl(EditParticipantsActivity.this,
                                "Seats limit reached!!\n Available seats " + (limit - total + oldTotal) + "\n Your seats " + count
                                , getLayoutInflater(), findViewById(R.id.toastbg));
                        EditParticipantsActivity.this.finish();
                    } else {
                        partRef.setValue(p);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("total", total + count - oldTotal);
                        eveRef.updateChildren(childUpdates);
                        if (pd1.isShowing())
                            pd1.dismiss();
                        Message.ts(getApplicationContext(), "Edited Successfully", getLayoutInflater(), findViewById(R.id.toastbg));
                        EditParticipantsActivity.this.finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else
            Message.ts(this, "Please Enter JC/Jcrt/Jr.JC Name", getLayoutInflater(), findViewById(R.id.toastbg));


    }

    private void go() {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Participant Details..");
        pd.setCancelable(false);
        pd.show();
        if (UniqueKeyE.getText().toString().equals("")) {
            Message.ts(getApplicationContext(), "Please provide Unique Key!!", getLayoutInflater(), findViewById(R.id.toastbg));
            pd.dismiss();
            return;
        }
        String uniqueKey = UniqueKeyE.getText().toString();
        partRef = FirebaseDatabase.getInstance().getReference("Participants/" + key + "/" + uniqueKey);
        partRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (pd.isShowing()) pd.dismiss();
                if (dataSnapshot == null || dataSnapshot.getChildren() == null || dataSnapshot.getValue() == null) {
                    Message.ts(getApplicationContext(), "Please check Unique Key!!", getLayoutInflater(), findViewById(R.id.toastbg));
                } else {
                    SubLayout.setVisibility(View.VISIBLE);
                    update.setVisibility(View.VISIBLE);
                    ParticipantDetails details = dataSnapshot.getValue(ParticipantDetails.class);
                    JCName.setText(details.getName());
                    Senior.setText("" + details.getSenior());
                    Junior.setText("" + details.getJunior());
                    Guest.setText("" + details.getGuest());
                    oldTotal = details.getTotal();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
