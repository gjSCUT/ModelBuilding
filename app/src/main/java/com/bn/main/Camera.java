package com.bn.main;


public class Camera {
    float distance;
    float distancetotal;//up�����������λ�������ϳɵ�ģ��
    float cx;    //�����λ��x
    float cy;   //�����λ��y
    float cz;   //�����λ��z
    float tx;   //�����Ŀ���x
    float ty;   //�����Ŀ���y
    float tz;   //�����Ŀ���z
    float upx;  //�����UP����X����
    float upy;//�����UP����Y����
    float upz; //�����UP����Z����
    float angelA;    //��ת��
    float angelB;    //��ת��
    float angelC;   //����up�����������λ�����������������еļн�

    public Camera() {
        distance = 70;
        distancetotal = (float) Math.sqrt(1 + distance * distance);
        cx = 0f;    //�����λ��x
        cy = 0f;   //�����λ��y
        cz = distance;   //�����λ��z
        tx = 0f;   //�����Ŀ���x
        ty = 0f;   //�����Ŀ���y
        tz = 0f;   //�����Ŀ���z
        upx = 0f;  //�����UP����X����
        upy = 1.0f;//�����UP����Y����
        upz = 0.0f; //�����UP����Z����
        angelA = 0;//(float) (Math.PI/4);	//��ת��
        angelB = 0;//(float) (Math.PI/4);	//��ת��
        angelC = (float) Math.atan(1 / distance);
    }

    public void setCarema() {

        cx = (float) (distance * Math.sin(angelA) * Math.cos(angelB));
        cy = (float) (distance * Math.sin(angelB));
        cz = (float) (distance * Math.cos(angelA) * Math.cos(angelB));
        upx = (float) (distancetotal * Math.cos(angelB + angelC) * Math.sin(angelA) - distance * Math.cos(angelB) * Math.sin(angelA));
        upy = (float) (distancetotal * Math.sin(angelB + angelC) - distance * Math.sin(angelB));
        upz = (float) (distancetotal * Math.cos(angelB + angelC) * Math.cos(angelA) - distance * Math.cos(angelB) * Math.cos(angelA));

        //���ô˷����������͸��ͶӰ����
        MatrixState.setProjectFrustum(-Constant.RATIO, Constant.RATIO, -1, 1, 10, 400);
        // ���ô˷������������9����λ�þ���
        MatrixState.setCamera(cx, cy, cz, tx, ty, tz, upx, upy, upz);
    }
}
