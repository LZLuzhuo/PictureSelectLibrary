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
package me.luzhuo.lib_picture_select.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.luzhuo.lib_core.ui.dialog.Dialog;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.adapter.PictureSelectAdapter;
import me.luzhuo.lib_picture_select.adapter.PictureSelectPreviewBottomAdapter;

/**
 * 相册预览的底部
 */
public class PictureSelectPreviewBottomBar extends RelativeLayout implements CompoundButton.OnCheckedChangeListener {
    private PictureSelectPreviewBottomAdapter adapter;
    private RecyclerView preview_bottom_list;
    private CheckBox preview_selected;
    private OnPreviewBottomBarCallback callback;

    public PictureSelectPreviewBottomBar(Context context) {
        super(context);
    }

    public PictureSelectPreviewBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectPreviewBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.picture_select_layout_preview_bottom_bar, this, true);
        preview_bottom_list = findViewById(R.id.picture_select_preview_bottom_list);
        preview_selected = findViewById(R.id.picture_select_preview_selected);

        preview_selected.setOnCheckedChangeListener(this);

        preview_bottom_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new PictureSelectPreviewBottomAdapter();
        preview_bottom_list.setAdapter(adapter);
    }

    public int getSelectCount() {
        return PictureSelectAdapter.selectCount;
    }

    public int getMaxCount() {
        return PictureSelectAdapter.maxCount;
    }

    public void setSelectPreviewDatas(List<FileBean> datas) {
        adapter.setData(datas);
    }

    /**
     * 当前位置的文件
     */
    public void currentSelectedData(FileBean data) {
        adapter.currentSelected(data);
    }

    /**
     * 添加被选中的文件
     */
    public void addSelectedData(FileBean data) {
        adapter.addSelected(data);
    }

    /**
     * 移除被选中的文件
     */
    public void removeSelectedData(FileBean data) {
        adapter.removeSelected(data);
    }

    /**
     * 检查是否选中
     */
    public void checkSelect(boolean checkSelect) {
        preview_selected.setChecked(checkSelect);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 不处理非用户触发的回调
        if (!buttonView.isPressed()) return;

        // 选择已达上限, 将不让选择
        if (isChecked && getSelectCount() >= getMaxCount()) {
            String content = new StringBuilder().append("你最多只能选择").append(getMaxCount()).append("文件").toString();
            Dialog.instance().show(getContext(), "注意", content, "确定", null, true, null, null);
            buttonView.setChecked(false);
            return;
        }

        if (callback != null) callback.onCheckedChanged(isChecked);
    }

    public interface OnPreviewBottomBarCallback {
        /**
         * 用户手动点击选择触发的回调
         */
        public void onCheckedChanged(boolean isChecked);
    }

    public void setOnPreviewBottomBarListener(OnPreviewBottomBarCallback callback) {
        this.callback = callback;
    }
}
