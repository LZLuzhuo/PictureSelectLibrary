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
package me.luzhuo.lib_picture_select;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.widget.PopupWindow;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.luzhuo.lib_core.app.base.CoreBaseActivity;
import me.luzhuo.lib_core.media.audio.AudioManager;
import me.luzhuo.lib_core.media.audio.IAudioCallback;
import me.luzhuo.lib_core.media.camera.CameraManager;
import me.luzhuo.lib_core.media.camera.ICameraCallback;
import me.luzhuo.lib_core.media.video.IVideoRecorderCallback;
import me.luzhuo.lib_core.media.video.VideoRecorderManager;
import me.luzhuo.lib_core.ui.dialog.Dialog;
import me.luzhuo.lib_file.FileManager;
import me.luzhuo.lib_file.FileStoreManager;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_file.store.FileStore;
import me.luzhuo.lib_file.store.FileStore.TypeFileStore;
import me.luzhuo.lib_permission.Permission;
import me.luzhuo.lib_permission.PermissionCallback;
import me.luzhuo.lib_picture_select.adapter.HeaderBucketPopAdapter;
import me.luzhuo.lib_picture_select.adapter.PictureSelectAdapter;
import me.luzhuo.lib_picture_select.adapter.PictureSelectAdapterListener;
import me.luzhuo.lib_picture_select.bean.PictureGroup;
import me.luzhuo.lib_picture_select.ui.PictureSelectBottomBar;
import me.luzhuo.lib_picture_select.ui.PictureSelectBucketView;
import me.luzhuo.lib_picture_select.ui.PictureSelectHeaderBar;

import static me.luzhuo.lib_file.store.FileStore.TypeImage;
import static me.luzhuo.lib_picture_select.ui.PictureSelectHeaderBar.DefaultBucketId;

/**
 * 图片选择的Activity界面
 */
public class PictureSelectActivity extends CoreBaseActivity implements PictureSelectAdapterListener, ICameraCallback, IVideoRecorderCallback, IAudioCallback, PictureSelectHeaderBar.PictureSelectHeaderListener, PictureSelectBottomBar.PictureSelectBottomListener, HeaderBucketPopAdapter.OnBucketPopCallback, PopupWindow.OnDismissListener {
    private RecyclerView picture_select_rec;
    private PictureSelectHeaderBar picture_select_header;
    private PictureSelectBottomBar picture_select_bottom;
    private PictureSelectBucketView picture_select_bucket;

    private int fileType;
    private int maxCount;
    private boolean isShowCamera;
    private boolean isOriginal;

    private PictureSelectAdapter adapter;
    private CameraManager camera;
    private VideoRecorderManager recorder;
    private AudioManager audio;
    private final FileManager fileManager = new FileManager();
    private final FileStoreManager fileStoreManager = new FileStoreManager();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final static Handler mainThread = new Handler(Looper.getMainLooper());
    @Nullable
    private static PictureSelectListener listener;

    public static void start(Context context, ActivityResultLauncher<Intent> startActivity, @TypeFileStore int fileType, int maxCount, boolean isShowCamera, boolean isOriginal) {
        Intent intent = new Intent(context, PictureSelectActivity.class);
        intent.putExtra("fileType", fileType);
        intent.putExtra("maxCount", maxCount);
        intent.putExtra("isShowCamera", isShowCamera);
        intent.putExtra("isOriginal", isOriginal);
        startActivity.launch(intent);
    }

    public static void start(Context context, PictureSelectListener listener, @TypeFileStore int fileType, int maxCount, boolean isShowCamera, boolean isOriginal) {
        PictureSelectActivity.listener = listener;
        Intent intent = new Intent(context, PictureSelectActivity.class);
        intent.putExtra("fileType", fileType);
        intent.putExtra("maxCount", maxCount);
        intent.putExtra("isShowCamera", isShowCamera);
        intent.putExtra("isOriginal", isOriginal);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.picture_select_activity);
        overridePendingTransition(R.anim.picture_select_activity_in, R.anim.picture_select_activity_normal);

        fileType = getIntent().getIntExtra("fileType", TypeImage);
        maxCount = getIntent().getIntExtra("maxCount", 0);
        isShowCamera = getIntent().getBooleanExtra("isShowCamera", false);
        isOriginal = getIntent().getBooleanExtra("isOriginal", false);

        picture_select_rec = findViewById(R.id.picture_select_rec);
        picture_select_header = findViewById(R.id.picture_select_header);
        picture_select_bottom = findViewById(R.id.picture_select_bottom);
        picture_select_bucket = findViewById(R.id.picture_select_bucket);

        camera = new CameraManager(this);
        camera.setCameraCallback(this);
        recorder = new VideoRecorderManager(this);
        recorder.setVideoRecorderCallback(this);
        audio = new AudioManager(this);
        audio.setAudioCallback(this);
        picture_select_header.setOnPictureSelectHeaderListener(this);
        picture_select_bottom.setOnPictureSelectBottomListener(this);
        picture_select_bottom.setOriginal(isOriginal);

        picture_select_bucket.setOnBucketPopCallback(this);
        picture_select_bucket.setOnDismissListener(this);

