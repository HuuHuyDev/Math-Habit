package com.kidsapp.ui.parent.report.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.kidsapp.ui.parent.report.model.WeeklyStat;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view để vẽ biểu đồ cột stacked bar chart
 * Hiển thị thống kê hàng tuần với gradient giống Figma
 */
public class WeeklyChartView extends View {

    private List<WeeklyStat> data = new ArrayList<>();
    private Paint habitPaint;
    private Paint quizPaint;
    private Paint labelPaint;
    private Paint gridPaint;
    private Paint valuePaint;
    private Paint legendPaint;
    private Paint legendBoxPaint;

    private float maxValue = 10f; // Giá trị max của trục Y
    private float animationProgress = 0f; // 0 -> 1 cho animation

    private static final int BAR_WIDTH_DP = 28;
    private static final int BAR_SPACING_DP = 12;
    private static final int CHART_PADDING_TOP_DP = 50;
    private static final int CHART_PADDING_BOTTOM_DP = 30;
    private static final int CORNER_RADIUS_DP = 6;

    public WeeklyChartView(Context context) {
        super(context);
        init();
    }

    public WeeklyChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeeklyChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Paint cho cột Habit (gradient xanh)
        habitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        habitPaint.setStyle(Paint.Style.FILL);

        // Paint cho cột Quiz (màu hồng)
        quizPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        quizPaint.setColor(Color.parseColor("#F472B6"));
        quizPaint.setStyle(Paint.Style.FILL);

