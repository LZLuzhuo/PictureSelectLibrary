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
package me.luzhuo.lib_picture_select.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.bean.PictureGroup;
import me.luzhuo.lib_picture_select.engine.GlideImageEngine;
import me.luzhuo.lib_picture_select.engine.GridImageEngine;

public class HeaderBucketPopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private GridImageEngine imageEngine = GlideImageEngine.getInstance();
    private Context context;
    private List<PictureGroup> pictureBucket = new ArrayList<>();
    private long bucketId = 0;
    @Nullable
    private OnBucketPopCallback callback;

    public void setDatas(Map<Long, PictureGroup> pictureBucket) {
        this.pictureBucket.clear();
        this.pictureBucket.addAll(pictureBucket.values());
        this.notifyDataSetChanged();
    }

    public void setCurrentBucket(long bucketId) {
        this.bucketId = bucketId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new RecyclerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_select_item_header_bucket_pop, parent, false));
    }

    @Override
    public int getItemCount() {
        return pictureBucket.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecyclerHolder) holder).bindData(pictureBucket.get(position));
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView bucket_cover;
        public TextView bucket_name;
        public TextView bucket_size;

        public RecyclerHolder(View itemView) {
            super(itemView);
            bucket_cover = itemView.findViewById(R.id.bucket_cover);
            bucket_name = itemView.findViewById(R.id.bucket_name);
            bucket_size = itemView.findViewById(R.id.bucket_size);

            itemView.setOnClickListener(this);
        }

        public void bindData(PictureGroup data) {
            if (data.bucketId == bucketId) itemView.setBackgroundResource(R.color.picture_select_bucket_select);
            else itemView.setBackgroundResource(R.color.picture_select_bucket_normal);

            bucket_name.setText(data.bucketName);
            bucket_size.setText(String.format(Locale.CHINESE, "(%d)", data.getSize()));

            FileBean coverFileBean = data.getFirstFile();
            if (coverFileBean != null) {
                if (data.getFirstFile() instanceof ImageFileBean) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridImage(context, coverFileBean.uriPath, bucket_cover);
                    else imageEngine.loadGridImage(context, coverFileBean.urlPath, bucket_cover);
                } else if (data.getFirstFile() instanceof VideoFileBean) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridVideoCover(context, coverFileBean.uriPath, bucket_cover);
                    else imageEngine.loadGridImage(context, coverFileBean.urlPath, bucket_cover);
                } else if (data.getFirstFile() instanceof AudioFileBean) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridAudio(context, coverFileBean.uriPath, bucket_cover);
                    else imageEngine.loadGridAudio(context, coverFileBean.urlPath, bucket_cover);
                } else { // 文档
                    imageEngine.loadGridOther(context, R.mipmap.picture_select_icon_document, bucket_cover);
                }
            } else {
                imageEngine.loadGridImage(context, "", bucket_cover);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                bucketId = pictureBucket.get(getLayoutPosition()).bucketId;
                notifyDataSetChanged();
                if (callback != null) callback.onBucketSelect(bucketId);
            }
        }
    }

    public interface OnBucketPopCallback {
        public void onBucketSelect(long bucket);
    }

    public void setOnBucketPopCallback(OnBucketPopCallback callback) {
        this.callback = callback;
    }
}

