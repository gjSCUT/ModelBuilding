package com.bn.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bn.csgStruct.Vector2f;
import com.bn.drawSweep.DrawSurfaceView;

import java.util.List;

public class DrawActivity extends Activity {

    private Button zhixian, quxian, sure, cancel;
    private DrawSurfaceView drawFace;
    int mode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setFinishOnTouchOutside(false);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mode = getIntent().getIntExtra("mode",1);
        // 设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_main);

        drawFace = (DrawSurfaceView) findViewById(R.id.drawFace);
        drawFace.requestFocus();//获取焦点
        drawFace.setFocusableInTouchMode(true);//设置为可触控
        drawFace.setFeatureMode(mode)
        ;
        zhixian = (Button) findViewById(R.id.zhixian);
        quxian = (Button) findViewById(R.id.quxian);
        sure = (Button) findViewById(R.id.sure);
        cancel = (Button) findViewById(R.id.cancel);

        zhixian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFace.setPaintType(0);
            }
        });
        quxian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFace.setPaintType(1);
            }
        });
        sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == 1) {
                    List<Vector2f> face = drawFace.getSweepFace();
                    List<Vector2f> line = drawFace.getSweepLine();
                    Intent intent = new Intent();
                    float[] faceFloat = new float[face.size() * 2];
                    int index = 0;
                    for (Vector2f vector2f : face) {
                        faceFloat[index++] = vector2f.x;
                        faceFloat[index++] = vector2f.y;
                    }
                    float[] lineFloat = new float[line.size() * 2];
                    index = 0;
                    for (Vector2f vector2f : line) {
                        lineFloat[index++] = vector2f.x;
                        lineFloat[index++] = vector2f.y;
                    }
                    intent.putExtra("face", faceFloat);
                    intent.putExtra("line", lineFloat);
                    DrawActivity.this.setResult(1, intent);
                }
                else if(mode == 2){
                    List<Vector2f> face = drawFace.getRevolveFace();
                    Intent intent = new Intent();
                    float[] faceFloat = new float[face.size() * 2];
                    int index = 0;
                    for (Vector2f vector2f : face) {
                        faceFloat[index++] = vector2f.x;
                        faceFloat[index++] = vector2f.y;
                    }
                    intent.putExtra("face", faceFloat);
                    DrawActivity.this.setResult(2, intent);
                }
                finish();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawActivity.this.setResult(0);
                finish();
            }
        });
    }
}
