package com.bn.object;

import android.opengl.GLES20;

import com.bn.main.MatrixState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.bn.main.Constant.UNIT_SIZE;

//����ϵ����
public class AxisLine {
    int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int muMMatrixHandle;//λ�á���ת�任��������
    int muProjCameraMatrixHandle;//ͶӰ���������Ͼ�������

    int maPositionHandle; //����λ���������� 
    int maColorHandle; //������ɫ��������
    String mVertexShader;//������ɫ������ű�  	 
    String mFragmentShader;//ƬԪ��ɫ������ű�

    FloatBuffer mVertexBuffer1;//�����������ݻ���
    FloatBuffer mColorBuffer1;//������ɫ���ݻ���
    FloatBuffer mVertexBuffer2;//�����������ݻ���
    FloatBuffer mColorBuffer2;//������ɫ���ݻ���
    int vCount = 0;


    public AxisLine() {
        //��ʼ��������������ɫ����
        initVertexData();
    }

    //��ʼ��������������ɫ���ݵķ���
    public void initVertexData() {
        //�����������ݵĳ�ʼ��================begin============================
        vCount = 2;


        float vertices1[] = new float[]
                {
                        2 * UNIT_SIZE, 0, 0,
                        2 * -UNIT_SIZE, 0, 0,
                };


        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb1 = ByteBuffer.allocateDirect(vertices1.length * 4);
        vbb1.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer1 = vbb1.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer1.put(vertices1);//�򻺳����з��붥����������
        mVertexBuffer1.position(0);//���û�������ʼλ��

        float vertices2[] = new float[]
                {
                        2.3f * UNIT_SIZE, 0, 0,
                        2.3f * -UNIT_SIZE, 0, 0,
                };

        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb2 = ByteBuffer.allocateDirect(vertices2.length * 4);
        vbb2.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer2 = vbb2.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer2.put(vertices2);//�򻺳����з��붥����������
        mVertexBuffer2.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�����������ݵĳ�ʼ��================end============================


        //������ɫ���ݵĳ�ʼ��================begin============================
        float colors1[] = new float[]//������ɫֵ���飬ÿ������4��ɫ��ֵRGBA
                {
                        0, 0, 1, 1,
                        0, 0, 1, 1,

                };


        //����������ɫ���ݻ���
        ByteBuffer cbb1 = ByteBuffer.allocateDirect(colors1.length * 4);
        cbb1.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mColorBuffer1 = cbb1.asFloatBuffer();//ת��ΪFloat�ͻ���
        mColorBuffer1.put(colors1);//�򻺳����з��붥����ɫ����
        mColorBuffer1.position(0);//���û�������ʼλ��

        //������ɫ���ݵĳ�ʼ��================begin============================
        float colors2[] = new float[]//������ɫֵ���飬ÿ������4��ɫ��ֵRGBA
                {
                        1, 0, 0, 1,
                        1, 0, 0, 1,

                };
        //����������ɫ���ݻ���
        ByteBuffer cbb2 = ByteBuffer.allocateDirect(colors2.length * 4);
        cbb2.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mColorBuffer2 = cbb2.asFloatBuffer();//ת��ΪFloat�ͻ���
        mColorBuffer2.put(colors2);//�򻺳����з��붥����ɫ����
        mColorBuffer2.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //������ɫ���ݵĳ�ʼ��================end============================

    }

    //��ʼ��shader
    public void initShader(int Program) {
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = Program;
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�����ɫ��������id  
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //��ȡ������ͶӰ���������Ͼ�������
        muProjCameraMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMProjCameraMatrix");
    }

    public void drawSelf(boolean isChoose) {

        //�ƶ�ʹ��ĳ��shader����
        GLES20.glUseProgram(mProgram);
        //�����ձ任������shader����
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //��λ�á���ת�任������shader����
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //��ͶӰ���������Ͼ�������ɫ������
        GLES20.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);


        FloatBuffer mVertexBuffer = (isChoose == false ? mVertexBuffer1 : mVertexBuffer2);
        FloatBuffer mColorBuffer = (isChoose == false ? mColorBuffer1 : mColorBuffer2);

        //���Ͷ���λ������
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //���Ͷ�����������
        GLES20.glVertexAttribPointer
                (
                        maColorHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        4 * 4,
                        mColorBuffer
                );

        //������λ����������
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        //��������ɫ��������
        GLES20.glEnableVertexAttribArray(maColorHandle);


        //���������Ĵ�ϸ
        GLES20.glLineWidth(8);
        //����
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount);


    }
}