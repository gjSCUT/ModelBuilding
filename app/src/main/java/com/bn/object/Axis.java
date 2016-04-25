package com.bn.object;

import com.bn.main.MatrixState;
import com.bn.main.MySurfaceView;

//立方体
public class Axis {
    //用于绘制各个面的颜色矩形
    AxisLine lx, ly, lz;

    public Axis() {
        //创建用于绘制各个面的颜色矩形
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

        //保护现场
        MatrixState.pushMatrix();


        //x轴
        MatrixState.pushMatrix();
        if (MySurfaceView.isAxle == 1) lx.drawSelf(true);
        else lx.drawSelf(false);
        MatrixState.popMatrix();

        //y轴
        MatrixState.pushMatrix();
        MatrixState.rotate(0, 0, 1, 90);
        if (MySurfaceView.isAxle == 2) ly.drawSelf(true);
        else ly.drawSelf(false);
        MatrixState.popMatrix();

        //z轴
        MatrixState.pushMatrix();
        MatrixState.rotate(0, 1, 0, 90);
        if (MySurfaceView.isAxle == 3) lz.drawSelf(true);
        else lz.drawSelf(false);
        MatrixState.popMatrix();


        //恢复现场
        MatrixState.popMatrix();

    }


}
