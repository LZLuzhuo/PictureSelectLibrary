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
package me.luzhuo.lib_picture_select.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import me.luzhuo.lib_core.app.base.CoreBaseFragment;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.engine.GlideImageEngine;
import me.luzhuo.lib_picture_select.engine.ImageEngine;
import me.luzhuo.lib_video.picture_select_preview.PictureSelectPreviewVideoView;

public class PictureSelectPreviewVideoFragment extends CoreBaseFragment implements PictureSelectPreviewVideoView.OnSingleClickCallback {
    private FileBean data;
    @Nullable
    private PictureSelectPreviewCallback callback;
    @Nullable
    private PictureSelectPreviewVideoView preview_video;
    private ImageEngine imageEngine = GlideImageEngine.getInstance();

    private PictureSelectPreviewVideoFragment() { }

    public static PictureSelectPreviewVideoFragment instance(FileBean data) {
        PictureSelectPreviewVideoFragment fragment = new PictureSelectPreviewVideoFragment();
        Bundle args = new Bundle();
        args.putParcelable("data", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate() {
        if (getArguments() != null) {
            data = getArguments().getParcelable("data");
        }
    }

    @Override
    public View initView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.picture_select_item_preview_video, viewGroup, false);
    }

    @Override
    public void initData(Bundle bundle) {
        preview_video = getView().findViewById(R.id.picture_select_preview_video);
        preview_video.setOnSingleClickCallback(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageEngine.loadVideoCover(getContext(), data.uriPath, preview_video.getCoverImageView());
            preview_video.setLocalData(data.uriPath);
        } else {
            imageEngine.loadVideoCover(getContext(), data.urlPath, preview_video.getCoverImageView());
            preview_video.setLocalData(data.urlPath);
        }
    }

    public PictureSelectPreviewVideoFragment setOnPictureSelectPreviewCallback(PictureSelectPreviewCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void onSingleClick() {
        if (callback != null) callback.onSingleClick(data);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            if (preview_video != null) preview_video.onDestroy();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        if (preview_video != null) preview_video.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (preview_video != null) preview_video.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (preview_video != null) preview_video.onDestroy();
        super.onDestroy();
    }
}
