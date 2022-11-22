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
package me.luzhuo.lib_picture_select.engine;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import me.luzhuo.lib_picture_select.R;

/**
 * 使用Glide实现图片加载
 */
public class GlideImageEngine implements GridImageEngine, ImageEngine {
    private GlideImageEngine() {}

    private static GlideImageEngine instance = new GlideImageEngine();

    public static GlideImageEngine getInstance() {
        return instance;
    }

    @Override
    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).load(url).override(300, 300).centerCrop().placeholder(R.mipmap.picture_select_icon_image).into(imageView);
    }

    @Override
    public void loadGridImage(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView) {
        Glide.with(context).load(uri).override(300, 300).centerCrop().placeholder(R.mipmap.picture_select_icon_image).into(imageView);
    }

    @Override
    public void loadGridVideoCover(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).asBitmap().load(url).override(600, 600).centerCrop().placeholder(R.mipmap.picture_select_icon_video).into(imageView);
    }

    @Override
    public void loadGridVideoCover(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView) {
        Glide.with(context).asBitmap().load(uri).override(600, 600).centerCrop().placeholder(R.mipmap.picture_select_icon_video).into(imageView);
    }

    @Override
    public void loadGridGif(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).asGif().load(url).into(imageView);
    }

    @Override
    public void loadGridGif(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView) {
        Glide.with(context).asGif().load(uri).into(imageView);
    }

    @Override
    public void loadGridAudio(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).load(url).override(300, 300).centerCrop().placeholder(R.mipmap.picture_select_icon_audio).into(imageView);
    }

    @Override
    public void loadGridAudio(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView) {
        Glide.with(context).load(uri).override(300, 300).centerCrop().placeholder(R.mipmap.picture_select_icon_audio).into(imageView);
    }

    @Override
    public void loadGridOther(@NonNull Context context, int redId, @NonNull ImageView imageView) {
        Glide.with(context).load(redId).override(300, 300).centerCrop().into(imageView);
    }

    @Override
    public void loadGif(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView) {
        Glide.with(context).asGif().load(uri).into(imageView);
    }

    @Override
    public void loadGif(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).asGif().load(url).into(imageView);
    }

    @Override
    public void loadVideoCover(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView) {
        Glide.with(context).asBitmap().load(uri).override(600, 600).placeholder(R.mipmap.picture_select_icon_video).into(imageView);
    }

    @Override
    public void loadVideoCover(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).asBitmap().load(url).override(600, 600).placeholder(R.mipmap.picture_select_icon_video).into(imageView);
    }
}
