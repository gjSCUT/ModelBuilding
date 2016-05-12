package com.bn.object;

import android.opengl.GLES20;

import com.bn.Main.MatrixState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.bn.Main.Constant.UNIT_SIZE;

//坐标系单轴
public class AxisLine {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵引用
    int muProjCameraMatrixHandle;//投影、摄像机组合矩阵引用

    int maPositionHandle; //顶点位置属性引用 
    int maColorHandle; //顶点颜色属性引用
    String mVertexShader;//顶点着色器代码脚本  	 
    String mFragmentShader;//片元着色器代码脚本

    FloatBuffer mVertexBuffer1;//顶点坐标数据缓冲
    FloatBuffer mColorBuffer1;//顶点着色数据缓冲
    FloatBuffer mVertexBuffer2;//顶点坐标数据缓冲
    FloatBuffer mColorBuffer2;//顶点着色数据缓冲
    int vCount = 0;


    public AxisLine() {
        //初始化顶点坐标与着色数据
        initVertexData();
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData() {
        //顶点坐标数据的初始化================begin============================
        vCount = 2;


        float vertices1[] = new float[]
                {
                        2 * UNIT_SIZE, 0, 0,
                        2 * -UNIT_SIZE, 0, 0,
                };


        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb1 = ByteBuffer.allocateDirect(vertices1.length * 4);
        vbb1.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer1 = vbb1.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer1.put(vertices1);//向缓冲区中放入顶点坐标数据
        mVertexBuffer1.position(0);//设置缓冲区起始位置

        float vertices2[] = new float[]
                {
                        2.3f * UNIT_SIZE, 0, 0,
                        2.3f * -UNIT_SIZE, 0, 0,
                };

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb2 = ByteBuffer.allocateDirect(vertices2.length * 4);
        vbb2.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer2 = vbb2.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer2.put(vertices2);//向缓冲区中放入顶点坐标数据
        mVertexBuffer2.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================


        //顶点着色数据的初始化================begin============================
        float colors1[] = new float[]//顶点颜色值数组，每个顶点4个色彩值RGBA
                {
                        0, 0, 1, 1,
                        0, 0, 1, 1,

                };


        //创建顶点着色数据缓冲
        ByteBuffer cbb1 = ByteBuffer.allocateDirect(colors1.length * 4);
        cbb1.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer1 = cbb1.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer1.put(colors1);//向缓冲区中放入顶点着色数据
        mColorBuffer1.position(0);//设置缓冲区起始位置

        //顶点着色数据的初始化================begin============================
        float colors2[] = new float[]//顶点颜色值数组，每个顶点4个色彩值RGBA
                {
                        1, 0, 0, 1,
                        1, 0, 0, 1,

                };
        //创建顶点着色数据缓冲
        ByteBuffer cbb2 = ByteBuffer.allocateDirect(colors2.length * 4);
        cbb2.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer2 = cbb2.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer2.put(colors2);//向缓冲区中放入顶点着色数据
        mColorBuffer2.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================

    }

    //初始化shader
    public void initShader(int Program) {
        //基于顶点着色器与片元着色器创建程序
        mProgram = Program;
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中投影、摄像机组合矩阵引用
        muProjCameraMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMProjCameraMatrix");
    }

    public void drawSelf(boolean isChoose) {

        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将投影、摄像机组合矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);


        FloatBuffer mVertexBuffer = (isChoose == false ? mVertexBuffer1 : mVertexBuffer2);
        FloatBuffer mColorBuffer = (isChoose == false ? mColorBuffer1 : mColorBuffer2);

        //传送顶点位置数据
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //传送顶点坐标数据
        GLES20.glVertexAttribPointer
                (
                        maColorHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        4 * 4,
                        mColorBuffer
                );

        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        //允许顶点颜色数据数组
        GLES20.glEnableVertexAttribArray(maColorHandle);


        //绘制线条的粗细
        GLES20.glLineWidth(8);
        //绘制
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount);


    }
}