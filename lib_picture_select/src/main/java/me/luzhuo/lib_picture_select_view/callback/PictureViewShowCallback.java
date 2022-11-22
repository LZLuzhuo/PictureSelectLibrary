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
package me.luzhuo.lib_picture_select_view.callback;

import java.util.List;

import me.luzhuo.lib_file.bean.CheckableFileBean;

/**
 * 相册显示回调
 */
public interface PictureViewShowCallback {

    /**
     * 图片显示回调
     * @param imageFileBean 点击查看的单个图片文件
     * @param index 在新集合中的索引
     * @param imageFileBeans 所有的图片文件新集合
     */
    public void onImageShowCallback(CheckableFileBean imageFileBean, int index, List<CheckableFileBean> imageFileBeans);

    /**
     * 视频显示回调
     * @param videoFileBean 点击查看的单个视频文件
     * @param index 在新集合中的索引
     * @param videoFileBeans 所有的视频文件新集合
     */
    public void onVideoShowCallback(CheckableFileBean videoFileBean, int index, List<CheckableFileBean> videoFileBeans);

    /**
     * 音频显示回调
     * @param audioFileBean 点击查看的单个音频文件
     * @param index 在新集合中的索引
     * @param audioFileBeans 所有的音频文件新集合
     */
    public void onAudioShowCallback(CheckableFileBean audioFileBean, int index, List<CheckableFileBean> audioFileBeans);
}
