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
import android.widget.TextView;

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
import me.luzhuo.lib_file.store.FileStore;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.engine.GlideImageEngine;
import me.luzhuo.lib_picture_select.engine.GridImageEngine;
import me.luzhuo.lib_picture_select_view.bean.AudioNetBean;
import me.luzhuo.lib_picture_select_view.bean.ImageNetBean;
import me.luzhuo.lib_picture_select_view.bean.VideoNetBean;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PictureViewSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CheckableFileBean> mDatas = new ArrayList<>();
    private final static int TYPE_ADD = 1, TYPE_NORMAL = 2;
    @LayoutRes
    private int layout_add;
    @LayoutRes
    private int layout_normal;
    @Nullable
    private OnPictureAdapterSelectListener selectListener;
    private Context context;
    private int maxCount;
    private int type; // 能选择的文件类型
    private final GridImageEngine imageEngine = GlideImageEngine.getInstance();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public PictureViewSelectAdapter(@LayoutRes int layout_add, @LayoutRes int layout_normal, @FileStore.TypeFileStore int type, int maxCount) {
        this.layout_add = layout_add;
        this.layout_normal = layout_normal;
        this.type = type;
        this.maxCount = maxCount;
    }

    public List<CheckableFileBean> getDatas() {
        return mDatas;
    }

    public void setDatas(List<? extends CheckableFileBean> datas) {
        this.mDatas.clear();
        this.mDatas.addAll(datas);
        this.notifyDataSetChanged();
    }

    public void addData(CheckableFileBean data) {
        this.mDatas.add(data);
        this.notifyDataSetChanged();
    }

    public void addDatas(List<? extends CheckableFileBean> data) {
        this.mDatas.addAll(data);
        this.notifyDataSetChanged();
    }

    public void removeData(CheckableFileBean file) {
        this.mDatas.remove(file);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (viewType == TYPE_ADD) return new RecyclerAddHolder(LayoutInflater.from(context).inflate(layout_add, parent, false));
        else return new RecyclerNormalHolder(LayoutInflater.from(context).inflate(layout_normal, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDatas.size() >= maxCount ? mDatas.size() : mDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) return TYPE_ADD;
        else return TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ADD) ((RecyclerAddHolder) holder).bindData();
        else ((RecyclerNormalHolder) holder).bindData(mDatas.get(position));
    }

    public class RecyclerAddHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Nullable
        public TextView picture_type_content;

        public RecyclerAddHolder(@NonNull View itemView) {
            super(itemView);
            try { picture_type_content = itemView.findViewById(R.id.picture_type_content); } catch (Exception e) { picture_type_content = null; }

            itemView.setOnClickListener(this);
        }

        public void bindData() {
            if (picture_type_content != null) {
                if (type == FileStore.TypeImage || type == FileStore.TypeGif || type == FileStore.TypeImage + FileStore.TypeGif) picture_type_content.setText("上传图片");
                else if (type == FileStore.TypeVideo) picture_type_content.setText("上传视频");
                else if (type == FileStore.TypeAudio) picture_type_content.setText("上传音频");
                else picture_type_content.setText("上传文件");
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                if (selectListener != null) selectListener.onPictureAdapterSelect();
            }
        }
    }

    public class RecyclerNormalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View picture_delete_btn;
        public ImageView picture_iv;
        public ImageView picture_play_icon;

        public RecyclerNormalHolder(@NonNull View itemView) {
            super(itemView);
            try { picture_delete_btn = itemView.findViewById(R.id.picture_delete_btn); } catch (Exception e) { picture_delete_btn = null; }
            try { picture_iv = itemView.findViewById(R.id.picture_iv); } catch (Exception e) { picture_iv = null; }
            try { picture_play_icon = itemView.findViewById(R.id.picture_play_icon); } catch (Exception e) { picture_play_icon = null; }

            picture_delete_btn.setOnClickListener(this);
            itemView.setOnClickListener(this);
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
                    imageEngine.loadGridAudio(context, ((AudioNetBean) data).netUrl, picture_iv);
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

        @Override
        public void onClick(View v) {
            if (v == picture_delete_btn) {
                if (selectListener != null) selectListener.onPictureAdapterDelete(getLayoutPosition());
            } else if (v == itemView) {
                if (selectListener != null) selectListener.onPictureAdapterShow(getLayoutPosition());
            }
        }
    }

    public void setOnPictureAdapterListener(OnPictureAdapterSelectListener selectListener) {
        this.selectListener = selectListener;
    }
}
