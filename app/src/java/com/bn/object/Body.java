package com.bn.object;

import android.opengl.Matrix;
import android.util.Log;

import com.bn.csgStruct.Vector2f;
import com.bn.csgStruct.Vector3f;
import com.bn.csgStruct.Bound;
import com.bn.csgStruct.Quaternion;
import com.bn.csgStruct.Quaternion.qType;

//物体基类
public class Body implements Cloneable {
    public Quaternion quater;
    public float xLength;//绕x轴平移距离
    public float yLength;//绕y轴平移距离
    public float zLength;//绕x轴平移距离
    public float xScale;//绕x轴放缩倍数
    public float yScale;//绕y轴放缩倍数
    public float zScale;//绕z轴放缩倍数 
    public boolean isChoosed;//是否被选择
    public Axis la;//坐标轴
    public Vector2f vx, vy, vz;//三个轴在屏幕投影的向量
    protected float[] vertexs;
    protected float[] normals;
    protected int vCount = 0;
    float[] m = new float[16];//仿射变换的矩阵
    Bound Box;//仿射变换之前的包围盒

    public Body() {
        quater = new Quaternion(qType.TRANSFFORM);
        xLength = 0;//绕x轴平移距离
        yLength = 0;//绕y轴平移距离
        zLength = 0;//绕x轴平移距离
        xScale = 1;//绕x轴放缩倍数
        yScale = 1;//绕y轴放缩倍数
        zScale = 1;//绕z轴放缩倍数
        isChoosed = false;
    }

    public void copyBody(Body b) {
        xLength = b.xLength;
        yLength = b.yLength;
        zLength = b.zLength;
        xScale = b.xScale;
        yScale = b.yScale;
        zScale = b.zScale;
        quater = new Quaternion(b.quater);
    }

    public void setBody() {
        //设置绕x轴缩放
        MatrixState.scale(xScale, 1, 1);
        //设置绕y轴缩放
        MatrixState.scale(1, yScale, 1);
        //设置绕z轴缩放
        MatrixState.scale(1, 1, zScale);

        //设置旋转矩阵
        MatrixState.rotate(quater.getRotateMatrix());


        //设置绕x轴移动
        MatrixState.translate(xLength, 0, 0);
        //设置绕y轴移动
        MatrixState.translate(0, -yLength, 0);
        //设置绕z轴移动
        MatrixState.translate(0, 0, zLength);


        //设置三轴向量
        setVN();
        //复制变换矩阵
        copyM();
    }

    private void setVN() {
        float[] vxp1 = new float[]{1, 0, 0, 1};
        float[] vxp2 = new float[]{-1, 0, 0, 1};
        float[] vyp1 = new float[]{0, 1, 0, 1};
        float[] vyp2 = new float[]{0, -1, 0, 1};
        float[] vzp1 = new float[]{0, 0, 1, 1};
        float[] vzp2 = new float[]{0, 0, -1, 1};
        //计算坐标轴三向量
        float[] winXY1 = new float[3];
        float[] winXY2 = new float[3];
        winXY1 = MatrixState.getProject(vxp1);
        winXY2 = MatrixState.getProject(vxp2);
        vx = new Vector2f(winXY1[0] - winXY2[0], winXY1[1] - winXY2[1]);
        winXY1 = MatrixState.getProject(vyp1);
        winXY2 = MatrixState.getProject(vyp2);
        vy = new Vector2f(winXY1[0] - winXY2[0], winXY1[1] - winXY2[1]);
        winXY1 = MatrixState.getProject(vzp1);
        winXY2 = MatrixState.getProject(vzp2);
        vz = new Vector2f(winXY1[0] - winXY2[0], winXY1[1] - winXY2[1]);
    }

    public float[] getVertexs() {
        return vertexs;
    }

    //复制变换矩阵
    private void copyM() {
        for (int i = 0; i < 16; i++) {
            m[i] = MatrixState.getMMatrix()[i];
        }
    }


    //更新AABB包围盒
    public Bound getCurrBox() {
        return Box.setToTransformedBox(m);//获取变换后的包围盒

    }

    //得到变换矩阵
    public float[] getM() {
        return m;
    }

    //沿轴平移
    public void Translate(float[] direction, float lengthMove) {
        Matrix.multiplyMV(direction, 0, quater.getRotateMatrix(), 0, direction, 0);

        xLength += direction[0] * lengthMove;
        yLength -= direction[1] * lengthMove;
        zLength += direction[2] * lengthMove;
    }


    public void Rotate(float x, float y, float z, float angle) {
        quater.addRoutate(angle, new Vector3f(x, y, z));
    }

    public void Scale(float x, float y, float z) {
        xScale *= x;
        yScale *= y;
        zScale *= z;
    }

