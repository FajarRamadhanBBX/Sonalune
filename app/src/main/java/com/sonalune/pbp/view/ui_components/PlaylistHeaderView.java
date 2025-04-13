package com.sonalune.pbp.view.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sonalune.pbp.R;

/**
 * TODO: document your custom view class.
 */

public class PlaylistHeaderView extends LinearLayout {
    public PlaylistHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.sample_playlist_header_view, this);
    }

    public void setCoverImage(int resId) {
        ImageView img = findViewById(R.id.img_cover);
        img.setImageResource(resId);
    }
}
