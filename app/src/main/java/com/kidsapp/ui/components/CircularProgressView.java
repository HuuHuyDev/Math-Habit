package com.kidsapp.ui.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.kidsapp.R;

public class CircularProgressView extends View {

    private final RectF arcBounds = new RectF();
    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float progress = 0f;
    private final float strokeWidth;
    private final float textSize;

    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        strokeWidth = getResources().getDimension(R.dimen.progress_stroke_width);
        textSize = getResources().getDimension(R.dimen.progress_text_size);
        initPaints();
    }

    private void initPaints() {
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth);
        bgPaint.setColor(ContextCompat.getColor(getContext(), R.color.progress_bg));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(ContextCompat.getColor(getContext(), R.color.progress_primary));

        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float padding = strokeWidth / 2f;
        arcBounds.set(padding, padding, getWidth() - padding, getHeight() - padding);

        canvas.drawArc(arcBounds, 0, 360, false, bgPaint);
        float sweep = 360f * progress / 100f;
        canvas.drawArc(arcBounds, -90, sweep, false, progressPaint);

        String text = (int) progress + "%";
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float x = getWidth() / 2f;
        float y = getHeight() / 2f - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(text, x, y, textPaint);
    }

    public void setProgress(float value) {
        progress = clamp(value);
        invalidate();
    }

    public void animateTo(float value) {
        float target = clamp(value);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, target);
        animator.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        animator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    private float clamp(float value) {
        return Math.max(0f, Math.min(100f, value));
    }
}

