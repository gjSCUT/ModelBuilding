package com.bn.csgStruct;


public class Struct {
    public static class Result {
        public boolean bIntersectResult;
        public Vector3f intersectionPoint;

        public Result() {
            bIntersectResult = true;
            intersectionPoint = new Vector3f(0, 0, 0);
        }
    }

    public static class object {
        public Object object;
        public int type;

        public object(Object o, int t) {
            object = o;
            type = t;
        }

        public Object getObject() {
            return object;
        }

        public int getType() {
            return type;
        }
    }

    public static class Vector2f {
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

    public static class Vector3f {
        public float x;
        public float y;
        public float z;

        public Vector3f() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
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

        //�ӷ�
        public Vector3f add(Vector3f v) {
            return new Vector3f(this.x + v.x, this.y + v.y, this.z + v.z);
        }

        //����
        public Vector3f minus(Vector3f v) {
            return new Vector3f(this.x - v.x, this.y - v.y, this.z - v.z);
        }

        //�˷�
        public float multiV(Vector3f v) {
            return this.x * v.x + this.y * v.y + this.z * v.z;
        }

        //�볣�����
        public Vector3f multiK(float k) {
            return new Vector3f(this.x * k, this.y * k, this.z * k);
        }

        //���
        public Vector3f cross(Vector3f b) {
            return new Vector3f(
                    y * b.z - z * b.y,
                    z * b.x - x * b.z,
                    x * b.y - y * b.x
            );
        }

        //���
        public void normalize() {
            float mod = module();
            x /= mod;
            y /= mod;
            z /= mod;
        }

        //ģ
        public float module() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

    }

}
