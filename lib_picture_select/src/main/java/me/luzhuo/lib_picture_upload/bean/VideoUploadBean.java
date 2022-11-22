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
package me.luzhuo.lib_picture_upload.bean;

import java.util.concurrent.atomic.AtomicInteger;

import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_picture_compress.bean.VideoCompressBean;

public class VideoUploadBean extends VideoCompressBean implements UploadState {
    /**
     * 上传后文件的网路路径
     */
    public String netUrl;

    /**
     * 当前上传状态
     */
    public AtomicInteger netState = new AtomicInteger(UploadStateStart);

    public VideoUploadBean(VideoFileBean videoFileBean) {
        super(videoFileBean);
    }

    @Override
    public boolean upload() {
        return false;
    }

    @Override
    public String toString() {
        return "VideoUploadBean{" +
                "netUrl='" + netUrl + '\'' +
                ", netState=" + netState +
                ", " + super.toString() +
                '}';
    }
}
