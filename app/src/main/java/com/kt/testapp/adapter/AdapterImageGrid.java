package com.kt.testapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.kt.testapp.R;
import com.kt.testapp.view.SqureImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by kim-young-hyun on 24/01/2019.
 */

public class AdapterImageGrid extends RecyclerView.Adapter<AdapterImageGrid.ViewHolder> {

    public static HashMap<Integer,ObjectKey> OBJ_KEY = new HashMap<>();
    private ArrayList<String> urls;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context ctx;
    public static final int FOOTER_VIEW = 1;
    private final int CONNECTION_TIME_OUT = 5000; // 5초
    private boolean isShowFooter = true;
    private HashMap<Integer,Integer> heights = new HashMap<>();
    private HashMap<Integer,View> views = new HashMap<>();

    // data is passed into the constructor
    public AdapterImageGrid(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.urls = data;
        this.ctx= context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_image_footer, parent, false);
            FooterViewHolder vh = new FooterViewHolder(v);

            return vh;
        }
        View view = mInflater.inflate(R.layout.layout_grid_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // footer
        if(position == urls.size()) {
            FooterViewHolder vh = (FooterViewHolder) holder;
            if(isShowFooter)
                vh.getFooterView().setVisibility(View.VISIBLE);
            else
                vh.getFooterView().setVisibility(View.GONE);

            // footer의 경우 좌우 모든칸 다 차지 하도록 설정
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
        // not footer
        else {
            String url = urls.get(position);

            ObjectKey key = new ObjectKey(String.valueOf(url));
            OBJ_KEY.put(position,key);

            // 글라이드 옵션 set
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.timeout((int) CONNECTION_TIME_OUT);
//              requestOptions.placeholder(R.drawable.default_image);
            requestOptions.priority(Priority.HIGH);
            requestOptions.signature(key);
            requestOptions.skipMemoryCache(true);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            requestOptions.error(R.drawable.default_image);
            try {
                // 글라이드 호출
                Glide.with(ctx)
                        .load(url)
//                        .transition(GenericTransitionOptions.with(R.anim.dim_anim))
                        .apply(requestOptions)
                        .into(holder.ivPic);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            int height = 0;


            if(heights.get(position) != null) {
                height = heights.get(position);
            }
            else {
                height = getRandomIntInRange(700,150);
                heights.put(position,height);
            }
            holder.ivPic.getLayoutParams().height = height;

            views.put(position,holder.ivPic);
        }
    }

    protected int getRandomIntInRange(int max, int min){
        return new Random().nextInt((max-min)+min)+min;
    }


    @Override
    public int getItemCount() {
//        return urls.size();

        if (urls == null) {
            return 0;
        }

        if (urls.size() == 0) {
            //Return 1 here to show nothing
            return 0;
        }

        // Add extra view to show the footer view
        return urls.size() + 1;
    }

//    public void setData(ArrayList<String> data) {
//        this.urls = data;
//    }


    public class FooterViewHolder extends ViewHolder implements View.OnClickListener{

        View FooterView;
        public FooterViewHolder(View itemView) {
            super(itemView);
            FooterView = itemView.findViewById(R.id.pb);

        }

        public View getFooterView() {
            return FooterView;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SqureImageView ivPic;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPic = itemView.findViewById(R.id.iv_pic);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

//    private String getItem(int id) {
//        return urls.get(id);
//    }


    public boolean isShowFooter() {
        return isShowFooter;
    }

    public void setShowFooter(boolean showFooter) {
        isShowFooter = showFooter;
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == urls.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }


    public View getPositionView(int position) {
        if(views != null && views.get(position) != null) {
            return views.get(position);
        }
        else {
            return null;
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}