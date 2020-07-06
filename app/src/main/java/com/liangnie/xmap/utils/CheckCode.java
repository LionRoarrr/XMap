package com.liangnie.xmap.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class CheckCode {
    //定义随机数组，去除一些混淆的数字和字母
    private static final char[] chars = {
            '2', '3', '4', '5',  '7', '8',
            'a',  'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p',  'r', 's',  'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B',  'D', 'E', 'F',  'H',  'J', 'K', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static CheckCode bmpCode;

    public static CheckCode getInstance() {
        if(bmpCode == null)
            bmpCode = new CheckCode();
        return bmpCode;
    }

    private static final int DEFAULT_CODE_LENGTH = 4;   //验证码的随机个数
    private static final int DEFAULT_CODE_FONT_SIZE = 25;   //默认字体大小
    private static final int DEFAULT_LINE_NUMBER = 5;   //默认线条的个数
    //padding值
    private static final int BASE_PADDING_LEFT = 10, RANGE_PADDING_LEFT = 15,
                             BASE_PADDING_TOP = 15, RANGE_PADDING_TOP = 20;
    private static final int DEFAULT_WIDTH = 100, DEFAULT_HEIGHT = 40;  //默认宽高

    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
    private int base_padding_left = BASE_PADDING_LEFT, range_padding_left = RANGE_PADDING_LEFT,
                base_padding_top = BASE_PADDING_TOP, range_padding_top = RANGE_PADDING_TOP;
    private int code_length = DEFAULT_CODE_LENGTH, line_number = DEFAULT_LINE_NUMBER, font_size = DEFAULT_CODE_FONT_SIZE;

    private String code;
    private int padding_left, padding_top;
    private Random random = new Random();   //随机数生成器
    //验证码图片
    public Bitmap createBitmap() {
        padding_left = 0;
        Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bp);  //画布

        code = createCode();

        c.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);   //设置抖动，使图像缓和
        paint.setTextSize(font_size);
        //画验证码
        for(int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);
        }
        //画线条
        for(int i = 0; i < line_number; i++) {
            drawLine(c, paint);
        }
        c.save();
        c.restore();
        return bp;
    }

    public String getCode() {
        return code;
    }
    //画干扰线
    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startx = random.nextInt(width);
        int starty = random.nextInt(height);
        int stopx = random.nextInt(width);
        int stopy = random.nextInt(height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startx, starty, stopx, stopy, paint);
    }
    //随机生成颜色
    private int randomColor() {
        return randomColor(1);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }
    //随机生成padding值
    private void randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left);
        padding_top = base_padding_top + random.nextInt(range_padding_top);
    }
    //随机生成字符样式、颜色、粗细、倾斜度
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());    //true为粗体，false为非粗体
        float skewx = random.nextInt(11) / 10;
        skewx = random.nextBoolean() ? skewx : -skewx;
        paint.setTextSkewX(skewx);  //float类型参数，负数表示右斜，正数左斜

    }
    //生成验证码
    private String createCode() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < code_length; i++) {
            builder.append(chars[random.nextInt(chars.length)]);    //生成四个字符
        }
        return builder.toString();
    }
}
