package com.bn.main;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class PrintActivity extends Activity {
    private Button ok, cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setFinishOnTouchOutside(false);
        //����Ϊȫ��
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // ����Ϊ����ģʽ
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_main);

        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.ok);

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PrintActivity.this, "����ɹ�", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
