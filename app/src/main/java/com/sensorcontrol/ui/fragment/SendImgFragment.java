package com.sensorcontrol.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.BuildConfig;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseFragment;
import com.sensorcontrol.module.Image;
import com.sensorcontrol.ui.activity.SendActivity;
import com.sensorcontrol.ui.adapter.ImageGridAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lizhe on 2017/11/17 0017.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SendImgFragment extends BaseFragment {
    @BindView(R.id.grid_view)
    GridView mGridView;

    private ImageGridAdapter mImageGridAdapter;
    private File mTmpFile;


    @Override
    protected int setLayout() {
        return R.layout.fragment_send_img;
    }

    @Override
    protected void init() {

    }

    private void initListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    showCameraAction();
                } else {
                    startClipPicture(mImageGridAdapter.getItem(position).getPath());
                }
            }
        });
    }



    private void startClipPicture(String path) {
        Intent intent = new Intent(getContext(), SendActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
    }
    private String mFilePath;
    /**
     * 选择相机
     */
    private void showCameraAction() {
        getActivity().startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),1);
    }

    /**
     * 加载手机中的相片
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(getContext(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> images = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        images.add(image);
                    } while (data.moveToNext());
                    mImageGridAdapter.setData(images);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    protected void setData() {
        getActivity().getSupportLoaderManager().initLoader(0, null, mLoaderCallback);
        initListener();
        mImageGridAdapter = new ImageGridAdapter(getContext());
        mGridView.setAdapter(mImageGridAdapter);
    }


}
