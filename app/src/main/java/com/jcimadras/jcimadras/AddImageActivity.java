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
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jcimadras.jcimadras.Adapter.ImagesViewAdapter;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.Images;

import java.util.ArrayList;

public class AddImageActivity extends AppCompatActivity implements View.OnClickListener {

    private final int SELECT_PHOTO = 2;
    private ArrayList<Images> ImagesList = new ArrayList<>();
    private ImagesViewAdapter adapter;
    private ProgressDialog pd;
    private EditText GalleryName;
    private StringBuffer paths = new StringBuffer();
    private StringBuilder pathName = new StringBuilder();
    private DatabaseReference dbref;
    private StorageReference stref;
    private int loop = 0;
    private String thumb;
    private String GalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button AddPhoto = (Button) findViewById(R.id.addPhoto);
        AddPhoto.setOnClickListener(this);


        GalleryName = (EditText) findViewById(R.id.galleryName);

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

    private void fetch() {
        if (ImagesList.size() == 0) {
            Message.ts(this, "Please add an image to continue!", getLayoutInflater(), findViewById(R.id.toastbg));
            return;
        }
        GalName = GalleryName.getText().toString();
        if (GalName.equals("")) {
            Message.ts(this, "Please enter Gallery name", getLayoutInflater(), findViewById(R.id.toastbg));
            return;
        }
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading photos");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();

        dbref = FirebaseDatabase.getInstance().getReference("Gallery");
        stref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://jcicentral-f8915.appspot.com/Gallery/Images/");
        final Images i = ImagesList.get(0);
        String[] key = dbref.push().toString().split("/");
        GalName += "," + key[key.length - 1];

        StorageReference GalleryRef = stref.child(GalName + "/" + i.getImageuri().getLastPathSegment());
        UploadTask uploadTask = GalleryRef.putFile(i.getImageuri());
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    pathName.append(i.getImageuri().getLastPathSegment()).append(",");
                    paths.append(task.getResult().getDownloadUrl());
                    thumb = paths.toString();
                    paths.append(",");
                    if (ImagesList.size() == 1) {
                        Images finalImage = new Images(GalName, paths.toString(), pathName.toString(), thumb);
                        dbref.child(GalName).setValue(finalImage);
                        pd.dismiss();
                        Message.ts(AddImageActivity.this, "Uploaded Successfully", getLayoutInflater(), findViewById(R.id.toastbg));
                        AddImageActivity.this.finish();
                    }else{
                        if (ImagesList.size() > 1) {
                            Boolean first = true;
                            for (final Images images : ImagesList) {
                                if (first) {
                                    ++loop;
                                    first = false;
                                    continue;
                                }
                                StorageReference Gallery = stref.child(GalName + "/" + images.getImageuri().getLastPathSegment());
                                UploadTask upload = Gallery.putFile(images.getImageuri());
                                upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            ++loop;
                                            paths.append(task.getResult().getDownloadUrl()).append(",");
                                            pathName.append(images.getImageuri().getLastPathSegment()).append(",");
                                            if (loop == ImagesList.size()) {
                                                Images finalImage = new Images(GalName, paths.toString(), pathName.toString(), thumb);
                                                dbref.child(GalName).setValue(finalImage);
                                                pd.dismiss();
                                                Message.ts(AddImageActivity.this, "Uploaded Successfully", getLayoutInflater(), findViewById(R.id.toastbg));
                                                AddImageActivity.this.finish();
                                            }
                                        } else {
                                            Message.ts(AddImageActivity.this, "Error!!", getLayoutInflater(), findViewById(R.id.toastbg));
                                            stref.child(GalName).delete();
                                            dbref.child(GalName).removeValue();
                                            pd.dismiss();
                                            AddImageActivity.this.finish();
                                        }
                                    }
                                });
                            }
                        }
                    }
                } else {
                    Message.ts(AddImageActivity.this, "Error!!", getLayoutInflater(), findViewById(R.id.toastbg));
                    pd.dismiss();
                    AddImageActivity.this.finish();
                }
            }
        });

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


}