    //判断轴
    public void switchAxis(Vector2f Vector2fPDownP2) {
        float degreeX = Math.abs(VectorUtil.getDegree(Vector2fPDownP2, vx));
        float degreeY = Math.abs(VectorUtil.getDegree(Vector2fPDownP2, vy));
        float degreeZ = Math.abs(VectorUtil.getDegree(Vector2fPDownP2, vz));

        if (degreeX > degreeY && degreeX > degreeZ) {
            if (vx.module() > Constant.HEIGHT * Constant.WIDTH / 50000) {
                MySurfaceView.modeP2 = 1;//设手指方向为vx轴
                MySurfaceView.isAxle = 1;//设置x轴变色
                MySurfaceView.modeP3 = 1;//设手指方向为vx轴
            } else if (degreeY > degreeZ) {
                MySurfaceView.modeP2 = 2;//设手指方向为vy轴
                MySurfaceView.isAxle = 2;//设置y轴变色
                MySurfaceView.modeP3 = 2;//设手指方向为v轴
            } else {
                MySurfaceView.modeP2 = 3;//设手指方向为vz轴
                MySurfaceView.isAxle = 3;//设置y轴变色
                MySurfaceView.modeP3 = 3;//设手指方向为v轴
            }
        } else if (degreeY > degreeX && degreeY > degreeZ) {
            if (vy.module() > Constant.HEIGHT * Constant.WIDTH / 50000) {
                MySurfaceView.modeP2 = 2;//设手指方向为vy轴
                MySurfaceView.isAxle = 2;//设置x轴变色
                MySurfaceView.modeP3 = 2;//设手指方向为v轴
            } else if (degreeX > degreeZ) {
                MySurfaceView.modeP2 = 1;//设手指方向为vx轴
                MySurfaceView.isAxle = 1;//设置y轴变色
                MySurfaceView.modeP3 = 1;//设手指方向为v轴
            } else {
                MySurfaceView.modeP2 = 3;//设手指方向为vz轴
                MySurfaceView.isAxle = 3;//设置y轴变色
                MySurfaceView.modeP3 = 3;//设手指方向为v轴
            }
        } else {
            if (vz.module() > Constant.HEIGHT * Constant.WIDTH / 50000) {
                MySurfaceView.modeP2 = 3;//设手指方向为vz轴
                MySurfaceView.isAxle = 3;//设置x轴变色
                MySurfaceView.modeP3 = 3;//设手指方向为v轴
            } else if (degreeY > degreeX) {
                MySurfaceView.modeP2 = 2;//设手指方向为vy轴
                MySurfaceView.isAxle = 2;//设置y轴变色
                MySurfaceView.modeP3 = 2;//设手指方向为v轴
            } else {
                MySurfaceView.modeP2 = 1;//设手指方向为vx轴
                MySurfaceView.isAxle = 1;//设置y轴变色
                MySurfaceView.modeP3 = 1;//设手指方向为v轴
            }
        }
    }

    //面贴合算法
    public void faceMatch(float[] begin, float[] end, Body endBody) {
        quater = new Quaternion(endBody.quater);
        float length = Math.abs(begin[0] * xScale + begin[1] * yScale + begin[2] * zScale) +
                Math.abs(end[0] * endBody.xScale + end[1] * endBody.yScale + end[2] * endBody.zScale);
        float[] rotateM = quater.getRotateMatrix();
        if (begin[0] != 0 && end[0] != 0 || begin[1] != 0 && end[1] != 0 || begin[2] != 0 && end[2] != 0) {
            //1,0,0	1,0,0 || 0,1,0  0,1,0 || 0,0,1  0,0,1
            Vector3f direction = new Vector3f(
                    Math.abs(begin[2] + end[2]) / 2,
                    Math.abs(begin[0] + end[0]) / 2,
                    Math.abs(begin[1] + end[1]) / 2);
            if (direction.x != 0 || direction.y != 0 || direction.z != 0) {
                quater.addRoutate(180, direction);
            }
        } else {
            Vector3f direction = new Vector3f(
                    (Math.abs(begin[1] + end[2]) + Math.abs(begin[2] - end[1]) - 1),
                    (Math.abs(begin[0] + end[2]) + Math.abs(begin[2] - end[0]) - 1),
                    (Math.abs(begin[0] + end[1]) + Math.abs(begin[1] - end[0]) - 1));
            if (direction.x != 0 || direction.y != 0 || direction.z != 0) {
                quater.addRoutate(90, direction);
            }
        }
        Matrix.multiplyMV(end, 0, rotateM, 0, end, 0);
        xLength = endBody.xLength + length * end[0];
        yLength = endBody.yLength - length * end[1];
        zLength = endBody.zLength + length * end[2];
    }

    //根据二维向量得到拾取面的三维向量
    public float[] getFitTargetFace(Vector2f fingerDirection, int mode) {
        fingerDirection = fingerDirection.normalize();
        //计算六个坐标轴方向（表面法向量方向）与手指法向量的点乘最大值
        float bestValue = -2;
        float[] rtn = new float[]{0, 0, 0, 1};
        float temp;

        temp = fingerDirection.multiV(vx);
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{1 * mode, 0, 0, 1};
        }
        temp = fingerDirection.multiV(vx.multiK(-1));
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{-1 * mode, 0, 0, 1};
        }
        temp = fingerDirection.multiV(vy);
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{0, 1 * mode, 0, 1};
        }
        temp = fingerDirection.multiV(vy.multiK(-1));
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{0, -1 * mode, 0, 1};
        }
        temp = fingerDirection.multiV(vz);
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{0, 0, 1 * mode, 1};
        }
        temp = fingerDirection.multiV(vz.multiK(-1));
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{0, 0, -1 * mode, 1};
        }

        return rtn;
    }

    @Override
    public Object clone() {
        Body o = null;
        try {
            o = (Body) super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e("error", e.toString());
        }
        o.quater = (Quaternion) quater.clone();
        return o;
    }

    public void initVertexData() {}

    public void initShader(int Program) {}

    public void drawSelf(int isShadow){}
}
