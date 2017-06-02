package com.ingenic.recyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;

    private int mOrientation = OrientationHelper.VERTICAL;
    private int mLayoutManagerFlag = 0;
    private ArrayList<String> mDatas = new ArrayList<String>();
    private ArrayList<Integer> mHeights = new ArrayList<Integer>();

    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: " + savedInstanceState);
        restoreInstanceState(savedInstanceState);

        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        initLayoutManager();
        initOrResetAdapter();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDivider(this));
    }

    private void initOrResetAdapter() {
        mRecyclerAdapter = new RecyclerAdapter(this, mDatas, mHeights, mOrientation);
        if (mDatas.size() == 0 && mHeights.size() == 0) {
            setAdapterData(mLayoutManagerFlag == 2);
        }
        mRecyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, "onItemClick: " + position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "onItemLongClick: " + position,
                        Toast.LENGTH_SHORT).show();

                mRecyclerAdapter.removeData(position);
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private void initLayoutManager() {
        if (mLinearLayoutManager == null)
            mLinearLayoutManager = new LinearLayoutManager(this, mOrientation, false);

        if (mGridLayoutManager == null)
            mGridLayoutManager = new GridLayoutManager(this, 4, mOrientation, false);

        if (mStaggeredGridLayoutManager == null)
            mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(4, mOrientation);

        switch (mLayoutManagerFlag) {
            case 0:
                mLayoutManager = mLinearLayoutManager;
                break;

            case 1:
                mLayoutManager = mGridLayoutManager;
                break;

            case 2:
                mLayoutManager = mStaggeredGridLayoutManager;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_add:
                mRecyclerAdapter.addData(1);
                break;

            case R.id.id_action_delete:
                mRecyclerAdapter.removeData(1);
                break;

            case R.id.id_action_toggle_orientation:
                if (mLayoutManagerFlag == 2) {
                    Toast.makeText(this, "Do not support the orientation toggle.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

                toggleOrientation();
                break;

            case R.id.id_action_list_view:
                mLayoutManagerFlag = 0;
                mLinearLayoutManager.setOrientation(mOrientation);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
                setAdapterData(false);
                break;

            case R.id.id_action_grid_view:
                mLayoutManagerFlag = 1;
                mGridLayoutManager.setOrientation(mOrientation);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
                setAdapterData(false);
                break;

            case R.id.id_action_staggered_grid_view:
                mLayoutManagerFlag = 2;
                mStaggeredGridLayoutManager.setOrientation(mOrientation);
                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                setAdapterData(true);
                break;
        }
        return true;
    }

    private void toggleOrientation() {
        if (mOrientation == OrientationHelper.VERTICAL) {
            mOrientation = OrientationHelper.HORIZONTAL;
        } else {
            mOrientation = OrientationHelper.VERTICAL;
        }

        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            mGridLayoutManager.setOrientation(mOrientation);
            layoutManager = mGridLayoutManager;
        } else if(layoutManager instanceof LinearLayoutManager) {
            mLinearLayoutManager.setOrientation(mOrientation);
            layoutManager = mLinearLayoutManager;
        } else if(layoutManager instanceof StaggeredGridLayoutManager) {
            mStaggeredGridLayoutManager.setOrientation(mOrientation);
            layoutManager = mStaggeredGridLayoutManager;
        }
        mRecyclerView.setLayoutManager(layoutManager);

        // reset adapter
        initOrResetAdapter();
    }

    private void setAdapterData(boolean isStaggered) {
        mDatas.clear();
        mHeights.clear();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("" + (char) i);

            if (isStaggered)
                mHeights.add((int) (100 + Math.random() * 300));
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;

        mOrientation = savedInstanceState.getInt("Orientation", mOrientation);
        mDatas = savedInstanceState.getStringArrayList("Datas");
        mHeights = savedInstanceState.getIntegerArrayList("Heights");

        if (mDatas == null)
            mDatas = new ArrayList<String>();

        if (mHeights == null)
            mHeights = new ArrayList<Integer>();

        mLayoutManagerFlag = savedInstanceState.getInt("LayoutManager", mLayoutManagerFlag);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Orientation", mOrientation);
        outState.putStringArrayList("Datas", mDatas);
        outState.putIntegerArrayList("Heights", mHeights);
        outState.putInt("LayoutManager", mLayoutManagerFlag);

        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: " + outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // ignore: The restore instance state has been processed in onCreate()
    }
}
