package com.bn.object;

import com.bn.main.MatrixState;
import com.bn.main.MySurfaceView;

//������
public class Axis {
    //���ڻ��Ƹ��������ɫ����
    AxisLine lx, ly, lz;

    public Axis() {
        //�������ڻ��Ƹ��������ɫ����
        lx = new AxisLine();
        ly = new AxisLine();
        lz = new AxisLine();
    }

    public void initShader(int mProgram) {
        lx.initShader(mProgram);
        ly.initShader(mProgram);
        lz.initShader(mProgram);
    }

    public void drawSelf() {

        //�����ֳ�
        MatrixState.pushMatrix();


        //x��
        MatrixState.pushMatrix();
        if (MySurfaceView.isAxle == 1) lx.drawSelf(true);
        else lx.drawSelf(false);
        MatrixState.popMatrix();

        //y��
        MatrixState.pushMatrix();
        MatrixState.rotate(0, 0, 1, 90);
        if (MySurfaceView.isAxle == 2) ly.drawSelf(true);
        else ly.drawSelf(false);
        MatrixState.popMatrix();

        //z��
        MatrixState.pushMatrix();
        MatrixState.rotate(0, 1, 0, 90);
        if (MySurfaceView.isAxle == 3) lz.drawSelf(true);
        else lz.drawSelf(false);
        MatrixState.popMatrix();


        //�ָ��ֳ�
        MatrixState.popMatrix();

    }


}
