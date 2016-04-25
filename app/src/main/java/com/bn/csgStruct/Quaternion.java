package com.bn.csgStruct;

import android.util.Log;

import com.bn.csgStruct.Struct.Vector3f;

/**
 * ��Ԫ����
 * ����������ʾ�������͵�����
 * ��һ������ά�㣬��Ҫ����Ԫ����type ����ΪqType.POINT
 * ��һ������ά�仯��type����ΪqType.TRANSFORM
 * <p/>
 * Ҫ������Ԫ���ı仯����Ҫһ����Ԫ����ʾ����ά�����ά�仯
 * ������仯����ӱ仯  ���磺addRoutate();
 * ������Ӷ����ͬ�ı仯
 * <p/>
 * Ȼ����ά�������Ҫ�ı仯 ����  point.doTransform(transform);
 *
 * @author vincent
 *         2015,1,25
 */
public class Quaternion implements Cloneable {
    static final String tagQuaternionString = "Quaternion";
    /**
     * ��Ԫ�������ݣ����鳤��4
     */
    private float scalar;
    private Vector3f vector;
    /**
     * ����Ԫ��������
     */
    private qType type;

    /**
     * ��ʼ��һ����Ԫ��
     * ��ʼ��һ����ʱ��Ҫ�����ά�������
     *
     * @param t
     * @param d
     */
    public Quaternion(qType t, Vector3f v) {
        if (t == qType.POINT) {
            type = t;
            scalar = 0;
            if (v == null) {
                vector = new Vector3f(0, 0, 0);
            } else {
                vector = v;
            }
        }
        //��ʱ����Ϊtransform
        else {
            type = qType.TRANSFFORM;
            scalar = 1;
            vector = new Vector3f(0, 0, 0);
        }
    }

    public Quaternion(qType t, float s, Vector3f v) {
        type = t;
        scalar = s;
        vector = v;
    }

    public Quaternion(Quaternion q) {
        type = q.type;
        scalar = q.scalar;
        vector = q.vector;
    }

    public Quaternion(qType t) {
        if (t == qType.TRANSFFORM) {
            type = qType.TRANSFFORM;
            scalar = 1;
            vector = new Vector3f(0, 0, 0);
        } else if (t == qType.POINT) {
            type = qType.POINT;
            scalar = 0;
            vector = new Vector3f(0, 0, 0);
        }
    }

    public Quaternion() {
        type = null;
        scalar = 0;
        vector = new Vector3f(0, 0, 0);
    }

    /**
     * ��Ԫ�����
     *
     * @param q
     * @return
     */
    public float dotMulti(Quaternion q) {
        return scalar * q.scalar + vector.multiV(q.vector);
    }

    /**
     * ��Ԫ���˷������Ʋ�ˣ�
     * ����һ����Ԫ��
     *
     * @param q
     * @return
     */
    public Quaternion Multi(Quaternion q) {
        Quaternion temp;
        if (type == qType.POINT || q.type == qType.POINT) {
            temp = new Quaternion(qType.POINT);
        } else {
            temp = new Quaternion(qType.TRANSFFORM);
        }
        temp.scalar = scalar * q.scalar - vector.multiV(q.vector);
        temp.vector = vector.multiK(q.scalar).add(q.vector.multiK(scalar)).add(vector.cross(q.vector));
        return temp;
    }

    /**
     * ��Ԫ��������
     * ����һ����
     *
     * @param num
     * @return
     */
    public Quaternion MultiNumber(float num) {
        return new Quaternion(type, scalar * num, vector.multiK(num));
    }

    /**
     * ����Ԫ���Ĺ���
     *
     * @return
     */
    public Quaternion conjugate() {
        return new Quaternion(type, scalar, vector.multiK(-1));
    }

    /**
     * ����Ԫ������
     *
     * @return
     */
    public Quaternion inverse() {
        return new Quaternion(this).conjugate().MultiNumber(1 / (this.dotMulti(this)));
    }

    /**
     * ��ģ
     *
     * @return
     */
    public float module() {
        return (float) Math.sqrt(dotMulti(this));
    }

    /**
     * ��Ԫ���ĵ�λ��
     *
     * @return
     */
    public Quaternion normalize() {
        return new Quaternion(this).MultiNumber(1 / module());
    }

    /**
     * ��transform ��Ԫ����������ת�仯
     *
     * @param angle
     * @param direction
     */
    public Quaternion addRoutate(float angle, Vector3f direction) {
        if (this.type != qType.TRANSFFORM)
            Log.e(tagQuaternionString, "wrong quaternion type");
        else {
            angle = (float) (angle / 360 * 2 * Math.PI);
            float sin = (float) Math.sin(angle / 2);
            float cos = (float) Math.cos(angle / 2);
            Quaternion newQua = new Quaternion(qType.TRANSFFORM);
            newQua.scalar = cos;
            direction.normalize();
            newQua.vector = direction.multiK(sin);
            newQua = newQua.Multi(this);
            scalar = newQua.scalar;
            vector = newQua.vector;
        }
        return this;
    }

    /**
     * ��һ����Ԫ����ʵʩ��Ԫ���仯
     *
     * @param point
     * @param transform
     */
    public Quaternion doTransform(Quaternion transform) {
        if (type != qType.POINT || type != qType.TRANSFFORM) {
            Log.e(tagQuaternionString, "wrong type in doTransform");
        }
        Quaternion temp = new Quaternion(qType.POINT);
        temp = Multi(this).Multi(inverse());
        this.scalar = temp.scalar;
        this.vector = temp.vector;
        return this;
    }

    public float[] getRotateMatrix() {
        float[] matrix = new float[16];//��ת����
        matrix[0] = 1.0f - 2.0f * (vector.y * vector.y + vector.z * vector.z);
        matrix[1] = 2.0f * (vector.x * vector.y - vector.z * scalar);
        matrix[2] = 2.0f * (vector.x * vector.z + vector.y * scalar);
        matrix[3] = 0.0f;
        matrix[4] = 2.0f * (vector.x * vector.y + vector.z * scalar);
        matrix[5] = 1.0f - 2.0f * (vector.x * vector.x + vector.z * vector.z);
        matrix[6] = 2.0f * (vector.y * vector.z - vector.x * scalar);
        matrix[7] = 0.0f;
        matrix[8] = 2.0f * (vector.x * vector.z - vector.y * scalar);
        matrix[9] = 2.0f * (vector.y * vector.z + vector.x * scalar);
        matrix[10] = 1.0f - 2.0f * (vector.x * vector.x + vector.y * vector.y);
        matrix[11] = 0.0f;
        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;

        return matrix;
    }

    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e("error", e.toString());
        }
        return o;
    }

    /**
     * ��Ԫ��������
     * 1.�㣺��ʾһ����
     * 2.�仯����ʾһ����ά�仯
     */
    public enum qType {
        POINT, TRANSFFORM
    }
}