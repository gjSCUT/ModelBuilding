package com.bn.main;

import android.opengl.GLU;
import android.opengl.Matrix;

import com.bn.csgStruct.Quaternion;
import com.bn.csgStruct.Quaternion.qType;
import com.bn.csgStruct.Struct.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//�洢ϵͳ����״̬����
public class MatrixState {
    public static float[] lightLocation = new float[]{0, 0, 0};//��λ���Դλ��
    public static float[] mMVPMatrix = new float[16];//���������任������˵�ģ�;���
    public static float[] mRotateMatrix = new float[16];//��ת����
    public static float[] mTranslateMatrix = new float[16];//��������
    public static float[] mScaleMatrix = new float[16];//ƽ�ƾ���
    public static FloatBuffer cameraFB;
    public static FloatBuffer lightPositionFB;
    //�����任�����ջ
    static float[][] mStack = new float[100][16];
    static int stackTop = -1;
    //���������
    static ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
    static float[] cameraLocation = new float[3];//�����λ��
    //���õƹ�λ�õķ���
    static ByteBuffer llbbL = ByteBuffer.allocateDirect(3 * 4);
    private static float[] currMatrix;//��ǰ�任����
    private static float[] mVMatrix = new float[16];//�����λ�ó���9��������
    private static float[] mProjMatrix = new float[16];//4x4���� ͶӰ��

    //��ȡ���任��ʼ����
    public static void setInitStack() {
        currMatrix = new float[16];
        Matrix.setIdentityM(currMatrix, 0);
        Matrix.setIdentityM(mRotateMatrix, 0);
        Matrix.setIdentityM(mTranslateMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
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

    //������xyz����ת
    public static void rotate(float[] tempMatrix) {
        //mRotateMatrix=nMatrix;
        Matrix.multiplyMM(currMatrix, 0, tempMatrix, 0, currMatrix, 0);
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

    //��ȡ���������ƽ�Ʒ�������
    public static float[] getTSMatrix() {
        return currMatrix;
    }

    //��ȡ��������ı任����
    public static float[] getMMatrix() {
        return currMatrix;
    }

    //��ȡ��ת����
    public static float[] getRotateMatrix() {
        return mRotateMatrix;
    }

    //��ȡͶӰ����
    public static float[] getProjMatrix() {
        return mProjMatrix;
    }

    //��ȡ���������ľ���
    public static float[] getCaMatrix() {
        return mVMatrix;
    }

    public static float[] getViewProjMatrix() {
        float[] mViewMatrix = new float[16];
        Matrix.multiplyMM(mViewMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        return mViewMatrix;
    }

    public static void setLightLocation(float x, float y, float z) {
        llbbL.clear();

        lightLocation[0] = x;
        lightLocation[1] = y;
        lightLocation[2] = z;

        llbbL.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        lightPositionFB = llbbL.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
    }

    //��ά����ת��ά����
    public static float[] getProject(float[] point) {
        float[] win = new float[3];
        int[] mView = {0, 0, Constant.WIDTH, Constant.HEIGHT};
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);

        GLU.gluProject(point[0], point[1], point[2],
                mMVPMatrix, 0, mProjMatrix, 0, mView, 0, win, 0);

        return win;
    }

    //��ά����ת��ά���� 
    public static float[] getUnProject(float x, float y) {

        float w = Constant.WIDTH;// ��Ļ���
        float h = Constant.HEIGHT;// ��Ļ�߶�
        float left = Constant.RATIO;//�ӽ�leftֵ
        float top = 1;//�ӽ�topֵ
        float near = 10;//�ӽ�nearֵ
        float far = 400;//�ӽ�farֵ
        //���ӿڵ�����������ԭ��ʱ�����ص������
        float x0 = x - w / 2;
        float y0 = h / 2 - y;
        //�����Ӧ��near���ϵ�x��y����
        float xNear = 2 * x0 * left / w;
        float yNear = 2 * y0 * top / h;
        //�����Ӧ��far���ϵ�x��y����
        float ratio = far / near;
        float xFar = ratio * xNear;
        float yFar = ratio * yNear;
        //���������ϵ��A������
        float ax = xNear;
        float ay = yNear;
        float az = -near;
        //���������ϵ��B������
        float bx = xFar;
        float by = yFar;
        float bz = -far;
        //ͨ�����������ϵ��A��B��������꣬����������ϵ��A��B���������
        float[] A = fromPtoPreP(new float[]{ax, ay, az});
        float[] B = fromPtoPreP(new float[]{bx, by, bz});
        return new float[]{//�������յ�AB��������
                A[0], A[1], A[2],
                B[0], B[1], B[2]
        };
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
