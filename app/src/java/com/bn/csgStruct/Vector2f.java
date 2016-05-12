package com.bn.csgStruct;

import com.bn.Main.Constant;

public class Vector2f {
    public float x;
    public float y;

    public Vector2f(float X, float Y) {
        x = X;
        y = Y;
    }

    public Vector2f add(Vector2f v) {
        return new Vector2f(this.x + v.x, this.y + v.y);
    }

    public Vector2f minus(Vector2f v) {
        return new Vector2f(this.x - v.x, this.y - v.y);
    }

    public Vector2f multiK(float k) {
        return new Vector2f(this.x * k, this.y * k);
    }

    public Vector2f normalize() {
        float mod = module();
        x /= mod;
        y /= mod;
        return new Vector2f(this.x, this.y);
    }

    public float cross(Vector2f v) {
        return x * v.y - y * v.x;
    }

    public float multiV(Vector2f v) {
        return this.x * v.x + this.y * v.y;
    }

    public float module(){
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2f vector2f = (Vector2f) o;

        return (Math.abs(vector2f.x-x) <= Constant.TOL) &&
                (Math.abs(vector2f.y-y) <= Constant.TOL);
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
