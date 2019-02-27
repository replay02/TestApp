package com.kt.testapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.kt.testapp.R;
import com.kt.testapp.adapter.AdapterImageGrid;
import com.kt.testapp.inf.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by kim-young-hyun on 22/01/2019.
 */

public class GridImageActivity extends BaseActivity implements EventListener{
    private ArrayList<String> data = new ArrayList<>();
    private AdapterImageGrid adapter;
    private boolean isListEnd = false;

    private final int NUM_OF_COLUMN = 3; // 화면 당 열 갯수
    private int ROW_CNT = 0;  // 화면 당 행 갯수
    private final int MAX_CONTENTS_CNT = 29;  // 화면 당 행 갯수

    private GetImageTask task;

    private Toolbar toolbar;


    private final String[] dummyData = new String[] {
            "https://cdn.pixabay.com/photo/2018/11/29/19/29/autumn-3846345__480.jpg",
            "https://cdn.pixabay.com/photo/2017/05/05/16/57/buzzard-2287699__480.jpg",
            "https://cdn.pixabay.com/photo/2018/12/25/21/45/crystal-ball-photography-3894871__480.jpg",
            "https://cdn.pixabay.com/photo/2018/12/15/02/53/flower-3876195__480.jpg",
            "https://cdn.pixabay.com/photo/2018/11/23/14/19/forest-3833973__480.jpg",
            "https://cdn.pixabay.com/photo/2018/11/29/21/19/hamburg-3846525__480.jpg",
            "https://cdn.pixabay.com/photo/2018/11/04/20/21/harley-davidson-3794909__480.jpg",
            "https://cdn.pixabay.com/photo/2018/11/11/16/51/ibis-3809147__480.jpg",
            "https://cdn.pixabay.com/photo/2018/07/16/13/17/kiss-3541905__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/28/16/11/landscape-3779159__480.jpg",
            "https://cdn.pixabay.com/photo/2018/12/09/14/44/leaf-3865014__480.jpg",
            "https://cdn.pixabay.com/photo/2018/11/24/02/05/lichterkette-3834926__480.jpg",
            "https://cdn.pixabay.com/photo/2019/01/05/17/05/man-3915438__480.jpg",
            "https://cdn.pixabay.com/photo/2018/08/06/16/30/mushroom-3587888__480.jpg",
            "https://cdn.pixabay.com/photo/2018/12/16/18/12/open-fire-3879031__480.jpg",
            "https://cdn.pixabay.com/photo/2018/12/29/23/49/rays-3902368__480.jpg",
            "https://cdn.pixabay.com/photo/2018/12/04/22/38/road-3856796__480.jpg",
            "https://cdn.pixabay.com/photo/2018/11/17/22/15/tree-3822149__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/21/21/28/autumn-3763897__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/14/13/01/background-3746423__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/18/23/53/cactus-3757657__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/13/17/31/fall-leaves-3744649__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/07/11/49/fallow-deer-3729821__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/12/22/08/flamingo-3743094__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/22/11/58/grass-3765172__480.jpg",
            "https://cdn.pixabay.com/photo/2018/10/11/23/08/hahn-3741129__480.jpg",
            "https://cdn.pixabay.com/photo/2018/09/06/23/37/hydrangea-3659614__480.jpg",
            "https://cdn.pixabay.com/photo/2018/12/28/01/34/rum-3898745__480.jpg"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_image);

        setToolbar();

