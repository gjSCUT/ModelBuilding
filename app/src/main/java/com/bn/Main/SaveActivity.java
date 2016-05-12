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
    // ��¼��ǰ�ĸ��ļ���
    File currentParent;
    // ��¼��ǰ·���µ������ļ����ļ�����
    File[] currentFiles;
    private ListView listView;
    private TextView path;
    private Button back, cancel, save;

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
        setContentView(R.layout.save_main);
        // ��ȡ�г�ȫ���ļ���ListView
        path = (TextView) findViewById(R.id.path);
        listView = (ListView) findViewById(R.id.list);
        back = (Button) findViewById(R.id.back);
        cancel = (Button) findViewById(R.id.cancel);
        save = (Button) findViewById(R.id.save);
        // ��ȡϵͳ��SD����Ŀ¼
        File root = Environment.getExternalStorageDirectory();
        // ��� SD������
        if (root.exists()) {
            currentParent = root;
            currentFiles = root.listFiles();
            len = currentFiles.length;
            curposition = -1;
            // ʹ�õ�ǰĿ¼�µ�ȫ���ļ����ļ��������ListView
            inflateListView();
        }
        // ΪListView���б���ĵ����¼��󶨼�����
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // �û��������ļ���ֱ�ӷ��أ������κδ���
                if (currentFiles[position].isFile()) {
                    curposition = position;
                    return;
                }
                // ��ȡ�û�������ļ����µ������ļ�
                File[] tmp = currentFiles[position].listFiles();
                if (tmp == null || tmp.length == 0) {
                    Toast.makeText(SaveActivity.this
                            , "��ǰ·�����ɷ��ʻ��·����û���ļ�",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // ��ȡ�û��������б����Ӧ���ļ��У���Ϊ��ǰ�ĸ��ļ���
                    currentParent = currentFiles[position];
                    // ���浱ǰ�ĸ��ļ����ڵ�ȫ���ļ����ļ���
                    currentFiles = tmp;
                    len = currentFiles.length;
                    curposition = -1;
                    // �ٴθ���ListView
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
                        // ��ȡ��һ��Ŀ¼
                        currentParent = currentParent.getParentFile();
                        // �г���ǰĿ¼�������ļ�
                        currentFiles = currentParent.listFiles();
                        len = currentFiles.length;
                        curposition = -1;
                        // �ٴθ���ListView
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
                        Toast.makeText(SaveActivity.this, "�ѳɹ����ļ�д��SD����.", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(SaveActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(SaveActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

				/*
				if(curposition != -1) {
					Toast.makeText(SaveActivity.this
						, "����"+currentFiles[curposition].getName(),
						Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(SaveActivity.this
							, "û��ѡ���κ��ļ�",
							Toast.LENGTH_SHORT).show();
				}
				*/
                } else Toast.makeText(SaveActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
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
        // ΪListView����Adapter
        listView.setAdapter(adapter);
        try {
            path.setText("��ǰ·��Ϊ��"
                    + currentParent.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}