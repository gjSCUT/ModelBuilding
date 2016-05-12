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

}
