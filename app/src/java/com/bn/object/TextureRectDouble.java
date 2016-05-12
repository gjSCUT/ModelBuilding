package com.bn.object;

import com.bn.Main.MatrixState;
import com.bn.csgStruct.Quaternion;

//纹理矩形双面
public class TextureRectDouble extends Body {
    //用于绘制各个面的颜色矩形
    TextureRect t1, t2;

    public TextureRectDouble(float size) {
        //创建用于绘制各个面的颜色矩形
        t1 = new TextureRect(size);
        t2 = new TextureRect(size);
    }

    @Override
    public void initShader(int mProgram) {
        t1.initShader(mProgram);
        t2.initShader(mProgram);
    }

    public void drawSelf(int TexId) {
        setBody();

        //保护现场
        MatrixState.pushMatrix();


        //正面
        MatrixState.pushMatrix();
        t1.drawSelf(TexId);
        MatrixState.popMatrix();

        //反面
        MatrixState.pushMatrix();
        MatrixState.rotate(1, 0, 0, 180);
        t2.drawSelf(TexId);
        MatrixState.popMatrix();


        //恢复现场
        MatrixState.popMatrix();

    }


    public void match(Body b, float[] face) {
        quater = new Quaternion(b.quater);
        xLength = b.xLength;//绕x轴平移距离
        yLength = b.yLength;//绕y轴平移距离
        zLength = b.zLength;//绕x轴平移距离
        xScale = 1.2f * b.xScale;//绕x轴放缩倍数
        yScale = 1.2f * b.yScale;//绕y轴放缩倍数
        zScale = 1.2f * b.zScale;//绕z轴放缩倍数

        //绘制前小面
        if (face[2] == 1) {
            Translate(face, zScale + 0.5f);
        }
        //绘制后小面
        else if (face[2] == -1) {
            Translate(face, zScale + 0.5f);
            Rotate(0, 1, 0, 180);
        }
        //绘制上大面
        else if (face[1] == 1) {
            Translate(face, yScale + 0.5f);
            Rotate(1, 0, 0, 90);
        }
        //绘制下大面
        else if (face[1] == -1) {
            Translate(face, yScale + 0.5f);
            Rotate(1, 0, 0, -90);
        }
        //绘制左大面
        else if (face[0] == -1) {
            Translate(face, xScale + 0.5f);
            Rotate(1, 0, 0, -90);
            Rotate(0, 1, 0, 90);
        }
        //绘制右大面
        else if (face[0] == 1) {
            Translate(face, xScale + 0.5f);
            Rotate(1, 0, 0, 90);
            Rotate(0, 1, 0, -90);
        }
    }
}
