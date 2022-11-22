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

import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_image_compress.ImageCompress;
import me.luzhuo.lib_picture_select.PictureSelectUtils;

public class ImageCompressBean extends ImageFileBean implements CompressState {

    /**
     * 压缩后文件的本地路径
     */
    public String compressPath;

    /**
     * 当前压缩状态
     */
    public AtomicInteger compressState = new AtomicInteger(CompressStateStart);

    public ImageCompressBean(ImageFileBean imageFileBean) {
        super(imageFileBean.id, imageFileBean.fileName, imageFileBean.mimeType, imageFileBean.uriPath, imageFileBean.urlPath, imageFileBean.bucketId, imageFileBean.bucketName, imageFileBean.size, imageFileBean.addedDate, imageFileBean.width, imageFileBean.height);
        super.isOrigin = imageFileBean.isOrigin;
    }

    @Override
    public boolean compress() {
        if (isOrigin) return false;
        this.compressState.set(CompressState.CompressStateCompressing);

        compressPath = new ImageCompress().compress(this);
        if (compressPath != null) {
            this.compressState.set(CompressState.CompressStateEnded);
            return true;
        } else {
            this.compressState.set(CompressState.CompressStateError);
            return false;
        }
    }

    @Override
    public boolean checkCopyFile() {
        return PictureSelectUtils.checkCopyFile(this);
    }

    @Override
    public String toString() {
        return "ImageCompressBean{" +
                "compressPath='" + compressPath + '\'' +
                ", compressState=" + compressState +
                ", " + super.toString() +
                '}';
    }
}
