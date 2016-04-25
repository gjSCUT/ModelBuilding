package com.bn.object;


import android.opengl.GLES20;

import com.bn.main.MatrixState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//������ε���
public class TextureRect extends Bo
    {
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ����������
    int maTexCoorHandle; //��������������������
    int muMMatrixHandle;
    
    int maCameraHandle; //�����λ���������� 
    int maNormalHandle; //���㷨������������ 
    int maLightLocationHandle;//��Դλ����������  
    
    
    String mVertexShader;//������ɫ������ű�  	 
    String mFragmentShader;//ƬԪ��ɫ������ű�
	
	FloatBuffer   mVertexBuffer;//�����������ݻ���
	FloatBuffer   mTexCoorBuffer;//���������������ݻ���
	FloatBuffer   mNormalBuffer;//���㷨�������ݻ���
	
    int vCount=0;   
    
    public TextureRect(float size)
    {    	
    	//��ʼ��������������ɫ����
    	initVertexData(size);
    }
    
    //��ʼ��������������ɫ���ݵķ���
    public void initVertexData(float size)
    {
    	//�����������ݵĳ�ʼ��================begin============================
        vCount=12;        
        float UNIT_SIZE=size;
        float vertices[]=new float[]
        {
    		//ǰ��
        	0,0,0,
        	UNIT_SIZE,UNIT_SIZE,0,
        	-UNIT_SIZE,UNIT_SIZE,0,
        	0,0,0,
        	-UNIT_SIZE,UNIT_SIZE,0,
        	-UNIT_SIZE,-UNIT_SIZE,0,
        	0,0,0,
        	-UNIT_SIZE,-UNIT_SIZE,0,
        	UNIT_SIZE,-UNIT_SIZE,0,
        	0,0,0,
        	UNIT_SIZE,-UNIT_SIZE,0,
        	UNIT_SIZE,UNIT_SIZE,0,
        };
		

        
        //���������ݳ�ʼ�� 
        float[] normals=new float[vertices.length];
        for(int i=0;i<normals.length;i+=3){
        	normals[i]=0;
        	normals[i+1]=0;
        	normals[i+2]=1;
        }


        float texCoor[]=new float[]
        {
        		0.5f,0.5f,	0,0, 	1,0, 
        		0.5f,0.5f, 	1,0, 	1,1, 
        		0.5f,0.5f, 	1,1, 	0,1,
        		0.5f,0.5f, 	0,1, 	0,0
        };        
        
        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        
        
        //�������㷨�������ݻ���
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);//�������㷨�������ݻ���
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨��������
        mNormalBuffer.position(0);//���û�������ʼλ��
        
        
        //�������������������ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texCoor);//�򻺳����з��붥����ɫ����
        mTexCoorBuffer.position(0);//���û�������ʼλ��

    }

    //�Զ����ʼ����ɫ����initShader����
    @Override
    public void initShader(int Program)
    {
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = Program;
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        
        //��ȡ�����ж��㷨������������id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal"); 
        //��ȡ�����������λ������id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        //��ȡ�����й�Դλ������id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation"); 
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        
        
    }
    
    public void drawSelf(int texId)
    {             

	    setBody();
	    
    	//�ƶ�ʹ��ĳ��shader����
   	 	GLES20.glUseProgram(mProgram);        
        //�����ձ任������shader����
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //��λ�á���ת�任������shader����
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
        //�������λ�ô���shader����   
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //����Դλ�ô���shader����   
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB); 
         
         //���Ͷ���λ������
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );     
         
         //���Ͷ���������������
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         ); 
         
         //���Ͷ��㷨��������
         GLES20.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         ); 
		
         
         //���ö���λ������
         GLES20.glEnableVertexAttribArray(maPositionHandle);
         //���ö�����������
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         //���ö��㷨��������
         GLES20.glEnableVertexAttribArray(maNormalHandle);
         
         //������
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //�����������
         MatrixState.pushMatrix();
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
         MatrixState.popMatrix();
         
    }
}
