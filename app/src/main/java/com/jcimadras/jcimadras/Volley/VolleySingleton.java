package com.jcimadras.jcimadras.Volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import static com.jcimadras.jcimadras.Volley.MyVolleySingleton.getContext;


public class VolleySingleton {
    private static VolleySingleton VSInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(getContext());

        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(
                LruBitmapCache.getCacheSize(getContext())));
    }

    public static VolleySingleton getInstance() {
        if (VSInstance == null) {
            VSInstance = new VolleySingleton();
        }
        return VSInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