        // grid 형태의 recyclerview set
        final RecyclerView recyclerView = findViewById(R.id.rvGridImage);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(NUM_OF_COLUMN, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        // footer의 경우 3칸 차지 하도록 설정
//        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                switch(adapter.getItemViewType(position)){
//                    case AdapterImageGrid.FOOTER_VIEW:
//                        return NUM_OF_COLUMN;
//                    default:
//                        return 1;
//                }
//            }
//        });

        // 스크롤 변경 사항 리스너
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 스크롤 최하단 도달 시
                if (!recyclerView.canScrollVertically(1)) {
                    if(isListEnd)
                        return;
                    if(data.size() >= MAX_CONTENTS_CNT) {
                        isListEnd = true;
                        Toast.makeText(GridImageActivity.this, getString(R.string.no_more_contents), Toast.LENGTH_SHORT).show();
                        adapter.setShowFooter(false);
                        int refreshCnt = ((data.size() % NUM_OF_COLUMN)==0 ? 4 : data.size() % NUM_OF_COLUMN + 1);
                        adapter.notifyItemRangeChanged(data.size() + 1 - refreshCnt, refreshCnt);
//                        adapter.notifyDataSetChanged();
                    }
                    else {
                        if(task == null) {
                            task = new GetImageTask(GridImageActivity.this,ROW_CNT,NUM_OF_COLUMN, data, false,
                                    dummyData,GridImageActivity.this);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                }
            }
        });
        adapter = new AdapterImageGrid(this, data);
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new AdapterImageGrid.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(GridImageActivity.this, ImageDetailActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("url",data);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Pair<View, String> pair1 = Pair.create(adapter.getPositionView(position), "station");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(GridImageActivity.this, pair1);
                    ActivityCompat.startActivity(GridImageActivity.this,intent,options.toBundle());
                }
                else {
                    GridImageActivity.this.startActivity(intent);
                }
            }
        });

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int height = recyclerView.getHeight();
                int width = recyclerView.getWidth() - (int)dp2px(getResources(),6.0f);
                int widthOneColumn = width / NUM_OF_COLUMN;
                ROW_CNT = (height / widthOneColumn) + 1;
                task = new GetImageTask(GridImageActivity.this,ROW_CNT,NUM_OF_COLUMN, data, true,
                        dummyData,GridImageActivity.this);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private void setTopImage() {
        int rnd = new Random().nextInt(dummyData.length);


        if(AdapterImageGrid.OBJ_KEY.get(rnd)==null) {
            ObjectKey key = new ObjectKey(String.valueOf(dummyData[rnd]));
            AdapterImageGrid.OBJ_KEY.put(rnd,key);
        }
        ImageView topImage = findViewById(R.id.topImage);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.priority(Priority.HIGH);
        requestOptions.signature(AdapterImageGrid.OBJ_KEY.get(rnd));
        requestOptions.skipMemoryCache(true);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        requestOptions.error(R.drawable.default_image);
        try {
            // 글라이드 호출
            Glide.with(this)
                    .load(dummyData[rnd])
//                    .transition(GenericTransitionOptions.with(R.anim.dim_anim))
                    .apply(requestOptions)
                    .into(topImage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.image_grid));

        //Toolbar 왼쪽에 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.xxx);

        setTopImage();

    }


    public float dp2px(Resources resources, float dp) {
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    @Override
    public void onEvent(Object obj) {
        if(adapter != null) {
            adapter.notifyItemRangeChanged(data.size(), ROW_CNT * NUM_OF_COLUMN + 1);
        }
        task = null;
    }

    // 이미지 url을
    public static class GetImageTask extends AsyncTask<String,Void,Boolean> {

        private EventListener listener;
        private BaseActivity activity;
        private ArrayList<String> data;

        private int cnt = 0;
        private int numOfColumn = 0;

        private boolean isProgressShow = true;

        private String[] dummyData;

        public GetImageTask(BaseActivity activity,int cnt, int numOfColumn, ArrayList<String> data,
                            boolean isProgressShow, String[] dummyData, EventListener listener) {
            this.listener = listener;
            this.activity = activity;
            this.data = data;
            this.cnt = cnt;
            this.numOfColumn = numOfColumn;
            this.isProgressShow = isProgressShow;
            this.dummyData = dummyData;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isProgressShow)
                activity.showProgress();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < cnt*numOfColumn; i++) {
                int rnd = new Random().nextInt(dummyData.length);
                data.add(dummyData[rnd]);
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            listener.onEvent(s);
            if(isProgressShow)
                activity.hideProgress();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:


                break;
            case android.R.id.home:

                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

