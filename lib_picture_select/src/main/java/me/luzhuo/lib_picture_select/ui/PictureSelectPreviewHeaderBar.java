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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import androidx.fragment.app.FragmentActivity;
import me.luzhuo.lib_core.app.color.ColorManager;
import me.luzhuo.lib_core.ui.calculation.UICalculation;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.adapter.PictureSelectAdapter;

/**
 * 相册预览的头部
 */
public class PictureSelectPreviewHeaderBar extends RelativeLayout implements View.OnClickListener {
    public PictureSelectPreviewHeaderBar(Context context) {
        super(context);
    }

    public PictureSelectPreviewHeaderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectPreviewHeaderBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final View picture_select_close;
    private final TextView picture_select_number;
    private final TextView picture_select_complete;
    private final ColorManager color = new ColorManager();
    private OnPreviewHeaderBarCallback callback;

    {
        LayoutInflater.from(getContext()).inflate(R.layout.picture_select_layout_preview_header_bar, this, true);
        View statusBarSpace = findViewById(R.id.picture_select_statusbar_space);
        picture_select_close = findViewById(R.id.picture_select_close);
        picture_select_number = findViewById(R.id.picture_select_number);
        picture_select_complete = findViewById(R.id.picture_select_complete);

        statusBarSpace.getLayoutParams().height = new UICalculation(getContext()).getStatusBarHeight(statusBarSpace);

        picture_select_close.setOnClickListener(this);
        picture_select_complete.setOnClickListener(this);
    }

    public int getSelectCount() {
        return PictureSelectAdapter.selectCount;
    }

    public int getMaxCount() {
        return PictureSelectAdapter.maxCount;
    }

    @Override
    public void onClick(View v) {
        if (v == picture_select_close) {
            ((FragmentActivity) getContext()).finish();
        } else if (v == picture_select_complete) {
            if (callback != null) callback.onComplete();
        }
    }

    public void setCurrentIndex(int index, int count) {
        picture_select_number.setText(String.format(Locale.CHINESE, "%d/%d", index + 1, count));
    }

    public void updateCompleteButton() {
        if (getSelectCount() > 0) {
            picture_select_complete.setBackgroundResource(R.drawable.picture_select_bg_complete);
            picture_select_complete.setTextColor(color.getColor(R.color.picture_select_complete_text));
            picture_select_complete.setText(String.format(Locale.CHINESE, "完成(%d/%d)", getSelectCount(), getMaxCount()));
        } else {
            picture_select_complete.setBackgroundResource(R.drawable.picture_select_bg_complete_default);
            picture_select_complete.setTextColor(color.getColor(R.color.picture_select_complete_text_default));
            picture_select_complete.setText("完成");
        }
    }

    public interface OnPreviewHeaderBarCallback {
        public void onComplete();
    }

    public void setOnPreviewHeaderBarCallback(OnPreviewHeaderBarCallback callback) {
        this.callback = callback;
    }
}
