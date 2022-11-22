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
package me.luzhuo.lib_picture_select.adapter;

import java.util.List;

import me.luzhuo.lib_file.bean.FileBean;

public interface PictureSelectAdapterListener {

    /**
     * 拍照
     * @param isLimit 是否已达上限
     */
    public void onCamera(boolean isLimit);

    /**
     * 选择了文件
     */
    public void onSelect(boolean isSingle, FileBean file);

    /**
     * 预览图片
     * @param file 单个文件
     * @param index 该文件所在的索引
     * @param files 所有文件
     */
    public void onShow(FileBean file, int index, List<FileBean> files);
}
