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
 * 相册选择的回调
 */
public interface PictureViewSelectCallback extends PictureViewShowCallback {
    /**
     * 选择了文件
     * @param fileBeans 选择的文件
     */
    public void onSelect(List<? extends CheckableFileBean> fileBeans);

    /**
     * 删除了文件
     * @param fileBean 删除的文件
     */
    public void onDelete(CheckableFileBean fileBean);
}
