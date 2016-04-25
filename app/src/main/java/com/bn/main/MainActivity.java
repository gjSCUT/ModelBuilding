package com.bn.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bn.object.Body;

import java.util.Vector;

public class MainActivity extends Activity {
    public Button menu, fill, object, property, delete, cylinder, cube, ball, cone, jiao, bing, cha, redo, undo;
    public LinearLayout first, slide;
    public RelativeLayout left, right;        //左右边相对布局
    ObjectAnimator in, out;        //回收动画和下拉动画
    boolean ifin = true;        //记录fill和object按钮是否下拉
    private MySurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //切换到主界面
        setContentView(R.layout.main);

        ShaderManager.loadCodeFromFile(MainActivity.this.getResources());

        //初始化GLSurfaceView
        mGLSurfaceView = (MySurfaceView) findViewById(R.id.mysurfaceview);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控

        menu = (Button) findViewById(R.id.menu);
        fill = (Button) findViewById(R.id.fill);
        object = (Button) findViewById(R.id.object);
        property = (Button) findViewById(R.id.object);

        jiao = (Button) findViewById(R.id.jiao);
        bing = (Button) findViewById(R.id.bing);
        cha = (Button) findViewById(R.id.cha);

        cylinder = (Button) findViewById(R.id.cylinder);
        cube = (Button) findViewById(R.id.cube);
        ball = (Button) findViewById(R.id.ball);
        cone = (Button) findViewById(R.id.cone);

        delete = (Button) findViewById(R.id.delete);
        redo = (Button) findViewById(R.id.redo);
        undo = (Button) findViewById(R.id.undo);

        first = (LinearLayout) findViewById(R.id.first_linear);
        slide = (LinearLayout) findViewById(R.id.slide1);

        left = (RelativeLayout) findViewById(R.id.left_relative);
        right = (RelativeLayout) findViewById(R.id.right_relative);

        menu.bringToFront();
        delete.bringToFront();
        redo.bringToFront();
        undo.bringToFront();
        first.bringToFront();
        left.bringToFront();
        right.bringToFront();

        //设置下拉动画
        slide.setAlpha(0);
        PropertyValuesHolder outx = PropertyValuesHolder.ofFloat("TranslationY", 200);
        PropertyValuesHolder outa = PropertyValuesHolder.ofFloat("Alpha", 1);
        out = ObjectAnimator.ofPropertyValuesHolder(slide, outx, outa);
        out.setDuration(500);
        out.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                slide.invalidate();
            }
        });


        PropertyValuesHolder inx = PropertyValuesHolder.ofFloat("TranslationY", 0);
        PropertyValuesHolder ina = PropertyValuesHolder.ofFloat("Alpha", 0);
        in = ObjectAnimator.ofPropertyValuesHolder(slide, inx, ina);
        in.setDuration(500);
        in.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                slide.invalidate();
            }
        });


        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifin) {
                    out.start();
                    slide.setClickable(true);
                    ifin = false;
                } else {
                    in.start();
                    slide.setClickable(false);
                    ifin = true;
                }
            }
        });


        //添加各按钮的点击事件
        menu.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                return true;
            }
        });


        cube.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mGLSurfaceView.isCreateNormal = true;
                mGLSurfaceView.createType = 1;
                mGLSurfaceView.isCreateNew = true;
                //mGLSurfaceView.createObject(1,true);
            }
        });
        cylinder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.isCreateNormal = true;
                mGLSurfaceView.createType = 2;
                mGLSurfaceView.isCreateNew = true;
                //mGLSurfaceView.createObject(2,true);
            }
        });
        cone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.isCreateNormal = true;
                mGLSurfaceView.createType = 3;
                mGLSurfaceView.isCreateNew = true;
                //mGLSurfaceView.createObject(3,true);
            }
        });
        ball.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.isCreateNormal = true;
                mGLSurfaceView.createType = 4;
                mGLSurfaceView.isCreateNew = true;
                //mGLSurfaceView.createObject(4,true);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mGLSurfaceView.BodyAll.remove(mGLSurfaceView.curBody)) {
                    if (mGLSurfaceView.BodyAll.size() != 0) {
                        mGLSurfaceView.curBody = mGLSurfaceView.BodyAll.get(mGLSurfaceView.BodyAll.size() - 1);
                        //当前被选图元
                        for (Body b : mGLSurfaceView.BodyAll) {
                            if (b.isChoosed)
                                b.isChoosed = false;
                        }
                        mGLSurfaceView.curBody.isChoosed = true;
                    }

                    Vector<Body> temp = new Vector<Body>();
                    for (Body e : mGLSurfaceView.BodyAll) {
                        Body tempBody = (Body) e.clone();
                        temp.add(tempBody);
                    }
                    mGLSurfaceView.indesign.addRedoStack(temp);
                }

            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.redo();
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.undo();
            }
        });
        object.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mGLSurfaceView.isObject)
                    mGLSurfaceView.isObject = false;
                else
                    mGLSurfaceView.isObject = true;
            }
        });
        fill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MySurfaceView.isFill)
                    MySurfaceView.isFill = false;
                else
                    MySurfaceView.isFill = true;
            }
        });

        property.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                  /*
  				float[] euler = mGLSurfaceView.curBody.quater.getEulerAxis();
  				Intent intent = new Intent(MainActivity.this, DrawActivity.class);
  				Bundle bundle = new Bundle();
  				float[] bodyPro=new float[]{
  						mGLSurfaceView.curBody.xLength,
  						mGLSurfaceView.curBody.yLength,
  						mGLSurfaceView.curBody.zLength,
  						mGLSurfaceView.curBody.xScale,
  						mGLSurfaceView.curBody.yScale,
  						mGLSurfaceView.curBody.zScale,
  						euler[0],
  						euler[1],
  						euler[2]};
  				bundle.putFloatArray("curBody", bodyPro);
  				startActivity(intent);
  				overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
  				*/
                return true;
            }
        });
        jiao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.isBool = true;
                mGLSurfaceView.boolMode = 1;
            }
        });
        bing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.isBool = true;
                mGLSurfaceView.boolMode = 2;
            }
        });
        cha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGLSurfaceView.isBool = true;
                mGLSurfaceView.boolMode = 3;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}