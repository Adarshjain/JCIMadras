package com.jcimadras.jcimadras.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Pojo.HeadDetails;
import com.jcimadras.jcimadras.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUsFragment extends Fragment implements View.OnClickListener {


    private CircleImageView PresidentPic, LadiesPic;
    private TextView PresidentName, LadiesName, Year1, Year2;
    private String Mem, MemAnniv;
    private ProgressBar pProgress, lProgress;

    //<editor-fold defaultstate="collapsed" desc="unwanted">
    public AboutUsFragment() {
    }
    //</editor-fold>

    public static AboutUsFragment newInstance() {
        return new AboutUsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        PresidentPic = (CircleImageView) view.findViewById(R.id.presidentPic);
        LadiesPic = (CircleImageView) view.findViewById(R.id.ladiesPic);
        pProgress = (ProgressBar) view.findViewById(R.id.pprogress);
        lProgress = (ProgressBar) view.findViewById(R.id.lprogress);
        PresidentName = (TextView) view.findViewById(R.id.presidentName);
        LadiesName = (TextView) view.findViewById(R.id.ladiesName);
        Year1 = (TextView) view.findViewById(R.id.year1);
        Year2 = (TextView) view.findViewById(R.id.year2);
        view.findViewById(R.id.memberDownload).setOnClickListener(this);
        view.findViewById(R.id.annivDownload).setOnClickListener(this);
        view.findViewById(R.id.phone).setOnClickListener(this);
        view.findViewById(R.id.mail).setOnClickListener(this);
        view.findViewById(R.id.fb).setOnClickListener(this);
        view.findViewById(R.id.insta).setOnClickListener(this);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("HeadInfo");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HeadDetails details = dataSnapshot.getValue(HeadDetails.class);
                PresidentName.setText(details.getPresidentName());
                LadiesName.setText(details.getLadiesName());
                Year1.setText(details.getYear());
                Year2.setText(details.getYear());
                Glide.with(AboutUsFragment.this)
                        .load(details.getPreUrl())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                pProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                pProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .error(R.drawable.error)
                        .crossFade()
                        .into(PresidentPic);
                Glide.with(AboutUsFragment.this)
                        .load(details.getLadUrl())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                lProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                lProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .error(R.drawable.error)
                        .crossFade()
                        .into(LadiesPic);
                Mem = details.getMember();
                MemAnniv = details.getMemAnniversary();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.memberDownload:
                Intent member = new Intent(Intent.ACTION_VIEW);
                member.setData(Uri.parse(Mem));
                startActivity(member);
                break;
            case R.id.annivDownload:
                Intent memberAnniv = new Intent(Intent.ACTION_VIEW);
                memberAnniv.setData(Uri.parse(MemAnniv));
                startActivity(memberAnniv);
                break;
            case R.id.phone:
                Intent phone = new Intent(Intent.ACTION_DIAL);
                phone.setData(Uri.parse("tel:+91868284784"));
                startActivity(phone);
                break;
            case R.id.mail:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:adarshjain9628@gmail.com?subject=Project Request&body=Hey there GeekyAdarsh! We have an amazing idea to share! Please contact us back ASAP!");
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.fb:
                String fburl = "https://www.facebook.com/adarshcooool007";
                Intent fb = new Intent(Intent.ACTION_VIEW);
                fb.setData(Uri.parse(fburl));
                startActivity(fb);
                break;
            case R.id.insta:
                String instaurl = "https://www.instagram.com/geekyadarsh/";
                Intent insta = new Intent(Intent.ACTION_VIEW);
                insta.setData(Uri.parse(instaurl));
                startActivity(insta);
                break;
        }
    }
}
