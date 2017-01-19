package com.jcimadras.jcimadras;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.Images;
import com.jcimadras.jcimadras.Volley.VolleySingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

    private int position;
    private Context con;
    private LayoutInflater li;
    private View vi;
    private ArrayList<String> ImagesList;
    private VolleySingleton mVolleySingleton = VolleySingleton.getInstance();
    private ImageLoader mImageLoader = mVolleySingleton.getImageLoader();
    private ProgressDialog pd;
    private String Key;

    public MyMenuItemClickListener(int positon, Context con, LayoutInflater li, View vi, ArrayList<String> imagesList, String key) {
        this.position = positon;
        this.con = con;
        this.li = li;
        this.vi = vi;
        this.ImagesList = imagesList;
        this.Key = key;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download:
                download();
                break;
            case R.id.delete:
                delete();
                break;
        }
        return false;
    }

    private void delete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
        alertDialogBuilder.setMessage("Are you sure?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Gallery/" + Key);
                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Images i = dataSnapshot.getValue(Images.class);
                                String[] pathName = i.getImagePathName().split(",");
                                if (pathName.length == 1) {
                                    deleteWhole();
                                } else {
                                    StorageReference stref = FirebaseStorage.getInstance().getReference().child("Gallery/Images/" + Key + "/" + pathName[position]);
                                    stref.delete();
                                    ArrayList<String> ImagePaths = new ArrayList<>(Arrays.asList(i.getImagePathName().split(",")));
                                    ImagePaths.remove(position);
                                    StringBuilder b = new StringBuilder();
                                    for (String image : ImagePaths) {
                                        b.append(image).append(",");
                                    }
                                    ArrayList<String> ImageUrl = new ArrayList<>(Arrays.asList(i.getImageUrl().split(",")));
                                    ImageUrl.remove(position);
                                    StringBuilder b1 = new StringBuilder();
                                    for (String image : ImageUrl) {
                                        b1.append(image).append(",");
                                    }
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("imagePathName", b.toString());
                                    childUpdates.put("imageUrl", b1.toString());
                                    if (position == 0) childUpdates.put("thumb", ImageUrl.get(0));
                                    dbref.updateChildren(childUpdates);
                                    Message.ts(con, "Successfully Deleted!", li, vi);
                                    Activity a = (Activity) con;
                                    a.finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
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

    private void deleteWhole() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
        alertDialogBuilder.setMessage("Album will be deleted! Are you sure?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Gallery/" + Key);
                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Images i = dataSnapshot.getValue(Images.class);
                                String[] pathName = i.getImagePathName().split(",");
                                StorageReference stref = FirebaseStorage.getInstance().getReference().child("Gallery/Images/" + Key);
                                for (String path : pathName) {
                                    stref.child(path).delete();
                                }
                                dbref.removeValue();
                                Message.ts(con, "Successfully Deleted!", li, vi);
                                Activity a = (Activity) con;
                                a.finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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

    private void download() {
        String url = ImagesList.get(position);
        pd = new ProgressDialog(con);
        pd.setMessage("Downloading...");
        pd.setCancelable(false);
        pd.show();
        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    try {
                        if (isStoragePermissionGranted()) {
                            String path = con.getFilesDir().toString();
                            OutputStream fOut;
                            File file = new File(path, "" + System.currentTimeMillis() / 1000);
                            fOut = new FileOutputStream(file);
                            Bitmap pictureBitmap = response.getBitmap();
                            pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            MediaStore.Images.Media.insertImage(con.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                            pd.dismiss();
                            Message.ts(con, "Downloaded to Pictures folder", li, vi);
                        } else {
                            pd.dismiss();
                            Message.ts(con, "Please grant permission!!", li, vi);
                        }
                    } catch (Exception f) {
                        pd.dismiss();
                        Message.ts(con, "Error!!", li, vi);
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (con.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions((Activity) con, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
