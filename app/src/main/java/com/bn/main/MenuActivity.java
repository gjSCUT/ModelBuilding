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

public class MenuActivity extends Activity {
    private Button menu2, draw, print, help, save, load;
    private float r;
    private float r18;
    private float r54;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.menu_longclick);

        r = getResources().getDimension(R.dimen.menu_longclick_radius);
        r18 = (float) (Math.PI / 10);
        r54 = (float) (54 * Math.PI / 180);
        menu2 = (Button) findViewById(R.id.menu_2);
        draw = (Button) findViewById(R.id.draw);
        print = (Button) findViewById(R.id.print);
        help = (Button) findViewById(R.id.help);
        save = (Button) findViewById(R.id.save);
        load = (Button) findViewById(R.id.load);

        draw.setTranslationY(-r);
        print.setTranslationX((float) (r * Math.cos(r18)));
        print.setTranslationY((float) (-r * Math.sin(r18)));
        help.setTranslationX((float) (r * Math.cos(r54)));
        help.setTranslationY((float) (r * Math.sin(r54)));
        save.setTranslationX((float) (-r * Math.cos(r54)));
        save.setTranslationY((float) (r * Math.sin(r54)));
        load.setTranslationX((float) (-r * Math.cos(r18)));
        load.setTranslationY((float) (-r * Math.sin(r18)));

        menu2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });
        draw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, DrawActivity.class);
                startActivity(intent);
            }
        });
        load.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LoadActivity.class);
                startActivity(intent);
            }
        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SaveActivity.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
        print.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PrintActivity.class);
                startActivity(intent);
            }
        });
    }

}
