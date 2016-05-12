package com.bn.drawSweep;

import android.opengl.Matrix;

import com.bn.csgStruct.Vector3f;
import com.bn.csgStruct.Quaternion;
import com.bn.csgStruct.Quaternion.qType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//存储系统矩阵状态的类
public class MatrixState {

    private static float[] mVMatrix = new float[16];                //摄像机位置朝向9参数矩阵
    private static float[] mProjMatrix = new float[16];             //投影矩阵

    private static float[] currMatrix;                              //当前几何变化矩阵

    public static FloatBuffer cameraFB;
    public static float[] mMVPMatrix = new float[16];              //摄像矩阵与变换矩阵相乘的模型矩阵
    //保护变换矩阵的栈
    static float[][] mStack = new float[100][16];
    static int stackTop = -1;
    //设置摄像机
    static ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
    static float[] cameraLocation = new float[3];//摄像机位置

    //获取不变换初始矩阵
    public static void setInitStack() {
        currMatrix = new float[16];
        Matrix.setIdentityM(currMatrix, 0);
    }

    //保护变换矩阵
    public static void pushMatrix() {
        stackTop++;
        for (int i = 0; i < 16; i++) {
            mStack[stackTop][i] = currMatrix[i];
        }

    }

    //恢复变换矩阵
    public static void popMatrix() {
        for (int i = 0; i < 16; i++) {
            currMatrix[i] = mStack[stackTop][i];
        }
        stackTop--;
    }

    //设置沿xyz轴移动
    public static void translate(float x, float y, float z) {
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x, y, z);
        //Matrix.multiplyMM(currMatrix, 0, currMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(currMatrix, 0, tempMatrix, 0, currMatrix, 0);
        //Matrix.translateM(currMatrix, 0, x, y, z);
    }

    //设置绕xyz轴旋转
    public static void rotate(float x, float y, float z, float angle) {
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Quaternion q = new Quaternion(qType.TRANSFFORM);
        q.addRoutate(angle, new Vector3f(x, y, z));
        Matrix.multiplyMM(currMatrix, 0, currMatrix, 0, q.getRotateMatrix(), 0);
        //Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }


    // 设置绕xyz缩放
    public static void scale(float x, float y, float z) {
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.scaleM(tempMatrix, 0, x, y, z);
        Matrix.multiplyMM(currMatrix, 0, tempMatrix, 0, currMatrix, 0);
        //Matrix.scaleM(currMatrix, 0, x, y, z);
    }

    public static void setCamera
            (
                    float cx,    //摄像机位置x
                    float cy,   //摄像机位置y
                    float cz,   //摄像机位置z
                    float tx,   //摄像机目标点x
                    float ty,   //摄像机目标点y
                    float tz,   //摄像机目标点z
                    float upx,  //摄像机UP向量X分量
                    float upy,  //摄像机UP向量Y分量
                    float upz   //摄像机UP向量Z分量
            ) {
        Matrix.setLookAtM
                (
                        mVMatrix,
                        0,
                        cx, cy, cz,
                        tx, ty, tz,
                        upx, upy, upz
                );
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;

        llbb.clear();
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    //设置透视投影参数
    public static void setProjectFrustum
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距离
            float far       //far面距离
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //设置正交投影参数
    public static void setProjectOrtho
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距离
            float far       //far面距离
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //获取具体物体的总变换矩阵
    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }


    //获取摄像机矩阵的逆矩阵的方法
    public static float[] getInvertMvMatrix() {
        float[] invM = new float[16];
        Matrix.invertM(invM, 0, mVMatrix, 0);//求逆矩阵
        return invM;
    }

    //通过摄像机变换后的点求变换前的点的方法：乘以摄像机矩阵的逆矩阵
    public static float[] fromPtoPreP(float[] p) {
        //通过逆变换，得到变换之前的点
        float[] inverM = getInvertMvMatrix();//获取逆变换矩阵
        float[] preP = new float[4];
        Matrix.multiplyMV(preP, 0, inverM, 0, new float[]{p[0], p[1], p[2], 1}, 0);//求变换前的点
        return new float[]{preP[0], preP[1], preP[2]};//变换前的点就是变换之前的法向量
    }
}
