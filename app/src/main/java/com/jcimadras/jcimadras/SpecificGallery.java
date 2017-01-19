package com.jcimadras.jcimadras;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Adapter.ImagesViewUrlAdapter;
import com.jcimadras.jcimadras.Extras.Message;

import java.util.ArrayList;
import java.util.Arrays;

public class SpecificGallery extends AppCompatActivity {

    private ArrayList<String> ImagePaths, ImagePathName;
    private String Key;
    private ProgressDialog pd;
    private Boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading..");
        pd.show();

        Bundle b = getIntent().getExtras();
        Key = b.getString("desc");
        getSupportActionBar().setTitle(Key.split(",")[0]);

        String path = b.getString("imageUrl");
        if (path == null) {
            Message.ts(this, "Error!!", getLayoutInflater(), findViewById(R.id.toastbg));
            this.finish();
        } else ImagePaths = new ArrayList<>(Arrays.asList(path.split(",")));

        String pathName = b.getString("imgPathName");
        if (pathName == null) {
            Message.ts(this, "Error!!", getLayoutInflater(), findViewById(R.id.toastbg));
            this.finish();
        } else ImagePathName = new ArrayList<>(Arrays.asList(pathName.split(",")));

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Gallery/" + Key + "/imageUrl");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (pd.isShowing()) pd.dismiss();
                if (first) first = false;
                else {
                    Message.ts(SpecificGallery.this, "Data Changed!Please Reopen Album", getLayoutInflater(), findViewById(R.id.toastbg));
                    SpecificGallery.this.finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.galleryRecycler);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        ImagesViewUrlAdapter adapter = new ImagesViewUrlAdapter(this, Key, findViewById(R.id.toastbg));
        adapter.setImagesList(ImagePaths, ImagePathName);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_add_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                this.finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent i = new Intent(SpecificGallery.this, AddToExistingGallery.class);
                i.putExtra("key", Key);
                startActivityForResult(i, 1);
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

//    private void delete() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("Are you sure?");
//        alertDialogBuilder.setPositiveButton("yes",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Gallery/" + Key);
//                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Images i = dataSnapshot.getValue(Images.class);
//                                String[] pathName = i.getImagePathName().split(",");
//                                StorageReference stref = FirebaseStorage.getInstance().getReference().child("Gallery/Images/" + Key);
//                                for (String path : pathName) {
//                                    stref.child(path).delete();
//                                }
//                                dbref.removeValue();
//                                Message.ts(SpecificGallery.this, "Successfully Deleted!", getLayoutInflater(), findViewById(R.id.toastbg));
//                                SpecificGallery.this.finish();
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                });
//
//        alertDialogBuilder.setNegativeButton("No",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }

}
