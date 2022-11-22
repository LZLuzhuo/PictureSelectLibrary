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

import androidx.annotation.WorkerThread;

/**
 * 压缩状态标识
 */
public interface CompressState {

    /**
     * 未开始 2
     */
    public static final int CompressStateStart = 1 << 1;

    /**
     * 压缩中 4
     */
    public static final int CompressStateCompressing = 1 << 2;

    /**
     * 已结束 8
     */
    public static final int CompressStateEnded = 1 << 3;

    /**
     * 压缩失败 16
     */
    public static final int CompressStateError = 1 << 4;

    /**
     * 压缩
     * @return true压缩成功, false压缩失败
     */
    @WorkerThread
    public boolean compress();

    /**
     * AndroidQ+ 将其移到私有目录
     * @return true移动成功, false移动失败
     */
    @WorkerThread
    public boolean checkCopyFile();
}
