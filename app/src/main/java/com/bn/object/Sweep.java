package com.bn.object;

import android.opengl.GLES20;

import com.bn.Main.MatrixState;
import com.bn.Main.MySurfaceView;
import com.bn.Main.ShaderManager;
import com.bn.Util.LoadUtil;
import com.bn.Util.VectorUtil;
import com.bn.csgStruct.Vector2f;
import com.bn.csgStruct.Vector3f;
import com.bn.csgStruct.Bound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;
import java.util.Vector;


public class Sweep extends Solid {
    int vCount = 0;
    float scale;

    public Sweep(float scale, List<Vector2f> face, List<Vector2f> line) {
        this.scale = scale;
        la = new Axis();
        la.initShader(ShaderManager.getLineShaderProgram());

        indices = new Vector<>();
        vertices = new Vector<>();

        //初始化顶点坐标与着色数据
        initVertexData(scale, face, line);

        Box = new Bound(vertexs);

        initShader(ShaderManager.getShadowshaderProgram());
    }

    //自定义的初始化顶点数据的方法
    public void initVertexData(float scale, List<Vector2f> face, List<Vector2f> line) {
        //成员变量初始化
        face.add(new Vector2f(face.get(0).x, face.get(0).y));

        Vector2f v1 = face.get(0).minus(face.get(1));
        Vector2f v2 = face.get(1).minus(face.get(2));

        if(v1.cross(v2) > 0)
            Collections.reverse(face);

        if(line.get(0).y > line.get(line.size()-1).y)
            Collections.reverse(line);



        //顶点
        int nRow = line.size() - 1;
        int nCol = face.size() - 1;

        for (int i = 0; i < nRow + 1; i++) {
            float r = line.get(i).x * scale;//当前圆的半径
            float y = line.get(i).y * scale;//当前y值
            for (Vector2f v : face)//重复了一列顶点，方便了索引的计算
            {
                vertices.add(new Vector3f(v.x * scale + r, y, v.y * scale));
            }
        }
        float r = line.get(0).x * scale;//当前圆的半径
        float y = line.get(0).y * scale;//当前y值
        vertices.add(new Vector3f(r, y, 0));
        r = line.get(line.size() - 1).x * scale;//当前圆的半径
        y = line.get(line.size() - 1).y * scale;//当前y值
        vertices.add(new Vector3f(r, y, 0));
        //索引
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                int index = i * (nCol + 1) + j;//当前索引
                //卷绕索引
                indices.add(index + 1);//下一列---1
                indices.add(index + nCol + 2);//下一行下一列---3
                indices.add(index + nCol + 1);//下一列---2

                indices.add(index + 1);//下一列---1
                indices.add(index + nCol + 1);//下一列---2
                indices.add(index);//当前---0
            }
        }

        for (int j = 0; j < nCol; j++) {
            int index = j;//当前索引
            //卷绕索引
            indices.add(index);//下一列---1
            indices.add(vertices.size() - 2);//下一列---2
            indices.add(index + 1);//当前---0

            index = nRow * (nCol + 1) + j;//当前索引
            //卷绕索引
            indices.add(index + 1);//下一列---1
            indices.add(vertices.size() - 1);//下一列---2
            indices.add(index);//当前---0
        }

        vCount = indices.size();//顶点个数，共有nColumn*nRow*2个三角形，每个三角形都有三个顶点

        Vector3f center = VectorUtil.getMean(vertices);
        Vector3f maxLen = VectorUtil.getMaxLen(vertices);
        for(Vector3f vector3f : vertices){
            vector3f.x -= center.x;
            vector3f.y -= center.y;
            vector3f.z -= center.z;
            vector3f.x /= maxLen.x;
            vector3f.y /= maxLen.y;
            vector3f.z /= maxLen.z;
        }
        this.xScale = maxLen.x;
        this.yScale = maxLen.y;
        this.zScale = maxLen.z;

        //计算卷绕顶点
        vertexs = VectorUtil.calVertices(vertices, indices);
        normals = LoadUtil.getNolmalsOnlyAverage(vertices, indices);

        float[] colors = new float[vCount * 4];
        for (int i = 0; i < vCount; i++) {
            colors[4 * i] = 0.60f;
            colors[4 * i + 1] = 0.70f;
            colors[4 * i + 2] = 0.90f;
            colors[4 * i + 3] = 1.00f;
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertexs);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点法向量数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

    //自定义初始化着色器initShader方法
    public void initShader(int program) {
        //基于顶点着色器与片元着色器创建程序
        mProgram = program;
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点发向量属性引用
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中顶点颜色属性引用id
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中视角变换矩阵
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中投影、摄像机组合矩阵引用
        muProjCameraMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMProjCameraMatrix");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中是否绘制阴影属性引用
        muIsShadow = GLES20.glGetUniformLocation(mProgram, "isShadow");
    }

    public void drawSelf(int isShadow) {
        setBody();
        setVN();

        if (isChoosed && isShadow == 0) {
            //绘制坐标轴
            MatrixState.pushMatrix();
            la.drawSelf();
            MatrixState.popMatrix();
        }

        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将投影、摄像机组合矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);
        //将光源位置传入着色器程序
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //将摄像机位置传入着色器程序
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //将是否绘制阴影属性传入着色器程序
        GLES20.glUniform1i(muIsShadow, isShadow);
        //将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //将顶点法向量数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        maNormalHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );
        //将顶点颜色向量数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        maColorHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        4 * 4,
                        mColorBuffer
                );
        //启用顶点位置、法向量数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);

        //绘制加载的物体
        if (MySurfaceView.isFill) {
            //绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        } else {
            //绘制线条的粗细
            GLES20.glLineWidth(2);
            //绘制线框
            //绘制加载的物体
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount);
        }
    }
}
