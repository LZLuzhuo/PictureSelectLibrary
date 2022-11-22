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
package me.luzhuo.lib_picture_select.bean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import me.luzhuo.lib_file.bean.FileBean;

/**
 * 相册组
 */
public class PictureGroup {
    /**
     * 相册id
     */
    public long bucketId;
    /**
     * 相册名
     */
    @NonNull
    public String bucketName;

    public List<FileBean> files = new ArrayList<>();

    public int getSize() {
        return files.size();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public PictureGroup(long bucketId, @NonNull String bucketName) {
        this.bucketId = bucketId;
        this.bucketName = bucketName;
    }

    @NonNull
    public PictureGroup addFile(FileBean fileBean) {
        this.files.add(fileBean);
        return this;
    }

    public PictureGroup addFile(int index, FileBean fIleBean) {
        this.files.add(0, fIleBean);
        return this;
    }

    @NonNull
    public PictureGroup addFiles(List<FileBean> fileBeans) {
        this.files.addAll(fileBeans);
        return this;
    }

    @NonNull
    public PictureGroup setFiles(List<FileBean> fileBeans) {
        this.files.clear();
        this.files.addAll(fileBeans);
        return this;
    }

    @Nullable
    public FileBean getFirstFile() {
        if (files.size() > 0) return files.get(0);
        return null;
    }

    @Override
    public String toString() {
        return "PictureGroup{" +
                "bucketId=" + bucketId +
                ", bucketName='" + bucketName + '\'' +
                '}';
    }
}
