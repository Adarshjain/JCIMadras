package com.jcimadras.jcimadras.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.jcimadras.jcimadras.R;
import com.jcimadras.jcimadras.Volley.VolleySingleton;
import com.jcimadras.jcimadras.ZoomImageview;

import java.util.ArrayList;


public class FullscreenImageview extends PagerAdapter {
    private Activity mActivity;
    private ArrayList<String> mPaths = new ArrayList<>();
    private VolleySingleton mVolleySingleton = VolleySingleton.getInstance();
    private ImageLoader mImageLoader = mVolleySingleton.getImageLoader();

    public FullscreenImageview(Activity activity, ArrayList<String> paths) {
        this.mActivity = activity;
        this.mPaths = paths;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageview NetView;
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.imageview_adapter, container, false);
        NetView = (ZoomImageview) view.findViewById(R.id.net_imageview);
        NetView.setImageUrl(mPaths.get(position), mImageLoader);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
