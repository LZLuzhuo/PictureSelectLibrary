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
package me.luzhuo.lib_picture_compress;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.CheckableFileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_picture_compress.bean.AudioCompressBean;
import me.luzhuo.lib_picture_compress.bean.CompressState;
import me.luzhuo.lib_picture_compress.bean.ImageCompressBean;
import me.luzhuo.lib_picture_compress.bean.VideoCompressBean;
import me.luzhuo.lib_picture_select_view.PictureSelectOriginView;
import me.luzhuo.lib_picture_select_view.adapter.PictureViewSelectAdapter;
import me.luzhuo.lib_picture_select_view.adapter.PictureViewShowAdapter;

/**
 * 相册选择 (具有压缩功能)
 */
public class PictureSelectCompressView extends PictureSelectOriginView {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public PictureSelectCompressView(@NonNull Context context) {
        super(context);
    }

    public PictureSelectCompressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectCompressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addDatas(List<? extends CheckableFileBean> files) {
        if (adapter instanceof PictureViewShowAdapter) {
            ((PictureViewShowAdapter) adapter).addDatas(files);
        } else if (adapter instanceof PictureViewSelectAdapter) {

            // local file cover compress file.
            List<CheckableFileBean> compressFiles = new ArrayList<>();
            ListIterator<? extends CheckableFileBean> listIterator = files.listIterator();
            while(listIterator.hasNext()) {
                CheckableFileBean data = listIterator.next();
                listIterator.remove();
                if (data instanceof ImageFileBean) compressFiles.add(new ImageCompressBean((ImageFileBean) data));
                else if (data instanceof VideoFileBean) compressFiles.add(new VideoCompressBean((VideoFileBean) data));
                else if (data instanceof AudioFileBean) compressFiles.add(new AudioCompressBean((AudioFileBean) data));
                else compressFiles.add(data);
            }
            ((PictureViewSelectAdapter) adapter).addDatas(compressFiles);

            // compress files
            for (CheckableFileBean compressFile : compressFiles) {
                if (compressFile instanceof CompressState) threadPool.execute(new CompressRunnable((CompressState) compressFile, (PictureViewSelectAdapter) adapter));
            }
        }
    }

    protected static class CompressRunnable implements Runnable {
        private final CompressState compressFile;
        private final PictureViewSelectAdapter adapter;

        public CompressRunnable(CompressState compressFile, PictureViewSelectAdapter adapter) {
            this.compressFile = compressFile;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            // 如果列表里不包含这个文件, 所以该文件可能已经被用户移出了
            if (!adapter.getDatas().contains(compressFile)) return;

            boolean compress = compressFile.compress();
            if (!compress) compressFile.checkCopyFile();
        }
    }

    /**
     * 获取数据, 包括网络文件, 压缩文件, 原始文件
     */
    public List<CheckableFileBean> getDatas() {
        return getOriginalDatas();
    }

}
