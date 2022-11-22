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

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cc.shinichi.library.ImagePreview;
import me.luzhuo.lib_core.app.base.CoreBaseApplication;
import me.luzhuo.lib_core.data.hashcode.HashManager;
import me.luzhuo.lib_file.FileManager;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.CheckableFileBean;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_file.enums.FileType;
import me.luzhuo.lib_picture_compress.bean.AudioCompressBean;
import me.luzhuo.lib_picture_compress.bean.ImageCompressBean;
import me.luzhuo.lib_picture_compress.bean.VideoCompressBean;
import me.luzhuo.lib_picture_select_view.bean.AudioNetBean;
import me.luzhuo.lib_picture_select_view.bean.ImageNetBean;
import me.luzhuo.lib_picture_select_view.bean.VideoNetBean;

public class PictureSelectUtils {

    /**
     * 从录制的Uri获取音频数据
     * @param context Context
     * @param uri Uri
     * @return AudioFileBean or null
     */
    @Nullable
    public static AudioFileBean queryAudioByUri(@Nullable Context context, @Nullable Uri uri) {
        if (context == null || uri == null) return null;

        Uri filesUri = MediaStore.Files.getContentUri("external");
        long _id = ContentUris.parseId(uri);
        Cursor cursor = context.getContentResolver().query(filesUri, null, "media_type=2 AND _id=?", new String[]{String.valueOf(_id)}, null);
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.MIME_TYPE));
            Uri pathUri = ContentUris.withAppendedId(filesUri, cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)));
            long addedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Downloads.DATE_ADDED));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Downloads.SIZE));

            long bucketId = cursor.getLong(cursor.getColumnIndex("bucket_id"));
            String bucketName = cursor.getString(cursor.getColumnIndex("bucket_display_name"));

            int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Downloads.WIDTH));
            int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Downloads.HEIGHT));
            int duration = cursor.getInt(cursor.getColumnIndex("duration"));

            String url = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)); // /storage/emulated/0/Music/music.flac

            if (FileType.getFileType(mimeType) == FileType.Audio) {
                AudioFileBean fileBean = new AudioFileBean(id, name, mimeType, pathUri, url, bucketId, bucketName, size, addedDate, duration);
                return fileBean;
            } else {
                return null;
            }
        }
        cursor.close();
        return null;
    }

    /**
     * AndroidQ+ 将其移到私有目录
     * @return true移动成功, false移动失败
     */
    public static boolean checkCopyFile(FileBean fileBean) {
        String copyFileUrl;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                String outPath = new FileManager().getCacheDirectory().getAbsolutePath() + File.separator + HashManager.getInstance().getUuid(fileBean.uriPath.toString());
                new FileManager().Stream2File(CoreBaseApplication.appContext.getContentResolver().openInputStream(fileBean.uriPath), outPath);
                copyFileUrl = outPath;
            } catch (Exception var3) {
                var3.printStackTrace();
                return false;
            }
        } else {
            copyFileUrl = fileBean.urlPath;
        }

        if (fileBean instanceof ImageCompressBean) ((ImageCompressBean) fileBean).compressPath = copyFileUrl;
        else if (fileBean instanceof VideoCompressBean) ((VideoCompressBean) fileBean).compressPath = copyFileUrl;
        else if (fileBean instanceof AudioCompressBean) ((AudioCompressBean) fileBean).compressPath = copyFileUrl;
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private static final FileManager fileManager = new FileManager();

    public static List<String> getOriginStringList(List<CheckableFileBean> fileBeans) {
        List<String> lists = new ArrayList<>();
        for (CheckableFileBean data : fileBeans) {
            if (data instanceof AudioNetBean) lists.add(((AudioNetBean) data).netUrl);
            else if (data instanceof VideoNetBean) lists.add(((VideoNetBean) data).netUrl);
            else if (data instanceof ImageNetBean) lists.add(((ImageNetBean) data).netUrl);
            else if (data instanceof AudioFileBean) {
                if (fileManager.needUri()) lists.add(((AudioFileBean) data).uriPath.toString());
                else lists.add(((AudioFileBean) data).urlPath);
            } else if (data instanceof VideoFileBean) {
                if (fileManager.needUri()) lists.add(((VideoFileBean) data).uriPath.toString());
                else lists.add(((VideoFileBean) data).urlPath);
            } else if (data instanceof ImageFileBean) {
                if (fileManager.needUri()) lists.add(((ImageFileBean) data).uriPath.toString());
                else lists.add(((ImageFileBean) data).urlPath);
            }
        }
        return lists;
    }

    public static void imagePreview(Context context, int index, List<String> fileBeans) {
        ImagePreview
                .getInstance()
                .setContext(context)
                .setIndex(index)
                .setImageList(fileBeans)
                .setEnableClickClose(true)
                .setEnableDragClose(true)
                .setEnableUpDragClose(true)
                .setShowDownButton(false)
                .setEnableDragCloseIgnoreScale(true)
                .start();
    }
}
