package com.bn.object;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.bn.main.MatrixState;
import com.bn.main.MySurfaceView;
import com.bn.main.ShaderManager;
import com.bn.csgStruct.Bound;
import com.bn.csgStruct.Quaternion;
import com.bn.csgStruct.Struct.Vector3f;
import com.bn.util.LoadUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;


public class Solid extends Body {
    int mProgram;
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ����������
    int maTexCoorHandle; //��������������������
    int maColorHandle;//������ɫ��������
    int muMMatrixHandle;//λ�á���ת�任����
    int muProjCameraMatrixHandle;//ͶӰ���������Ͼ�������
    int maCameraHandle; //�����λ����������
    int maNormalHandle; //���㷨������������
    int maLightLocationHandle;//��Դλ����������
    int muIsShadow;//�Ƿ������Ӱ��������
    String mVertexShader;//������ɫ������ű�
    String mFragmentShader;//ƬԪ��ɫ������ű�
    FloatBuffer mVertexBuffer;//�����������ݻ���
    FloatBuffer mNormalBuffer;//���㷨�������ݻ���
    FloatBuffer mColorBuffer;//���㷨�������ݻ���
    private float[] stlVec;       //STL��ʽ��������ո�ʽ
    private float[] stlNormals;//STL��ʽ����ĳ�ʼÿ��������Ƭ�ķ�����
    private float[] stlVertexs;//STL��ʽ����ĳ�ʼÿ��������Ƭ�Ķ�������
    private Vector<Integer> indices;
    private Vector<Vector3f> vertices;
    private int normalMode;


    public Solid(MySurfaceView mv, Vector<Vector3f> vSet, Vector<Integer> iSet, int Mode) {
        la = new Axis();
        la.initShader(ShaderManager.getLineShaderProgram());
        //Box =new Bound(vSet);

        vertices = vSet;
        indices = iSet;
        normalMode = Mode;

        //��ȡһ��ʼģ�͵�������Ƭ
        stlNormals = LoadUtil.getStlNolmalsOnlyFace(vertices, indices);
        stlVertexs = LoadUtil.getVerticesOnlyFace(vertices, indices);
        initPoint(stlNormals, stlVertexs);

        //��ʼ������ͷ���������
        if (normalMode == 1) {
            normals = LoadUtil.getNolmalsOnlyAverage(vertices, indices);
            vertexs = LoadUtil.getVerticesOnlyAverage(vertices, indices);
        } else if (normalMode == 2) {
            normals = LoadUtil.getNolmalsOnlyFace(vertices, indices);
            vertexs = LoadUtil.getVerticesOnlyFace(vertices, indices);
        }

        Box = new Bound(vertexs);
        //��ʼ��������������ɫ����
        initVertexData();
        initShader(ShaderManager.getShadowshaderProgram());
    }


    public Solid(MySurfaceView mv, Body b) {
        la = new Axis();
        la.initShader(ShaderManager.getLineShaderProgram());

        Solid s = (Solid) b;
        vertices = s.getVertices();
        indices = s.getIndices();
        normalMode = s.getNormalMode();

        stlNormals = LoadUtil.getStlNolmalsOnlyFace(vertices, indices);
        stlVertexs = LoadUtil.getVerticesOnlyFace(vertices, indices);
        initPoint(stlNormals, stlVertexs);

        quater = new Quaternion(b.quater);
        xLength = b.xLength;//��x��ƽ�ƾ���
        yLength = b.yLength;//��y��ƽ�ƾ���
        zLength = b.zLength;//��x��ƽ�ƾ���
        xScale = b.xScale;//��x���������
        yScale = b.yScale;//��y���������
        zScale = b.zScale;//��z���������

        normals = b.normals;
        vertexs = b.vertexs;

        Box = new Bound(vertexs);
        //��ʼ��������������ɫ����
        initVertexData();
        initShader(ShaderManager.getShadowshaderProgram());
    }

    private void initPoint(float[] n, float[] v) {
        stlVec = new float[stlNormals.length + stlVertexs.length];
        int num = 0, num1 = 0, num2 = 0;//����ר��
        for (int i = 0; i < stlVec.length; i += 12) {
            for (int j = 0; j < 3; j++)
                stlVec[num++] = stlNormals[num1++];
            for (int k = 0; k < 9; k++)
                stlVec[num++] = stlVertexs[num2++];
        }
    }

