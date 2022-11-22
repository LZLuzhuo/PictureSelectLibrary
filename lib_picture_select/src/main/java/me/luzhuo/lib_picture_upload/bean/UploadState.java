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

import androidx.annotation.WorkerThread;

/**
 * 上传文件接口
 */
public interface UploadState {
    /**
     * 未开始 4
     */
    public static final int UploadStateStart = 2 << 1;

    /**
     * 上传中 8
     */
    public static final int UploadStateCompressing = 2 << 2;

    /**
     * 上传成功 16
     */
    public static final int UploadStateEnded = 2 << 3;

    /**
     * 上传失败 32
     */
    public static final int UploadStateError = 2 << 4;

    /**
     * 上传文件
     * @return true上传成功, false上传失败
     */
    @WorkerThread
    public boolean upload();
}
