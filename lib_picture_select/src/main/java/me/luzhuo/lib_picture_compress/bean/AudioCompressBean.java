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
package me.luzhuo.lib_picture_compress.bean;

import java.util.concurrent.atomic.AtomicInteger;

import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_picture_select.PictureSelectUtils;

public class AudioCompressBean extends AudioFileBean implements CompressState{

    /**
     * 压缩后文件的本地路径
     */
    public String compressPath;

    /**
     * 当前压缩状态
     */
    public AtomicInteger compressState = new AtomicInteger(CompressStateStart);

    public AudioCompressBean(AudioFileBean audioFileBean) {
        super(audioFileBean.id, audioFileBean.fileName, audioFileBean.mimeType, audioFileBean.uriPath, audioFileBean.urlPath, audioFileBean.bucketId, audioFileBean.bucketName, audioFileBean.size, audioFileBean.addedDate, audioFileBean.duration);
        super.isOrigin = audioFileBean.isOrigin;
    }

    @Override
    public boolean compress() {
        if (isOrigin) return false;
        this.compressState.set(CompressState.CompressStateCompressing);

        // TODO 压缩音频
        this.compressState.set(CompressState.CompressStateError);
        return false;
    }

    @Override
    public boolean checkCopyFile() {
        return PictureSelectUtils.checkCopyFile(this);
    }

    @Override
    public String toString() {
        return "AudioCompressBean{" +
                "compressPath='" + compressPath + '\'' +
                ", compressState=" + compressState +
                ", " + super.toString() +
                '}';
    }
}