    public float[] getStlPoint() {
        float[] tempVec = new float[stlVec.length];
        for (int i = 0; i < stlVec.length; i += 3) {
            float[] cur = new float[4];
            //��任��ĵ�
            Matrix.multiplyMV(cur, 0, getM(), 0, new float[]{stlVec[i], stlVec[i + 1], stlVec[i + 2], 1}, 0);
            tempVec[i] = cur[0];
            tempVec[i + 1] = cur[1];
            tempVec[i + 2] = cur[2];
        }

        return tempVec;
    }


    //��ʼ��������������ɫ���ݵķ���
    @Override
    public void initVertexData() {


        //�����������ݵĳ�ʼ��================begin============================
        vCount = vertexs.length / 3;

        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertexs);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�����������ݵĳ�ʼ��================end============================

        //���㷨�������ݵĳ�ʼ��================begin============================  
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨��������
        mNormalBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //������ɫ���ݵĳ�ʼ��================end============================

        float[] colors = new float[vCount * 4];
        for (int i = 0; i < vCount; i++) {
            colors[4 * i] = 0.60f;
            colors[4 * i + 1] = 0.70f;
            colors[4 * i + 2] = 0.90f;
            colors[4 * i + 3] = 1.00f;
        }
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mColorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mColorBuffer.put(colors);//�򻺳����з��붥�㷨��������
        mColorBuffer.position(0);//���û�������ʼλ��
    }


    //��ʼ��shader
    @Override
    public void initShader(int Program) {
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = Program;
        //��ȡ�����ж���λ����������  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж��㷢������������  
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        //��ȡ�����ж�����ɫ��������id  
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //��ȡ�������ӽǱ任����
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //��ȡλ�á���ת�任��������
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //��ȡ������ͶӰ���������Ͼ�������
        muProjCameraMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMProjCameraMatrix");
        //��ȡ�����й�Դλ������
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //��ȡ�����������λ������
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        //��ȡ�������Ƿ������Ӱ��������
        muIsShadow = GLES20.glGetUniformLocation(mProgram, "isShadow");
    }

    public void drawSelf(int isShadow) {


        setBody();

        if (isChoosed && isShadow == 0) {
            //����������
            MatrixState.pushMatrix();
            la.drawSelf();
            MatrixState.popMatrix();
        }

        //�ƶ�ʹ��ĳ����ɫ������
        GLES20.glUseProgram(mProgram);
        //�����ձ任��������ɫ������
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //��λ�á���ת�任��������ɫ������
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //��ͶӰ���������Ͼ�������ɫ������
        GLES20.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);
        //����Դλ�ô�����ɫ������
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //�������λ�ô�����ɫ������
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //���Ƿ������Ӱ���Դ�����ɫ������
        GLES20.glUniform1i(muIsShadow, isShadow);
        //������λ�����ݴ�����Ⱦ����
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //�����㷨�������ݴ�����Ⱦ����
        GLES20.glVertexAttribPointer
                (
                        maNormalHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );
        //��������ɫ�������ݴ�����Ⱦ����
        GLES20.glVertexAttribPointer
                (
                        maColorHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        4 * 4,
                        mColorBuffer
                );
        //���ö���λ�á�����������
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);

        //���Ƽ��ص�����
        if (MySurfaceView.isFill) {
            //����������
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        } else {
            //���������Ĵ�ϸ
            GLES20.glLineWidth(2);
            //�����߿�
            //���Ƽ��ص�����
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount);
        }
    }

    public Vector<Vector3f> getVertices() {
        Vector<Vector3f> newVertices = new Vector<Vector3f>();

        for (int i = 0; i < vertices.size(); i++) {
            newVertices.add(vertices.get(i));
        }

        return newVertices;
    }

    public Vector<Integer> getIndices() {
        Vector<Integer> newIndices = new Vector<Integer>();

        for (int i = 0; i < indices.size(); i++) {
            newIndices.add(indices.get(i));
        }

        return newIndices;
    }

    public int getNormalMode() {

        return normalMode;
    }

    public boolean isEmpty() {
        if (indices.size() == 0) {
            return true;
        } else {
            return false;
        }
    }


    public Vector3f getMean() {
        Vector3f mean = new Vector3f(0, 0, 0);
        for (int i = 0; i < vertices.size(); i++) {
            mean.x += vertices.get(i).x;
            mean.y += vertices.get(i).y;
            mean.z += vertices.get(i).z;
        }
        mean.x /= vertices.size();
        mean.y /= vertices.size();
        mean.z /= vertices.size();

        return mean;
    }


}
