package com.bn.Util;

import com.bn.csgStruct.Vector2f;
import com.bn.csgStruct.Vector3f;

import java.util.List;

//计算三角形法向量的工具类
public class VectorUtil {


    //两点距离
    public static float Length(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    //两点距离
    public static float Length(Vector2f v1, Vector2f v2) {
        return (float) Math.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y));
    }

    //二维向量点乘
    public static float Product(Vector2f v1, Vector2f v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    //二维向量是否垂直
    public static boolean isVertical(Vector2f v1, Vector2f v2) {
        if (getDegree(v1, v2) > -Math.sqrt(2.0f) / 2 && getDegree(v1, v2) < Math.sqrt(2.0f) / 2)
            return true;
        else
            return false;
    }

    //二维向量夹角
    public static float getDegree(Vector2f v1, Vector2f v2) {
        if (v2.x == 0 && v2.y == 0)
            return 0;
        else {
            return Product(v1, v2) / (v1.module() * v2.module());
        }
    }

    public static Vector3f getMean(List<Vector3f> vertices){
        Vector3f center = new Vector3f(0,0,0);
        for(Vector3f vector3f : vertices){
            center = center.add(vector3f);
        }
        return center.divideK(vertices.size());
    }

    public static Vector3f getMaxLen(List<Vector3f> vertices){
        Vector3f maxPos = new Vector3f(0,0,0);
        Vector3f minPos = new Vector3f(0,0,0);
        for(Vector3f vector3f : vertices){
            maxPos.x = Math.max(maxPos.x, vector3f.x);
            maxPos.y = Math.max(maxPos.y, vector3f.y);
            maxPos.z = Math.max(maxPos.z, vector3f.z);

            minPos.x = Math.min(minPos.x, vector3f.x);
            minPos.y = Math.min(minPos.y, vector3f.y);
            minPos.z = Math.min(minPos.z, vector3f.z);
        }
        return maxPos.minus(minPos).multiK(0.5f);
    }

    //射线与三角形相交的方法
    public static float IntersectTriangle(
            float[] g_nearxyz, float[] g_farxyz,
            float[] V0, float[] V1, float[] V2) {
        float[] edge1 = new float[3];
        float[] edge2 = new float[3];

        edge1[0] = V1[0] - V0[0];
        edge1[1] = V1[1] - V0[1];
        edge1[2] = V1[2] - V0[2];

        edge2[0] = V2[0] - V0[0];
        edge2[1] = V2[1] - V0[1];
        edge2[2] = V2[2] - V0[2];

        float[] dir = new float[3];
        dir[0] = g_farxyz[0] - g_nearxyz[0];
        dir[1] = g_farxyz[1] - g_nearxyz[1];
        dir[2] = g_farxyz[2] - g_nearxyz[2];

        float w = (float) Math.sqrt((double) Math.pow(dir[0], 2.0) + (double) Math.pow(dir[1], 2.0) + (double) Math.pow(dir[2], 2.0));
        dir[0] /= w;
        dir[1] /= w;
        dir[2] /= w;

        float[] pvec = new float[3];
        pvec[0] = dir[1] * edge2[2] - dir[2] * edge2[1];
        pvec[1] = dir[2] * edge2[0] - dir[0] * edge2[2];
        pvec[2] = dir[0] * edge2[1] - dir[1] * edge2[0];

        float det;
        det = edge1[0] * pvec[0] + edge1[1] * pvec[1] + edge1[2] * pvec[2];

        float[] tvec = new float[3];
        if (det > 0) {
            tvec[0] = g_nearxyz[0] - V0[0];
            tvec[1] = g_nearxyz[1] - V0[1];
            tvec[2] = g_nearxyz[2] - V0[2];
        } else {
            tvec[0] = V0[0] - g_nearxyz[0];
            tvec[1] = V0[1] - g_nearxyz[1];
            tvec[2] = V0[2] - g_nearxyz[2];
            det = -det;
        }

        if (det < 0.0001f) return -1;

        float u;
        u = tvec[0] * pvec[0] + tvec[1] * pvec[1] + tvec[2] * pvec[2];

        if (u < 0.0f || u > det)
            return -1;

        float[] qvec = new float[3];
        qvec[0] = tvec[1] * edge1[2] - tvec[2] * edge1[1];
        qvec[1] = tvec[2] * edge1[0] - tvec[0] * edge1[2];
        qvec[2] = tvec[0] * edge1[1] - tvec[1] * edge1[0];

        float v;
        v = dir[0] * qvec[0] + dir[1] * qvec[1] + dir[2] * qvec[2];
        if (v < 0.0f || u + v > det)
            return -1;

        float t = edge2[0] * qvec[0] + edge2[1] * qvec[1] + edge2[2] * qvec[2];
        float fInvDet = 1.0f / det;
        t *= fInvDet;
        u *= fInvDet;
        v *= fInvDet;
        return t;
    }



    public static float[] calVertices(
            List<Vector3f> alv,//原顶点列表（未卷绕）
            List<Integer> alFaceIndex//组织成面的顶点的索引值列表（按逆时针卷绕）
    )
    {
        float[] vertices=new float[alFaceIndex.size()*3];
        //生成顶点的数组
        int vCount=0;
        for(int i:alFaceIndex){
            vertices[vCount++]=alv.get(i).x;
            vertices[vCount++]=alv.get(i).y;
            vertices[vCount++]=alv.get(i).z;
        }
        return vertices;
    }
}
