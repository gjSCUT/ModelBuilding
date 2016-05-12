package com.bn.Main;


import android.content.res.Resources;

import com.bn.Util.ShaderUtil;

public class ShaderManager {
    final static int shaderCount = 4;
    final static String[][] shaderName =
            {
                    {"vertex.sh", "frag.sh"},
                    {"vertexTexture.sh", "fragTexture.sh"},
                    {"vertex_shadow.sh", "frag_shadow.sh"},
                    {"vertex_tex.sh", "frag_tex.sh"},
            };
    static String[] mVertexShader = new String[shaderCount];
    static String[] mFragmentShader = new String[shaderCount];
    static int[] program = new int[shaderCount];

    public static void loadCodeFromFile(Resources r) {
        for (int i = 0; i < shaderCount; i++) {
            //加载顶点着色器的脚本内容
            mVertexShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][0], r);
            //加载片元着色器的脚本内容
            mFragmentShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
        }
    }

    //编译3D物体的shader
    public static void compileShader() {
        for (int i = 0; i < shaderCount; i++) {
            program[i] = ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
        }
    }

    //这里返回的是坐标轴的shader程序
    public static int getLineShaderProgram() {
        return program[0];
    }

    //这里返回的是普通物体的shader程序
    public static int getObjectshaderProgram() {
        return program[1];
    }

    //这里返回的是阴影物体的shader程序
    public static int getShadowshaderProgram() {
        return program[2];
    }

    //这里返回的是阴影物体的shader程序
    public static int getTexshaderProgram() {
        return program[3];
    }
}
