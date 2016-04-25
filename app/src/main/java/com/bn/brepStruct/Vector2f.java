package com.bn.brepStruct;

public class Vector2f {
    public float x;
    public float y;
    public float mod;

    public Vector2f(float X, float Y) {
        x = X;
        y = Y;
        mod = (float) Math.sqrt(x * x + y * y);
    }

    //�ӷ�
    public Vector2f add(Vector2f v) {
        return new Vector2f(this.x + v.x, this.y + v.y);
    }

    //����
    public Vector2f minus(Vector2f v) {
        return new Vector2f(this.x - v.x, this.y - v.y);
    }

    //�˷�
    public float multi(Vector2f v) {
        return this.x * v.x + this.y * v.y;
    }

    //�볣�����
    public Vector2f multiK(float k) {
        return new Vector2f(this.x * k, this.y * k);
    }

    //���
    public void normalize() {
        x /= mod;
        y /= mod;
    }
}
