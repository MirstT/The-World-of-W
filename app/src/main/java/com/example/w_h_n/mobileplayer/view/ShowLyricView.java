package com.example.w_h_n.mobileplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.w_h_n.mobileplayer.domain.Lyric;
import com.example.w_h_n.mobileplayer.utils.DensityUtil;

import java.util.ArrayList;

public class ShowLyricView extends android.support.v7.widget.AppCompatTextView {
    private ArrayList<Lyric> lyrics;//歌词列表
    private Paint paint;
    private Paint whitepaint;

    private int width;
    private int height;
    private int index;//歌词列表中的索引,是第几句歌词
    private float textHeight; //每行的高
    private float currentPosition; //当前播放进度
    private float sleepTime;//高亮显示的时间或者是休眠时间
    private float timePoint;//时间戳，什么时刻到高亮哪句歌词

    //设置歌词列表
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

    }

    private void initView(Context context) {
        textHeight = DensityUtil.dip2px(context,19);

        //创建画笔
        paint = new Paint();
        paint.setColor(Color.rgb(246,230,121));
        paint.setTextSize(DensityUtil.dip2px(context,18));
        paint.setAntiAlias(true);
        //设置居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setTextSize(DensityUtil.dip2px(context,16));
        whitepaint.setAntiAlias(true);
        whitepaint.setTextAlign(Paint.Align.CENTER);

//        lyrics = new ArrayList<>();
//
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1000; i++) {
//            lyric.setTimePoint(1000 * i);
//            lyric.setSleepTime(1500 + i);
//            lyric.setContent(i + "aaaaaaaaaaaaaaa" + i);
//            //把歌词添加到集合中
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {
            //往上推移
            float plush = 0;
            if (sleepTime == 0){
                plush =0;
            }else {
                //平移
                float delta = ((currentPosition - timePoint)/sleepTime)*textHeight;
                plush = textHeight + delta;
            }
            canvas.translate(0,-plush);


            //绘制歌词:绘制当前句
            String currentText = lyrics.get(index).getContent();
            canvas.drawText(currentText, width / 2, height / 2, paint);
            //绘制前面部分，
            float tempY = height / 2;//Y轴的中间坐标
            for (int i = index - 1; i >= 0; i--) {
                //每一句歌词
                String preContent = lyrics.get(i).getContent();

                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitepaint);
            }


            // 绘制后面部分
            tempY = height / 2;//Y轴的中间坐标
            for (int i = index + 1; i < lyrics.size(); i++) {
                //每一句歌词
                String nextContent = lyrics.get(i).getContent();

                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitepaint);
            }

        } else {
            //没有歌词
            canvas.drawText("没有歌词", width / 2, height / 2, paint);
        }
    }

    //根据当前播放位置，找出该高亮显示哪一句
    public void setshowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null || lyrics.size() == 0)
            return;


        for (int i = 1; i < lyrics.size(); i++) {
            if (currentPosition < lyrics.get(i).getTimePoint()){
                int tempIndex = i -1;
                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()){
                    //当前正在播放的那句歌词
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();

                }
            }
        }

        //重新绘制
        invalidate();//在主线程中

    }
}
