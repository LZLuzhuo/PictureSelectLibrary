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
package me.luzhuo.lib_picture_dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Pair;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import me.luzhuo.lib_core.media.audio.AudioManager;
import me.luzhuo.lib_core.media.audio.IAudioCallback;
import me.luzhuo.lib_core.media.camera.CameraManager;
import me.luzhuo.lib_core.media.camera.ICameraCallback;
import me.luzhuo.lib_core.media.video.IVideoRecorderCallback;
import me.luzhuo.lib_core.media.video.VideoRecorderManager;
import me.luzhuo.lib_core.ui.dialog.BottomDialog;
import me.luzhuo.lib_core.ui.dialog.Dialog;
import me.luzhuo.lib_file.FileManager;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_file.store.FileStore;
import me.luzhuo.lib_permission.Permission;
import me.luzhuo.lib_permission.PermissionCallback;
import me.luzhuo.lib_picture_compress.PictureSelectCompressView;
import me.luzhuo.lib_picture_select.PictureSelectActivity;
import me.luzhuo.lib_picture_select.PictureSelectUtils;

import static me.luzhuo.lib_picture_select.adapter.PictureSelectAdapter.selectCount;
import static me.luzhuo.lib_picture_select.ui.PictureSelectHeaderBar.DefaultBucketId;

/**
 * 底部有弹窗选择的Dialog
 */
public class PictureSelectBottomDialogView extends PictureSelectCompressView implements ICameraCallback, IVideoRecorderCallback, IAudioCallback {
    public PictureSelectBottomDialogView(@NonNull Context context) {
        super(context);
    }

    public PictureSelectBottomDialogView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectBottomDialogView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final CameraManager camera;
    private final VideoRecorderManager recorder;
    private final AudioManager audio;
    private final FileManager fileManager = new FileManager();
    private BottomSheetDialog bottomDialog;

    {
        camera = new CameraManager((FragmentActivity) getContext());
        camera.setCameraCallback(this);
        recorder = new VideoRecorderManager((FragmentActivity) getContext());
        recorder.setVideoRecorderCallback(this);
        audio = new AudioManager((FragmentActivity) getContext());
        audio.setAudioCallback(this);
    }

    @Override
    public void onPictureAdapterSelect() {
        if (bottomDialog == null) bottomDialog = BottomDialog.instance().showMenu(getContext(), Arrays.asList(getFirstMenuName(flags), "相册"), menuItemClick);
        else bottomDialog.show();
    }

    private final BottomDialog.OnMenuItemClick menuItemClick = new BottomDialog.OnMenuItemClick() {
        @Override
        public void onItemClick(List<String> list, int i, String s) {
            if (bottomDialog == null) return;
            bottomDialog.dismiss();

            if (!checkLimit(flags, selectCount >= maxCount)) return;

            if ("相册".equals(s)) {
                PictureSelectActivity.start(getContext(), pictureSelectListener, flags, maxCount - adapter.getItemCount() + 1, showCamera, isOriginal);
            } else if ("录制视频".equals(s)) {
                Permission.request((FragmentActivity) getContext(), new PermissionCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onGranted() {
                        recorder.show();
                    }
                }, Manifest.permission.CAMERA);
            } else if ("录制音频".equals(s)) {
                audio.show();
            } else {
                Permission.request((FragmentActivity) getContext(), new PermissionCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onGranted() {
                        camera.show();
                    }
                }, Manifest.permission.CAMERA);
            }
        }
    };

    private boolean checkLimit(int fileType, boolean isLimit) {
        String content = "你的选择已达上限, 不能进行";
        if (fileType == FileStore.TypeImage || fileType == FileStore.TypeGif) content += "拍照";
        else if (fileType == FileStore.TypeVideo) content += "录像";
        else if (fileType == FileStore.TypeAudio) content += "录音";
        else content += "拍摄";

        if (isLimit) {
            Dialog.instance().show(getContext(), "标题", content, "确定", null, true, null, null);
            return false;
        }
        return true;
    }

    private String getFirstMenuName(int type) {
        if (type == FileStore.TypeImage || type == FileStore.TypeGif || type == FileStore.TypeImage + FileStore.TypeGif) return "拍照";
        else if (type == FileStore.TypeVideo) return "录像";
        else if (type == FileStore.TypeAudio) return "录音";
        else return "拍摄";
    }

    @Override
    public void onCameraCallback(@NonNull String filePath) {
        final File file = new File(filePath);
        Pair<Integer, Integer> size = fileManager.getImageWidthHeight(filePath);
        ImageFileBean fileBean = new ImageFileBean(0, file.getName(), "image/jpeg", Uri.fromFile(file), file.getAbsolutePath(), -1, "Camera", file.length(), System.currentTimeMillis() / 1000, size.first, size.second);

        onCallbackRefresh(fileBean);
    }

    @Override
    public void onVideoRecorderCallback(@NonNull Uri uri, @NonNull File file) {
        Pair<Pair<Integer, Integer>, Long> size = fileManager.getVideoWidthHeight(uri);
        VideoFileBean fileBean = new VideoFileBean(0, file.getName(), "video/mp4", uri, file.getAbsolutePath(), DefaultBucketId, "Camera", file.length(), System.currentTimeMillis() / 1000, size.first.first, size.first.second, size.second.intValue());

        onCallbackRefresh(fileBean);
    }

    @Override
    public void onAudioCallback(@NonNull Uri uri) {
        AudioFileBean fileBean = PictureSelectUtils.queryAudioByUri(getContext(), uri);
        if (fileBean == null) return;

        onCallbackRefresh(fileBean);
    }

    private void onCallbackRefresh(@NonNull FileBean fileBean) {
        fileBean.isChecked = true;
        getOriginalDatas().add(fileBean);
        adapter.notifyDataSetChanged();
    }
}
