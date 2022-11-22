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
package me.luzhuo.lib_picture_select_view.bean;

import androidx.annotation.Nullable;
import me.luzhuo.lib_file.bean.CheckableFileBean;

/**
 * 网络的图片文件
 */
public class ImageNetBean extends CheckableFileBean {
    public String netUrl;
    @Nullable
    public Object tag;

    public ImageNetBean(String netUrl) {
        this.netUrl = netUrl;
    }

    @Override
    public String toString() {
        return "ImageNetBean{" +
                "netUrl='" + netUrl + '\'' +
                ", tag=" + tag +
                '}';
    }
}