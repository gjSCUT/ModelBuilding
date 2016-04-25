package com.bn.main;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bn.draw.DrawSurfaceView;

public class DrawActivity extends Activity {

    private Button pen, eraser, ok, cancel;
    private SeekBar adjust;
    private TextView strech;
    private DrawSurfaceView drawSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setFinishOnTouchOutside(false);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_main);

        pen = (Button) findViewById(R.id.pen);
        eraser = (Button) findViewById(R.id.eraser);
        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);
        adjust = (SeekBar) findViewById(R.id.adjust);
        //drawSurfaceView = (DrawSurfaceView)findViewById(R.id.drawSurfaceView);
        //drawSurfaceView = new DrawSurfaceView(this);
        strech = (TextView) findViewById(R.id.stretch);

        pen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根

            }
        });
        eraser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根

            }
        });
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根

            }
        });
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                finish();
            }
        });
        adjust.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO 自动生成的方法存根

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO 自动生成的方法存根

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO 自动生成的方法存根

            }
        });

    }

}
