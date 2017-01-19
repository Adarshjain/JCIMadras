package com.jcimadras.jcimadras;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jcimadras.jcimadras.Adapter.FullscreenImageview;

import java.util.ArrayList;

public class ImageviewFull extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_full);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        ArrayList<String> paths = b.getStringArrayList("paths");
        int position = b.getInt("position");
        FullscreenImageview adapter = new FullscreenImageview(ImageviewFull.this, paths, b.getString("stRef"));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }
}