        initView();
        initData();
    }

    private void initView() {
        picture_select_rec.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new PictureSelectAdapter(fileType, maxCount, isShowCamera);
        picture_select_rec.setAdapter(adapter);
        adapter.setPictureSelectListener(this);

        picture_select_header.updateCompleteButton();
        picture_select_bottom.updatePreviewButton();
    }

    private void initData() {
        Permission.request(this, new PermissionCallback() {
            @Override
            public void onGranted() {
                getMediaData();
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 获取媒体数据
     */
    private void getMediaData() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                List<FileBean> lists = fileStoreManager.queryList(fileType);
                picture_select_header.setPictureBucket(lists); // 分组

                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        picture_select_header.updateBucket(DefaultBucketId);
                    }
                });
            }
        });
    }

    @Override
    public void onCamera(boolean isLimit) {
        String content = "你的选择已达上限, 不能进行";
        if (fileType == FileStore.TypeImage || fileType == FileStore.TypeGif) content += "拍照";
        else if (fileType == FileStore.TypeVideo) content += "录像";
        else if (fileType == FileStore.TypeAudio) content += "录音";
        else content += "拍摄";

        if (isLimit) {
            Dialog.instance().show(this, "标题", content, "确定", null, true, null, null);
            return;
        }

        if (fileType == FileStore.TypeImage || fileType == FileStore.TypeGif || fileType == FileStore.TypeImage + FileStore.TypeGif) {
            Permission.request(this, new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onGranted() {
                    camera.show();
                }
            }, Manifest.permission.CAMERA);
        } else if (fileType == FileStore.TypeVideo) {
            Permission.request(this, new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onGranted() {
                    recorder.show();
                }
            }, Manifest.permission.CAMERA);
        } else if (fileType == FileStore.TypeAudio) {
            audio.show();
        } else {
            Permission.request(this, new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onGranted() {
                    camera.show();
                }
            }, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onSelect(boolean isSingle, FileBean file) {
        if (isSingle) onCompleteButton();
        else {
            picture_select_header.updateCompleteButton();
            picture_select_bottom.updatePreviewButton();
        }
    }

    @Override
    public void onShow(FileBean file, int index, List<FileBean> files) {
        PictureSelectPreviewActivity.start(this, index, files, picture_select_header.getBucket(DefaultBucketId).files, previewListener);
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
        AudioFileBean fileBean = PictureSelectUtils.queryAudioByUri(this, uri);
        if (fileBean == null) return;

        onCallbackRefresh(fileBean);
    }

    private void onCallbackRefresh(@NonNull FileBean fileBean) {
        fileBean.isChecked = true;
        picture_select_header.addFile2Bucket(fileBean);
        picture_select_header.updateBucket(picture_select_header.currentBucketId);

        adapter.setSelected(true);
        picture_select_header.updateCompleteButton();
        picture_select_bottom.updatePreviewButton();

        // 单选
        if (maxCount <= 1) onCompleteButton();
    }

    @Override
    public void onCompleteButton() {
        selectComplete(getSelectedFiles());
    }

    /**
     * 获取所有已选中的文件
     * @return 没有已选中的文件, 返回空集合
     */
    @NonNull
    public ArrayList<FileBean> getSelectedFiles() {
        ArrayList<FileBean> selectedFiles = new ArrayList<>();
        PictureGroup bucketGroup = picture_select_header.getBucket(DefaultBucketId);
        if (bucketGroup == null) return selectedFiles;

        for (FileBean fileBean : bucketGroup.files) {
            if (fileBean.isChecked) {
                fileBean.isOrigin = picture_select_bottom.isOrigin();
                selectedFiles.add(fileBean);
            }
        }
        return selectedFiles;
    }

    @Override
    public void onSwitchBucket(@Nullable List<FileBean> files) {
        adapter.setData(files);
    }

    @Override
    public void onClose() {
        finish();
    }

    @Override
    public void openBucketView(long currentBucketId, Map<Long, PictureGroup> pictureBucket) {
        if (picture_select_bucket.isAnimating) return;
        picture_select_header.rotateArrow();
        picture_select_bucket.setDatas(pictureBucket);
        picture_select_bucket.show(currentBucketId);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.picture_select_activity_normal, R.anim.picture_select_activity_out);
    }

    private void selectComplete(ArrayList<FileBean> selectFiles) {
        if (listener == null) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("result", selectFiles);
            setResult(RESULT_OK, intent);
        } else {
            listener.onPictureSelect(selectFiles);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        listener = null;
        super.onDestroy();
    }

    @Override
    public void onPreview() {
        PictureSelectPreviewActivity.start(this, 0, getSelectedFiles(), picture_select_header.getBucket(DefaultBucketId).files, previewListener);
    }

    private final PictureSelectPreviewActivity.OnPictureSelectPreviewListener previewListener = new PictureSelectPreviewActivity.OnPictureSelectPreviewListener(){
        @Override
        public void onCheckedChanged(boolean isChecked) {
            adapter.setSelected(isChecked);
            adapter.notifyDataSetChanged();
            picture_select_header.updateCompleteButton();
            picture_select_bottom.updatePreviewButton();
        }

        @Override
        public void onComplete() {
            onCompleteButton();
        }
    };

    @Override
    public void onBucketSelect(long bucket) {
        picture_select_header.updateBucket(bucket);
    }

    @Override
    public void onDismiss() {
        if (picture_select_bucket.isAnimating) return;
        picture_select_header.rotateArrow();
    }
}
