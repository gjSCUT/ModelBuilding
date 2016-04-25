package com.bn.brepStruct;


public class Point3f {
    public float x;
    public float y;
    public float z;

    public Point3f() {
    }

    public Point3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3f(float[] v) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }

    public Point3f(Point3f p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    //加法
    public Point3f add(Point3f v) {
        return new Point3f(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    //减法
    public Point3f minus(Point3f v) {
        return new Point3f(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    //与常量相乘
    public Point3f multiK(float k) {
        return new Point3f(this.x * k, this.y * k, this.z * k);
    }


}
