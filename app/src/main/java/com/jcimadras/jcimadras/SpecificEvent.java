package com.jcimadras.jcimadras;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.Pojo.SpecificEventDetails;
import com.jcimadras.jcimadras.Volley.VolleySingleton;

import java.util.ArrayList;
import java.util.Arrays;

public class SpecificEvent extends AppCompatActivity implements View.OnClickListener {

    private TextView EventName, EventDesc, Date, Total;
    private CardView Map, Images;
    private NetworkImageView MapView;
    private LinearLayout Imagesll;
    private Button register;

    private ArrayList<String> imgPaths, imgPathName;
    private int i;
    private String key;
    private int limit;

    private VolleySingleton mVolleySingleton = VolleySingleton.getInstance();
    private ImageLoader mImageLoader = mVolleySingleton.getImageLoader();
    private ArrayList<ImageView> ivs = new ArrayList<>();
    private ArrayList<Target<GlideDrawable>> glides = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_event);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (toolbar != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            EventName = (TextView) findViewById(R.id.eventNameDisp);
            EventDesc = (TextView) findViewById(R.id.eventDescDisp);
            Total = (TextView) findViewById(R.id.total);
            Date = (TextView) findViewById(R.id.dateDisp);
            Map = (CardView) findViewById(R.id.staticMapCard);
            Images = (CardView) findViewById(R.id.imageCard);
            MapView = (NetworkImageView) findViewById(R.id.mapView);
            Imagesll = (LinearLayout) findViewById(R.id.imagesll);
            Button ViewParticipants = (Button) findViewById(R.id.viewParticipants);
            ViewParticipants.setOnClickListener(this);
            register = (Button) findViewById(R.id.register);
            register.setOnClickListener(this);

            Bundle b = getIntent().getExtras();
            key = b.getString("key");
            if (b.getBoolean("SeatFull"))
                register.setVisibility(View.INVISIBLE);
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events/" + key);
            DatabaseReference partRef = FirebaseDatabase.getInstance().getReference("Participants/" + key);

            ValueEventListener data = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot == null || dataSnapshot.getChildren() == null || dataSnapshot.getValue() == null) {
                        SpecificEvent.this.finish();
                    } else {
                        final SpecificEventDetails details = dataSnapshot.getValue(SpecificEventDetails.class);

                        limit = details.getLimit();
                        if (limit != -1 && limit <= details.getTotal())
                            register.setVisibility(View.GONE);
                        else register.setVisibility(View.VISIBLE);
                        EventName.setText(details.getEventName());
                        EventDesc.setText(details.getEventDesc());
                        if (details.getTotal() == 0)
                            Total.setVisibility(View.GONE);
                        else
                            Total.setText("Total Participants : " + details.getTotal());
                        Date.setText(details.getDate());
                        if (details.getCoordinates() != null) {
                            Map.setVisibility(View.VISIBLE);
                            final String[] ltlng = details.getCoordinates().split(",");
                            String url = "http://maps.google.com/maps/api/staticmap?center=" + ltlng[0] + "," + ltlng[1] + "&zoom=17&size=640x640&sensor=false";
                            MapView.setImageUrl(url, mImageLoader);
                            MapView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String geoUri = "http://maps.google.com/maps?q=loc:" + ltlng[0] + "," + ltlng[1] + " (" + details.getEventName() + ")";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                                    SpecificEvent.this.startActivity(intent);
                                }
                            });
                        } else Map.setVisibility(View.GONE);

                        if (details.getPhotoAdded()) {
                            StorageReference stref = FirebaseStorage.getInstance().getReference().child("Gallery/Events/" + key);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(300), dpToPx(300));
                            params.setMarginStart(dpToPx(8));
                            imgPaths = new ArrayList<>(Arrays.asList(details.getImgPath().split(",")));
                            imgPathName = new ArrayList<>(Arrays.asList(details.getImgPathName().split(",")));
                            Images.setVisibility(View.VISIBLE);
                            Imagesll.removeAllViews();
                            for (i = 0; i < imgPaths.size(); i++) {
                                FrameLayout ll = new FrameLayout(SpecificEvent.this);

                                final ProgressBar pg = new ProgressBar(SpecificEvent.this, null, android.R.attr.progressBarStyleLarge);
                                pg.setIndeterminate(true);
                                pg.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(100, 100);
                                param.addRule(RelativeLayout.CENTER_IN_PARENT);

                                ImageView iv = new ImageView(SpecificEvent.this);
                                iv.setLayoutParams(params);
                                iv.setAdjustViewBounds(true);
                                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                try {
                                    glides.add(Glide.with(SpecificEvent.this)
                                            .using(new FirebaseImageLoader())
                                            .load(stref.child(imgPathName.get(i)))
                                            .listener(new RequestListener<StorageReference, GlideDrawable>() {
                                                @Override
                                                public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                    pg.setVisibility(View.GONE);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                    pg.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .error(R.drawable.error)
                                            .crossFade()
                                            .into(iv));
                                } catch (Exception ignored) {
                                    FirebaseCrash.report(ignored);
                                    FirebaseCrash.log("Don't Know y!");
                                }
                                ivs.add(iv);
                                final int pos = i;
                                iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(SpecificEvent.this, ImageviewFull.class);
                                        Bundle b = new Bundle();
                                        b.putStringArrayList("paths", imgPathName);
                                        b.putInt("position", pos);
                                        b.putString("stRef", "Gallery/Events/" + key);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                ll.addView(iv);
                                ll.addView(pg, param);
                                Imagesll.addView(ll);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Message.ts(SpecificEvent.this, "Error occurred while loadings!", getLayoutInflater(), findViewById(R.id.toastbg));
                }
            };
            eventRef.addValueEventListener(data);
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                register();
                break;
            case R.id.viewParticipants:
                Intent intent = new Intent(this, ViewParticipants.class);
                intent.putExtra("key", key);
                startActivity(intent);
                break;
        }
    }

    private void register() {
        Intent i = new Intent(this, RegisterEvent.class);
        i.putExtra("key", key);
        startActivity(i);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (Target<GlideDrawable> gli : glides) {
            Glide.clear(gli);
        }
        for (ImageView iv : ivs) {
            Glide.clear(iv);
        }

    }

}

