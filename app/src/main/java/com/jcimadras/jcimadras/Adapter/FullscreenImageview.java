package com.jcimadras.jcimadras.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jcimadras.jcimadras.R;
import com.jcimadras.jcimadras.ZoomImageview;

import java.util.ArrayList;

public class FullscreenImageview extends PagerAdapter {
    private Activity mActivity;
    private ArrayList<String> mPaths = new ArrayList<>();
    private String stRef;
    private StorageReference storRef;
    private ProgressBar progressBar;

    public FullscreenImageview(Activity activity, ArrayList<String> paths, String stRef) {
        this.mActivity = activity;
        this.mPaths = paths;
        this.stRef = stRef;
        storRef = FirebaseStorage.getInstance().getReference();
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
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        Glide.with(mActivity)
                .using(new FirebaseImageLoader())
                .load(storRef.child(stRef).child(mPaths.get(position)))
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.error)
                .crossFade()
                .into(NetView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
