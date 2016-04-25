package com.bn.object;

import android.opengl.Matrix;
import android.util.Log;

import com.bn.main.Constant;
import com.bn.main.MatrixState;
import com.bn.main.MySurfaceView;
import com.bn.csgStruct.Bound;
import com.bn.csgStruct.Quaternion;
import com.bn.csgStruct.Quaternion.qType;
import com.bn.csgStruct.Struct.Vector2f;
import com.bn.csgStruct.Struct.Vector3f;
import com.bn.util.VectorUtil;

//�������
public class Body implements Cloneable {
    public Quaternion quater;
    public float xLength;//��x��ƽ�ƾ���
    public float yLength;//��y��ƽ�ƾ���
    public float zLength;//��x��ƽ�ƾ���
    public float xScale;//��x���������
    public float yScale;//��y���������
    public float zScale;//��z��������� 
    public boolean isChoosed;//�Ƿ�ѡ��
    public Axis la;//������
    public Vector2f vx, vy, vz;//����������ĻͶӰ������
    protected float[] vertexs;
    protected float[] normals;
    protected int vCount = 0;
    float[] m = new float[16];//����任�ľ���
    Bound Box;//����任֮ǰ�İ�Χ��

    public Body() {
        quater = new Quaternion(qType.TRANSFFORM);
        xLength = 0;//��x��ƽ�ƾ���
        yLength = 0;//��y��ƽ�ƾ���
        zLength = 0;//��x��ƽ�ƾ���
        xScale = 1;//��x���������
        yScale = 1;//��y���������
        zScale = 1;//��z���������
        isChoosed = false;
        //Box=new Bound(new Vector3f(1,0,1),new Vector3f(1,2,-1),new Vector3f(-1,0,-1));
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
        //������x������
        MatrixState.scale(xScale, 1, 1);
        //������y������
        MatrixState.scale(1, yScale, 1);
        //������z������
        MatrixState.scale(1, 1, zScale);

        //������ת����
        MatrixState.rotate(quater.getRotateMatrix());


        //������x���ƶ�
        MatrixState.translate(xLength, 0, 0);
        //������y���ƶ�
        MatrixState.translate(0, -yLength, 0);
        //������z���ƶ�
        MatrixState.translate(0, 0, zLength);


        //������������
        setVN();
        //���Ʊ任����
        copyM();
    }

    private void setVN() {
        float[] vxp1 = new float[]{1, 0, 0, 1};
        float[] vxp2 = new float[]{-1, 0, 0, 1};
        float[] vyp1 = new float[]{0, 1, 0, 1};
        float[] vyp2 = new float[]{0, -1, 0, 1};
        float[] vzp1 = new float[]{0, 0, 1, 1};
        float[] vzp2 = new float[]{0, 0, -1, 1};
        //����������������
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


    //���Ʊ任����
    private void copyM() {
        for (int i = 0; i < 16; i++) {
            m[i] = MatrixState.getMMatrix()[i];
        }
    }


    //����AABB��Χ��
    public Bound getCurrBox() {
        return Box.setToTransformedBox(m);//��ȡ�任��İ�Χ��

    }

    //�õ��任����
    public float[] getM() {
        return m;
    }

    //����ƽ��
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

    //�ж���
    public void switchAxis(Vector2f Vector2fPDownP2) {
        float degreeX = Math.abs(VectorUtil.getDegree(Vector2fPDownP2, vx));
        float degreeY = Math.abs(VectorUtil.getDegree(Vector2fPDownP2, vy));
        float degreeZ = Math.abs(VectorUtil.getDegree(Vector2fPDownP2, vz));

        if (degreeX > degreeY && degreeX > degreeZ) {
            if (vx.mod > Constant.HEIGHT * Constant.WIDTH / 50000) {
                MySurfaceView.modeP2 = 1;//����ָ����Ϊvx��
                MySurfaceView.isAxle = 1;//����x���ɫ
                MySurfaceView.modeP3 = 1;//����ָ����Ϊvx��
            } else if (degreeY > degreeZ) {
                MySurfaceView.modeP2 = 2;//����ָ����Ϊvy��
                MySurfaceView.isAxle = 2;//����y���ɫ
                MySurfaceView.modeP3 = 2;//����ָ����Ϊv��
            } else {
                MySurfaceView.modeP2 = 3;//����ָ����Ϊvz��
                MySurfaceView.isAxle = 3;//����y���ɫ
                MySurfaceView.modeP3 = 3;//����ָ����Ϊv��
            }
        } else if (degreeY > degreeX && degreeY > degreeZ) {
            if (vy.mod > Constant.HEIGHT * Constant.WIDTH / 50000) {
                MySurfaceView.modeP2 = 2;//����ָ����Ϊvy��
                MySurfaceView.isAxle = 2;//����x���ɫ
                MySurfaceView.modeP3 = 2;//����ָ����Ϊv��
            } else if (degreeX > degreeZ) {
                MySurfaceView.modeP2 = 1;//����ָ����Ϊvx��
                MySurfaceView.isAxle = 1;//����y���ɫ
                MySurfaceView.modeP3 = 1;//����ָ����Ϊv��
            } else {
                MySurfaceView.modeP2 = 3;//����ָ����Ϊvz��
                MySurfaceView.isAxle = 3;//����y���ɫ
                MySurfaceView.modeP3 = 3;//����ָ����Ϊv��
            }
        } else {
            if (vz.mod > Constant.HEIGHT * Constant.WIDTH / 50000) {
                MySurfaceView.modeP2 = 3;//����ָ����Ϊvz��
                MySurfaceView.isAxle = 3;//����x���ɫ
                MySurfaceView.modeP3 = 3;//����ָ����Ϊv��
            } else if (degreeY > degreeX) {
                MySurfaceView.modeP2 = 2;//����ָ����Ϊvy��
                MySurfaceView.isAxle = 2;//����y���ɫ
                MySurfaceView.modeP3 = 2;//����ָ����Ϊv��
            } else {
                MySurfaceView.modeP2 = 1;//����ָ����Ϊvx��
                MySurfaceView.isAxle = 1;//����y���ɫ
                MySurfaceView.modeP3 = 1;//����ָ����Ϊv��
            }
        }
    }

    //�������㷨
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

    //���ݶ�ά�����õ�ʰȡ�����ά����
    public float[] getFitTargetFace(Vector2f fingerDirection, int mode) {
        fingerDirection.normalize();
        //�������������᷽�򣨱��淨������������ָ�������ĵ�����ֵ
        float bestValue = -2;
        float[] rtn = new float[]{0, 0, 0, 1};
        float temp;

        temp = fingerDirection.multi(vx);
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{1 * mode, 0, 0, 1};
        }
        temp = fingerDirection.multi(vx.multiK(-1));
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{-1 * mode, 0, 0, 1};
        }
        temp = fingerDirection.multi(vy);
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{0, 1 * mode, 0, 1};
        }
        temp = fingerDirection.multi(vy.multiK(-1));
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{0, -1 * mode, 0, 1};
        }
        temp = fingerDirection.multi(vz);
        if (temp > bestValue) {
            bestValue = temp;
            rtn = new float[]{0, 0, 1 * mode, 1};
        }
        temp = fingerDirection.multi(vz.multiK(-1));
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

    public void initVertexData() {
    }

    public void initShader(int Program) {
    }
}
