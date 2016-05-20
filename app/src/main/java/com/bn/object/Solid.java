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
    FloatBuffer mVertexBuffer;//�����������ݻ���
    FloatBuffer mNormalBuffer;//���㷨�������ݻ���
    FloatBuffer mColorBuffer;//���㷨�������ݻ���

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


    public Solid(Body b) {
        la = new Axis();
        la.initShader(ShaderManager.getLineShaderProgram());

        Solid s = (Solid) b;
        vertices = s.getVertices();
        indices = s.getIndices();
        normalMode = s.getNormalMode();

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


    public void setVN() {
        //����������������
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