        // Paint cho label (T2, T3, ...)
        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.parseColor("#6B7280"));
        labelPaint.setTextSize(dpToPx(12));
        labelPaint.setTextAlign(Paint.Align.CENTER);

        // Paint cho grid (nét đứt)
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E5E7EB"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(dpToPx(1));

        // Paint cho số hiển thị trên cột
        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.parseColor("#1F2937"));
        valuePaint.setTextSize(dpToPx(11));
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setFakeBoldText(true);

        // Paint cho legend text
        legendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        legendPaint.setColor(Color.parseColor("#6B7280"));
        legendPaint.setTextSize(dpToPx(12));
        legendPaint.setTextAlign(Paint.Align.LEFT);

        // Paint cho legend box
        legendBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        legendBoxPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Set dữ liệu cho chart
     */
    public void setData(List<WeeklyStat> stats) {
        this.data = stats != null ? stats : new ArrayList<>();

        // Tính max value để scale chart
        maxValue = 10f; // min 10
        for (WeeklyStat stat : this.data) {
            int total = stat.getTotalCount();
            if (total > maxValue) {
                maxValue = total;
            }
        }
        maxValue = (float) Math.ceil(maxValue * 1.2); // thêm 20% padding

        // Animate chart
        animateChart();
    }

    /**
     * Animation tăng chiều cao cột
     */
    private void animateChart() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data.isEmpty()) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        float chartHeight = height - dpToPx(CHART_PADDING_TOP_DP) - dpToPx(CHART_PADDING_BOTTOM_DP);
        float chartTop = dpToPx(CHART_PADDING_TOP_DP);

        float barWidth = dpToPx(BAR_WIDTH_DP);
        float barSpacing = dpToPx(BAR_SPACING_DP);
        float totalBarSpace = data.size() * barWidth + (data.size() - 1) * barSpacing;
        float startX = (width - totalBarSpace) / 2f;

        // Vẽ legend (chú thích) ở trên cùng
        drawLegend(canvas, width);

        // Vẽ grid lines (nét ngang)
        drawGridLines(canvas, chartTop, chartHeight, startX, totalBarSpace);

        // Vẽ từng cột
        for (int i = 0; i < data.size(); i++) {
            WeeklyStat stat = data.get(i);
            float x = startX + i * (barWidth + barSpacing);

            // Vẽ cột với animation
            drawBar(canvas, stat, x, chartTop, barWidth, chartHeight);

            // Vẽ label (T2, T3, ...)
            float labelY = height - dpToPx(10);
            canvas.drawText(stat.getLabel(), x + barWidth / 2, labelY, labelPaint);
        }
    }

    /**
     * Vẽ legend (chú thích) phân biệt Thói quen và Quiz
     */
    private void drawLegend(Canvas canvas, int width) {
        float legendY = dpToPx(15);
        float centerX = width / 2f;
        
        // Tính tổng độ rộng của legend để căn giữa
        float boxSize = dpToPx(12);
        float spacing = dpToPx(8);
        float textWidth1 = legendPaint.measureText("Thói quen");
        float textWidth2 = legendPaint.measureText("Quiz");
        float totalWidth = boxSize + spacing + textWidth1 + dpToPx(16) + boxSize + spacing + textWidth2;
        
        float startX = centerX - totalWidth / 2f;
        
        // Legend 1: Thói quen (gradient xanh)
        RectF habitBox = new RectF(startX, legendY - boxSize / 2, startX + boxSize, legendY + boxSize / 2);
        LinearGradient habitGradient = new LinearGradient(
                habitBox.left, habitBox.top,
                habitBox.left, habitBox.bottom,
                Color.parseColor("#5DD5F6"),
                Color.parseColor("#43B8E0"),
                Shader.TileMode.CLAMP
        );
        legendBoxPaint.setShader(habitGradient);
        canvas.drawRoundRect(habitBox, dpToPx(3), dpToPx(3), legendBoxPaint);
        
        canvas.drawText("Thói quen", startX + boxSize + spacing, legendY + dpToPx(4), legendPaint);
        
        // Legend 2: Quiz (màu hồng)
        float legend2StartX = startX + boxSize + spacing + textWidth1 + dpToPx(16);
        RectF quizBox = new RectF(legend2StartX, legendY - boxSize / 2, legend2StartX + boxSize, legendY + boxSize / 2);
        legendBoxPaint.setShader(null);
        legendBoxPaint.setColor(Color.parseColor("#F472B6"));
        canvas.drawRoundRect(quizBox, dpToPx(3), dpToPx(3), legendBoxPaint);
        
        canvas.drawText("Quiz", legend2StartX + boxSize + spacing, legendY + dpToPx(4), legendPaint);
    }

    /**
     * Vẽ grid lines ngang
     */
    private void drawGridLines(Canvas canvas, float chartTop, float chartHeight, float startX, float totalBarSpace) {
        int gridCount = 4;
        for (int i = 0; i <= gridCount; i++) {
            float y = chartTop + (chartHeight / gridCount) * i;
            canvas.drawLine(startX, y, startX + totalBarSpace, y, gridPaint);
        }
    }

    /**
     * Vẽ một cột stacked (Habit + Quiz)
     */
    private void drawBar(Canvas canvas, WeeklyStat stat, float x, float chartTop, float barWidth, float chartHeight) {
        int habitCount = stat.getHabitCount();
        int quizCount = stat.getQuizCount();
        int totalCount = stat.getTotalCount();

        if (totalCount == 0) {
            return;
        }

        // Tính chiều cao tương ứng với data
        float totalHeight = (totalCount / maxValue) * chartHeight * animationProgress;
        float habitHeight = (habitCount / (float) totalCount) * totalHeight;
        float quizHeight = (quizCount / (float) totalCount) * totalHeight;

        float cornerRadius = dpToPx(CORNER_RADIUS_DP);

        // Vẽ phần Quiz (phía dưới - màu hồng)
        if (quizCount > 0) {
            RectF quizRect = new RectF(
                    x,
                    chartTop + chartHeight - quizHeight,
                    x + barWidth,
                    chartTop + chartHeight
            );

            // Chỉ bo góc dưới nếu không có Habit phía trên
            if (habitCount == 0) {
                canvas.drawRoundRect(quizRect, cornerRadius, cornerRadius, quizPaint);
            } else {
                // Không bo góc nếu có Habit phía trên
                canvas.drawRect(quizRect, quizPaint);
            }
        }

        // Vẽ phần Habit (phía trên - gradient xanh)
        if (habitCount > 0) {
            RectF habitRect = new RectF(
                    x,
                    chartTop + chartHeight - totalHeight,
                    x + barWidth,
                    chartTop + chartHeight - quizHeight
            );

            // Tạo gradient cho Habit
            LinearGradient gradient = new LinearGradient(
                    x, habitRect.top,
                    x, habitRect.bottom,
                    Color.parseColor("#5DD5F6"),
                    Color.parseColor("#43B8E0"),
                    Shader.TileMode.CLAMP
            );
            habitPaint.setShader(gradient);

            // Bo góc trên
            canvas.drawRoundRect(habitRect, cornerRadius, cornerRadius, habitPaint);

            // Nếu có Quiz bên dưới, vẽ thêm hình chữ nhật để che phần bo góc dưới
            if (quizCount > 0 && habitRect.height() > cornerRadius) {
                RectF bottomRect = new RectF(
                        habitRect.left,
                        habitRect.bottom - cornerRadius,
                        habitRect.right,
                        habitRect.bottom
                );
                canvas.drawRect(bottomRect, habitPaint);
            }
        }

        // Vẽ số tổng phía trên cột (nếu có animation đủ lớn)
        if (animationProgress > 0.3f && totalCount > 0) {
            float valueY = chartTop + chartHeight - totalHeight - dpToPx(8);
            canvas.drawText(String.valueOf(totalCount), x + barWidth / 2, valueY, valuePaint);
        }
    }

    /**
     * Convert dp to px
     */
    private float dpToPx(int dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
