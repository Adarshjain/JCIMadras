package com.jcimadras.jcimadras.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
                        .error(R.drawable.error)
                        .crossFade()
                        .into(PresidentPic);
                Glide.with(AboutUsFragment.this)
                        .load(details.getLadUrl())
                        .error(R.drawable.error)
                        .crossFade()
                        .into(LadiesPic);
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
                String memberUrl = "https://firebasestorage.googleapis.com/v0/b/jcicentral-f8915.appspot.com/o/Files%2FNUMBER.xlsx?alt=media&token=6dff8fe4-b09e-4a2f-9de6-50c88715f8a8";
                Intent member = new Intent(Intent.ACTION_VIEW);
                member.setData(Uri.parse(memberUrl));
                startActivity(member);
                break;
            case R.id.annivDownload:
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
