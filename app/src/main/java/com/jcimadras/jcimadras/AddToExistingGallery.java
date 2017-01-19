package com.jcimadras.jcimadras;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jcimadras.jcimadras.Adapter.ImagesViewAdapter;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.Images;

import java.util.ArrayList;

public class AddToExistingGallery extends AppCompatActivity implements View.OnClickListener {

    private final int SELECT_PHOTO = 2;
    private String Key;
    private ImagesViewAdapter adapter;
    private ArrayList<Images> ImagesList = new ArrayList<>();
    private ProgressDialog pd;
    private DatabaseReference dbref;
    private StorageReference stref;
    private Images image;
    private StringBuffer paths = new StringBuffer();
    private StringBuilder pathName = new StringBuilder();
    private int loop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_existing_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        Key = b.getString("key");

        dbref = FirebaseDatabase.getInstance().getReference("Gallery/" + Key);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                image = dataSnapshot.getValue(Images.class);
                getSupportActionBar().setTitle(image.getDesc().split(",")[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button AddPhoto = (Button) findViewById(R.id.addPhoto);
        AddPhoto.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imageViews);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ImagesViewAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        ImagesList = adapter.getImagesList();
                        ImagesList.add(new Images(uri));
                        adapter.setImagesList(ImagesList);
                    }
                } else {
                    Message.ts(this, "You haven't picked Image", getLayoutInflater(), findViewById(R.id.toastbg));
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                fetch();
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPhoto:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
        }
    }

    private void fetch() {
        if (ImagesList.size() == 0) {
            Message.ts(this, "Please add an image to continue!", getLayoutInflater(), findViewById(R.id.toastbg));
            return;
        }
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading photos");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();

        stref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://jcicentral-f8915.appspot.com/Gallery/Images/" + Key);

        for (final Images images : ImagesList) {
            StorageReference Gallery = stref.child(images.getImageuri().getLastPathSegment());
            UploadTask upload = Gallery.putFile(images.getImageuri());
            upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        ++loop;
                        paths.append(task.getResult().getDownloadUrl()).append(",");
                        pathName.append(images.getImageuri().getLastPathSegment()).append(",");
                        if (loop == ImagesList.size()) {
                            image.setImageUrl(image.getImageUrl() + paths);
                            image.setImagePathName(image.getImagePathName() + pathName);
                            dbref.setValue(image);
                            pd.dismiss();
                            Message.ts(AddToExistingGallery.this, "Uploaded Successfully", getLayoutInflater(), findViewById(R.id.toastbg));
                            AddToExistingGallery.this.finish();
                        }
                    } else {
                        Message.ts(AddToExistingGallery.this, "Error!!", getLayoutInflater(), findViewById(R.id.toastbg));
                        stref.delete();
                        dbref.removeValue();
                        pd.dismiss();
                        AddToExistingGallery.this.finish();
                    }
                }
            });
        }
    }
}
