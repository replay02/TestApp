package com.kt.testapp.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.kt.testapp.R;

import java.util.ArrayList;

public class PictureAdapter extends PagerAdapter {

    private Context ctx;
    private PhotoView[] photoView = null;
    private ArrayList<String> url = null;

    public PictureAdapter(Context c, ArrayList<String> urls) {
        super();
        ctx = c;
        url = urls;

        if(url != null)
            photoView = new PhotoView[url.size()];
    }

    @Override
    public int getCount() {
        if(url != null) {
            return url.size();
        }
        else {
            return 1;
        }
    }

    public void setInitPicture(int position) {
        if(photoView != null && photoView[position] != null) {
            setImages(photoView[position], position);
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup view = (ViewGroup) View.inflate(ctx, R.layout.viewpager_image_detail, null);
        photoView[position] = (PhotoView) view.findViewById(R.id.images);
        setImages(photoView[position], position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void setImages(final PhotoView thumb1, int position) {

        String s = "";
        if(url != null && url.size() > 0) {
            s = url.get(position);
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.priority(Priority.HIGH);
        requestOptions.signature(AdapterImageGrid.OBJ_KEY.get(position));
        requestOptions.skipMemoryCache(true);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        requestOptions.error(R.drawable.default_image);
        try {
            // 글라이드 호출
            Glide.with(ctx)
                    .load(s)
//                    .transition(GenericTransitionOptions.with(R.anim.dim_anim))
                    .apply(requestOptions)
                    .into(thumb1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View pager, Object obj) {
        boolean retVal = false;
        if (pager == (View) obj) {
            retVal = true;
        }
        return retVal;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    @Override
    public void finishUpdate(View arg0) {
    }
}
