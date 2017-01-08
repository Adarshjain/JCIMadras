package com.jcimadras.jcimadras;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Fragments.AboutUsFragment;
import com.jcimadras.jcimadras.Fragments.EventFragment;
import com.jcimadras.jcimadras.Fragments.GalleryFragment;
import com.jcimadras.jcimadras.Pojo.AnniversaryDetails;
import com.jcimadras.jcimadras.tabs.SlidingTabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);


        DatabaseReference AnnivRef = FirebaseDatabase.getInstance().getReference("Anniversary");
        AnnivRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean haiKya = false;
                StringBuilder Names = new StringBuilder();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    AnniversaryDetails details = ds.getValue(AnniversaryDetails.class);
                    dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    String Today = dateFormatter.format(new Date());
                    if (details.getDate().equals(Today)) {
                        haiKya = true;
                        Names.append("Jc Name : ").append(details.getJCName()).append("\n");
                        Names.append("Jcrt Name : ").append(details.getJcrtName()).append("\n");
                        Names.append("Date : ").append(details.getDate()).append("\n\n");
//                        Name1.setText("Jc Name : " + details.getJCName());
//                        TextView Name2 = (TextView) dialog.findViewById(R.id.avJcrtName);
//                        Name2.setText("Jcrt Name : " + details.getJcrtName());
//                        TextView date = (TextView) dialog.findViewById(R.id.avDate);
//                        date.setText("Date : " + details.getDate());
                    }
                }
                if (haiKya) {
                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.anniversary);
                    TextView Name1 = (TextView) dialog.findViewById(R.id.avJcName);
                    Name1.setText(Names.toString());
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ViewPager myViewPager = (ViewPager) findViewById(R.id.myViewPager);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, R.id.tabText);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorBlue));
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.white));
        myViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout.setViewPager(myViewPager);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        int icons[] = {R.drawable.ic_event_note_black_24dp, R.drawable.ic_insert_photo_black_24dp, R.drawable.ic_error_black_24dp};
        String[] tab = {"Events", "Gallery", "About Us"};

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = EventFragment.newInstance();
                    break;
                case 1:
                    fragment = GalleryFragment.newInstance();
                    break;
                case 2:
                    fragment = AboutUsFragment.newInstance();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = getResources().getDrawable(icons[position]);
            drawable.setBounds(0, 0, 54, 54);
            ImageSpan imageSpan = new ImageSpan(drawable);
            SpannableString spannableString = new SpannableString(tab[position]);
            spannableString.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }
}
