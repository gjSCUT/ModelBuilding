package com.bn.csgStruct;


import com.bn.Main.Constant;

public class Vector3f {
    public float x;
    public float y;
    public float z;

    public Vector3f() {
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(float[] v) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }

    public Vector3f add(Vector3f v) {
        return new Vector3f(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector3f minus(Vector3f v) {
        return new Vector3f(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vector3f multiK(float k) {
        return new Vector3f(this.x * k, this.y * k, this.z * k);
    }

    public Vector3f divideK(float k) {
        return new Vector3f(this.x / k, this.y / k, this.z / k);
    }


    public Vector3f cross(Vector3f b) {
        return new Vector3f(
                y * b.z - z * b.y,
                z * b.x - x * b.z,
                x * b.y - y * b.x
        );
    }

    public Vector3f normalize() {
        float mod = module();
        x /= mod;
        y /= mod;
        z /= mod;
        return new Vector3f(x, y, z);
    }

    public float multiV(Vector3f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public float module() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3f vector3f = (Vector3f) o;

        return (Math.abs(vector3f.x-x) <= Constant.TOL) &&
                (Math.abs(vector3f.y-y) <= Constant.TOL) &&
                (Math.abs(vector3f.z-z) <= Constant.TOL);

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }
}
