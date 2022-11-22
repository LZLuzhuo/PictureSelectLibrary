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
package me.luzhuo.lib_picture_select_view.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.CheckableFileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.engine.GlideImageEngine;
import me.luzhuo.lib_picture_select.engine.GridImageEngine;
import me.luzhuo.lib_picture_select_view.bean.AudioNetBean;
import me.luzhuo.lib_picture_select_view.bean.ImageNetBean;
import me.luzhuo.lib_picture_select_view.bean.VideoNetBean;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PictureViewShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CheckableFileBean> mDatas = new ArrayList<>();
    private Context context;
    @LayoutRes
    private int layout_show;
    @Nullable
    private OnPictureAdapterShowListener listener;
    private final GridImageEngine imageEngine = GlideImageEngine.getInstance();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public PictureViewShowAdapter(@LayoutRes int layout_show) {
        this.layout_show = layout_show;
    }

    public void setData(List<? extends CheckableFileBean> datas) {
        this.mDatas.clear();
        this.mDatas.addAll(datas);
        this.notifyDataSetChanged();
    }

    public void addDatas(List<? extends CheckableFileBean> datas) {
        this.mDatas.addAll(datas);
        this.notifyDataSetChanged();
    }

    public List<CheckableFileBean> getDatas() {
        return this.mDatas;
    }

    public void removeData(CheckableFileBean file) {
        this.mDatas.remove(file);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new RecyclerShowHolder(LayoutInflater.from(context).inflate(layout_show, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerShowHolder) holder).bindData(mDatas.get(position));
    }

    public class RecyclerShowHolder extends RecyclerView.ViewHolder {
        public ImageView picture_iv;
        public ImageView picture_play_icon;

        public RecyclerShowHolder(@NonNull View itemView) {
            super(itemView);
            try { picture_iv = itemView.findViewById(R.id.picture_iv); } catch (Exception e) { picture_iv = null; }
            try { picture_play_icon = itemView.findViewById(R.id.picture_play_icon); } catch (Exception e) { picture_play_icon = null; }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPictureAdapterShow(getLayoutPosition());
            });
        }

        public void bindData(CheckableFileBean data) {
            if (picture_iv != null) {
                if (data instanceof ImageFileBean) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridImage(context, ((ImageFileBean) data).uriPath, picture_iv);
                    else imageEngine.loadGridImage(context, ((ImageFileBean) data).urlPath, picture_iv);
                } else if (data instanceof VideoFileBean) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridVideoCover(context, ((VideoFileBean) data).uriPath, picture_iv);
                    else imageEngine.loadGridVideoCover(context, ((VideoFileBean) data).urlPath, picture_iv);
                } else if (data instanceof AudioFileBean) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridAudio(context, ((AudioFileBean) data).uriPath, picture_iv);
                    else imageEngine.loadGridAudio(context, ((AudioFileBean) data).urlPath, picture_iv);
                } else if (data instanceof ImageNetBean) {
                    imageEngine.loadGridImage(context, ((ImageNetBean) data).netUrl, picture_iv);
                } else if (data instanceof VideoNetBean) {
                    imageEngine.loadGridVideoCover(context, TextUtils.isEmpty(((VideoNetBean) data).coverUrl) ? ((VideoNetBean) data).netUrl : ((VideoNetBean) data).coverUrl, picture_iv);
                } else if (data instanceof AudioNetBean) {
                    imageEngine.loadGridAudio(context, TextUtils.isEmpty(((AudioNetBean) data).coverUrl) ? ((AudioNetBean) data).netUrl : ((AudioNetBean) data).coverUrl, picture_iv);
                }
            }

            if (picture_play_icon != null) {
                if (data instanceof VideoFileBean || data instanceof VideoNetBean) {
                    picture_play_icon.setVisibility(View.VISIBLE);
                    picture_play_icon.setImageResource(R.mipmap.picture_select_btn_video_play);
                } else if (data instanceof AudioFileBean || data instanceof AudioNetBean) {
                    picture_play_icon.setVisibility(View.VISIBLE);
                    picture_play_icon.setImageResource(R.mipmap.picture_select_btn_audio_play);
                } else {
                    picture_play_icon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void setOnPictureAdapterListener(OnPictureAdapterShowListener listener) {
        this.listener = listener;
    }
}
