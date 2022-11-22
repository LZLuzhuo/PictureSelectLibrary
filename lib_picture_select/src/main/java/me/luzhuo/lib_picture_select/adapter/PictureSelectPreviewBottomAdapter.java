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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.engine.GlideImageEngine;
import me.luzhuo.lib_picture_select.engine.GridImageEngine;

public class PictureSelectPreviewBottomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FileBean> mDatas = new ArrayList<>();
    private Context context;
    private GridImageEngine imageEngine = GlideImageEngine.getInstance();
    @Nullable
    private FileBean currentFile;

    public void setData(List<FileBean> datas) {
        this.mDatas.clear();
        this.mDatas.addAll(datas);
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new RecyclerHolder(android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_select_item_preview_bottom_list, parent, false));
    }

    public void currentSelected(FileBean data) {
        this.currentFile = data;
        this.notifyDataSetChanged();
    }

    public void addSelected(FileBean data) {
        this.mDatas.add(data);
        this.notifyDataSetChanged();
    }

    public void removeSelected(FileBean data) {
        ListIterator<FileBean> lit = this.mDatas.listIterator();
        while (lit.hasNext()) {
            if (lit.next() == data) lit.remove();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecyclerHolder) holder).bindData(mDatas.get(position));
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        public ImageView preview_list_cover;
        private View preview_current_selected;

        public RecyclerHolder(View itemView) {
            super(itemView);
            preview_list_cover = itemView.findViewById(R.id.picture_select_preview_list_cover);
            preview_current_selected = itemView.findViewById(R.id.picture_select_preview_current_selected);
        }

        public void bindData(FileBean data) {
            if (data instanceof ImageFileBean) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridImage(context, data.uriPath, preview_list_cover);
                else imageEngine.loadGridImage(context, data.urlPath, preview_list_cover);
            } else if (data instanceof VideoFileBean) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridVideoCover(context, data.uriPath, preview_list_cover);
                else imageEngine.loadGridImage(context, data.urlPath, preview_list_cover);
            } else if (data instanceof AudioFileBean) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) imageEngine.loadGridAudio(context, data.uriPath, preview_list_cover);
                else imageEngine.loadGridAudio(context, data.urlPath, preview_list_cover);
            } else { // 文档
                imageEngine.loadGridOther(context, R.mipmap.picture_select_icon_document, preview_list_cover);
            }

            if (data == currentFile) preview_current_selected.setVisibility(View.VISIBLE);
            else preview_current_selected.setVisibility(View.INVISIBLE);
        }
    }
}

