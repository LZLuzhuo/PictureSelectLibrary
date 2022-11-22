/* Copyright 2022 Luzhuo. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.luzhuo.lib_picture_select;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;
import me.luzhuo.lib_core.app.base.CoreBaseActivity;
import me.luzhuo.lib_core.app.impl.OnPageChangeListenerImpl;
import me.luzhuo.lib_core.ui.statusbar.StatusBarManager;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_picture_select.adapter.PictureSelectPreviewAdapter;
import me.luzhuo.lib_picture_select.fragments.PictureSelectPreviewCallback;
import me.luzhuo.lib_picture_select.ui.PictureSelectPreviewBottomBar;
import me.luzhuo.lib_picture_select.ui.PictureSelectPreviewHeaderBar;

/**
 * 相册的预览功能
 */
public class PictureSelectPreviewActivity extends CoreBaseActivity implements PictureSelectPreviewCallback, PictureSelectPreviewBottomBar.OnPreviewBottomBarCallback, PictureSelectPreviewHeaderBar.OnPreviewHeaderBarCallback {

    /**
     * 当前预览的相册组
     */
    @Nullable
    private static List<FileBean> currentBucketFiles;
    /**
     * 所有的相册文件, 用于展示已选中的文件
     */
    @Nullable
    private static List<FileBean> allFiles;
    @Nullable
    private static OnPictureSelectPreviewListener listener;
    private int index;

    private ViewPager preview_viewpager;
    private PictureSelectPreviewHeaderBar preview_header;
    private PictureSelectPreviewBottomBar preview_bottom;

    private PictureSelectPreviewAdapter pictureSelectPreviewAdapter;
    private FileBean currentFileData;

    public static void start(Context context, int index, List<FileBean> currentBucketFiles, List<FileBean> allFiles, OnPictureSelectPreviewListener listener) {
        PictureSelectPreviewActivity.currentBucketFiles = currentBucketFiles;
        PictureSelectPreviewActivity.allFiles = allFiles;
        PictureSelectPreviewActivity.listener = listener;
        Intent intent = new Intent(context, PictureSelectPreviewActivity.class);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StatusBarManager.getInstance().transparent(this, false);
        setContentView(R.layout.picture_select_activity_preview);
        overridePendingTransition(R.anim.picture_select_preview_activity_in, R.anim.picture_select_activity_normal);

        index = getIntent().getIntExtra("index", 0);

        initView();
        initData();
    }

    private void initView() {
        preview_viewpager = findViewById(R.id.picture_select_preview_viewpager);
        preview_header = findViewById(R.id.picture_select_preview_header);
        preview_bottom = findViewById(R.id.picture_select_preview_bottom);

        preview_bottom.setOnPreviewBottomBarListener(this);
        preview_header.setOnPreviewHeaderBarCallback(this);

        pictureSelectPreviewAdapter = new PictureSelectPreviewAdapter(this, currentBucketFiles);
        preview_viewpager.setAdapter(pictureSelectPreviewAdapter);
        pictureSelectPreviewAdapter.setOnPictureSelectPreviewCallback(this);
        preview_viewpager.addOnPageChangeListener(new OnPageChangeListenerImpl() {
            @Override
            public void onPageSelected(int position) {
                if (currentBucketFiles == null) return;
                preview_header.setCurrentIndex(position, currentBucketFiles.size());
                updateFileBean(currentBucketFiles.get(position));
            }
        });
        preview_viewpager.setCurrentItem(index);

    }

    private void initData() {
        if (allFiles == null || currentBucketFiles == null) return;
        List<FileBean> currentSelectFiles = getCurrentSelectFiles(allFiles);
        preview_bottom.setSelectPreviewDatas(currentSelectFiles);
        preview_header.updateCompleteButton();
        preview_header.setCurrentIndex(index, currentBucketFiles.size());
        updateFileBean(currentBucketFiles.get(index));
    }

    private List<FileBean> getCurrentSelectFiles(List<FileBean> fileBeans) {
        List<FileBean> selectFiles = new ArrayList<>();
        for (FileBean fileBean : fileBeans) {
            if (fileBean.isChecked) selectFiles.add(fileBean);
        }
        return selectFiles;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.picture_select_activity_normal, R.anim.picture_select_preview_activity_out);
    }

    @Override
    protected void onDestroy() {
        currentBucketFiles = null;
        allFiles = null;
        listener = null;
        super.onDestroy();
    }

    @Override
    public void onSingleClick(FileBean data) {
        TransitionManager.beginDelayedTransition(preview_header, getSlide(Gravity.TOP));
        TransitionManager.beginDelayedTransition(preview_bottom, getSlide(Gravity.BOTTOM));

        if (preview_header.getVisibility() != View.VISIBLE) {
            preview_header.setVisibility(View.VISIBLE);
            preview_bottom.setVisibility(View.VISIBLE);
        } else {
            preview_header.setVisibility(View.INVISIBLE);
            preview_bottom.setVisibility(View.INVISIBLE);
        }
    }

    private Slide getSlide(int slideEdge) {
        Slide slide = new Slide();
        slide.setDuration(300);
        slide.setSlideEdge(slideEdge);
        return slide;
    }

    /**
     * 更新文件相关的界面
     * 1. 底部选择 是否选中
     * 2. 底部列表 选框是否选中
     */
    private void updateFileBean(FileBean data) {
        this.currentFileData = data;
        preview_bottom.checkSelect(data.isChecked);
        preview_bottom.currentSelectedData(data);
    }

    @Override
    public void onCheckedChanged(boolean isChecked) {
        this.currentFileData.isChecked = isChecked;
        if (listener != null) listener.onCheckedChanged(isChecked);

        preview_header.updateCompleteButton();
        if (isChecked) {
            preview_bottom.addSelectedData(currentFileData);
            preview_bottom.currentSelectedData(currentFileData);
        } else preview_bottom.removeSelectedData(currentFileData);
    }

    @Override
    public void onComplete() {
        finish();
        if (listener != null) listener.onComplete();
    }

    public interface OnPictureSelectPreviewListener {
        public void onCheckedChanged(boolean isChecked);
        public void onComplete();
    }
}
