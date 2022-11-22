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
package me.luzhuo.lib_picture_upload;

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
import me.luzhuo.lib_picture_dialog.PictureSelectBottomDialogView;
import me.luzhuo.lib_picture_select_view.adapter.PictureViewSelectAdapter;
import me.luzhuo.lib_picture_select_view.adapter.PictureViewShowAdapter;
import me.luzhuo.lib_picture_upload.bean.AudioUploadBean;
import me.luzhuo.lib_picture_upload.bean.ImageUploadBean;
import me.luzhuo.lib_picture_upload.bean.UploadState;
import me.luzhuo.lib_picture_upload.bean.VideoUploadBean;

/**
 * 用户选择后, 自动上传到网络 (一个一个的上传)
 * 继承的功能有: 底部弹窗 / 文件压缩 / 拍摄
 */
public class PictureSelectUploadView extends PictureSelectBottomDialogView {
    private static final ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public PictureSelectUploadView(@NonNull Context context) {
        super(context);
    }

    public PictureSelectUploadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectUploadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addDatas(List<? extends CheckableFileBean> files) {
        // TODO 因为父类已经将files清空, 如果不清空, 就会重复添加
        super.addDatas(files);
        if (adapter instanceof PictureViewShowAdapter) {
            ((PictureViewShowAdapter) adapter).addDatas(files);
        } else if (adapter instanceof PictureViewSelectAdapter) {

            // local file cover upload file.
            List<CheckableFileBean> uploadFiles = new ArrayList<>();
            ListIterator<? extends CheckableFileBean> listIterator = files.listIterator();
            while(listIterator.hasNext()) {
                CheckableFileBean data = listIterator.next();
                listIterator.remove();
                if (data instanceof ImageFileBean) uploadFiles.add(getImageUploadBean((ImageFileBean) data));
                else if (data instanceof VideoFileBean) uploadFiles.add(getVideoUploadBean((VideoFileBean) data));
                else if (data instanceof AudioFileBean) uploadFiles.add(getAudioUploadBean((AudioFileBean) data));
                else uploadFiles.add(data);
            }
            ((PictureViewSelectAdapter) adapter).addDatas(uploadFiles);

            // compress files
            for (CheckableFileBean uploadFile : uploadFiles) {
                if (uploadFile instanceof UploadState) threadPool.execute(new UploadRunnable((UploadState) uploadFile, (PictureViewSelectAdapter) adapter));
            }
        }
    }

    public ImageUploadBean getImageUploadBean(ImageFileBean data){
        return new ImageUploadBean(data);
    }

    public VideoUploadBean getVideoUploadBean(VideoFileBean data){
        return new VideoUploadBean(data);
    }

    public AudioUploadBean getAudioUploadBean(AudioFileBean data){
        return new AudioUploadBean(data);
    }

    protected static class UploadRunnable implements Runnable {
        private final UploadState uploadFile;
        private final PictureViewSelectAdapter adapter;

        public UploadRunnable(UploadState uploadFile, PictureViewSelectAdapter adapter) {
            this.uploadFile = uploadFile;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            if (!adapter.getDatas().contains(uploadFile)) return;

            uploadFile.upload();
        }
    }
}
