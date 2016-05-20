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

        //��ʼ��������������ɫ����
        initVertexData(scale, face, line);

        Box = new Bound(vertexs);

        initShader(ShaderManager.getShadowshaderProgram());
    }

    //�Զ���ĳ�ʼ���������ݵķ���
    public void initVertexData(float scale, List<Vector2f> face, List<Vector2f> line) {
        //��Ա������ʼ��
        face.add(new Vector2f(face.get(0).x, face.get(0).y));

        Vector2f v1 = face.get(0).minus(face.get(1));
        Vector2f v2 = face.get(1).minus(face.get(2));

        if(v1.cross(v2) > 0)
            Collections.reverse(face);

        if(line.get(0).y > line.get(line.size()-1).y)
            Collections.reverse(line);



        //����
        int nRow = line.size() - 1;
        int nCol = face.size() - 1;

        for (int i = 0; i < nRow + 1; i++) {
            float r = line.get(i).x * scale;//��ǰԲ�İ뾶
            float y = line.get(i).y * scale;//��ǰyֵ
            for (Vector2f v : face)//�ظ���һ�ж��㣬�����������ļ���
            {
                vertices.add(new Vector3f(v.x * scale + r, y, v.y * scale));
            }
        }
        float r = line.get(0).x * scale;//��ǰԲ�İ뾶
        float y = line.get(0).y * scale;//��ǰyֵ
        vertices.add(new Vector3f(r, y, 0));
        r = line.get(line.size() - 1).x * scale;//��ǰԲ�İ뾶
        y = line.get(line.size() - 1).y * scale;//��ǰyֵ
        vertices.add(new Vector3f(r, y, 0));
        //����
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                int index = i * (nCol + 1) + j;//��ǰ����
                //��������
                indices.add(index + 1);//��һ��---1
                indices.add(index + nCol + 2);//��һ����һ��---3
                indices.add(index + nCol + 1);//��һ��---2

                indices.add(index + 1);//��һ��---1
                indices.add(index + nCol + 1);//��һ��---2
                indices.add(index);//��ǰ---0
            }
        }

        for (int j = 0; j < nCol; j++) {
            int index = j;//��ǰ����
            //��������
            indices.add(index);//��һ��---1
            indices.add(vertices.size() - 2);//��һ��---2
            indices.add(index + 1);//��ǰ---0

            index = nRow * (nCol + 1) + j;//��ǰ����
            //��������
            indices.add(index + 1);//��һ��---1
            indices.add(vertices.size() - 1);//��һ��---2
            indices.add(index);//��ǰ---0
        }

        vCount = indices.size();//�������������nColumn*nRow*2�������Σ�ÿ�������ζ�����������

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

        //������ƶ���
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
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertexs);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨��������
        mNormalBuffer.position(0);//���û�������ʼλ��

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mColorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mColorBuffer.put(colors);//�򻺳����з��붥�㷨��������
        mColorBuffer.position(0);//���û�������ʼλ��
    }

    //�Զ����ʼ����ɫ��initShader����
    public void initShader(int program) {
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = program;
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
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount);
        }
    }
}
