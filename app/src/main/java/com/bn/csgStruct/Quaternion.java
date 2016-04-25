package com.bn.csgStruct;

import android.util.Log;

import com.bn.csgStruct.Struct.Vector3f;

/**
 * 四元数类
 * 可以用来表示两种类型的数据
 * 第一种是三维点，需要将四元数的type 设置为qType.POINT
 * 另一种是三维变化，type设置为qType.TRANSFORM
 * <p/>
 * 要进行四元数的变化，需要一个四元数表示的三维点和三维变化
 * 首先向变化里添加变化  例如：addRoutate();
 * 可以添加多个不同的变化
 * <p/>
 * 然后三维点调用需要的变化 例如  point.doTransform(transform);
 *
 * @author vincent
 *         2015,1,25
 */
public class Quaternion implements Cloneable {
    static final String tagQuaternionString = "Quaternion";
    /**
     * 四元数的数据，数组长度4
     */
    private float scalar;
    private Vector3f vector;
    /**
     * 此四元数的类型
     */
    private qType type;

    /**
     * 初始化一个四元数
     * 初始化一个点时需要点的三维坐标参数
     *
     * @param t
     * @param v
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
        //此时类型为transform
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
     * 四元数点乘
     *
     * @param q
     * @return
     */
    public float dotMulti(Quaternion q) {
        return scalar * q.scalar + vector.multiV(q.vector);
    }

    /**
     * 四元数乘法（类似叉乘）
     * 返回一个四元数
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
     * 四元数的数乘
     * 乘以一个数
     *
     * @param num
     * @return
     */
    public Quaternion MultiNumber(float num) {
        return new Quaternion(type, scalar * num, vector.multiK(num));
    }

    /**
     * 求四元数的共轭
     *
     * @return
     */
    public Quaternion conjugate() {
        return new Quaternion(type, scalar, vector.multiK(-1));
    }

    /**
     * 求四元数的逆
     *
     * @return
     */
    public Quaternion inverse() {
        return new Quaternion(this).conjugate().MultiNumber(1 / (this.dotMulti(this)));
    }

    /**
     * 求模
     *
     * @return
     */
    public float module() {
        return (float) Math.sqrt(dotMulti(this));
    }

    /**
     * 四元数的单位化
     *
     * @return
     */
    public Quaternion normalize() {
        return new Quaternion(this).MultiNumber(1 / module());
    }

    /**
     * 在transform 四元数中增加旋转变化
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
     * 对一个四元数点实施四元数变化
     *
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
        float[] matrix = new float[16];//旋转矩阵
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
     * 四元数的类型
     * 1.点：表示一个点
     * 2.变化：表示一种三维变化
     */
    public enum qType {
        POINT, TRANSFFORM
    }
}