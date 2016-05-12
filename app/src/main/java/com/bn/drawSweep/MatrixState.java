package com.bn.drawSweep;

import android.opengl.Matrix;

import com.bn.csgStruct.Vector3f;
import com.bn.csgStruct.Quaternion;
import com.bn.csgStruct.Quaternion.qType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//�洢ϵͳ����״̬����
public class MatrixState {

    private static float[] mVMatrix = new float[16];                //�����λ�ó���9��������
    private static float[] mProjMatrix = new float[16];             //ͶӰ����

    private static float[] currMatrix;                              //��ǰ���α仯����

    public static FloatBuffer cameraFB;
    public static float[] mMVPMatrix = new float[16];              //���������任������˵�ģ�;���
    //�����任�����ջ
    static float[][] mStack = new float[100][16];
    static int stackTop = -1;
    //���������
    static ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
    static float[] cameraLocation = new float[3];//�����λ��

    //��ȡ���任��ʼ����
    public static void setInitStack() {
        currMatrix = new float[16];
        Matrix.setIdentityM(currMatrix, 0);
    }

    //�����任����
    public static void pushMatrix() {
        stackTop++;
        for (int i = 0; i < 16; i++) {
            mStack[stackTop][i] = currMatrix[i];
        }

    }

    //�ָ��任����
    public static void popMatrix() {
        for (int i = 0; i < 16; i++) {
            currMatrix[i] = mStack[stackTop][i];
        }
        stackTop--;
    }

    //������xyz���ƶ�
    public static void translate(float x, float y, float z) {
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x, y, z);
        //Matrix.multiplyMM(currMatrix, 0, currMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(currMatrix, 0, tempMatrix, 0, currMatrix, 0);
        //Matrix.translateM(currMatrix, 0, x, y, z);
    }

    //������xyz����ת
    public static void rotate(float x, float y, float z, float angle) {
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Quaternion q = new Quaternion(qType.TRANSFFORM);
        q.addRoutate(angle, new Vector3f(x, y, z));
        Matrix.multiplyMM(currMatrix, 0, currMatrix, 0, q.getRotateMatrix(), 0);
        //Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }


    // ������xyz����
    public static void scale(float x, float y, float z) {
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.scaleM(tempMatrix, 0, x, y, z);
        Matrix.multiplyMM(currMatrix, 0, tempMatrix, 0, currMatrix, 0);
        //Matrix.scaleM(currMatrix, 0, x, y, z);
    }

    public static void setCamera
            (
                    float cx,    //�����λ��x
                    float cy,   //�����λ��y
                    float cz,   //�����λ��z
                    float tx,   //�����Ŀ���x
                    float ty,   //�����Ŀ���y
                    float tz,   //�����Ŀ���z
                    float upx,  //�����UP����X����
                    float upy,  //�����UP����Y����
                    float upz   //�����UP����Z����
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
        llbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    //����͸��ͶӰ����
    public static void setProjectFrustum
    (
            float left,        //near���left
            float right,    //near���right
            float bottom,   //near���bottom
            float top,      //near���top
            float near,        //near�����
            float far       //far�����
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //��������ͶӰ����
    public static void setProjectOrtho
    (
            float left,        //near���left
            float right,    //near���right
            float bottom,   //near���bottom
            float top,      //near���top
            float near,        //near�����
            float far       //far�����
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //��ȡ����������ܱ任����
    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }


    //��ȡ���������������ķ���
    public static float[] getInvertMvMatrix() {
        float[] invM = new float[16];
        Matrix.invertM(invM, 0, mVMatrix, 0);//�������
        return invM;
    }

    //ͨ��������任��ĵ���任ǰ�ĵ�ķ������������������������
    public static float[] fromPtoPreP(float[] p) {
        //ͨ����任���õ��任֮ǰ�ĵ�
        float[] inverM = getInvertMvMatrix();//��ȡ��任����
        float[] preP = new float[4];
        Matrix.multiplyMV(preP, 0, inverM, 0, new float[]{p[0], p[1], p[2], 1}, 0);//��任ǰ�ĵ�
        return new float[]{preP[0], preP[1], preP[2]};//�任ǰ�ĵ���Ǳ任֮ǰ�ķ�����
    }
}
