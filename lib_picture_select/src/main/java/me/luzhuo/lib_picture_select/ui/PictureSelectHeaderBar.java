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
package me.luzhuo.lib_picture_select.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import me.luzhuo.lib_core.app.color.ColorManager;
import me.luzhuo.lib_core.ui.animation.AnimationManager;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.adapter.PictureSelectAdapter;
import me.luzhuo.lib_picture_select.bean.PictureGroup;

/**
 * 相册选择的头部
 */
public class PictureSelectHeaderBar extends RelativeLayout implements View.OnClickListener {
    private final TextView select_complete;
    private final View bucket_select;
    private final TextView bucket_select_name;
    private final ImageView bucket_select_orientation;
    private final View select_close;
    private final ColorManager color = new ColorManager();
    private final AnimationManager animation = new AnimationManager();
    @Nullable
    private PictureSelectHeaderListener listener;

    /**
     * 相册组
     * <相册id, 文件组>
     */
    public final Map<Long, PictureGroup> pictureBucket = new LinkedHashMap<>();
    /**
     * 默认为"所有相册", 且相册id为0
     */
    public final static long DefaultBucketId = 0L;
    public final static String DefaultBucketName = "所有相册";
    public long currentBucketId = DefaultBucketId;

    public PictureSelectHeaderBar(Context context) {
        super(context);
    }

    public PictureSelectHeaderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectHeaderBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.picture_select_layout_header_bar, this, true);
        select_complete = findViewById(R.id.picture_select_complete);
        bucket_select = findViewById(R.id.picture_select_bucket_select);
        bucket_select_name = findViewById(R.id.picture_select_bucket_select_name);
        bucket_select_orientation = findViewById(R.id.picture_select_bucket_select_orientation);
        select_close = findViewById(R.id.picture_select_close);

        select_complete.setOnClickListener(this);
        bucket_select.setOnClickListener(this);
        select_close.setOnClickListener(this);

        updateCompleteButton();
    }

    public int getSelectCount() {
        return PictureSelectAdapter.selectCount;
    }

    public int getMaxCount() {
        return PictureSelectAdapter.maxCount;
    }

    public void updateCompleteButton() {
        if (getSelectCount() > 0) {
            select_complete.setBackgroundResource(R.drawable.picture_select_bg_complete);
            select_complete.setTextColor(color.getColor(R.color.picture_select_complete_text));
            select_complete.setText(String.format(Locale.CHINESE, "完成(%d/%d)", getSelectCount(), getMaxCount()));
        } else {
            select_complete.setBackgroundResource(R.drawable.picture_select_bg_complete_default);
            select_complete.setTextColor(color.getColor(R.color.picture_select_complete_text_default));
            select_complete.setText("完成");
        }
    }

    /**
     * 获取指定相册组里的文件
     * @param bucketId 相册组id
     * @return 相册组数据
     */
    @Nullable
    public PictureGroup getBucket(long bucketId) {
        return pictureBucket.get(bucketId);
    }

    @Override
    public void onClick(View v) {
        if (v == select_complete) {
            if (getSelectCount() > 0 && listener != null) listener.onCompleteButton();
        } else if (v == bucket_select) {
            if (listener != null) listener.openBucketView(currentBucketId, pictureBucket);
        } else if (v == select_close) {
            if (listener != null) listener.onClose();
        }
    }

    // 旋转箭头
    public void rotateArrow() {
        animation.rotateArrow(bucket_select_orientation);
    }

    /**
     * 设置相册分组
     */
    @WorkerThread
    public void setPictureBucket(List<FileBean> files) {
        // 所有相册
        this.pictureBucket.put(DefaultBucketId, new PictureGroup(DefaultBucketId, DefaultBucketName).setFiles(files));
        // 详细相册
        for (FileBean fileBean : files) {
            PictureGroup pictureGroup = this.pictureBucket.get(fileBean.bucketId);
            if (pictureGroup == null) {
                pictureGroup = new PictureGroup(fileBean.bucketId, fileBean.bucketName);
                this.pictureBucket.put(fileBean.bucketId, pictureGroup);
            }
            pictureGroup.addFile(fileBean);
        }
    }

    /**
     * 更新相册分组数据
     */
    @MainThread
    public void updateBucket(long bucketId) {
        this.currentBucketId = bucketId;
        PictureGroup pictureGroup = pictureBucket.get(currentBucketId);
        if (pictureGroup == null) return;

        bucket_select_name.setText(pictureGroup.bucketName);
        if (listener != null) {
            listener.onSwitchBucket(pictureGroup.files);
        }
    }

    /**
     * 添加File到相册分组
     * @param fileBean 预添加的文件
     */
    public void addFile2Bucket(FileBean fileBean) {
        // 所有相册
        if (fileBean.bucketId != DefaultBucketId) {
            PictureGroup pictureGroup = pictureBucket.get(DefaultBucketId);
            if (pictureGroup != null) {
                pictureGroup.addFile(0, fileBean);
            }
        }

        // 其他相册
        PictureGroup pictureGroup = pictureBucket.get(fileBean.bucketId);
        if (pictureGroup == null) {
            pictureGroup = new PictureGroup(fileBean.bucketId, fileBean.bucketName);
            this.pictureBucket.put(fileBean.bucketId, pictureGroup);
        }
        pictureGroup.addFile(0, fileBean);
    }

    public interface PictureSelectHeaderListener {
        /**
         * 用户点击了发送按钮
         * 仅在有选中图片的时候才回调
         */
        public void onCompleteButton();

        /**
         * 切换相册组
         * @param files 如果切换到相册组没有数据, 则返回null
         */
        public void onSwitchBucket(@Nullable List<FileBean> files);

        /**
         * 关闭
         */
        public void onClose();

        /**
         * 打开相册组
         */
        public void openBucketView(long currentBucketId, Map<Long, PictureGroup> pictureBucket);
    }

    public void setOnPictureSelectHeaderListener(@Nullable PictureSelectHeaderListener listener) {
        this.listener = listener;
    }
}
