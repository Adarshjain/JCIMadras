package com.jcimadras.jcimadras;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.ParticipantDetails;

import java.util.HashMap;
import java.util.Map;

public class RegisterEvent extends AppCompatActivity implements View.OnClickListener {

    private String key;
    private int count = 0;
    private TextView EventName;
    private EditText JCName, Senior, Junior, Guest, Total;
    private ProgressDialog pd, pd1;
    private EditText KeyDisplay;
    private LinearLayout MainL, KeyL;
    private Button Register;
    private String[] x;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(RegisterEvent.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        Bundle b = getIntent().getExtras();
        key = b.getString("key");

        EventName = (TextView) findViewById(R.id.eventName);
        JCName = (EditText) findViewById(R.id.jcName);
        Senior = (EditText) findViewById(R.id.senior);
        Junior = (EditText) findViewById(R.id.junior);
        Guest = (EditText) findViewById(R.id.guest);


        //Display Key
        Button copy = (Button) findViewById(R.id.copy);
        copy.setOnClickListener(this);
        MainL = (LinearLayout) findViewById(R.id.mainLayout);
        KeyL = (LinearLayout) findViewById(R.id.keyLayout);
        KeyL.setVisibility(View.GONE);
        Register = (Button) findViewById(R.id.register);
        Register.setOnClickListener(this);
        KeyDisplay = (EditText) findViewById(R.id.keyDisplay);
        KeyDisplay.setFocusable(false);
        KeyDisplay.setClickable(true);
        Total = (EditText) findViewById(R.id.total);
        Total.setFocusable(false);
        Total.setClickable(true);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("events/" + key);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EventName.setText(dataSnapshot.child("eventName").getValue().toString());
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                register();
                break;
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Key", x[x.length - 1]);
                clipboard.setPrimaryClip(clip);
                Message.ts(this, "Copied to Clipboard!!", getLayoutInflater(), findViewById(R.id.toastbg));
                break;
        }
    }

    private void register() {
        pd1 = new ProgressDialog(RegisterEvent.this);
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
            if (count == 0){
                Message.ts(this, "Please Enter Senior|Junior|Guest count", getLayoutInflater(), findViewById(R.id.toastbg));
                return;
            }

            details.setTotal(count);

            reference = FirebaseDatabase.getInstance().getReference("Participants/" + key).push();
            final ParticipantDetails p = details;

            x = reference.toString().split("/");

            final DatabaseReference eveRef = FirebaseDatabase.getInstance().getReference("events/" + key);
            eveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int limit = Integer.parseInt(dataSnapshot.child("limit").getValue().toString());
                    int total = Integer.parseInt(dataSnapshot.child("total").getValue().toString());
                    if (limit < (total + count) && limit != -1) {
                        if (pd1.isShowing())
                            pd.dismiss();
                        Message.tl(RegisterEvent.this,
                                "Seats limit reached!!\n Available seats " + (limit - total) + "\n Your seats " + count
                                , getLayoutInflater(), findViewById(R.id.toastbg));
                        RegisterEvent.this.finish();
                    } else {
                        reference.setValue(p);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("total", total + count);
                        eveRef.updateChildren(childUpdates);
                        KeyDisplay.setText(x[x.length - 1]);
                        Total.setText(count);
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Key", x[x.length - 1]);
                        clipboard.setPrimaryClip(clip);
                        Message.ts(RegisterEvent.this, "Copied to Clipboard!!", getLayoutInflater(), findViewById(R.id.toastbg));
                        KeyL.setVisibility(View.VISIBLE);
                        MainL.setVisibility(View.GONE);
                        Register.setVisibility(View.GONE);
                        JCName.setText("");
                        Senior.setText("");
                        Junior.setText("");
                        Guest.setText("");
                        if (pd1.isShowing())
                            pd1.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else
            Message.ts(this, "Please Enter JC/Jcrt/Jr.JC Name", getLayoutInflater(), findViewById(R.id.toastbg));
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
