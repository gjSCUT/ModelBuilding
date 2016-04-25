package com.bn.main;


import android.content.res.Resources;

import com.bn.util.ShaderUtil;

public class ShaderManager {
    final static int shaderCount = 3;
    final static String[][] shaderName =
            {
                    {"vertex.sh", "frag.sh"},
                    {"vertexTexture.sh", "fragTexture.sh"},
                    {"vertex_shadow.sh", "frag_shadow.sh"}
            };
    static String[] mVertexShader = new String[shaderCount];
    static String[] mFragmentShader = new String[shaderCount];
    static int[] program = new int[shaderCount];

    public static void loadCodeFromFile(Resources r) {
        for (int i = 0; i < shaderCount; i++) {
            //���ض�����ɫ���Ľű�����
            mVertexShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][0], r);
            //����ƬԪ��ɫ���Ľű�����
            mFragmentShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
        }
    }

    //����3D�����shader
    public static void compileShader() {
        for (int i = 0; i < shaderCount; i++) {
            program[i] = ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
        }
    }

    //���ﷵ�ص����������shader����
    public static int getLineShaderProgram() {
        return program[0];
    }

    //���ﷵ�ص�����ͨ�����shader����
    public static int getObjectshaderProgram() {
        return program[1];
    }

    //���ﷵ�ص�����Ӱ�����shader����
    public static int getShadowshaderProgram() {
        return program[2];
    }
}
