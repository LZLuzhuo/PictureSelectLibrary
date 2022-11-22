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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.PopupWindow;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.luzhuo.lib_core.app.impl.AnimatorListenerImpl;
import me.luzhuo.lib_core.ui.calculation.UICalculation;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select.adapter.HeaderBucketPopAdapter;
import me.luzhuo.lib_picture_select.bean.PictureGroup;

/**
 * 相册组
 */
public class PictureSelectBucketView extends LinearLayoutCompat implements HeaderBucketPopAdapter.OnBucketPopCallback, View.OnClickListener {
    public PictureSelectBucketView(@NonNull Context context) {
        super(context);
    }

    public PictureSelectBucketView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectBucketView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final HeaderBucketPopAdapter adapter;
    @Nullable
    private HeaderBucketPopAdapter.OnBucketPopCallback callback;
    @Nullable
    private PopupWindow.OnDismissListener dismissListener;
    private final RecyclerView recyclerView;
    private final View pop_bg;
    private final View pop_rec_bg;

    {
        this.setVisibility(View.GONE);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.picture_select_layout_header_bucket_pop, this, true);
        recyclerView = view.findViewById(R.id.header_bucket_pop_rec);
        pop_bg = view.findViewById(R.id.header_bucket_pop_bg);
        pop_rec_bg = view.findViewById(R.id.header_bucket_pop_rec_bg);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HeaderBucketPopAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnBucketPopCallback(this);

        this.setOnClickListener(this);

        initRecyclerHeight();
    }

    // 超过最大数量, 将限制RecyclerView的高度
    private int MaxCountLimit;
    // 最大高度的比例
    private final static float MaxHeightLimitPercent = 0.6f;
    private int MaxHeightLimit;
    private void initRecyclerHeight() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.picture_select_item_header_bucket_pop, null, false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = new UICalculation(getContext()).getDisplay()[1];
        MaxHeightLimit = (int) (height * MaxHeightLimitPercent);
        MaxCountLimit = MaxHeightLimit / height;
    }

    public void setDatas(Map<Long, PictureGroup> pictureBucket) {
        if (pictureBucket.size() > MaxCountLimit) recyclerView.getLayoutParams().height = MaxHeightLimit;
        else recyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        adapter.setDatas(pictureBucket);
    }

    public void show(long bucketId) {
        if (this.getVisibility() == View.VISIBLE) {
            dismiss();
        } else {
            adapter.setCurrentBucket(bucketId);
            show();
        }
    }

    public void setOnBucketPopCallback(HeaderBucketPopAdapter.OnBucketPopCallback callback) {
        this.callback = callback;
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void onBucketSelect(long bucket) {
        if (isAnimating) return;
        if (callback != null) callback.onBucketSelect(bucket);
        dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == this) {
            if (!isAnimating) dismiss();
            if (dismissListener != null) dismissListener.onDismiss();
        }
    }

    public boolean isAnimating = false;
    public void show() {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(pop_bg, "alpha", 0f, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(pop_rec_bg, "translationY", -MaxHeightLimit, 0f);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerImpl() {
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
                PictureSelectBucketView.this.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                isAnimating = false;
            }
        });
        set.start();
    }

    public void dismiss() {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(pop_bg, "alpha", 1f, 0f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(pop_rec_bg, "translationY", 0f, -MaxHeightLimit);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerImpl() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                PictureSelectBucketView.this.setVisibility(View.GONE);
            }
        });
        set.start();
    }
}
