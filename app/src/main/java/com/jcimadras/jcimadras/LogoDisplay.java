package com.jcimadras.jcimadras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Extras.Message;

public class LogoDisplay extends AppCompatActivity {
    private ProgressDialog pd;
    private String adminPass;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_display);

        startService(new Intent(this, Notify.class));

        findViewById(R.id.hideLL).setVisibility(View.VISIBLE);
        final Context context = getApplicationContext();
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            Message.tl(getApplicationContext(), "Please connect to Internet!!", getLayoutInflater(), findViewById(R.id.toastbg));
            findViewById(R.id.hideLL).setVisibility(View.GONE);
        } else {
            pd = new ProgressDialog(LogoDisplay.this);
            pd.setMessage("Please wait Initialising");
            pd.setCancelable(false);
            pd.show();
            if (!getPermission()) {
                Message.tl(getApplicationContext(), "Please grant storage permission to continue!!", getLayoutInflater(), findViewById(R.id.toastbg));
                LogoDisplay.this.finish();
                return;
            }
        }

        password = (EditText) findViewById(R.id.mainPass);
        Button go = (Button) findViewById(R.id.go);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                    Log.d("Sign In", "Success");
                else {
                    Message.L("Sign In", task.getException().toString());
                    Log.d("Sign In", "Fail");
                }
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("password/client");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adminPass = dataSnapshot.getValue(String.class);
                pd.dismiss();
                if (adminPass.equals("") || adminPass == null) {
                    Message.tl(getApplicationContext(), "Network Error!!", getLayoutInflater(), findViewById(R.id.toastbg));
                    findViewById(R.id.hideLL).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((password.getText() != null) && (password.getText().toString().equals(adminPass))) {
                    password.setText("");
                    LogoDisplay.this.finish();
                    startActivity(new Intent(LogoDisplay.this, com.jcimadras.jcimadras.MainActivity.class));
                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(LogoDisplay.this);
                    alertDialogBuilder.setMessage("Incorrect Password!!");
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });
                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

    }

    public boolean getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}

