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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * Grid图片加载引擎
 */
public interface GridImageEngine {
    /**
     * 加载图片封面
     * @param context Context
     * @param url 图片本地路径
     * @param imageView ImageView
     */
    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * 加载图片封面
     * @param context Context
     * @param uri 图片本地Uri路径
     * @param imageView ImageView
     */
    public void loadGridImage(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView);

    /**
     * 加载视频的封面
     * @param context Context
     * @param url 视频本地地址
     * @param imageView ImageView
     */
    public void loadGridVideoCover(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * 加载视频的封面
     * @param context Context
     * @param uri 视频本地Uri地址
     * @param imageView ImageView
     */
    public void loadGridVideoCover(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView);

    /**
     * 加载Gif图片封面
     * @param context Context
     * @param url Gif的本地地址
     * @param imageView ImageView
     */
    public void loadGridGif(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * 加载Gif图片封面
     * @param context Context
     * @param uri Gif的本地Uri地址
     * @param imageView ImageView
     */
    public void loadGridGif(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView);

    /**
     * 加载音频的封面
     * @param context Context
     * @param url 音频本地路径
     * @param imageView ImageView
     */
    public void loadGridAudio(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * 加载音频的封面
     * @param context Context
     * @param uri 音频的本地Uri路径
     * @param imageView ImageView
     */
    public void loadGridAudio(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView);

    /**
     * 加载系统资源图片
     * @param context Context
     * @param redId 系统资源id
     * @param imageView ImageView
     */
    public void loadGridOther(@NonNull Context context, @DrawableRes int redId, @NonNull ImageView imageView);
}
