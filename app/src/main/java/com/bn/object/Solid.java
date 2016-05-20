package com.bn.object;

import android.opengl.GLES20;

import com.bn.Main.MatrixState;
import com.bn.Main.MySurfaceView;
import com.bn.Main.ShaderManager;
import com.bn.csgStruct.Vector2f;
import com.bn.csgStruct.Vector3f;
import com.bn.csgStruct.Bound;
import com.bn.csgStruct.Quaternion;
import com.bn.Util.LoadUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class Solid extends Body {
    int mProgram;
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    int maColorHandle;//顶点颜色属性引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int muProjCameraMatrixHandle;//投影、摄像机组合矩阵引用
    int maCameraHandle; //摄像机位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用
    int muIsShadow;//是否绘制阴影属性引用
    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    FloatBuffer mColorBuffer;//顶点法向量数据缓冲

    List<Integer> indices;
    List<Vector3f> vertices;
    private int normalMode;
    float[] vxp1 = new float[]{1, 0, 0, 1};
    float[] vxp2 = new float[]{-1, 0, 0, 1};
    float[] vyp1 = new float[]{0, 1, 0, 1};
    float[] vyp2 = new float[]{0, -1, 0, 1};
    float[] vzp1 = new float[]{0, 0, 1, 1};
    float[] vzp2 = new float[]{0, 0, -1, 1};
    float[] winXY1 = new float[3];
    float[] winXY2 = new float[3];

    public Solid(){

    }

    public Solid(List<Vector3f> vSet, List<Integer> iSet, int Mode) {
        la = new Axis();
        la.initShader(ShaderManager.getLineShaderProgram());

        vertices = vSet;
        indices = iSet;
        normalMode = Mode;

        //初始化顶点和法向量数组
        if (normalMode == 1) {
            normals = LoadUtil.getNolmalsOnlyAverage(vertices, indices);
            vertexs = LoadUtil.getVerticesOnlyAverage(vertices, indices);
        } else if (normalMode == 2) {
            normals = LoadUtil.getNolmalsOnlyFace(vertices, indices);
            vertexs = LoadUtil.getVerticesOnlyFace(vertices, indices);
        }

        Box = new Bound(vertexs);
        //初始化顶点坐标与着色数据
        initVertexData();
        initShader(ShaderManager.getShadowshaderProgram());
    }


    public Solid(Body b) {
        la = new Axis();
        la.initShader(ShaderManager.getLineShaderProgram());

        Solid s = (Solid) b;
        vertices = s.getVertices();
        indices = s.getIndices();
        normalMode = s.getNormalMode();

        quater = new Quaternion(b.quater);
        xLength = b.xLength;//绕x轴平移距离
        yLength = b.yLength;//绕y轴平移距离
        zLength = b.zLength;//绕x轴平移距离
        xScale = b.xScale;//绕x轴放缩倍数
        yScale = b.yScale;//绕y轴放缩倍数
        zScale = b.zScale;//绕z轴放缩倍数

        normals = b.normals;
        vertexs = b.vertexs;

        Box = new Bound(vertexs);
        //初始化顶点坐标与着色数据
        initVertexData();
        initShader(ShaderManager.getShadowshaderProgram());
    }



    //初始化顶点坐标与着色数据的方法
    @Override
    public void initVertexData() {


        //顶点坐标数据的初始化================begin============================
        vCount = vertexs.length / 3;

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertexs);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================

        //顶点法向量数据的初始化================begin============================  
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================

        float[] colors = new float[vCount * 4];
        for (int i = 0; i < vCount; i++) {
            colors[4 * i] = 0.60f;
            colors[4 * i + 1] = 0.70f;
            colors[4 * i + 2] = 0.90f;
            colors[4 * i + 3] = 1.00f;
        }
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点法向量数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }


    //初始化shader
    @Override
    public void initShader(int Program) {
        //基于顶点着色器与片元着色器创建程序
        mProgram = Program;
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


    public void setVN() {
        //计算坐标轴三向量
        MatrixState.getProject(vxp1, winXY1);
        MatrixState.getProject(vxp2, winXY2);
        vx.x = winXY1[0] - winXY2[0];
        vx.y = winXY1[1] - winXY2[1];
        MatrixState.getProject(vyp1, winXY1);
        MatrixState.getProject(vyp2, winXY2);
        vy.x = winXY1[0] - winXY2[0];
        vy.y = winXY1[1] - winXY2[1];
        MatrixState.getProject(vzp1, winXY1);
        MatrixState.getProject(vzp2, winXY2);
        vz.x = winXY1[0] - winXY2[0];
        vz.y = winXY1[1] - winXY2[1];
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
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, vCount);
        }
    }

    public List<Vector3f> getVertices() {
        List<Vector3f> newVertices = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            newVertices.add(vertices.get(i));
        }

        return newVertices;
    }

    public List<Integer> getIndices() {
        List<Integer> newIndices = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            newIndices.add(indices.get(i));
        }

        return newIndices;
    }

    public int getNormalMode() {

        return normalMode;
    }

}
