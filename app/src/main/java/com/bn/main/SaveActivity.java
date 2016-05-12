package com.bn.Main;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bn.Util.Stl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

;

public class SaveActivity extends Activity {
    int curposition, len;
    // 记录当前的父文件夹
    File currentParent;
    // 记录当前路径下的所有文件的文件数组
    File[] currentFiles;
    private ListView listView;
    private TextView path;
    private Button back, cancel, save;

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
        setContentView(R.layout.save_main);
        // 获取列出全部文件的ListView
        path = (TextView) findViewById(R.id.path);
        listView = (ListView) findViewById(R.id.list);
        back = (Button) findViewById(R.id.back);
        cancel = (Button) findViewById(R.id.cancel);
        save = (Button) findViewById(R.id.save);
        // 获取系统的SD卡的目录
        File root = Environment.getExternalStorageDirectory();
        // 如果 SD卡存在
        if (root.exists()) {
            currentParent = root;
            currentFiles = root.listFiles();
            len = currentFiles.length;
            curposition = -1;
            // 使用当前目录下的全部文件、文件夹来填充ListView
            inflateListView();
        }
        // 为ListView的列表项的单击事件绑定监听器
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 用户单击了文件，直接返回，不做任何处理
                if (currentFiles[position].isFile()) {
                    curposition = position;
                    return;
                }
                // 获取用户点击的文件夹下的所有文件
                File[] tmp = currentFiles[position].listFiles();
                if (tmp == null || tmp.length == 0) {
                    Toast.makeText(SaveActivity.this
                            , "当前路径不可访问或该路径下没有文件",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
                    currentParent = currentFiles[position];
                    // 保存当前的父文件夹内的全部文件和文件夹
                    currentFiles = tmp;
                    len = currentFiles.length;
                    curposition = -1;
                    // 再次更新ListView
                    inflateListView();
                }
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View source) {
                try {
                    if (!currentParent.getCanonicalPath()
                            .equals("/")) {
                        // 获取上一级目录
                        currentParent = currentParent.getParentFile();
                        // 列出当前目录下所有文件
                        currentFiles = currentParent.listFiles();
                        len = currentFiles.length;
                        curposition = -1;
                        // 再次更新ListView
                        inflateListView();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MySurfaceView.getStlPrint() != null) {
                    String filename = android.os.Environment.getExternalStorageDirectory()
                            + "/haha.stl";
                    try {
                        FileOutputStream fos = new FileOutputStream(filename);
                        Stl.writeInBinary(MySurfaceView.getStlPrint(), fos);
                        Toast.makeText(SaveActivity.this, "已成功将文件写到SD卡上.", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(SaveActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(SaveActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

				/*
				if(curposition != -1) {
					Toast.makeText(SaveActivity.this
						, "加载"+currentFiles[curposition].getName(),
						Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(SaveActivity.this
							, "没有选中任何文件",
							Toast.LENGTH_SHORT).show();
				}
				*/
                } else Toast.makeText(SaveActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inflateListView() {
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return len;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View converView, ViewGroup parent) {
                View view = View.inflate(SaveActivity.this, R.layout.line, null);
                TextView textview = (TextView) view.findViewById(R.id.file_name);
                ImageView imageview = (ImageView) view.findViewById(R.id.icon);
                textview.setText(currentFiles[position].getName());
                if (currentFiles[position].isDirectory())
                    imageview.setImageResource(R.drawable.folder);
                else
                    imageview.setImageResource(R.drawable.file);
                return view;
            }
        };
        // 为ListView设置Adapter
        listView.setAdapter(adapter);
        try {
            path.setText("当前路径为："
                    + currentParent.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}